const { app, BrowserWindow, ipcMain } = require('electron');
const { spawn } = require('child_process');
const http = require('http');
const path = require('path');

let mainWindow;
let backendProcess = null;

const BACKEND_URL = 'http://localhost:8080';

function checkBackend() {
    return new Promise((resolve) => {
        const req = http.get(BACKEND_URL, (res) => {
            resolve(res.statusCode === 200);
        });
        req.on('error', () => resolve(false));
        req.end();
    });
}

async function waitForBackend(maxAttempts = 30) {
    for (let i = 0; i < maxAttempts; i++) {
        if (await checkBackend()) {
            console.log('Backend is ready!');
            return true;
        }
        console.log(`Waiting for backend... (${i + 1}/${maxAttempts})`);
        await new Promise(resolve => setTimeout(resolve, 2000));
    }
    return false;
}

function startBackend() {
    console.log('Starting Spring Boot...');

    const isDev = !app.isPackaged;

    if (isDev) {
        const isWindows = process.platform === 'win32';
        const mvnCmd = isWindows ? 'mvnw.cmd' : './mvnw';

        backendProcess = spawn(mvnCmd, ['spring-boot:run'], {
            shell: true,
            stdio: 'inherit'
        });
    } else {
        const jarPath = path.join(process.resourcesPath, 'backend', 'profis-1.0.0.jar');
        console.log('JAR path:', jarPath);

        backendProcess = spawn('java', ['-jar', jarPath], {
            stdio: 'inherit'
        });
    }

    backendProcess.on('close', (code) => {
        console.log(`Backend stopped with code ${code}`);
        backendProcess = null;
    });
}

function stopBackend() {
    if (backendProcess) {
        console.log('Stopping backend...');
        backendProcess.kill();
        backendProcess = null;
    }
}


function createWindow() {
    mainWindow = new BrowserWindow({
        width: 1400,
        height: 900,
        minWidth: 1000,
        minHeight: 700,
        title: 'ProfIS',
        frame: false,  // ← Frameless!
        backgroundColor: '#0f0f0f',
        webPreferences: {
            nodeIntegration: false,
            contextIsolation: true,
            preload: path.join(__dirname, 'preload.js')  // ← Preload script!
        }
    });

    // Load the app
    mainWindow.loadURL(BACKEND_URL);

    mainWindow.on('closed', () => {
        mainWindow = null;
    });

}

function setupIPC() {
    ipcMain.on('window-minimize', () => {
        if (mainWindow) mainWindow.minimize();
    });

    ipcMain.on('window-maximize', () => {
        if (mainWindow) {
            if (mainWindow.isMaximized()) {
                mainWindow.unmaximize();
            } else {
                mainWindow.maximize();
            }
        }
    });

    ipcMain.on('window-close', () => {
        if (mainWindow) mainWindow.close();
    });
}

app.whenReady().then(async () => {
    console.log('Electron app is ready');

    setupIPC();

    try {
        const alreadyRunning = await checkBackend();

        if (!alreadyRunning) {
            startBackend();
            const isReady = await waitForBackend();
            if (!isReady) {
                console.error('Backend failed to start!');
                app.quit();
                return;
            }
        }

        createWindow();
    } catch (error) {
        console.error('Failed to start application:', error);
        app.quit();
    }
});

app.on('window-all-closed', () => {
    stopBackend();
    app.quit();
});

app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
        createWindow();
    }
});

app.on('before-quit', () => {
    stopBackend();
});

process.on('uncaughtException', (error) => {
    console.error('Uncaught Exception:', error);
});
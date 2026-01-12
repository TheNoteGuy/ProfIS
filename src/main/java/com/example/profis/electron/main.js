const { app, BrowserWindow } = require('electron');
const { spawn } = require('child_process');
const http = require('http');

let mainWindow;
let backendProcess = null;

const BACKEND_URL = 'http://localhost:8080';

/**
 * Check if Spring Boot is running
 */
function checkBackend() {
    return new Promise((resolve) => {
        const req = http.get(BACKEND_URL, (res) => {
            resolve(res.statusCode === 200);
        });
        req.on('error', () => resolve(false));
        req.end();
    });
}

/**
 * Wait for backend to start
 */
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

/**
 * Start Spring Boot
 */
function startBackend() {
    console.log('Starting Spring Boot...');

    // Starte einfach die JAR oder Maven
    const isWindows = process.platform === 'win32';
    const mvnCmd = isWindows ? 'mvnw.cmd' : './mvnw';

    backendProcess = spawn(mvnCmd, ['spring-boot:run'], {
        shell: true,
        stdio: 'inherit' // Zeigt logs in console
    });

    backendProcess.on('close', (code) => {
        console.log(`Backend stopped with code ${code}`);
        backendProcess = null;
    });
}

/**
 * Stop Spring Boot
 */
function stopBackend() {
    if (backendProcess) {
        console.log('Stopping backend...');
        backendProcess.kill();
        backendProcess = null;
    }
}

/**
 * Create the Electron window
 */
function createWindow() {
    mainWindow = new BrowserWindow({
        width: 1400,
        height: 900,
        title: 'ProfIS',
        webPreferences: {
            nodeIntegration: false,
            contextIsolation: true
        }
    });

    // Einfach die Spring Boot URL laden!
    mainWindow.loadURL(BACKEND_URL);

    mainWindow.on('closed', () => {
        mainWindow = null;
    });
}

// App startup
app.whenReady().then(async () => {
    console.log('Starting ProfIS...');

    // Check if backend already running
    const alreadyRunning = await checkBackend();

    if (!alreadyRunning) {
        // Start backend
        startBackend();

        // Wait for it
        const isReady = await waitForBackend();
        if (!isReady) {
            console.error('Backend failed to start!');
            app.quit();
            return;
        }
    }

    // Open window
    createWindow();
});

// Cleanup
app.on('window-all-closed', () => {
    stopBackend();
    app.quit();
});

app.on('before-quit', () => {
    stopBackend();
});
const API_BASE_URL = 'http://localhost:8080/api';

const AppState = {
    currentView: 'dashboard',
    students: [],
    theses: [],
    professors: [],
    semesters: [],
    programs: [],
    selectedStudent: null,
    selectedThesis: null,
    selectedProfessor: null
};

const ViewManager = {
    views: {},
    register(name, viewFunction) {
        this.views[name] = viewFunction;
    },
    render(viewName) {
        if (!this.views[viewName]) {
            console.error(`View ${viewName} not found`);
            return;
        }
        AppState.currentView = viewName;
        document.getElementById('app').innerHTML = this.views[viewName]();

        // Update navigation - KORRIGIERT für neue Struktur
        document.querySelectorAll('.nav-link').forEach(link => {
            link.classList.remove('active');
            if (link.dataset.view === viewName) {
                link.classList.add('active');
            }
        });

        const initFn = window[`init${viewName.charAt(0).toUpperCase() + viewName.slice(1)}View`];
        if (initFn) initFn();
    }
};

// Dashboard View
ViewManager.register('dashboard', () => `
    <div class="view-container">
        <div class="dashboard-grid">
            <div class="dashboard-card" data-view="theses">
                <h3>Arbeiten</h3>
                <div class="dashboard-stat" id="dashThesesCount">0</div>
                <div class="dashboard-label">Gesamt</div>
            </div>
            <div class="dashboard-card" data-view="students">
                <h3>Studenten</h3>
                <div class="dashboard-stat" id="dashStudentsCount">0</div>
                <div class="dashboard-label">Registriert</div>
            </div>
            <div class="dashboard-card" data-view="professors">
                <h3>Professoren</h3>
                <div class="dashboard-stat" id="dashProfessorsCount">0</div>
                <div class="dashboard-label">Aktiv</div>
            </div>
            <div class="dashboard-card" data-view="reports">
                <h3>Aktuelles Semester</h3>
                <div class="dashboard-stat" id="currentSemester">-</div>
                <div class="dashboard-label" id="currentSemesterInfo"></div>
            </div>
        </div>
    </div>
`);

// Reports View
ViewManager.register('reports', () => `
    <div class="view-container">
        <div class="top-bar">
            <h2 style="color: var(--text-primary); font-size: 1.5rem; font-weight: 700;">Reports & Auswertungen</h2>
        </div>
        <div class="main-content" style="display: block; overflow-y: auto;">
            <div class="report-card">
                <h3>SWS-Übersicht nach Professor</h3>
                <div style="margin: 16px 0;">
                    <select id="reportProfessor" class="form-select" onchange="loadProfessorReport(this.value)">
                        <option value="">Professor auswählen...</option>
                    </select>
                </div>
                <div id="professorReportContent"></div>
            </div>

            <div class="report-card">
                <h3>Arbeiten pro Semester</h3>
                <div style="margin: 16px 0;">
                    <select id="reportSemester" class="form-select" onchange="loadSemesterReport(this.value)">
                        <option value="">Semester auswählen...</option>
                    </select>
                </div>
                <div id="semesterReportContent"></div>
            </div>
        </div>
    </div>
`);

// Config View
ViewManager.register('config', () => `
    <div class="view-container">
        <div class="top-bar">
            <h2 style="color: var(--text-primary); font-size: 1.5rem; font-weight: 700;">Konfiguration</h2>
        </div>
        <div class="main-content" style="display: block; overflow-y: auto;">
            <div class="config-section">
                <h3>Semester-Verwaltung</h3>
                <button class="action-button" onclick="openAddSemesterModal()">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="12" y1="5" x2="12" y2="19"/>
                        <line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                    Neues Semester
                </button>
                <div style="margin-top: 16px;">
                    <table id="semestersTable">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Typ</th>
                                <th>Start</th>
                                <th>Ende</th>
                                <th>Status</th>
                                <th>Aktionen</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>

            <div class="config-section">
                <h3>Studiengänge</h3>
                <button class="action-button" onclick="alert('Studiengänge verwalten')">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="12" y1="5" x2="12" y2="19"/>
                        <line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                    Neuer Studiengang
                </button>
                <div style="margin-top: 16px;" id="programsList"></div>
            </div>
        </div>
    </div>
`);

// Students View
ViewManager.register('students', () => `
    <div class="view-container">
        <div class="top-bar">
            <button class="action-button" onclick="openAddStudentModal()">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="12" y1="5" x2="12" y2="19"/>
                    <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
                Neuer Student
            </button>
            <input type="text" class="global-search" placeholder="Studenten durchsuchen..." oninput="filterStudents(this.value)">
            <div class="quick-stats">
                <div class="stat-item">
                    <span>Studenten:</span>
                    <span class="stat-value" id="totalStudentsCount">0</span>
                </div>
            </div>
        </div>
        <div class="main-content">
            <div class="details-panel empty" id="studentDetails">Wählen Sie einen Studenten aus der Liste aus</div>
            <div class="list-sidebar">
                <div class="sidebar-header">
                    <div class="sidebar-title">Studenten</div>
                    <div class="sidebar-count" id="visibleStudentsCount">0</div>
                </div>
                <div id="studentsList"></div>
            </div>
        </div>
    </div>
`);

// Theses View
ViewManager.register('theses', () => `
    <div class="view-container">
        <div class="top-bar">
            <button class="action-button" onclick="openAddThesisModal()">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="12" y1="5" x2="12" y2="19"/>
                    <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
                Neue Arbeit
            </button>
            <input type="text" class="global-search" placeholder="Arbeiten durchsuchen..." oninput="filterTheses(this.value)">
            <div class="quick-stats">
                <div class="stat-item">
                    <span>Arbeiten:</span>
                    <span class="stat-value" id="totalThesesCount">0</span>
                </div>
            </div>
        </div>
        <div class="main-content">
            <div class="details-panel empty" id="thesisDetails">Wählen Sie eine Arbeit aus der Liste aus</div>
            <div class="list-sidebar">
                <div class="sidebar-header">
                    <div class="sidebar-title">Arbeiten</div>
                    <div class="sidebar-count" id="visibleThesesCount">0</div>
                </div>
                <div id="thesesList"></div>
            </div>
        </div>
    </div>
`);

// Professors View
ViewManager.register('professors', () => `
    <div class="view-container">
        <div class="top-bar">
            <button class="action-button" onclick="openAddProfessorModal()">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="12" y1="5" x2="12" y2="19"/>
                    <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
                Neuer Professor
            </button>
            <input type="text" class="global-search" placeholder="Professoren durchsuchen..." oninput="filterProfessors(this.value)">
            <div class="quick-stats">
                <div class="stat-item">
                    <span>Professoren:</span>
                    <span class="stat-value" id="totalProfessorsCount">0</span>
                </div>
            </div>
        </div>
        <div class="main-content">
            <div class="details-panel empty" id="professorDetails">Wählen Sie einen Professor aus der Liste aus</div>
            <div class="list-sidebar">
                <div class="sidebar-header">
                    <div class="sidebar-title">Professoren</div>
                    <div class="sidebar-count" id="visibleProfessorsCount">0</div>
                </div>
                <div id="professorsList"></div>
            </div>
        </div>
    </div>
`);

// ==================== VIEW INITIALIZATION ====================

async function initDashboardView() {
    try {
        await Promise.all([loadStudents(), loadTheses(), loadProfessors(), loadSemesters()]);
        document.getElementById('dashStudentsCount').textContent = AppState.students.length;
        document.getElementById('dashThesesCount').textContent = AppState.theses.length;
        document.getElementById('dashProfessorsCount').textContent = AppState.professors.length;

        const currentSem = AppState.semesters.find(s => s.isCurrent);
        if (currentSem) {
            document.getElementById('currentSemester').textContent = currentSem.name;
            document.getElementById('currentSemesterInfo').textContent =
                `${new Date(currentSem.startDate).toLocaleDateString('de-DE')} - ${new Date(currentSem.endDate).toLocaleDateString('de-DE')}`;
        }
    } catch (error) {
        console.error('Error loading dashboard data:', error);
    }
}

async function initReportsView() {
    await Promise.all([loadProfessors(), loadSemesters()]);

    const profSelect = document.getElementById('reportProfessor');
    profSelect.innerHTML = '<option value="">Professor auswählen...</option>' +
        AppState.professors.map(p => `<option value="${p.id}">${p.fullName}</option>`).join('');

    const semSelect = document.getElementById('reportSemester');
    semSelect.innerHTML = '<option value="">Semester auswählen...</option>' +
        AppState.semesters.map(s => `<option value="${s.id}">${s.name}</option>`).join('');
}

async function initConfigView() {
    await loadSemesters();
    await loadPrograms();
    renderSemestersTable();
    renderProgramsList();
}

async function initStudentsView() {
    await loadStudents();
    renderStudentsList(AppState.students);
    updateStudentsStats();
}

async function initThesesView() {
    await loadTheses();
    renderThesesList(AppState.theses);
    updateThesesStats();
}

async function initProfessorsView() {
    await loadProfessors();
    renderProfessorsList(AppState.professors);
    updateProfessorsStats();
}

// ==================== API FUNCTIONS ====================

async function loadStudents() {
    try {
        const response = await fetch(`${API_BASE_URL}/students`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        AppState.students = await response.json();
        return AppState.students;
    } catch (error) {
        console.error('Error loading students:', error);
        AppState.students = [];
        return [];
    }
}

async function loadTheses() {
    try {
        const response = await fetch(`${API_BASE_URL}/theses`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        AppState.theses = await response.json();
        return AppState.theses;
    } catch (error) {
        console.error('Error loading theses:', error);
        AppState.theses = [];
        return [];
    }
}

async function loadProfessors() {
    try {
        const response = await fetch(`${API_BASE_URL}/professors`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        AppState.professors = await response.json();
        return AppState.professors;
    } catch (error) {
        console.error('Error loading professors:', error);
        AppState.professors = [];
        return [];
    }
}

async function loadSemesters() {
    try {
        const response = await fetch(`${API_BASE_URL}/semesters`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        AppState.semesters = await response.json();
        return AppState.semesters;
    } catch (error) {
        console.error('Error loading semesters:', error);
        AppState.semesters = [];
        return [];
    }
}

async function loadPrograms() {
    try {
        const response = await fetch(`${API_BASE_URL}/programs`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        AppState.programs = await response.json();
        return AppState.programs;
    } catch (error) {
        console.error('Error loading programs:', error);
        AppState.programs = [];
        return [];
    }
}

// ==================== RENDER FUNCTIONS ====================

function renderStudentsList(students) {
    const container = document.getElementById('studentsList');
    if (!students || students.length === 0) {
        container.innerHTML = '<div style="text-align: center; color: var(--text-tertiary); padding: 20px;">Keine Studenten gefunden</div>';
        return;
    }
    container.innerHTML = students.map(s => `
        <div class="list-item ${AppState.selectedStudent?.matrikelnummer === s.matrikelnummer ? 'active' : ''}" onclick="selectStudent(${s.matrikelnummer})">
            <div class="item-header">
                <div class="item-badge">Student</div>
                <div class="item-meta">Mat.-Nr. ${s.matrikelnummer}</div>
            </div>
            <div class="item-title">${s.vorname} ${s.nachname}</div>
            <div class="item-subtitle">${s.mail}</div>
        </div>
    `).join('');
}

function renderThesesList(theses) {
    const container = document.getElementById('thesesList');
    if (!theses || theses.length === 0) {
        container.innerHTML = '<div style="text-align: center; color: var(--text-tertiary); padding: 20px;">Keine Arbeiten gefunden</div>';
        return;
    }
    container.innerHTML = theses.map(t => `
        <div class="list-item ${AppState.selectedThesis?.id === t.id ? 'active' : ''}" onclick="selectThesis(${t.id})">
            <div class="item-header">
                <div class="item-badge">${t.type || 'N/A'}</div>
                <div class="item-meta">${t.status || 'N/A'}</div>
            </div>
            <div class="item-title">${t.studentName || 'Unbekannt'}</div>
            <div class="item-subtitle">${t.title}</div>
        </div>
    `).join('');
}

function renderProfessorsList(professors) {
    const container = document.getElementById('professorsList');
    if (!professors || professors.length === 0) {
        container.innerHTML = '<div style="text-align: center; color: var(--text-tertiary); padding: 20px;">Keine Professoren gefunden</div>';
        return;
    }
    container.innerHTML = professors.map(p => `
        <div class="list-item ${AppState.selectedProfessor?.id === p.id ? 'active' : ''}" onclick="selectProfessor(${p.id})">
            <div class="item-header">
                <div class="item-badge">Professor</div>
                <div class="item-meta">${p.isExternal ? 'Extern' : 'Intern'}</div>
            </div>
            <div class="item-title">${p.fullName}</div>
            <div class="item-subtitle">${p.email}</div>
        </div>
    `).join('');
}

// ==================== SELECTION FUNCTIONS ====================

function selectStudent(matrikelnummer) {
    AppState.selectedStudent = AppState.students.find(s => s.matrikelnummer === matrikelnummer);
    renderStudentsList(AppState.students);
    displayStudentDetails(AppState.selectedStudent);
}

function selectThesis(id) {
    AppState.selectedThesis = AppState.theses.find(t => t.id === id);
    renderThesesList(AppState.theses);
    displayThesisDetails(AppState.selectedThesis);
}

function selectProfessor(id) {
    AppState.selectedProfessor = AppState.professors.find(p => p.id === id);
    renderProfessorsList(AppState.professors);
    displayProfessorDetails(AppState.selectedProfessor);
}

// ==================== DISPLAY DETAILS ====================

function displayStudentDetails(student) {
    const container = document.getElementById('studentDetails');
    const initials = student.vorname.charAt(0) + student.nachname.charAt(0);
    container.className = 'details-panel';
    container.innerHTML = `
        <div class="detail-header">
            <div class="detail-icon">${initials}</div>
            <div class="detail-header-info">
                <h2>${student.vorname} ${student.nachname}</h2>
                <p>Matrikelnummer: ${student.matrikelnummer}</p>
            </div>
            <div class="detail-actions">
                <div class="icon-button delete" onclick="deleteStudent(${student.matrikelnummer})" title="Löschen">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="3 6 5 6 21 6"/>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                    </svg>
                </div>
            </div>
        </div>
        <div class="detail-grid">
            <div class="detail-field">
                <div class="detail-label">Vorname</div>
                <div class="detail-value">${student.vorname}</div>
            </div>
            <div class="detail-field">
                <div class="detail-label">Nachname</div>
                <div class="detail-value">${student.nachname}</div>
            </div>
            <div class="detail-field full-width">
                <div class="detail-label">E-Mail</div>
                <div class="detail-value">${student.mail}</div>
            </div>
            <div class="detail-field">
                <div class="detail-label">Studiengang-ID</div>
                <div class="detail-value">${student.idStudiengang}</div>
            </div>
            <div class="detail-field">
                <div class="detail-label">Scheinfrei</div>
                <div class="detail-value">${student.scheinfrei ? 'Ja' : 'Nein'}</div>
            </div>
        </div>
    `;
}

function displayThesisDetails(thesis) {
    const container = document.getElementById('thesisDetails');
    const formatDate = (dateStr) => dateStr ? new Date(dateStr).toLocaleDateString('de-DE') : '-';
    container.className = 'details-panel';
    container.innerHTML = `
        <div class="detail-header">
            <div class="detail-icon">${thesis.type ? thesis.type.charAt(0) : 'T'}</div>
            <div class="detail-header-info">
                <h2>${thesis.studentName || 'Unbekannt'}</h2>
                <p>${thesis.type || 'N/A'} | Status: ${thesis.status || 'N/A'}</p>
            </div>
            <div class="detail-actions">
                <div class="icon-button delete" onclick="deleteThesis(${thesis.id})" title="Löschen">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="3 6 5 6 21 6"/>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                    </svg>
                </div>
            </div>
        </div>
        <div class="detail-grid">
            <div class="detail-field full-width">
                <div class="detail-label">Titel</div>
                <div class="detail-value">${thesis.title}</div>
            </div>
            <div class="detail-field">
                <div class="detail-label">Betreuer</div>
                <div class="detail-value">${thesis.supervisorName || '-'}</div>
            </div>
            <div class="detail-field">
                <div class="detail-label">Korreferent</div>
                <div class="detail-value">${thesis.coSupervisorName || '-'}</div>
            </div>
            <div class="detail-field">
                <div class="detail-label">Startdatum</div>
                <div class="detail-value">${formatDate(thesis.startDate)}</div>
            </div>
            <div class="detail-field">
                <div class="detail-label">Abgabedatum</div>
                <div class="detail-value">${formatDate(thesis.submissionDate)}</div>
            </div>
            ${thesis.finalGrade ? `
            <div class="detail-field">
                <div class="detail-label">Endnote</div>
                <div class="detail-value">${thesis.finalGrade}</div>
            </div>` : ''}
        </div>
    `;
}

function displayProfessorDetails(prof) {
    const container = document.getElementById('professorDetails');
    const initials = (prof.firstName?.charAt(0) || '') + (prof.lastName?.charAt(0) || '');
    container.className = 'details-panel';
    container.innerHTML = `
        <div class="detail-header">
            <div class="detail-icon">${initials}</div>
            <div class="detail-header-info">
                <h2>${prof.fullName}</h2>
                <p>${prof.isExternal ? 'Externer Referent' : 'Interner Referent'}</p>
            </div>
            <div class="detail-actions">
                <div class="icon-button delete" onclick="deleteProfessor(${prof.id})" title="Löschen">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="3 6 5 6 21 6"/>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                    </svg>
                </div>
            </div>
        </div>
        <div class="detail-grid">
            <div class="detail-field">
                <div class="detail-label">Anrede</div>
                <div class="detail-value">${prof.salutation || '-'}</div>
            </div>
            <div class="detail-field">
                <div class="detail-label">Name</div>
                <div class="detail-value">${prof.fullName}</div>
            </div>
            <div class="detail-field full-width">
                <div class="detail-label">E-Mail</div>
                <div class="detail-value">${prof.email}</div>
            </div>
            <div class="detail-field">
                <div class="detail-label">Telefon</div>
                <div class="detail-value">${prof.phone || '-'}</div>
            </div>
            <div class="detail-field full-width">
                <div class="detail-label">Adresse</div>
                <div class="detail-value">${prof.address || '-'}</div>
            </div>
        </div>
    `;
}

// ==================== FILTER FUNCTIONS ====================

function filterStudents(searchTerm) {
    let filtered = AppState.students;
    if (searchTerm) {
        filtered = AppState.students.filter(s =>
            `${s.vorname} ${s.nachname}`.toLowerCase().includes(searchTerm.toLowerCase()) ||
            s.mail.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }
    renderStudentsList(filtered);
    document.getElementById('visibleStudentsCount').textContent = filtered.length;
}

function filterTheses(searchTerm) {
    let filtered = AppState.theses;
    if (searchTerm) {
        filtered = AppState.theses.filter(t =>
            t.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
            (t.studentName && t.studentName.toLowerCase().includes(searchTerm.toLowerCase()))
        );
    }
    renderThesesList(filtered);
    document.getElementById('visibleThesesCount').textContent = filtered.length;
}

function filterProfessors(searchTerm) {
    let filtered = AppState.professors;
    if (searchTerm) {
        filtered = AppState.professors.filter(p =>
            p.fullName.toLowerCase().includes(searchTerm.toLowerCase()) ||
            p.email.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }
    renderProfessorsList(filtered);
    document.getElementById('visibleProfessorsCount').textContent = filtered.length;
}

// ==================== STATS UPDATES ====================

function updateStudentsStats() {
    document.getElementById('totalStudentsCount').textContent = AppState.students.length;
    document.getElementById('visibleStudentsCount').textContent = AppState.students.length;
}

function updateThesesStats() {
    document.getElementById('totalThesesCount').textContent = AppState.theses.length;
    document.getElementById('visibleThesesCount').textContent = AppState.theses.length;
}

function updateProfessorsStats() {
    document.getElementById('totalProfessorsCount').textContent = AppState.professors.length;
    document.getElementById('visibleProfessorsCount').textContent = AppState.professors.length;
}

// ==================== MODAL FUNCTIONS ====================

function openAddStudentModal() {
    document.getElementById('modals').innerHTML = `
        <div class="modal-overlay active" id="addStudentModal">
            <div class="modal">
                <div class="modal-header">
                    <div class="modal-title">Neuen Studenten anlegen</div>
                    <div class="modal-close" onclick="closeModal('addStudentModal')">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="6" y1="6" x2="18" y2="18"/>
                            <line x1="18" y1="6" x2="6" y2="18"/>
                        </svg>
                    </div>
                </div>
                <div class="modal-body">
                    <div id="errorMessage"></div>
                    <form id="addStudentForm" onsubmit="submitNewStudent(event)">
                        <div class="form-grid">
                            <div class="form-field">
                                <label class="form-label">Vorname *</label>
                                <input type="text" class="form-input" id="studentFirstName" required>
                            </div>
                            <div class="form-field">
                                <label class="form-label">Nachname *</label>
                                <input type="text" class="form-input" id="studentLastName" required>
                            </div>
                            <div class="form-field full-width">
                                <label class="form-label">E-Mail *</label>
                                <input type="email" class="form-input" id="studentEmail" required>
                            </div>
                            <div class="form-field">
                                <label class="form-label">Studiengang *</label>
                                <select class="form-select" id="studentProgram" required>
                                    <option value="">Bitte wählen</option>
                                    <option value="1">BSc Wirtschaftsinformatik</option>
                                    <option value="2">MSc Wirtschaftsinformatik</option>
                                </select>
                            </div>
                            <div class="form-field">
                                <label class="form-checkbox" style="visibility: hidden">
                                    <input type="checkbox" id="studentScheinfrei"style="visibility: hidden">
                                    <span class="form-label"style="visibility: hidden">Scheinfrei</span>
                                </label>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button class="modal-button cancel" onclick="closeModal('addStudentModal')">Abbrechen</button>
                    <button class="modal-button primary" onclick="document.getElementById('addStudentForm').requestSubmit()">Anlegen</button>
                </div>
            </div>
        </div>
    `;
}

async function openAddThesisModal() {
    await loadStudents();
    await loadProfessors();
    const studentsOptions = AppState.students.map(s => `<option value="${s.matrikelnummer}">${s.vorname} ${s.nachname} (${s.matrikelnummer})</option>`).join('');
    const professorsOptions = AppState.professors.map(p => `<option value="${p.id}">${p.fullName}</option>`).join('');
    document.getElementById('modals').innerHTML = `
        <div class="modal-overlay active" id="addThesisModal">
            <div class="modal">
                <div class="modal-header">
                    <div class="modal-title">Neue Arbeit anlegen</div>
                    <div class="modal-close" onclick="closeModal('addThesisModal')">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="6" y1="6" x2="18" y2="18"/>
                            <line x1="18" y1="6" x2="6" y2="18"/>
                        </svg>
                    </div>
                </div>
                <div class="modal-body">
                    <div id="errorMessage"></div>
                    <form id="addThesisForm" onsubmit="submitNewThesis(event)">
                        <div class="form-grid">
                            <div class="form-field full-width">
                                <label class="form-label">Student *</label>
                                <select class="form-select" id="thesisStudent" required>
                                    <option value="">Bitte wählen</option>${studentsOptions}
                                </select>
                            </div>
                            <div class="form-field full-width">
                                <label class="form-label">Titel *</label>
                                <input type="text" class="form-input" id="thesisTitle" required>
                            </div>
                            <div class="form-field">
                                <label class="form-label">Betreuer *</label>
                                <select class="form-select" id="thesisSupervisor" required>
                                    <option value="">Bitte wählen</option>${professorsOptions}
                                </select>
                            </div>
                            <div class="form-field">
                                <label class="form-label">Korreferent *</label>
                                <select class="form-select" id="thesisCoSupervisor" required>
                                    <option value="">Bitte wählen</option>${professorsOptions}
                                </select>
                            </div>
                            <div class="form-field">
                                <label class="form-label">Startdatum *</label>
                                <input type="date" class="form-input" id="thesisStartDate" required>
                            </div>
                            <div class="form-field">
                                <label class="form-label">Abgabedatum *</label>
                                <input type="date" class="form-input" id="thesisSubmissionDate" required>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button class="modal-button cancel" onclick="closeModal('addThesisModal')">Abbrechen</button>
                    <button class="modal-button primary" onclick="document.getElementById('addThesisForm').requestSubmit()">Anlegen</button>
                </div>
            </div>
        </div>
    `;
}

function openAddProfessorModal() {
    document.getElementById('modals').innerHTML = `
        <div class="modal-overlay active" id="addProfessorModal">
            <div class="modal">
                <div class="modal-header">
                    <div class="modal-title">Neuen Professor anlegen</div>
                    <div class="modal-close" onclick="closeModal('addProfessorModal')">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="6" y1="6" x2="18" y2="18"/>
                            <line x1="18" y1="6" x2="6" y2="18"/>
                        </svg>
                    </div>
                </div>
                <div class="modal-body">
                    <div id="errorMessage"></div>
                    <form id="addProfessorForm" onsubmit="submitNewProfessor(event)">
                        <div class="form-grid">
                            <div class="form-field">
                                <label class="form-label">Anrede</label>
                                <select class="form-select" id="professorSalutation">
                                    <option value="">Bitte wählen</option>
                                    <option value="Herr">Herr</option>
                                    <option value="Frau">Frau</option>
                                </select>
                            </div>
                            <div class="form-field"></div>
                            <div class="form-field">
                                <label class="form-label">Vorname *</label>
                                <input type="text" class="form-input" id="professorFirstName" required>
                            </div>
                            <div class="form-field">
                                <label class="form-label">Nachname *</label>
                                <input type="text" class="form-input" id="professorLastName" required>
                            </div>
                            <div class="form-field full-width">
                                <label class="form-label">E-Mail *</label>
                                <input type="email" class="form-input" id="professorEmail" required>
                            </div>
                            <div class="form-field">
                                <label class="form-label">Telefon *</label>
                                <input type="tel" class="form-input" id="professorPhone" required>
                            </div>
                            <div class="form-field">
                                <label class="form-checkbox">
                                    <input type="checkbox" id="professorExternal">
                                    <span class="form-label">Extern</span>
                                </label>
                            </div>
                            <div class="form-field full-width">
                                <label class="form-label">Adresse</label>
                                <input type="text" class="form-input" id="professorAddress">
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button class="modal-button cancel" onclick="closeModal('addProfessorModal')">Abbrechen</button>
                    <button class="modal-button primary" onclick="document.getElementById('addProfessorForm').requestSubmit()">Anlegen</button>
                </div>
            </div>
        </div>
    `;
}

function openAddSemesterModal() {
    document.getElementById('modals').innerHTML = `
        <div class="modal-overlay active">
            <div class="modal">
                <div class="modal-header">
                    <h3 class="modal-title">Neues Semester anlegen</h3>
                    <div class="modal-close" onclick="closeModal()">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="6" y1="6" x2="18" y2="18"/>
                            <line x1="18" y1="6" x2="6" y2="18"/>
                        </svg>
                    </div>
                </div>
                <div class="modal-body">
                    <form id="semesterForm" onsubmit="submitSemester(event)">
                        <div class="form-grid">
                            <div class="form-field">
                                <label class="form-label">Name *</label>
                                <input type="text" class="form-input" name="name" required placeholder="WS 2025/26">
                            </div>
                            <div class="form-field">
                                <label class="form-label">Typ *</label>
                                <select class="form-select" name="type" required>
                                    <option value="WINTER">Wintersemester</option>
                                    <option value="SUMMER">Sommersemester</option>
                                </select>
                            </div>
                            <div class="form-field">
                                <label class="form-label">Startdatum *</label>
                                <input type="date" class="form-input" name="startDate" required>
                            </div>
                            <div class="form-field">
                                <label class="form-label">Enddatum *</label>
                                <input type="date" class="form-input" name="endDate" required>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button class="modal-button cancel" onclick="closeModal()">Abbrechen</button>
                    <button class="modal-button primary" onclick="document.getElementById('semesterForm').requestSubmit()">Anlegen</button>
                </div>
            </div>
        </div>
    `;
}

function closeModal(modalId) {
    if (modalId) {
        const modal = document.getElementById(modalId);
        if (modal) modal.remove();
    } else {
        document.getElementById('modals').innerHTML = '';
    }
}

// ==================== SUBMIT FUNCTIONS ====================

async function submitNewStudent(event) {
    event.preventDefault();
    const newStudent = {
        vorname: document.getElementById('studentFirstName').value,
        nachname: document.getElementById('studentLastName').value,
        mail: document.getElementById('studentEmail').value,
        scheinfrei: document.getElementById('studentScheinfrei').checked,
        idStudiengang: parseInt(document.getElementById('studentProgram').value)
    };
    try {
        const response = await fetch(`${API_BASE_URL}/students`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newStudent)
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        await loadStudents();
        renderStudentsList(AppState.students);
        updateStudentsStats();
        closeModal('addStudentModal');
    } catch (error) {
        console.error('Error creating student:', error);
        document.getElementById('errorMessage').innerHTML = `<div class="error-message">${error.message}</div>`;
    }
}

async function submitNewThesis(event) {
    event.preventDefault();
    const newThesis = {
        matrikelnummer: parseInt(document.getElementById('thesisStudent').value),
        title: document.getElementById('thesisTitle').value,
        supervisorId: parseInt(document.getElementById('thesisSupervisor').value),
        coSupervisorId: parseInt(document.getElementById('thesisCoSupervisor').value),
        startDate: document.getElementById('thesisStartDate').value,
        submissionDate: document.getElementById('thesisSubmissionDate').value
    };
    try {
        const response = await fetch(`${API_BASE_URL}/theses`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newThesis)
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        await loadTheses();
        renderThesesList(AppState.theses);
        updateThesesStats();
        closeModal('addThesisModal');
    } catch (error) {
        console.error('Error creating thesis:', error);
        document.getElementById('errorMessage').innerHTML = `<div class="error-message">${error.message}</div>`;
    }
}

async function submitNewProfessor(event) {
    event.preventDefault();
    const newProfessor = {
        salutation: document.getElementById('professorSalutation').value,
        firstName: document.getElementById('professorFirstName').value,
        lastName: document.getElementById('professorLastName').value,
        email: document.getElementById('professorEmail').value,
        phone: document.getElementById('professorPhone').value,
        isExternal: document.getElementById('professorExternal').checked,
        address: document.getElementById('professorAddress').value
    };
    try {
        const response = await fetch(`${API_BASE_URL}/professors`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newProfessor)
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        await loadProfessors();
        renderProfessorsList(AppState.professors);
        updateProfessorsStats();
        closeModal('addProfessorModal');
    } catch (error) {
        console.error('Error creating professor:', error);
        document.getElementById('errorMessage').innerHTML = `<div class="error-message">${error.message}</div>`;
    }
}

async function submitSemester(e) {
    e.preventDefault();
    const form = e.target;
    const data = {
        name: form.name.value,
        type: form.type.value,
        startDate: form.startDate.value,
        endDate: form.endDate.value
    };

    await fetch(`${API_BASE_URL}/semesters`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });

    await loadSemesters();
    renderSemestersTable();
    closeModal();
}

// ==================== DELETE FUNCTIONS ====================

async function deleteStudent(matrikelnummer) {
    if (!confirm('Student wirklich löschen?')) return;
    try {
        const response = await fetch(`${API_BASE_URL}/students/${matrikelnummer}`, { method: 'DELETE' });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        await loadStudents();
        renderStudentsList(AppState.students);
        updateStudentsStats();
        document.getElementById('studentDetails').className = 'details-panel empty';
        document.getElementById('studentDetails').textContent = 'Wählen Sie einen Studenten aus der Liste aus';
    } catch (error) {
        console.error('Error deleting student:', error);
        alert('Fehler beim Löschen: ' + error.message);
    }
}

async function deleteThesis(id) {
    if (!confirm('Arbeit wirklich löschen?')) return;
    try {
        const response = await fetch(`${API_BASE_URL}/theses/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        await loadTheses();
        renderThesesList(AppState.theses);
        updateThesesStats();
        document.getElementById('thesisDetails').className = 'details-panel empty';
        document.getElementById('thesisDetails').textContent = 'Wählen Sie eine Arbeit aus der Liste aus';
    } catch (error) {
        console.error('Error deleting thesis:', error);
        alert('Fehler beim Löschen: ' + error.message);
    }
}

async function deleteProfessor(id) {
    if (!confirm('Professor wirklich löschen?')) return;
    try {
        const response = await fetch(`${API_BASE_URL}/professors/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        await loadProfessors();
        renderProfessorsList(AppState.professors);
        updateProfessorsStats();
        document.getElementById('professorDetails').className = 'details-panel empty';
        document.getElementById('professorDetails').textContent = 'Wählen Sie einen Professor aus der Liste aus';
    } catch (error) {
        console.error('Error deleting professor:', error);
        alert('Fehler beim Löschen: ' + error.message);
    }
}

async function deleteSemester(id) {
    if (!confirm('Semester wirklich löschen?')) return;
    await fetch(`${API_BASE_URL}/semesters/${id}`, { method: 'DELETE' });
    await loadSemesters();
    renderSemestersTable();
}

// ==================== REPORT FUNCTIONS ====================

async function loadProfessorReport(profId) {
    if (!profId) return;
    try {
        const res = await fetch(`${API_BASE_URL}/reports/sws-calculation?professorId=${profId}`);
        const data = await res.json();

        const calc = data.swsCalculation;
        const content = document.getElementById('professorReportContent');
        const percentage = (calc.totalSws / calc.maxSwsAllowed * 100);
        content.innerHTML = `
            <div style="margin: 16px 0;">
                <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px;">
                    <div>
                        <div style="color: var(--text-tertiary); font-size: 12px;">Gesamt SWS</div>
                        <div style="color: var(--accent-color); font-size: 24px; font-weight: bold;">${calc.totalSws.toFixed(2)}</div>
                    </div>
                    <div>
                        <div style="color: var(--text-tertiary); font-size: 12px;">Als Betreuer</div>
                        <div style="color: var(--accent-color); font-size: 24px; font-weight: bold;">${calc.swsAsSupervisor.toFixed(2)}</div>
                    </div>
                    <div>
                        <div style="color: var(--text-tertiary); font-size: 12px;">Als Korreferent</div>
                        <div style="color: var(--accent-color); font-size: 24px; font-weight: bold;">${calc.swsAsCoSupervisor.toFixed(2)}</div>
                    </div>
                </div>
                <div class="progress-bar">
                    <div class="progress-fill" style="width: ${percentage}%; background-color: ${calc.isOverLimit ? 'var(--error-color)' : 'var(--accent-color)'};">
                        ${percentage.toFixed(0)}%
                    </div>
                </div>
                <div style="color: var(--text-tertiary); font-size: 12px; margin-top: 4px;">
                    ${calc.totalSws.toFixed(2)} von ${calc.maxSwsAllowed.toFixed(2)} SWS belegt
                    ${calc.isOverLimit ? ' - LIMIT ÜBERSCHRITTEN!' : ''}
                </div>
            </div>
            <table style="margin-top: 16px;">
                <thead><tr><th>Arbeit</th><th>Rolle</th><th>SWS</th></tr></thead>
                <tbody>
                    ${data.breakdown.map(b => `
                        <tr>
                            <td>${b.thesisTitle}</td>
                            <td>${b.role === 'SUPERVISOR' ? 'Betreuer' : 'Korreferent'}</td>
                            <td>${b.sws.toFixed(2)}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    } catch (err) {
        console.error(err);
    }
}

async function loadSemesterReport(semId) {
    if (!semId) return;
    const semTheses = AppState.theses.filter(t => t.semester && t.semester.id == semId);
    const content = document.getElementById('semesterReportContent');
    content.innerHTML = `
        <div style="margin: 16px 0;">
            <div style="color: var(--accent-color); font-size: 24px; font-weight: bold;">
                ${semTheses.length} Arbeiten
            </div>
        </div>
        <table>
            <thead><tr><th>Student</th><th>Titel</th><th>Typ</th><th>Status</th></tr></thead>
            <tbody>
                ${semTheses.map(t => `
                    <tr>
                        <td>${t.studentName || '-'}</td>
                        <td>${t.title}</td>
                        <td>${t.type}</td>
                        <td>${t.status}</td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
}

// ==================== CONFIG FUNCTIONS ====================

function renderSemestersTable() {
    const tbody = document.querySelector('#semestersTable tbody');
    tbody.innerHTML = AppState.semesters.map(s => `
        <tr>
            <td>${s.name}</td>
            <td>${s.type === 'WINTER' ? 'Wintersemester' : 'Sommersemester'}</td>
            <td>${new Date(s.startDate).toLocaleDateString('de-DE')}</td>
            <td>${new Date(s.endDate).toLocaleDateString('de-DE')}</td>
            <td>${s.isCurrent ? '<span style="color: var(--success-color);">Aktuell</span>' : '-'}</td>
            <td>
                <button class="icon-button" onclick="editSemester(${s.id})">Bearbeiten</button>
                <button class="icon-button delete" onclick="deleteSemester(${s.id})">Löschen</button>
            </td>
        </tr>
    `).join('');
}

function renderProgramsList() {
    const container = document.getElementById('programsList');
    container.innerHTML = AppState.programs.map(p => `
        <div style="background: var(--tertiary-bg); padding: 12px; border-radius: var(--radius-md); margin-bottom: 8px; border: 1px solid var(--border-color);">
            <div style="font-weight: bold; color: var(--text-primary);">${p.degreeTitle}</div>
            <div style="color: var(--text-secondary); font-size: 13px;">${p.departmentName || 'Unbekannt'} - ${p.degreeTypeName || 'Unbekannt'}</div>
        </div>
    `).join('');
}

function editSemester(id) {
    alert('Bearbeiten-Funktion noch nicht implementiert für Semester ' + id);
}

// ==================== NAVIGATION EVENT LISTENER ====================

document.addEventListener('click', e => {
    const link = e.target.closest('[data-view]');
    if (!link) return;
    e.preventDefault();
    const viewName = link.dataset.view;
    if (!viewName) return;
    ViewManager.render(viewName);
});

// ==================== INITIALIZATION ====================

ViewManager.render('dashboard');
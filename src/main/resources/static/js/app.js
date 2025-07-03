document.addEventListener('DOMContentLoaded', () => {
    const state = { user: null, employees: [], managers: [] };

    // --- Helper Functions ---
    async function apiFetch(url, options = {}) {
        try {
            const response = await fetch(url, options);
            if (!response.ok) {
                if (response.status === 401 || response.status === 403 || response.redirected) {
                     window.location.href = '/login.html'; return;
                }
                const errorData = await response.json().catch(() => ({ message: 'Invalid JSON response' }));
                throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
            }
            if (response.status === 204) return null;
            return response.json();
        } catch (error) {
            console.error('API Fetch Error:', error);
            alert(`An error occurred: ${error.message}`);
            throw error;
        }
    }
    function showView(viewId) {
        document.querySelectorAll('.view').forEach(view => view.classList.remove('active-view'));
        document.getElementById(viewId).classList.add('active-view');
        const navLinks = document.querySelectorAll('.nav-link');
        const activeLink = document.querySelector(`.nav-link[data-view="${viewId}"]`);
        navLinks.forEach(link => link.classList.remove('active'));
        if (activeLink) {
            activeLink.classList.add('active');
            document.getElementById('view-title').textContent = activeLink.textContent.trim();
        }
    }
    function formatDate(dateString) { if (!dateString) return 'N/A'; return new Date(dateString).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' }); }
    function toggleModal(modalId, show) {
        const modal = document.getElementById(modalId);
        const modalContainer = document.getElementById('modal-container');
        if (show) { modalContainer.classList.remove('hidden'); modal.classList.remove('hidden');
        } else { modalContainer.classList.add('hidden'); modal.classList.add('hidden'); }
    }

    // --- UI Setup & Rendering ---
    function setupUIForRole(role) {
        // Hide all role-specific elements first
        document.querySelectorAll('.role-manager, .role-admin').forEach(el => el.classList.add('hidden'));

        // Show elements based on the role
        if (role === 'ROLE_MANAGER') {
            document.querySelectorAll('.role-manager').forEach(el => el.classList.remove('hidden'));
        } else if (role === 'ROLE_ADMIN') {
            // Admin sees ONLY admin links. Manager functionality is separate.
            document.querySelectorAll('.role-admin').forEach(el => el.classList.remove('hidden'));
        }
        const roleName = role.replace('ROLE_', '').toLowerCase();
        document.getElementById('user-role').textContent = roleName;
        document.getElementById('user-role').className = `role-badge ${roleName}`;
    }

    async function fetchDashboardData() {
        const data = await apiFetch('/api/dashboard');
        if (!data) return;
        state.user = data.userInfo;

        document.getElementById('user-fullname').textContent = state.user.fullname;
        setupUIForRole(state.user.role);

        // Populate universal data
        const openTasks = data.myOpenTasks?.filter(task => task.status !== 'COMPLETE') || [];
        document.getElementById('dashboard-open-tasks').textContent = openTasks.length;
        document.getElementById('dashboard-leave-requests').textContent = data.myLeaveRequests?.length || 0;

        const myTasksList = document.getElementById('dashboard-my-tasks-list');
        myTasksList.innerHTML = '';
        if (openTasks.length > 0) {
            openTasks.slice(0, 5).forEach(task => myTasksList.innerHTML += `<tr><td>${task.title}</td><td><span class="status-badge status-${task.status.toLowerCase().replace('_', '-')}">${task.status.replace('_', ' ')}</span></td><td>${formatDate(task.dueDate)}</td></tr>`);
        } else { myTasksList.innerHTML = `<tr><td colspan="3">No open tasks.</td></tr>`; }

        const managerInfoEl = document.getElementById('manager-info');
        if (state.user.role === 'ROLE_EMPLOYEE' && data.userInfo.managerName) {
            managerInfoEl.textContent = `Your Manager: ${data.userInfo.managerName}`;
            managerInfoEl.classList.remove('hidden');
        } else { managerInfoEl.classList.add('hidden'); }
        
        // Hide all role-specific dashboard sections before showing the correct one
        document.getElementById('dashboard-approval-section').classList.add('hidden');
        document.getElementById('dashboard-manager-tasks-section').classList.add('hidden');

        // Populate Manager-specific dashboard
        if (state.user.role === 'ROLE_MANAGER') {
            document.getElementById('dashboard-approval-section').classList.remove('hidden');
            document.getElementById('dashboard-manager-tasks-section').classList.remove('hidden');
            
            document.getElementById('dashboard-pending-approvals').textContent = data.pendingLeaveApprovals?.length || 0;
            const approvalList = document.getElementById('dashboard-leave-approval-list');
            approvalList.innerHTML = '';
            if (data.pendingLeaveApprovals?.length > 0) {
                data.pendingLeaveApprovals.slice(0, 5).forEach(req => approvalList.innerHTML += `<tr><td>${req.employee.fullname}</td><td>${req.leaveType}</td><td>${formatDate(req.startDate)} - ${formatDate(req.endDate)}</td><td><button class="btn btn-success btn-sm" onclick="app.handleLeaveAction(${req.id}, 'approve')">Approve</button> <button class="btn btn-danger btn-sm" onclick="app.handleLeaveAction(${req.id}, 'reject')">Reject</button></td></tr>`);
            } else { approvalList.innerHTML = `<tr><td colspan="4">No pending leave approvals.</td></tr>`; }

            const assignedByMeList = document.getElementById('dashboard-assigned-by-me-list');
            assignedByMeList.innerHTML = '';
            if (data.tasksAssignedByMe?.length > 0) {
                data.tasksAssignedByMe.slice(0, 5).forEach(task => assignedByMeList.innerHTML += `<tr><td>${task.title}</td><td>${task.assignedTo.fullname}</td><td><span class="status-badge status-${task.status.toLowerCase().replace('_', '-')}">${task.status.replace('_', ' ')}</span></td><td>${formatDate(task.dueDate)}</td></tr>`);
            } else { assignedByMeList.innerHTML = `<tr><td colspan="4">You have not assigned any tasks.</td></tr>`; }
        }
        
    }

    async function fetchMyTasks() {
        const tasks = await apiFetch('/api/tasks/my-tasks');
        if(!tasks) return;
        const tableBody = document.getElementById('my-tasks-table-body');
        tableBody.innerHTML = '';
        if(tasks.length > 0) {
            tasks.forEach(task => {
                const assignedByText = task.assignedBy ? `${task.assignedBy.fullname} (${task.assignedBy.username})` : 'N/A';
                tableBody.innerHTML += `<tr><td>${task.title}</td><td>${task.description || 'N/A'}</td><td>${assignedByText}</td><td>${formatDate(task.dueDate)}</td><td><span class="status-badge status-${task.status.toLowerCase().replace('_', '-')}">${task.status.replace('_', ' ')}</span></td><td><select class="task-status-updater" data-task-id="${task.taskId}" ${task.status === 'COMPLETE' ? 'disabled' : ''}><option value="">Update Status</option><option value="IN_PROGRESS" ${task.status === 'IN_PROGRESS' ? 'disabled' : ''}>In Progress</option><option value="COMPLETE" ${task.status === 'COMPLETE' ? 'disabled' : ''}>Complete</option></select></td></tr>`;
            });
        } else { tableBody.innerHTML = `<tr><td colspan="6">No tasks assigned to you.</td></tr>`; }
        document.querySelectorAll('.task-status-updater').forEach(select => {
            select.addEventListener('change', (e) => { if (e.target.value) updateTaskStatus(e.target.dataset.taskId, e.target.value); });
        });
    }

    async function fetchMyLeave() {
        const requests = await apiFetch('/api/leave/my-requests');
        if(!requests) return;
        const tableBody = document.getElementById('my-leave-table-body');
        tableBody.innerHTML = '';
        if (requests.length > 0) {
            requests.forEach(req => tableBody.innerHTML += `<tr><td>${req.leaveType}</td><td>${formatDate(req.startDate)}</td><td>${formatDate(req.endDate)}</td><td>${req.reason || 'N/A'}</td><td><span class="status-badge status-${req.status.toLowerCase()}">${req.status}</span></td></tr>`);
        } else { tableBody.innerHTML = `<tr><td colspan="5">You have not submitted any leave requests.</td></tr>`; }
    }
    
    async function fetchLeaveApprovals() {
         const requests = await apiFetch('/api/leave/pending');
         if(!requests) return;
         const tableBody = document.getElementById('leave-approval-table-body');
         tableBody.innerHTML = '';
         if(requests.length > 0) {
            requests.forEach(req => tableBody.innerHTML += `<tr><td>${req.employee.fullname}</td><td>${req.leaveType}</td><td>${formatDate(req.startDate)} - ${formatDate(req.endDate)}</td><td>${req.reason || 'N/A'}</td><td><button class="btn btn-success btn-sm" onclick="app.handleLeaveAction(${req.id}, 'approve')">Approve</button> <button class="btn btn-danger btn-sm" onclick="app.handleLeaveAction(${req.id}, 'reject')">Reject</button></td></tr>`);
         } else { tableBody.innerHTML = `<tr><td colspan="5">No pending leave requests to approve.</td></tr>`; }
    }

    async function populateAssignableUsersDropdown() {
        if (state.employees.length === 0) {
            const employees = await apiFetch('/api/users/employees');
            const managers = await apiFetch('/api/users/managers');
            if (employees && managers) state.employees = [...employees, ...managers];
        }
        const select = document.getElementById('task-assign-to');
        select.innerHTML = '<option value="">Select a user</option>';
        state.employees.forEach(emp => { if (emp.userId !== state.user.userId) select.innerHTML += `<option value="${emp.userId}">${emp.fullname} (${emp.role.replace('ROLE_', '')})</option>`; });
    }
    
    async function fetchUsersForManagement() {
        const users = await apiFetch('/api/users');
        if(!users) return;
        const tableBody = document.getElementById('user-management-table-body');
        tableBody.innerHTML = '';
        users.forEach(user => tableBody.innerHTML += `<tr><td>${user.fullname}</td><td>${user.username}</td><td>${user.email}</td><td>${user.role.replace('ROLE_', '')}</td><td>${user.managerName || 'N/A'}</td><td><button class="btn btn-danger btn-sm" onclick="app.deleteUser(${user.userId}, '${user.username}')">Delete</button></td></tr>`);
    }
    
    async function populateManagerDropdown() {
        if (state.managers.length === 0) { state.managers = await apiFetch('/api/users/managers'); }
        const select = document.getElementById('user-create-manager');
        select.innerHTML = '<option value="">Select a manager (Optional)</option>';
        if (state.managers) { state.managers.forEach(mgr => { select.innerHTML += `<option value="${mgr.userId}">${mgr.fullname}</option>`; }); }
    }

    function setupEventListeners() {
        document.querySelectorAll('.nav-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const viewId = link.dataset.view;
                showView(viewId);
                switch(viewId) {
                    case 'dashboard-view': fetchDashboardData(); break;
                    case 'my-tasks-view': fetchMyTasks(); break;
                    case 'my-leave-view': fetchMyLeave(); break;
                    case 'assign-task-view': populateAssignableUsersDropdown(); break;
                    case 'leave-approval-view': fetchLeaveApprovals(); break;
                    case 'user-management-view': fetchUsersForManagement(); break;
                }
            });
        });
        const applyLeaveBtn = document.getElementById('apply-leave-btn');
        if (applyLeaveBtn) applyLeaveBtn.addEventListener('click', () => toggleModal('apply-leave-modal', true));
        const createUserBtn = document.getElementById('create-user-btn');
        if (createUserBtn) {
            createUserBtn.addEventListener('click', () => {
                populateManagerDropdown();
                document.getElementById('manager-select-group').classList.remove('hidden');
                toggleModal('create-user-modal', true);
            });
        }
        document.getElementById('modal-container').addEventListener('click', (e) => {
            if (e.target.id === 'modal-container' || e.target.hasAttribute('data-close-modal')) {
                toggleModal('apply-leave-modal', false);
                toggleModal('create-user-modal', false);
            }
        });
        const applyLeaveForm = document.getElementById('apply-leave-form');
        if (applyLeaveForm) applyLeaveForm.addEventListener('submit', handleApplyLeave);
        const assignTaskForm = document.getElementById('assign-task-form');
        if (assignTaskForm) assignTaskForm.addEventListener('submit', handleAssignTask);
        const createUserForm = document.getElementById('create-user-form');
        if (createUserForm) createUserForm.addEventListener('submit', handleCreateUser);
    }

    window.app = {
        async handleLeaveAction(requestId, action) {
            if (!confirm(`Are you sure you want to ${action} this leave request?`)) return;
            await apiFetch(`/api/leave/${requestId}/${action}`, { method: 'POST' });
            alert(`Leave request has been ${action}d.`);
            fetchDashboardData();
            if (document.getElementById('leave-approval-view').classList.contains('active-view')) fetchLeaveApprovals();
        },
        async deleteUser(userId, username) {
            if (!confirm(`Are you sure you want to delete user "${username}"? This action cannot be undone.`)) return;
            await apiFetch(`/api/users/${userId}`, { method: 'DELETE' });
            alert(`User ${username} has been deleted.`);
            fetchUsersForManagement();
        }
    };
    
    async function updateTaskStatus(taskId, newStatus) {
        try {
            await apiFetch(`/api/tasks/${taskId}/status`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(newStatus) });
            alert('Task status updated successfully!');
            fetchMyTasks();
            fetchDashboardData();
        } catch (error) {
            console.error('Failed to update task status:', error);
            document.querySelector(`.task-status-updater[data-task-id="${taskId}"]`).value = "";
        }
    }

    async function handleApplyLeave(e) {
        e.preventDefault();
        const payload = { leaveType: document.getElementById('leave-type').value, startDate: document.getElementById('leave-start-date').value, endDate: document.getElementById('leave-end-date').value, reason: document.getElementById('leave-reason').value };
        await apiFetch('/api/leave/apply', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
        alert('Leave request submitted successfully!');
        e.target.reset();
        toggleModal('apply-leave-modal', false);
        fetchMyLeave();
        fetchDashboardData();
    }

    async function handleAssignTask(e) {
        e.preventDefault();
        const payload = { title: document.getElementById('task-title').value, description: document.getElementById('task-description').value, dueDate: document.getElementById('task-due-date').value, assignedToId: document.getElementById('task-assign-to').value };
        await apiFetch('/api/tasks', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
        alert('Task assigned successfully!');
        e.target.reset();
    }
    
    async function handleCreateUser(e) {
        e.preventDefault();
        const payload = { username: document.getElementById('user-create-username').value, fullname: document.getElementById('user-create-fullname').value, email: document.getElementById('user-create-email').value, password: document.getElementById('user-create-password').value, role: document.getElementById('user-create-role').value, managerId: document.getElementById('user-create-manager').value };
        if (!payload.managerId) {
            delete payload.managerId;
        }
        await apiFetch('/api/users', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
        alert('User created successfully!');
        e.target.reset();
        toggleModal('create-user-modal', false);
        fetchUsersForManagement();
    }

    function init() {
        setupEventListeners();
        fetchDashboardData();
        showView('dashboard-view');
    }

    init();
});
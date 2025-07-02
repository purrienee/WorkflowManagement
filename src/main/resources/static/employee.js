document.addEventListener('DOMContentLoaded', () => {
    const addLeaveBtn = document.getElementById('add-leave-btn');
    const modal = document.getElementById('leave-request-modal');
    const closeModalBtn = modal.querySelector('.close-button');
    const leaveForm = document.getElementById('leave-request-form');
    const requestsBody = document.getElementById('my-requests-body');
    const errorMessage = document.getElementById('form-error-message');

    const API_URL = '/api/leave-requests';

    // --- UTILITY FUNCTIONS ---
    const showSpinner = (button) => {
        button.disabled = true;
        button.querySelector('.btn-text').style.display = 'none';
        button.querySelector('.spinner').style.display = 'inline-block';
    };

    const hideSpinner = (button) => {
        button.disabled = false;
        button.querySelector('.btn-text').style.display = 'inline-block';
        button.querySelector('.spinner').style.display = 'none';
    };

    // --- MODAL LOGIC ---
    addLeaveBtn.addEventListener('click', () => modal.classList.add('show'));
    closeModalBtn.addEventListener('click', () => modal.classList.remove('show'));
    window.addEventListener('click', (e) => {
        if (e.target === modal) modal.classList.remove('show');
    });

    // --- API & RENDER LOGIC ---
    const fetchAndRenderRequests = async () => {
        try {
            const response = await fetch(`${API_URL}/my`);
            if (!response.ok) throw new Error('Failed to fetch leave requests.');
            const requests = await response.json();
            
            requestsBody.innerHTML = ''; // Clear table
            requests.forEach(req => {
                const row = `
                    <tr>
                        <td>${req.startDate}</td>
                        <td>${req.endDate}</td>
                        <td>${req.leaveType}</td>
                        <td>${req.reason}</td>
                        <td><span class="status status-${req.status}">${req.status}</span></td>
                    </tr>`;
                requestsBody.innerHTML += row;
            });
        } catch (error) {
            console.error('Error:', error);
            requestsBody.innerHTML = `<tr><td colspan="5" class="form-error" style="display:table-cell; text-align:center;">${error.message}</td></tr>`;
        }
    };

    // --- FORM SUBMISSION ---
    leaveForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        errorMessage.style.display = 'none';

        const startDate = document.getElementById('start-date').value;
        const endDate = document.getElementById('end-date').value;
        
        if (new Date(endDate) < new Date(startDate)) {
            errorMessage.textContent = 'End date cannot be before start date.';
            errorMessage.style.display = 'block';
            return;
        }

        const formData = {
            leaveType: document.getElementById('leave-type').value,
            startDate: startDate,
            endDate: endDate,
            reason: document.getElementById('reason').value,
        };

        const submitBtn = leaveForm.querySelector('button[type="submit"]');
        showSpinner(submitBtn);

        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to submit request.');
            }

            modal.classList.remove('show');
            leaveForm.reset();
            await fetchAndRenderRequests(); // Refresh list
        } catch (error) {
            errorMessage.textContent = error.message;
            errorMessage.style.display = 'block';
        } finally {
            hideSpinner(submitBtn);
        }
    });

    // Initial load
    fetchAndRenderRequests();
});
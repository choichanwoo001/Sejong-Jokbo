(function () {
    const SEARCH_FORM_ID = 'searchForm';
    const TABLE_SELECTOR = '#historyTable tbody tr';
    const NO_RESULTS_CLASS = 'no-results';
    const HISTORY_TABLE_BODY = '#historyTable tbody';

    function init() {
        const searchForm = document.getElementById(SEARCH_FORM_ID);
        if (!searchForm) {
            return;
        }

        searchForm.addEventListener('submit', (event) => {
            event.preventDefault();
            performSearch();
        });

        const resetButton = document.getElementById('resetSearchBtn');
        if (resetButton) {
            resetButton.addEventListener('click', resetSearch);
        }

        document.querySelectorAll('.filter-select').forEach((select) => {
            select.addEventListener('change', applyFilters);
        });
    }

    function performSearch() {
        const rows = document.querySelectorAll(TABLE_SELECTOR);
        if (!rows.length) {
            return;
        }

        const processDate = getInputValue('processDate');
        const bookTitle = getInputValue('bookTitle').toLowerCase();
        const registrant = getInputValue('registrant').toLowerCase();

        let hasVisibleRows = false;

        rows.forEach((row) => {
            const rowDate = row.getAttribute('data-process-date');
            const rowBookTitle = (row.getAttribute('data-book-title') || '').toLowerCase();
            const rowRegistrant = (row.getAttribute('data-registrant') || '').toLowerCase();

            const dateMatch = !processDate || rowDate === processDate;
            const bookMatch = !bookTitle || rowBookTitle.includes(bookTitle);
            const registrantMatch = !registrant || rowRegistrant.includes(registrant);

            if (dateMatch && bookMatch && registrantMatch) {
                row.style.display = '';
                hasVisibleRows = true;
            } else {
                row.style.display = 'none';
            }
        });

        toggleNoResultsMessage(hasVisibleRows);
    }

    function resetSearch() {
        const searchForm = document.getElementById(SEARCH_FORM_ID);
        if (!searchForm) {
            return;
        }

        searchForm.reset();

        document.querySelectorAll(TABLE_SELECTOR).forEach((row) => {
            row.style.display = '';
        });

        removeNoResultsMessage();
    }

    function applyFilters() {
        const actionFilter = getSelectValue('actionFilter');
        const previousStatusFilter = getSelectValue('previousStatusFilter');
        const newStatusFilter = getSelectValue('newStatusFilter');

        const params = new URLSearchParams({ page: '0' });

        if (actionFilter) params.append('action', actionFilter);
        if (previousStatusFilter) params.append('previousStatus', previousStatusFilter);
        if (newStatusFilter) params.append('newStatus', newStatusFilter);

        const baseUrl = window.location.pathname.split('?')[0];
        window.location.href = `${baseUrl}?${params.toString()}`;
    }

    function getInputValue(id) {
        const element = document.getElementById(id);
        return element ? element.value : '';
    }

    function getSelectValue(id) {
        const element = document.getElementById(id);
        return element ? element.value : '';
    }

    function toggleNoResultsMessage(hasVisibleRows) {
        const existingMessage = document.querySelector(`.${NO_RESULTS_CLASS}`);

        if (hasVisibleRows) {
            if (existingMessage) {
                existingMessage.remove();
            }
            return;
        }

        if (existingMessage) {
            return;
        }

        const tableBody = document.querySelector(HISTORY_TABLE_BODY);
        if (!tableBody) {
            return;
        }

        const messageRow = document.createElement('tr');
        messageRow.className = NO_RESULTS_CLASS;
        messageRow.innerHTML = `
            <td colspan="9" style="text-align: center; padding: 2.5rem 0; color: #666;">
                검색 조건에 해당하는 승인 이력이 없습니다.
            </td>
        `;

        tableBody.appendChild(messageRow);
    }

    function removeNoResultsMessage() {
        const existingMessage = document.querySelector(`.${NO_RESULTS_CLASS}`);
        if (existingMessage) {
            existingMessage.remove();
        }
    }

    document.addEventListener('DOMContentLoaded', init);
})();



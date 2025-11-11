(function () {
    function init() {
        document.querySelectorAll('.jokbo-filter-select').forEach((select) => {
            select.addEventListener('change', applyFilters);
        });
    }

    function applyFilters() {
        const actionFilter = getSelectValue('actionFilter');
        const previousStatusFilter = getSelectValue('previousStatusFilter');
        const newStatusFilter = getSelectValue('newStatusFilter');

        const params = new URLSearchParams({ page: '0' });

        if (actionFilter) params.append('action', actionFilter);
        if (previousStatusFilter) params.append('previousStatus', previousStatusFilter);
        if (newStatusFilter) params.append('newStatus', newStatusFilter);

        const jokboId = extractJokboId();
        if (!jokboId) {
            console.warn('URL에서 족보 ID를 찾을 수 없습니다.');
            return;
        }

        window.location.href = `/admin/jokbo/${jokboId}/history?${params.toString()}`;
    }

    function getSelectValue(id) {
        const element = document.getElementById(id);
        return element ? element.value : '';
    }

    function extractJokboId() {
        const segments = window.location.pathname.split('/').filter(Boolean);
        const index = segments.indexOf('jokbo');
        if (index === -1 || index + 1 >= segments.length) {
            return null;
        }

        return segments[index + 1];
    }

    document.addEventListener('DOMContentLoaded', init);
})();



// 관리자 페이지 공통 SSE 초기화 스크립트
(function () {
    const ADMIN_PATH_PREFIX = '/admin';
    const SYNC_BUTTON_ID = 'syncBtn';
    const onSyncButtonClick = (event) => {
        if (typeof handleSyncButton === 'function') {
            handleSyncButton(event.currentTarget);
        }
    };

    function isAdminPage() {
        return window.location.pathname.startsWith(ADMIN_PATH_PREFIX);
    }

    function initAdminSSE() {
        if (typeof initSSE !== 'function') {
            console.warn('initSSE 함수가 정의되지 않았습니다. sse.js가 로드되었는지 확인하세요.');
            return;
        }

        // 이미 초기화된 경우 재사용
        if (window.adminSseManager && window.adminSseManager.type === 'admin') {
            return;
        }

        initSSE('admin');
    }

    function bindSyncButton() {
        if (typeof handleSyncButton !== 'function') {
            return;
        }

        const syncButton = document.getElementById(SYNC_BUTTON_ID);
        if (syncButton) {
            syncButton.removeEventListener('click', onSyncButtonClick);
            syncButton.addEventListener('click', onSyncButtonClick);
        }
    }

    document.addEventListener('DOMContentLoaded', () => {
        if (!isAdminPage()) {
            return;
        }

        initAdminSSE();
        bindSyncButton();
    });
})();


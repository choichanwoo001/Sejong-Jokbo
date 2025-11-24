(function () {
    document.addEventListener('DOMContentLoaded', () => {
        if (typeof initSSE !== 'function') {
            debugWarn('initSSE 함수가 정의되지 않았습니다. sse.js 로드를 확인하세요.');
            return;
        }

        initSSE('user');
    });
})();



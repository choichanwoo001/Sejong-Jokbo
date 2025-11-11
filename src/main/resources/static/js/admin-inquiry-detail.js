(function () {
    const FORM_ID = 'replyForm';
    const TEXTAREA_ID = 'content';
    const CONTAINER_SELECTOR = '.inquiry-detail';

    function init() {
        const form = document.getElementById(FORM_ID);
        if (!form) {
            return;
        }

        const inquiryId = extractInquiryId();
        if (!inquiryId) {
            console.warn('문의 ID를 찾을 수 없습니다. 데이터 속성을 확인해주세요.');
            return;
        }

        form.addEventListener('submit', (event) => handleSubmit(event, inquiryId));
    }

    function extractInquiryId() {
        const container = document.querySelector(CONTAINER_SELECTOR);
        return container ? container.getAttribute('data-inquiry-id') : null;
    }

    function handleSubmit(event, inquiryId) {
        event.preventDefault();

        const contentElement = document.getElementById(TEXTAREA_ID);
        if (!contentElement) {
            return;
        }

        const content = contentElement.value.trim();
        if (!content) {
            alert('답변 내용을 입력해주세요.');
            return;
        }

        const submitButton = event.submitter || event.target.querySelector('button[type="submit"]');
        if (submitButton) {
            submitButton.disabled = true;
        }

        fetch(`/admin/inquiry/${inquiryId}/comment`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `content=${encodeURIComponent(content)}`
        })
            .then((response) => response.text())
            .then((result) => {
                if (result === 'success') {
                    alert('답변이 등록되었습니다.');
                    window.location.reload();
                } else {
                    alert(`답변 등록 중 오류가 발생했습니다: ${result}`);
                }
            })
            .catch(() => {
                alert('답변 등록 중 오류가 발생했습니다.');
            })
            .finally(() => {
                if (submitButton) {
                    submitButton.disabled = false;
                }
            });
    }

    document.addEventListener('DOMContentLoaded', init);
})();



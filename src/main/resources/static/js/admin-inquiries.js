(function () {
    const SEARCH_FORM_ID = 'searchForm';
    const INQUIRY_LIST_ID = 'inquiryList';
    const INQUIRY_ITEMS_SELECTOR = '.inquiry-item';
    const NO_RESULTS_CLASS = 'no-results';

    function init() {
        const searchForm = document.getElementById(SEARCH_FORM_ID);
        if (!searchForm) {
            return;
        }

        searchForm.addEventListener('submit', (event) => {
            event.preventDefault();
            performSearch();
        });

        const resetButton = document.getElementById('resetInquirySearchBtn');
        if (resetButton) {
            resetButton.addEventListener('click', resetSearch);
        }

        // 삭제 버튼 이벤트 리스너 추가
        document.addEventListener('click', function (e) {
            if (e.target && e.target.classList.contains('delete-inquiry-btn')) {
                const inquiryId = e.target.getAttribute('data-inquiry-id');
                if (inquiryId) {
                    confirmDelete(inquiryId);
                }
            }
        });
    }

    function confirmDelete(inquiryId) {
        if (confirm('정말로 이 문의를 삭제하시겠습니까? 삭제된 문의는 복구할 수 없습니다.')) {
            deleteInquiry(inquiryId);
        }
    }

    function deleteInquiry(inquiryId) {
        fetch(`/admin/inquiry/${inquiryId}`, {
            method: 'DELETE'
        })
            .then(response => response.text())
            .then(result => {
                if (result === 'success') {
                    alert('문의가 삭제되었습니다.');
                    location.reload();
                } else {
                    alert('문의 삭제에 실패했습니다: ' + result);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('문의 삭제 중 오류가 발생했습니다.');
            });
    }

    function performSearch() {
        const inquiryItems = document.querySelectorAll(INQUIRY_ITEMS_SELECTOR);
        if (!inquiryItems.length) {
            return;
        }

        const studentName = getInputValue('studentName').toLowerCase();
        const creationDate = getInputValue('creationDate');

        let hasVisibleItems = false;

        inquiryItems.forEach((item) => {
            const name = (item.getAttribute('data-student-name') || '').toLowerCase();
            const date = item.getAttribute('data-creation-date');

            const nameMatch = !studentName || name.includes(studentName);
            const dateMatch = !creationDate || date === creationDate;

            if (nameMatch && dateMatch) {
                item.style.display = 'block';
                hasVisibleItems = true;
            } else {
                item.style.display = 'none';
            }
        });

        toggleNoResultsMessage(hasVisibleItems);
    }

    function resetSearch() {
        const searchForm = document.getElementById(SEARCH_FORM_ID);
        if (searchForm) {
            searchForm.reset();
        }

        document.querySelectorAll(INQUIRY_ITEMS_SELECTOR).forEach((item) => {
            item.style.display = 'block';
        });

        removeNoResultsMessage();
    }

    function getInputValue(id) {
        const element = document.getElementById(id);
        return element ? element.value : '';
    }

    function toggleNoResultsMessage(hasVisibleItems) {
        const existingMessage = document.querySelector(`.${NO_RESULTS_CLASS}`);

        if (hasVisibleItems) {
            if (existingMessage) {
                existingMessage.remove();
            }
            return;
        }

        if (existingMessage) {
            return;
        }

        const container = document.getElementById(INQUIRY_LIST_ID);
        if (!container) {
            return;
        }

        const message = document.createElement('div');
        message.className = NO_RESULTS_CLASS;
        message.textContent = '검색 결과가 없습니다.';

        container.appendChild(message);
    }

    function removeNoResultsMessage() {
        const existingMessage = document.querySelector(`.${NO_RESULTS_CLASS}`);
        if (existingMessage) {
            existingMessage.remove();
        }
    }

    document.addEventListener('DOMContentLoaded', init);
})();



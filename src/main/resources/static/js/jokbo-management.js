// 상태별 필터링
function filterByStatus() {
    const status = document.getElementById('statusFilter').value;
    const currentUrl = new URL(window.location);

    if (status) {
        currentUrl.searchParams.set('status', status);
    } else {
        currentUrl.searchParams.delete('status');
    }
    currentUrl.searchParams.delete('page'); // 페이지는 초기화

    window.location.href = currentUrl.toString();
}

// 승인/반려/취소 모달 표시
function showApprovalModal(jokboId, actionType) {
    const modal = document.getElementById('approvalModal');
    const modalTitle = document.getElementById('modalTitle');
    const confirmButton = document.getElementById('confirmButton');
    const jokboIdInput = document.getElementById('jokboId');
    const actionTypeInput = document.getElementById('actionType');
    const commentInput = document.getElementById('comment');

    jokboIdInput.value = jokboId;
    actionTypeInput.value = actionType;
    commentInput.value = '';

    if (actionType === 'approve') {
        modalTitle.textContent = '족보 승인';
        confirmButton.textContent = '승인';
        confirmButton.className = 'btn-approve';
    } else if (actionType === 'reject') {
        modalTitle.textContent = '족보 반려';
        confirmButton.textContent = '반려';
        confirmButton.className = 'btn-reject';
    } else if (actionType === 'cancel') {
        modalTitle.textContent = '승인 취소';
        confirmButton.textContent = '승인취소';
        confirmButton.className = 'btn-cancel';
    }

    modal.style.display = 'block';
}

// 승인/반려 모달 닫기
function closeApprovalModal() {
    const modal = document.getElementById('approvalModal');
    modal.style.display = 'none';
}

// 상세 보기 모달 열기
function openDetailModal(button) {
    const tr = button.closest('tr');
    const modal = document.getElementById('detailModal');

    // 데이터 가져오기
    const bookTitle = tr.dataset.bookTitle;
    const registrant = tr.dataset.registrant;
    const date = tr.dataset.registrationDate;
    const type = tr.dataset.contentType;
    const status = tr.dataset.status;
    const comment = tr.dataset.comment;
    const content = tr.dataset.content;
    const contentUrl = tr.dataset.contentUrl;

    // 모달 내용 채우기
    document.getElementById('detailBookTitle').textContent = bookTitle;
    document.getElementById('detailRegistrant').textContent = registrant;
    document.getElementById('detailDate').textContent = date;

    // 타입 표시
    const typeSpan = document.getElementById('detailType');
    if (type === 'text') {
        typeSpan.textContent = '텍스트';
        typeSpan.className = 'badge badge-text';
    } else {
        typeSpan.textContent = '파일';
        typeSpan.className = 'badge badge-file';
    }

    // 상태 표시
    const statusSpan = document.getElementById('detailStatus');
    statusSpan.textContent = status;
    statusSpan.className = 'status-badge';
    if (status === '대기') statusSpan.classList.add('status-pending');
    else if (status === '승인') statusSpan.classList.add('status-approved');
    else if (status === '반려') statusSpan.classList.add('status-rejected');

    // 코멘트 표시
    document.getElementById('detailComment').textContent = comment || '없음';

    // 족보 내용 표시
    const contentBox = document.getElementById('detailContent');
    contentBox.innerHTML = ''; // 초기화

    if (type === 'text') {
        contentBox.textContent = content;
    } else if (type === 'file') {
        if (contentUrl) {
            const link = document.createElement('a');
            link.href = contentUrl;
            link.textContent = '파일 다운로드';
            link.className = 'btn-download';
            link.target = '_blank';
            contentBox.appendChild(link);
        } else {
            contentBox.textContent = '파일 URL이 없습니다.';
        }
    }

    modal.style.display = 'block';
}

// 상세 보기 모달 닫기
function closeDetailModal() {
    const modal = document.getElementById('detailModal');
    modal.style.display = 'none';
}

// 족보 승인 (새로운 방식)
function approveJokbo(jokboId, comment = '') {
    const formData = new FormData();
    if (comment) formData.append('comment', comment);

    fetch(`/admin/jokbo/${jokboId}/approve`, {
        method: 'POST',
        body: formData
    })
        .then(response => response.text())
        .then(result => {
            if (result === 'success') {
                alert('족보가 승인되었습니다.');
                location.reload();
            } else {
                alert('승인에 실패했습니다: ' + result);
            }
        })
        .catch(error => {
            alert('오류가 발생했습니다: ' + error);
        });
}

// 족보 반려 (새로운 방식)
function rejectJokbo(jokboId, comment = '') {
    const formData = new FormData();
    if (comment) formData.append('comment', comment);

    fetch(`/admin/jokbo/${jokboId}/reject`, {
        method: 'POST',
        body: formData
    })
        .then(response => response.text())
        .then(result => {
            if (result === 'success') {
                alert('족보가 반려되었습니다.');
                location.reload();
            } else {
                alert('반려에 실패했습니다: ' + result);
            }
        })
        .catch(error => {
            alert('오류가 발생했습니다: ' + error);
        });
}

// 승인 취소
function cancelApproval(jokboId, comment = '') {
    const formData = new FormData();
    if (comment) formData.append('comment', comment);

    fetch(`/admin/jokbo/${jokboId}/cancel-approval`, {
        method: 'POST',
        body: formData
    })
        .then(response => response.text())
        .then(result => {
            if (result === 'success') {
                alert('족보 승인이 취소되었습니다.');
                location.reload();
            } else {
                alert('승인 취소에 실패했습니다: ' + result);
            }
        })
        .catch(error => {
            alert('오류가 발생했습니다: ' + error);
        });
}

// 족보 삭제
function deleteJokbo(jokboId) {
    if (!confirm('정말로 이 족보를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
        return;
    }

    fetch(`/admin/jokbo/${jokboId}`, {
        method: 'DELETE'
    })
        .then(response => response.text())
        .then(result => {
            if (result === 'success') {
                alert('족보가 삭제되었습니다.');
                location.reload();
            } else {
                alert('삭제에 실패했습니다: ' + result);
            }
        })
        .catch(error => {
            alert('오류가 발생했습니다: ' + error);
        });
}

// 페이지 로드 시 모달 이벤트 리스너 초기화
document.addEventListener('DOMContentLoaded', function () {
    const statusFilter = document.getElementById('statusFilter');
    if (statusFilter) {
        statusFilter.addEventListener('change', filterByStatus);
    }

    document.querySelectorAll('.approval-trigger').forEach((button) => {
        button.addEventListener('click', () => {
            const jokboId = button.getAttribute('data-jokbo-id');
            const action = button.getAttribute('data-approval-action');

            if (!jokboId || !action) {
                return;
            }

            showApprovalModal(jokboId, action);
        });
    });

    document.querySelectorAll('[data-close-approval-modal]').forEach((element) => {
        element.addEventListener('click', closeApprovalModal);
    });

    // 삭제 버튼 이벤트 리스너
    document.querySelectorAll('.delete-trigger').forEach((button) => {
        button.addEventListener('click', () => {
            const jokboId = button.getAttribute('data-jokbo-id');
            if (jokboId) {
                deleteJokbo(jokboId);
            }
        });
    });

    const detailModal = document.getElementById('detailModal');
    const approvalModal = document.getElementById('approvalModal');
    const approvalForm = document.getElementById('approvalForm');

    // 상세 모달 이벤트
    if (detailModal) {
        detailModal.addEventListener('click', function (e) {
            if (e.target === this) {
                closeDetailModal();
            }
        });
    }

    // 승인 모달 이벤트
    if (approvalModal) {
        approvalModal.addEventListener('click', function (e) {
            if (e.target === this) {
                closeApprovalModal();
            }
        });
    }

    // 승인 폼 제출 처리
    if (approvalForm) {
        approvalForm.addEventListener('submit', function (e) {
            e.preventDefault();

            const jokboId = document.getElementById('jokboId').value;
            const actionType = document.getElementById('actionType').value;
            const comment = document.getElementById('comment').value;

            closeApprovalModal();

            if (actionType === 'approve') {
                approveJokbo(jokboId, comment);
            } else if (actionType === 'reject') {
                rejectJokbo(jokboId, comment);
            } else if (actionType === 'cancel') {
                cancelApproval(jokboId, comment);
            }
        });
    }

    // ESC 키로 모달 닫기
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            if (detailModal && detailModal.style.display === 'block') {
                closeDetailModal();
            }
            if (approvalModal && approvalModal.style.display === 'block') {
                closeApprovalModal();
            }
        }
    });

    if (window.AdminSearch) {
        window.AdminSearch.init({
            formId: 'searchForm',
            itemSelector: '#jokboTable tbody tr',
            fieldConfigs: [
                { name: 'bookTitle', datasetKey: 'bookTitle', matchType: 'includes' },
                { name: 'registrant', datasetKey: 'registrant', matchType: 'includes' },
                { name: 'registrationDate', datasetKey: 'registrationDate', matchType: 'equals' }
            ],
            visibleDisplayStyle: 'table-row',
            noResult: {
                targetSelector: '#jokboTableContainer',
                text: '검색 결과가 없습니다.'
            }
        });
    }
});
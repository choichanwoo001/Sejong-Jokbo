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
        confirmButton.textContent = '취소';
        confirmButton.className = 'btn-cancel';
    }
    
    modal.style.display = 'block';
}

// 승인/반려 모달 닫기
function closeApprovalModal() {
    const modal = document.getElementById('approvalModal');
    modal.style.display = 'none';
}

// 족보 이력 보기
function viewJokboHistory(jokboId) {
    window.open(`/admin/jokbo/${jokboId}/history`, '_blank');
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

// 텍스트 족보 내용 보기
function viewTextJokbo(content) {
    const modal = document.getElementById('textModal');
    const textContent = document.getElementById('textContent');
    
    if (modal && textContent) {
        textContent.textContent = content;
        modal.style.display = 'block';
    }
}

// 텍스트 모달 닫기
function closeTextModal() {
    const modal = document.getElementById('textModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// 페이지 로드 시 모달 이벤트 리스너 초기화
document.addEventListener('DOMContentLoaded', function() {
    const textModal = document.getElementById('textModal');
    const approvalModal = document.getElementById('approvalModal');
    const approvalForm = document.getElementById('approvalForm');
    
    // 텍스트 모달 이벤트
    if (textModal) {
        textModal.addEventListener('click', function(e) {
            if (e.target === this) {
                closeTextModal();
            }
        });
    }
    
    // 승인 모달 이벤트
    if (approvalModal) {
        approvalModal.addEventListener('click', function(e) {
            if (e.target === this) {
                closeApprovalModal();
            }
        });
    }
    
    // 승인 폼 제출 처리
    if (approvalForm) {
        approvalForm.addEventListener('submit', function(e) {
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
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            if (textModal && textModal.style.display === 'block') {
                closeTextModal();
            }
            if (approvalModal && approvalModal.style.display === 'block') {
                closeApprovalModal();
            }
        }
    });
}); 
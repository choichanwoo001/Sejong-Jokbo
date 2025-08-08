// 족보 승인
function approveJokbo(jokboId) {
    if (confirm('이 족보를 승인하시겠습니까?')) {
        fetch(`/admin/jokbo/${jokboId}/approve`, {
            method: 'POST'
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
}

// 족보 반려
function rejectJokbo(jokboId) {
    if (confirm('이 족보를 반려하시겠습니까?')) {
        fetch(`/admin/jokbo/${jokboId}/reject`, {
            method: 'POST'
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
    const modal = document.getElementById('textModal');
    
    if (modal) {
        // 모달 외부 클릭 시 닫기
        modal.addEventListener('click', function(e) {
            if (e.target === this) {
                closeTextModal();
            }
        });
        
        // ESC 키로 모달 닫기
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape' && modal.style.display === 'block') {
                closeTextModal();
            }
        });
    }
}); 
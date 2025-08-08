// 문의 작성 폼 처리
document.addEventListener('DOMContentLoaded', function() {
    const inquiryForm = document.getElementById('inquiryForm');
    
    if (inquiryForm) {
        inquiryForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // 폼 데이터 수집
            const formData = new FormData(inquiryForm);
            
            // 공개 설정 체크박스 처리
            const isPublicCheckbox = document.getElementById('isPublic');
            formData.set('isPublic', isPublicCheckbox.checked);
            
            // 서버로 전송
            fetch('/inquiry', {
                method: 'POST',
                body: formData
            })
            .then(response => response.text())
            .then(result => {
                if (result === 'success') {
                    alert('문의가 성공적으로 등록되었습니다.');
                    window.location.href = '/inquiry';
                } else {
                    alert(result);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('문의 등록 중 오류가 발생했습니다.');
            });
        });
    }
    
    // 입력 필드 유효성 검사
    const nameInput = document.getElementById('name');
    const messageInput = document.getElementById('message');
    
    if (nameInput) {
        nameInput.addEventListener('input', function() {
            if (this.value.trim().length > 0) {
                this.style.borderColor = '#d1d5db';
            } else {
                this.style.borderColor = '#ef4444';
            }
        });
    }
    
    if (messageInput) {
        messageInput.addEventListener('input', function() {
            if (this.value.trim().length > 0) {
                this.style.borderColor = '#d1d5db';
            } else {
                this.style.borderColor = '#ef4444';
            }
        });
    }
});

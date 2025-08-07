// 전역 변수
let verificationCode = '';
let isEmailVerified = false;

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeFileUpload();
    initializeFormHandlers();
    updateSubmitButtonState();
});

// 족보 탭 전환 함수
function showJokboTab(tabName) {
    // 모든 탭과 콘텐츠 비활성화
    document.querySelectorAll('.jokbo-tab').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.jokbo-content').forEach(content => content.classList.remove('active'));
    
    // 선택된 탭과 콘텐츠 활성화
    event.target.classList.add('active');
    document.getElementById(tabName).classList.add('active');
}

// 등록 방식 탭 전환 함수
function showRegisterTab(tabName) {
    // 모든 등록 탭과 콘텐츠 비활성화
    document.querySelectorAll('.register-tab').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.register-content').forEach(content => content.classList.remove('active'));
    
    // 선택된 탭과 콘텐츠 활성화
    event.target.classList.add('active');
    document.getElementById(tabName + '-register').classList.add('active');
}

// 인증번호 발송
function sendVerificationCode(button) {
    const emailInput = button.parentElement.querySelector('input[type="email"]');
    const email = emailInput.value;
    
    if (!email) {
        alert('이메일을 입력해주세요.');
        return;
    }
    
    // 버튼 상태 변경
    button.disabled = true;
    button.textContent = '발송 중...';
    
    // 서버에 인증번호 발송 요청
    fetch('/api/send-verification', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email: email })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('인증번호가 발송되었습니다.');
            button.textContent = '재발송';
            
            // 1분 후 재발송 가능하도록 설정
            setTimeout(() => {
                button.disabled = false;
                button.textContent = '인증번호 발송';
            }, 60000);
        } else {
            alert('인증번호 발송에 실패했습니다: ' + data.message);
            button.disabled = false;
            button.textContent = '인증번호 발송';
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('인증번호 발송 중 오류가 발생했습니다.');
        button.disabled = false;
        button.textContent = '인증번호 발송';
    });
}

// 인증번호 확인
function verifyCode(button) {
    const emailInput = button.closest('.form-group').parentElement.querySelector('input[type="email"]');
    const codeInput = button.parentElement.querySelector('input[name="verificationCode"]');
    const email = emailInput.value;
    const inputCode = codeInput.value;
    
    if (!email || !inputCode) {
        alert('이메일과 인증번호를 모두 입력해주세요.');
        return;
    }
    
    // 버튼 상태 변경
    button.disabled = true;
    button.textContent = '확인 중...';
    
    // 서버에 인증번호 확인 요청
    fetch('/api/verify-code', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ 
            email: email,
            code: inputCode 
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            isEmailVerified = true;
            button.textContent = '인증완료';
            button.disabled = true;
            button.style.backgroundColor = '#28a745';
            
            // 모든 인증 상태 표시 업데이트
            updateVerificationStatus('이메일 인증이 완료되었습니다.', 'success');
            updateSubmitButtonState();
        } else {
            isEmailVerified = false;
            button.disabled = false;
            button.textContent = '인증확인';
            updateVerificationStatus('인증번호가 일치하지 않습니다.', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('인증번호 확인 중 오류가 발생했습니다.');
        button.disabled = false;
        button.textContent = '인증확인';
    });
}

// 인증 상태 표시 업데이트
function updateVerificationStatus(message, type) {
    const statusDivs = document.querySelectorAll('#verificationStatus');
    statusDivs.forEach(statusDiv => {
        statusDiv.className = `verification-status verification-${type}`;
        statusDiv.textContent = message;
        statusDiv.style.display = 'block';
    });
}

// 제출 버튼 상태 업데이트
function updateSubmitButtonState() {
    const submitButtons = document.querySelectorAll('.form-button');
    submitButtons.forEach(button => {
        if (!isEmailVerified) {
            button.disabled = true;
            button.textContent = '이메일 인증 후 등록 가능';
            button.style.backgroundColor = '#6c757d';
        } else {
            button.disabled = false;
            if (button.textContent.includes('텍스트')) {
                button.textContent = '텍스트 족보 등록';
            } else if (button.textContent.includes('파일')) {
                button.textContent = '파일 족보 등록';
            }
            button.style.backgroundColor = '#007bff';
        }
    });
}

// 파일 업로드 초기화
function initializeFileUpload() {
    const fileUpload = document.getElementById('fileUpload');
    const fileInput = document.getElementById('fileInput');
    
    if (fileUpload && fileInput) {
        fileUpload.addEventListener('click', () => fileInput.click());
        
        fileUpload.addEventListener('dragover', (e) => {
            e.preventDefault();
            fileUpload.classList.add('dragover');
        });
        
        fileUpload.addEventListener('dragleave', () => {
            fileUpload.classList.remove('dragover');
        });
        
        fileUpload.addEventListener('drop', (e) => {
            e.preventDefault();
            fileUpload.classList.remove('dragover');
            fileInput.files = e.dataTransfer.files;
            updateFileDisplay();
        });
        
        fileInput.addEventListener('change', updateFileDisplay);
    }
}

// 파일 표시 업데이트
function updateFileDisplay() {
    const fileInput = document.getElementById('fileInput');
    const fileUpload = document.getElementById('fileUpload');
    const fileInfo = document.getElementById('fileInfo');
    const fileName = document.getElementById('fileName');
    
    if (fileInput.files.length > 0) {
        const file = fileInput.files[0];
        
        // 파일 크기 검증 (10MB 제한)
        if (file.size > 10 * 1024 * 1024) {
            alert('파일 크기가 10MB를 초과합니다.');
            removeFile();
            return;
        }
        
        // 파일 확장자 검증
        const allowedExtensions = ['pdf', 'jpg', 'jpeg', 'png', 'gif', 'txt'];
        const fileExtension = file.name.split('.').pop().toLowerCase();
        if (!allowedExtensions.includes(fileExtension)) {
            alert('허용되지 않는 파일 형식입니다. (허용: pdf, jpg, jpeg, png, gif, txt)');
            removeFile();
            return;
        }
        
        // 파일 정보 표시
        fileName.textContent = `${file.name} (${formatFileSize(file.size)})`;
        fileInfo.style.display = 'block';
        fileUpload.style.display = 'none';
    } else {
        fileInfo.style.display = 'none';
        fileUpload.style.display = 'block';
    }
}

// 파일 크기 포맷팅
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// 파일 삭제
function removeFile() {
    const fileInput = document.getElementById('fileInput');
    const fileUpload = document.getElementById('fileUpload');
    const fileInfo = document.getElementById('fileInfo');
    
    fileInput.value = '';
    fileInfo.style.display = 'none';
    fileUpload.style.display = 'block';
    fileUpload.innerHTML = '<p>파일을 드래그하여 놓거나 클릭하여 선택하세요</p>';
}

// 폼 핸들러 초기화
function initializeFormHandlers() {
    const textForm = document.getElementById('textJokboForm');
    const fileForm = document.getElementById('fileJokboForm');
    
    if (textForm) {
        textForm.addEventListener('submit', handleTextJokboSubmit);
    }
    
    if (fileForm) {
        fileForm.addEventListener('submit', handleFileJokboSubmit);
    }
}

// 텍스트 족보 등록 처리
function handleTextJokboSubmit(e) {
    e.preventDefault();
    
    if (!isEmailVerified) {
        alert('이메일 인증을 완료해주세요.');
        return;
    }
    
    const formData = new FormData(this);
    const bookId = this.closest('.book-detail').dataset.bookId;
    
    fetch(`/book/${bookId}/jokbo/text`, {
        method: 'POST',
        body: formData
    })
    .then(response => response.text())
    .then(result => {
        if (result === 'success') {
            alert('족보가 성공적으로 등록되었습니다.');
            this.reset();
            isEmailVerified = false;
            document.querySelectorAll('#verificationStatus').forEach(status => {
                status.style.display = 'none';
            });
            updateSubmitButtonState();
            
            // 인증 버튼들 초기화
            document.querySelectorAll('.verify-button').forEach(button => {
                if (button.textContent.includes('인증확인')) {
                    button.disabled = false;
                    button.textContent = '인증확인';
                    button.style.backgroundColor = '';
                }
            });
        } else {
            alert('족보 등록에 실패했습니다: ' + result);
        }
    })
    .catch(error => {
        alert('오류가 발생했습니다: ' + error);
    });
}

// 파일 족보 등록 처리
function handleFileJokboSubmit(e) {
    e.preventDefault();
    
    if (!isEmailVerified) {
        alert('이메일 인증을 완료해주세요.');
        return;
    }
    
    const fileInput = document.getElementById('fileInput');
    if (!fileInput.files || fileInput.files.length === 0) {
        alert('업로드할 파일을 선택해주세요.');
        return;
    }
    
    const formData = new FormData(this);
    const bookId = this.closest('.book-detail').dataset.bookId;
    
    // 제출 버튼 비활성화
    const submitButton = this.querySelector('.form-button');
    const originalText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = '업로드 중...';
    
    fetch(`/book/${bookId}/jokbo/file`, {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.text();
    })
    .then(result => {
        if (result === 'success') {
            alert('족보가 성공적으로 등록되었습니다.');
            this.reset();
            removeFile();
            isEmailVerified = false;
            document.querySelectorAll('#verificationStatus').forEach(status => {
                status.style.display = 'none';
            });
            updateSubmitButtonState();
            
            // 인증 버튼들 초기화
            document.querySelectorAll('.verify-button').forEach(button => {
                if (button.textContent.includes('인증확인')) {
                    button.disabled = false;
                    button.textContent = '인증확인';
                    button.style.backgroundColor = '';
                }
            });
        } else {
            alert('족보 등록에 실패했습니다: ' + result);
        }
    })
    .catch(error => {
        console.error('Upload error:', error);
        alert('파일 업로드 중 오류가 발생했습니다. 다시 시도해주세요.');
    })
    .finally(() => {
        // 제출 버튼 복원
        submitButton.disabled = false;
        submitButton.textContent = originalText;
    });
} 
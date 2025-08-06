// 전역 변수
let verificationCode = '';
let isEmailVerified = false;

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeFileUpload();
    initializeFormHandlers();
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
function sendVerificationCode() {
    const email = event.target.parentElement.querySelector('input[type="email"]').value;
    
    if (!email) {
        alert('이메일을 입력해주세요.');
        return;
    }
    
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
            event.target.disabled = true;
            event.target.textContent = '재발송';
            setTimeout(() => {
                event.target.disabled = false;
                event.target.textContent = '인증번호 발송';
            }, 60000); // 1분 후 재발송 가능
        } else {
            alert('인증번호 발송에 실패했습니다: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('인증번호 발송 중 오류가 발생했습니다.');
    });
}

// 인증번호 확인
function verifyCode() {
    const email = event.target.parentElement.parentElement.querySelector('input[type="email"]').value;
    const inputCode = event.target.parentElement.querySelector('input[name="verificationCode"]').value;
    const statusDiv = document.getElementById('verificationStatus');
    
    if (!email || !inputCode) {
        alert('이메일과 인증번호를 모두 입력해주세요.');
        return;
    }
    
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
            statusDiv.className = 'verification-status verification-success';
            statusDiv.textContent = '이메일 인증이 완료되었습니다.';
            statusDiv.style.display = 'block';
            event.target.disabled = true;
            event.target.textContent = '인증완료';
        } else {
            isEmailVerified = false;
            statusDiv.className = 'verification-status verification-error';
            statusDiv.textContent = '인증번호가 일치하지 않습니다.';
            statusDiv.style.display = 'block';
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('인증번호 확인 중 오류가 발생했습니다.');
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
    
    if (fileInput.files.length > 0) {
        const file = fileInput.files[0];
        fileUpload.innerHTML = `<p>선택된 파일: ${file.name}</p>`;
    } else {
        fileUpload.innerHTML = '<p>파일을 드래그하여 놓거나 클릭하여 선택하세요</p>';
    }
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
            document.getElementById('verificationStatus').style.display = 'none';
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
    
    const formData = new FormData(this);
    const bookId = this.closest('.book-detail').dataset.bookId;
    
    fetch(`/book/${bookId}/jokbo/file`, {
        method: 'POST',
        body: formData
    })
    .then(response => response.text())
    .then(result => {
        if (result === 'success') {
            alert('족보가 성공적으로 등록되었습니다.');
            this.reset();
            document.getElementById('fileUpload').innerHTML = '<p>파일을 드래그하여 놓거나 클릭하여 선택하세요</p>';
            isEmailVerified = false;
            document.getElementById('verificationStatus').style.display = 'none';
        } else {
            alert('족보 등록에 실패했습니다: ' + result);
        }
    })
    .catch(error => {
        alert('오류가 발생했습니다: ' + error);
    });
} 
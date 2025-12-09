// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function () {
    initializeFileUpload();
    initializeFormHandlers();
    updateSubmitButtonState();
    checkTabParameter();
    initializeTabHandlers();
    initializeFileRemovalButtons();
});

// Quill 에디터 전역 변수
let quill;

// Quill 에디터 초기화
function initializeQuill() {
    if (document.getElementById('editor')) {
        // 폰트 크기를 style 속성(px)으로 사용하도록 설정
        var Size = Quill.import('attributors/style/size');
        Size.whitelist = ['12px', '14px', '16px', '18px', '20px', '24px', '30px', '32px'];
        Quill.register(Size, true);

        quill = new Quill('#editor', {
            theme: 'snow',
            placeholder: '요약 내용을 입력하세요...',
            modules: {
                toolbar: [
                    [{ 'size': Size.whitelist }], // 폰트 크기 (숫자)
                    ['bold', 'italic', 'underline', 'strike'],
                    [{ 'color': [] }, { 'background': [] }],
                    [{ 'list': 'ordered' }, { 'list': 'bullet' }]
                ]
            }
        });
    }
}

// URL 파라미터 확인하여 탭 설정
function checkTabParameter() {
    const tab = resolveActiveTab();
    if (tab === 'list') {
        showJokboTabByName('list');
    }
    // 기본값은 등록 탭이므로 별도 처리 불필요
}

function resolveActiveTab() {
    const container = document.querySelector('.book-detail');
    if (container && container.dataset.activeTab) {
        return container.dataset.activeTab;
    }

    if (typeof activeTab !== 'undefined') {
        return activeTab;
    }

    return null;
}

// 족보 탭 전환 함수 (탭 이름으로 직접 호출)
function showJokboTabByName(tabName) {
    showJokboTab(tabName);
}

// 족보 탭 전환 함수 (이벤트 핸들러용)
function showJokboTab(tabName, triggerElement = null) {
    if (!tabName) {
        return;
    }

    document.querySelectorAll('.jokbo-tab').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.jokbo-content').forEach(content => content.classList.remove('active'));

    if (triggerElement) {
        triggerElement.classList.add('active');
    } else {
        const tabButton = document.querySelector(`.jokbo-tab[data-tab-target="${tabName}"]`);
        if (tabButton) {
            tabButton.classList.add('active');
        }
    }

    const targetContent = document.getElementById(tabName);
    if (targetContent) {
        targetContent.classList.add('active');
    }
}

// 등록 방식 탭 전환 함수
function showRegisterTab(tabName, triggerElement = null) {
    if (!tabName) {
        return;
    }

    document.querySelectorAll('.register-tab').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.register-content').forEach(content => content.classList.remove('active'));

    if (triggerElement) {
        triggerElement.classList.add('active');
    } else {
        const tabButton = document.querySelector(`.register-tab[data-register-target="${tabName}"]`);
        if (tabButton) {
            tabButton.classList.add('active');
        }
    }

    const content = document.getElementById(`${tabName}-register`);
    if (content) {
        content.classList.add('active');
    }
}

// 제출 버튼 상태 업데이트
function updateSubmitButtonState() {
    const submitButtons = document.querySelectorAll('.form-button');
    submitButtons.forEach(button => {
        button.disabled = false;
        if (button.textContent.includes('텍스트')) {
            button.textContent = '텍스트 요약 등록';
        } else if (button.textContent.includes('파일')) {
            button.textContent = '파일 요약 등록';
        }
        button.style.backgroundColor = '#007bff';
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

function initializeTabHandlers() {
    document.querySelectorAll('.jokbo-tab[data-tab-target]').forEach((tabButton) => {
        tabButton.addEventListener('click', () => {
            const tabName = tabButton.getAttribute('data-tab-target');
            showJokboTab(tabName, tabButton);
        });
    });

    document.querySelectorAll('.register-tab[data-register-target]').forEach((tabButton) => {
        tabButton.addEventListener('click', () => {
            const tabName = tabButton.getAttribute('data-register-target');
            showRegisterTab(tabName, tabButton);
        });
    });
}

function initializeFileRemovalButtons() {
    document.querySelectorAll('[data-action="remove-file"]').forEach((button) => {
        button.addEventListener('click', () => removeFile());
    });
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

    // Quill 초기화 호출
    initializeQuill();

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

    // Quill 에디터 내용 확인
    if (quill.getLength() <= 1) { // 빈 에디터는 길이가 1임 (\n)
        alert('요약 내용을 입력해주세요.');
        return;
    }

    // Quill 내용을 hidden input에 설정
    const contentHidden = document.getElementById('contentHidden');
    contentHidden.value = quill.root.innerHTML;

    const formData = new FormData(this);
    const bookId = this.closest('.book-detail').dataset.bookId;

    fetch(`/book/${bookId}/jokbo/text`, {
        method: 'POST',
        body: formData
    })
        .then(response => response.text())
        .then(result => {
            if (result === 'success') {
                alert('요약 등록 요청이 완료되었습니다.\n관리자가 승인하면 요약 목록에 등록됩니다.');
                this.reset();
                quill.setContents([]); // 에디터 초기화
                updateSubmitButtonState();
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
                alert('요약 등록 요청이 완료되었습니다.\n관리자가 승인하면 요약 목록에 등록됩니다.');
                this.reset();
                removeFile();
                updateSubmitButtonState();
            } else {
                alert('족보 등록에 실패했습니다: ' + result);
            }
        })
        .catch(error => {
            // debugError 함수가 정의되어 있지 않을 수 있으므로 console.error 사용
            console.error('Upload error:', error);
            alert('파일 업로드 중 오류가 발생했습니다. 다시 시도해주세요.');
        })
        .finally(() => {
            // 제출 버튼 복원
            submitButton.disabled = false;
            submitButton.textContent = originalText;
        });
}
// 검색 기능
const searchInput = document.querySelector('.search-input');
const searchForm = document.getElementById('searchForm');

// 검색 입력 이벤트
searchInput.addEventListener('focus', function () {
    this.style.transform = 'scale(1.005)';
});

searchInput.addEventListener('blur', function () {
    this.style.transform = 'scale(1)';
});

// 실시간 검색 (Enter 키 또는 검색 버튼 클릭 시)
searchInput.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        e.preventDefault();
        performSearch();
    }
});

// 검색 수행 함수
function performSearch() {
    const keyword = searchInput.value.trim();
    if (keyword) {
        searchForm.submit();
    }
}

// 카테고리 탭 클릭 이벤트
document.querySelectorAll('.category-tab').forEach(tab => {
    tab.addEventListener('click', function () {
        // 모든 탭에서 active 클래스 제거
        document.querySelectorAll('.category-tab').forEach(t => t.classList.remove('active'));
        // 클릭된 탭에 active 클래스 추가
        this.classList.add('active');

        // 모든 카테고리 숨기기
        document.querySelectorAll('.book-display').forEach(display => {
            display.style.display = 'none';
        });

        // 클릭된 탭에 해당하는 카테고리 표시
        const tabText = this.textContent.trim();
        const targetCategory = getCategoryId(tabText);

        if (targetCategory) {
            document.getElementById(targetCategory).style.display = 'block';

            // 현재 필터 적용
            const sortFilter = document.getElementById('sortFilter');
            const currentFilter = sortFilter ? sortFilter.value : 'name';
            debugLog('카테고리 변경, 필터 적용:', currentFilter);
            sortBooks(currentFilter);
        }
    });
});

// 페이지 로드 시 검색 결과가 있으면 검색 입력 필드에 포커스
document.addEventListener('DOMContentLoaded', function () {
    const searchResults = document.querySelector('.search-results');
    if (searchResults) {
        searchInput.focus();
    }

    document.querySelectorAll('.book-item[data-book-url]').forEach((item) => {
        item.addEventListener('click', () => {
            const url = item.getAttribute('data-book-url');
            if (url) {
                window.location.href = url;
            }
        });

        item.addEventListener('keypress', (event) => {
            if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                const url = item.getAttribute('data-book-url');
                if (url) {
                    window.location.href = url;
                }
            }
        });
    });
});

// 카테고리 탭 클릭 효과
document.querySelectorAll('.category-tab').forEach(button => {
    button.addEventListener('click', function (e) {
        this.style.transform = 'scale(0.98)';
        setTimeout(() => {
            this.style.transform = '';
        }, 150);
    });
});

// 필터 드롭다운 이벤트
document.addEventListener('DOMContentLoaded', function () {
    const sortFilter = document.getElementById('sortFilter');
    if (sortFilter) {
        sortFilter.addEventListener('change', function () {
            const selectedValue = this.value;
            // 디버그 함수 안전하게 사용
            const log = (typeof debugLog === 'function') ? debugLog : console.log;
            log('필터 변경:', selectedValue);
            sortBooks(selectedValue);
        });
    }
});

// 책 정렬 함수
function sortBooks(sortType) {
    // 디버그 함수 안전하게 사용
    const log = (typeof debugLog === 'function') ? debugLog : console.log;

    const currentCategory = document.querySelector('.category-tab.active');
    if (!currentCategory) return;

    const tabText = currentCategory.textContent.trim();

    // getCategoryId 함수 존재 여부 확인
    if (typeof getCategoryId !== 'function') {
        console.error('getCategoryId function is missing!');
        return;
    }

    const targetCategory = getCategoryId(tabText);

    if (targetCategory) {
        const bookGrid = document.querySelector(`#${targetCategory} .books-grid`);
        if (!bookGrid) return;

        const books = Array.from(bookGrid.children);

        if (books.length === 0) return;

        books.sort((a, b) => {
            try {
                const titleA = a.querySelector('.book-title').textContent.trim();
                const titleB = b.querySelector('.book-title').textContent.trim();

                if (sortType === 'name') {
                    return titleA.localeCompare(titleB, 'ko');
                } else if (sortType === 'jokbo') {
                    // 족보 많은 순 정렬
                    const countElA = a.querySelector('.jokbo-count');
                    const countElB = b.querySelector('.jokbo-count');

                    // 숫자 추출 실패 시 0으로 처리
                    const countA = countElA ? parseInt((countElA.textContent.match(/\d+/) || ['0'])[0]) : 0;
                    const countB = countElB ? parseInt((countElB.textContent.match(/\d+/) || ['0'])[0]) : 0;

                    if (countB !== countA) {
                        return countB - countA; // 내림차순 (많은 순)
                    }
                    // 족보 수가 같으면 이름순으로 정렬 (2차 정렬)
                    return titleA.localeCompare(titleB, 'ko');
                }
            } catch (e) {
                console.error('Sort error:', e);
                return 0;
            }
            return 0;
        });

        // 정렬된 결과를 다시 DOM에 추가
        books.forEach(book => bookGrid.appendChild(book));

        // 정렬 완료 알림
        log(`${sortType} 기준으로 정렬 완료`);
    }
}

// 부드러운 페이지 로드 애니메이션
window.addEventListener('load', function () {
    document.querySelector('.container').classList.add('fade-in');
});

// 파일 크기 검증 관련 함수들
const FILE_SIZE_LIMITS = {
    GENERAL_UPLOAD: 10 * 1024 * 1024, // 10MB
    EMAIL_ATTACHMENT: 1 * 1024 * 1024  // 1MB
};

/**
 * 파일 크기 검증
 */
function validateFileSize(file, maxSize, context = 'general') {
    if (!file) {
        return { isValid: false, message: '파일을 선택해주세요.' };
    }

    if (file.size > maxSize) {
        const maxSizeFormatted = formatFileSize(maxSize);
        const currentSizeFormatted = formatFileSize(file.size);

        let contextMessage = '';
        if (context === 'email') {
            contextMessage = '이메일 첨부 파일은 ';
        } else {
            contextMessage = '업로드 가능한 파일 크기는 ';
        }

        return {
            isValid: false,
            message: `${contextMessage}${maxSizeFormatted}까지입니다.\n현재 선택된 파일: ${currentSizeFormatted}`
        };
    }

    return { isValid: true, message: '파일 크기가 적절합니다.' };
}

/**
 * 파일 확장자 검증
 */
function validateFileExtension(file, allowedExtensions = ['pdf', 'jpg', 'jpeg', 'png', 'gif', 'txt']) {
    if (!file || !file.name) {
        return { isValid: false, message: '유효하지 않은 파일입니다.' };
    }

    const extension = file.name.split('.').pop().toLowerCase();

    if (!allowedExtensions.includes(extension)) {
        return {
            isValid: false,
            message: `허용되지 않는 파일 형식입니다.\n허용 형식: ${allowedExtensions.join(', ')}`
        };
    }

    return { isValid: true, message: '지원되는 파일 형식입니다.' };
}

/**
 * 종합 파일 검증
 */
function validateFile(file, context = 'general') {
    const maxSize = context === 'email' ? FILE_SIZE_LIMITS.EMAIL_ATTACHMENT : FILE_SIZE_LIMITS.GENERAL_UPLOAD;

    // 파일 존재 확인
    if (!file) {
        return { isValid: false, message: '파일을 선택해주세요.' };
    }

    // 크기 검증
    const sizeValidation = validateFileSize(file, maxSize, context);
    if (!sizeValidation.isValid) {
        return sizeValidation;
    }

    // 확장자 검증
    const extensionValidation = validateFileExtension(file);
    if (!extensionValidation.isValid) {
        return extensionValidation;
    }

    return { isValid: true, message: '업로드 가능한 파일입니다.' };
}

/**
 * 사용자 친화적 에러 메시지 표시
 */
function showFileValidationMessage(message, isError = false) {
    // 기존 메시지 제거
    const existingMessage = document.querySelector('.file-validation-message');
    if (existingMessage) {
        existingMessage.remove();
    }

    // 새 메시지 생성
    const messageElement = document.createElement('div');
    messageElement.className = `file-validation-message ${isError ? 'error' : 'success'}`;
    messageElement.style.cssText = `
        padding: 0.625rem;
        margin: 0.625rem 0;
        border-radius: 0.25rem;
        font-size: 0.875rem;
        white-space: pre-line;
        ${isError ?
            'background-color: #fee; border: 0.0625rem solid #fcc; color: #c33;' :
            'background-color: #efe; border: 0.0625rem solid #cfc; color: #3c3;'
        }
    `;
    messageElement.textContent = message;

    return messageElement;
}

/**
 * 파일 입력 필드에 이벤트 리스너 추가
 */
document.addEventListener('DOMContentLoaded', function () {
    // 일반 파일 업로드 필드들
    const fileInputs = document.querySelectorAll('input[type="file"]');

    fileInputs.forEach(input => {
        input.addEventListener('change', function (e) {
            const file = e.target.files[0];
            const context = this.dataset.context || 'general'; // data-context 속성으로 컨텍스트 지정

            if (file) {
                const validation = validateFile(file, context);
                const messageElement = showFileValidationMessage(validation.message, !validation.isValid);

                // 메시지를 파일 입력 필드 다음에 삽입
                this.parentNode.insertBefore(messageElement, this.nextSibling);

                // 유효하지 않으면 파일 선택 해제
                if (!validation.isValid) {
                    this.value = '';
                }
            }
        });
    });
});
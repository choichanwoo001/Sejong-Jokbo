// 공통 유틸리티 함수들

/**
 * 파일 크기를 사람이 읽기 쉬운 형태로 변환
 * @param {number} bytes - 바이트 단위 파일 크기
 * @returns {string} 포맷된 파일 크기 문자열
 */
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

/**
 * 카테고리 이름을 ID로 변환
 * @param {string} categoryName - 카테고리 이름 (서양, 동서양, 동양, 과학)
 * @returns {string|null} 카테고리 ID 또는 null
 */
function getCategoryId(categoryName) {
    const categoryMap = {
        '서양': 'category-western',
        '동서양': 'category-east-west',
        '동양': 'category-eastern',
        '과학': 'category-science'
    };
    return categoryMap[categoryName] || null;
}

/**
function debugError(...args) {
    if (DEBUG_MODE) {
        console.error(...args);
    }
}

/**
 * 조건부 콘솔 경고
 */
function debugWarn(...args) {
    if (DEBUG_MODE) {
        console.warn(...args);
    }
}


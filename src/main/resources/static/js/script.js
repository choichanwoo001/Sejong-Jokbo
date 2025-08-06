// 검색 기능
const searchInput = document.querySelector('.search-input');
const searchForm = document.getElementById('searchForm');

// 검색 입력 이벤트
searchInput.addEventListener('focus', function() {
    this.style.transform = 'scale(1.005)';
});

searchInput.addEventListener('blur', function() {
    this.style.transform = 'scale(1)';
});

// 실시간 검색 (Enter 키 또는 검색 버튼 클릭 시)
searchInput.addEventListener('keypress', function(e) {
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
    tab.addEventListener('click', function() {
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
        let targetCategory;

        switch(tabText) {
            case '서양':
                targetCategory = 'category-western';
                break;
            case '동서양':
                targetCategory = 'category-east-west';
                break;
            case '동양':
                targetCategory = 'category-eastern';
                break;
            case '과학사':
                targetCategory = 'category-science';
                break;
        }

        if (targetCategory) {
            document.getElementById(targetCategory).style.display = 'block';
        }
    });
});

// 페이지 로드 시 검색 결과가 있으면 검색 입력 필드에 포커스
document.addEventListener('DOMContentLoaded', function() {
    const searchResults = document.querySelector('.search-results');
    if (searchResults) {
        searchInput.focus();
    }
});

// 카테고리 탭 클릭 효과
document.querySelectorAll('.category-tab').forEach(button => {
    button.addEventListener('click', function(e) {
        this.style.transform = 'scale(0.98)';
        setTimeout(() => {
            this.style.transform = '';
        }, 150);
    });
});

// 부드러운 페이지 로드 애니메이션
window.addEventListener('load', function() {
    document.querySelector('.container').classList.add('fade-in');
}); 
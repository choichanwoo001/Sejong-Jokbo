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
            
            // 현재 필터 적용
            const sortFilter = document.getElementById('sortFilter');
            const currentFilter = sortFilter ? sortFilter.value : 'name';
            console.log('카테고리 변경, 필터 적용:', currentFilter);
            sortBooks(currentFilter);
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

// 필터 드롭다운 이벤트
document.addEventListener('DOMContentLoaded', function() {
    const sortFilter = document.getElementById('sortFilter');
    if (sortFilter) {
        sortFilter.addEventListener('change', function() {
            const selectedValue = this.value;
            console.log('필터 변경:', selectedValue);
            sortBooks(selectedValue);
        });
    }
});

// 책 정렬 함수
function sortBooks(sortType) {
    const currentCategory = document.querySelector('.category-tab.active');
    if (!currentCategory) return;

    const tabText = currentCategory.textContent.trim();
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
        const bookGrid = document.querySelector(`#${targetCategory} .books-grid`);
        if (!bookGrid) return;
        
        const books = Array.from(bookGrid.children);
        
        if (books.length === 0) return;

        books.sort((a, b) => {
            const titleA = a.querySelector('.book-title').textContent.trim();
            const titleB = b.querySelector('.book-title').textContent.trim();

            if (sortType === 'name') {
                return titleA.localeCompare(titleB, 'ko');
            } else if (sortType === 'jokbo') {
                // 족보 많은 순 정렬
                const jokboCountA = parseInt(a.querySelector('.jokbo-count').textContent.match(/\d+/)[0]);
                const jokboCountB = parseInt(b.querySelector('.jokbo-count').textContent.match(/\d+/)[0]);
                return jokboCountB - jokboCountA; // 내림차순 (많은 순)
            }
            return 0;
        });

        // 정렬된 결과를 다시 DOM에 추가
        books.forEach(book => bookGrid.appendChild(book));
        
        // 정렬 완료 알림 (개발용)
        console.log(`${sortType} 기준으로 정렬 완료`);
    }
}

// 부드러운 페이지 로드 애니메이션
window.addEventListener('load', function() {
    document.querySelector('.container').classList.add('fade-in');
}); 
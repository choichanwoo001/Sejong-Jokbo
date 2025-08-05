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

// 검색 입력 이벤트
const searchInput = document.querySelector('.search-input');
searchInput.addEventListener('focus', function() {
    this.style.transform = 'scale(1.005)';
});

searchInput.addEventListener('blur', function() {
    this.style.transform = 'scale(1)';
});

// 버튼 클릭 효과
document.querySelectorAll('button, .auth-button').forEach(button => {
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
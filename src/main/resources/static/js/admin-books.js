function getCategoryId(category) {
    switch (category) {
        case '서양':
            return 'category-western';
        case '동서양':
            return 'category-east-west';
        case '동양':
            return 'category-eastern';
        case '과학':
            return 'category-science';
        default:
            return null;
    }
}

function setupCategoryTabs() {
    const categoryTabs = document.querySelectorAll('.category-tab');
    const bookDisplays = document.querySelectorAll('.book-display');

    categoryTabs.forEach(function (tab) {
        tab.addEventListener('click', function () {
            const category = this.getAttribute('data-category');

            categoryTabs.forEach(function (item) {
                item.classList.remove('active');
            });

            bookDisplays.forEach(function (display) {
                display.style.display = 'none';
            });

            this.classList.add('active');

            const categoryId = getCategoryId(category);
            if (categoryId) {
                const targetDisplay = document.getElementById(categoryId);
                if (targetDisplay) {
                    targetDisplay.style.display = 'block';
                }
            }
        });
    });
}

function updateCategoryEmptyStates() {
    const displays = document.querySelectorAll('.book-display');

    displays.forEach(function (display) {
        const grid = display.querySelector('.books-grid');
        if (!grid) {
            return;
        }

        const items = grid.querySelectorAll('.book-item');
        const hasVisible = Array.prototype.some.call(items, function (item) {
            return item.style.display !== 'none';
        });

        let message = grid.querySelector('.no-results[data-role="category-no-result"]');

        if (!hasVisible) {
            if (!message) {
                message = document.createElement('div');
                message.className = 'no-results';
                message.dataset.role = 'category-no-result';
                message.textContent = '검색 결과가 없습니다.';
                grid.appendChild(message);
            }
        } else if (message) {
            message.remove();
        }
    });
}

function initAdminSearch() {
    if (!window.AdminSearch) {
        return;
    }

    window.AdminSearch.init({
        formId: 'searchForm',
        itemSelector: '.book-item',
        fieldConfigs: [
            { name: 'bookTitle', datasetKey: 'title', matchType: 'includes' },
            { name: 'author', datasetKey: 'author', matchType: 'includes' },
            {
                name: 'category',
                datasetKey: 'category',
                matchType: 'custom',
                matches: function (itemValueRaw, searchValue) {
                    if (!searchValue) {
                        return true;
                    }

                    const normalize = function (value) {
                        return (value || '')
                            .toString()
                            .trim()
                            .toLowerCase()
                            .replace(/사$/, '');
                    };

                    return normalize(itemValueRaw) === normalize(searchValue);
                }
            }
        ],
        visibleDisplayStyle: 'block',
        afterFilter: function () {
            updateCategoryEmptyStates();
        },
        onReset: function () {
            clearCategoryNoResultMessages();
        }
    });
}

function clearCategoryNoResultMessages() {
    const messages = document.querySelectorAll('.no-results[data-role="category-no-result"]');
    messages.forEach(function (message) {
        message.remove();
    });
}

document.addEventListener('DOMContentLoaded', function () {
    setupCategoryTabs();
    initAdminSearch();
    clearCategoryNoResultMessages();
});


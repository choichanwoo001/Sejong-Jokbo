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
    setupJokboManagement();
});

function setupJokboManagement() {
    // Close dropdowns when clicking outside
    document.addEventListener('click', function (event) {
        if (!event.target.closest('.jokbo-management-wrapper')) {
            document.querySelectorAll('.jokbo-dropdown').forEach(d => d.style.display = 'none');
        }
    });

    const wrappers = document.querySelectorAll('.jokbo-management-wrapper');

    wrappers.forEach(wrapper => {
        const btn = wrapper.querySelector('.manage-btn');
        const dropdown = wrapper.querySelector('.jokbo-dropdown');

        btn.addEventListener('click', function (e) {
            e.stopPropagation(); // Prevent document click from closing immediately

            // Close all other dropdowns
            document.querySelectorAll('.jokbo-dropdown').forEach(d => {
                if (d !== dropdown) d.style.display = 'none';
            });

            // Toggle current
            if (dropdown.style.display === 'block') {
                dropdown.style.display = 'none';
            } else {
                dropdown.style.display = 'block';
                loadJokbos(wrapper);
            }
        });
    });
}

function loadJokbos(wrapper) {
    const bookId = wrapper.dataset.bookId;
    const listContainer = wrapper.querySelector('.jokbo-list');
    const spinner = wrapper.querySelector('.loading-spinner');

    if (wrapper.dataset.loaded === 'true') {
        return;
    }

    spinner.style.display = 'block';
    listContainer.innerHTML = '';

    fetch(`/admin/api/book/${bookId}/jokbos`)
        .then(response => response.json())
        .then(jokbos => {
            wrapper.dataset.loaded = 'true';
            spinner.style.display = 'none';
            renderJokboList(jokbos, listContainer);
        })
        .catch(error => {
            console.error('Error fetching jokbos:', error);
            spinner.style.display = 'none';
            listContainer.innerHTML = '<div class="jokbo-empty">오류가 발생했습니다.</div>';
        });
}

function renderJokboList(jokbos, container) {
    if (!jokbos || jokbos.length === 0) {
        container.innerHTML = '<div class="jokbo-empty">등록된 족보가 없습니다.</div>';
        return;
    }

    jokbos.forEach(jokbo => {
        const item = document.createElement('div');
        item.className = 'jokbo-list-item';

        const statusClass = getStatusClass(jokbo.status);
        const date = new Date(jokbo.createdAt).toLocaleDateString();

        item.innerHTML = `
            <div class="jokbo-item-header">
                <span>${jokbo.uploaderName}</span>
                <span class="jokbo-status ${statusClass}">${jokbo.status}</span>
            </div>
            <div style="font-size: 0.75rem; color: #666;">${date} | ${jokbo.contentType}</div>
            <div class="jokbo-item-actions">
                <a href="${getContentUrl(jokbo)}" target="_blank" class="jokbo-action-btn btn-view">확인</a>
                <button class="jokbo-action-btn btn-reject" onclick="rejectJokbo(${jokbo.jokboId}, this)">반려</button>
                <button class="jokbo-action-btn btn-delete" onclick="deleteJokbo(${jokbo.jokboId}, this)">삭제</button>
            </div>
        `;
        container.appendChild(item);
    });
}

function getStatusClass(status) {
    switch (status) {
        case '승인': return 'status-approved';
        case '대기': return 'status-pending';
        case '반려': return 'status-rejected';
        default: return '';
    }
}

function getContentUrl(jokbo) {
    if (jokbo.contentType === 'text') {
        return `/jokbo/view/text/${jokbo.jokboId}`;
    } else {
        return `/jokbo/view/${jokbo.contentUrl}`;
    }
}

// Make functions global for onclick handlers
window.rejectJokbo = function (jokboId, btn) {
    const comment = prompt('반려 사유를 입력하세요:');
    if (comment === null) return; // Cancelled

    if (!confirm('정말로 반려하시겠습니까?')) return;

    fetch(`/admin/jokbo/${jokboId}/reject?comment=${encodeURIComponent(comment)}`, {
        method: 'POST'
    })
        .then(response => response.text())
        .then(result => {
            if (result === 'success') {
                alert('반려되었습니다.');
                // Reload the list or update status
                refreshJokboList(btn);
            } else {
                alert(result);
            }
        })
        .catch(error => alert('오류가 발생했습니다: ' + error));
};

window.deleteJokbo = function (jokboId, btn) {
    if (!confirm('정말로 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) return;

    fetch(`/admin/jokbo/${jokboId}`, {
        method: 'DELETE'
    })
        .then(response => response.text())
        .then(result => {
            if (result === 'success') {
                alert('삭제되었습니다.');
                // Remove item from list
                const item = btn.closest('.jokbo-list-item');
                item.remove();

                // Check if empty
                const container = btn.closest('.jokbo-list');
                if (container.children.length === 0) {
                    container.innerHTML = '<div class="jokbo-empty">등록된 족보가 없습니다.</div>';
                }
            } else {
                alert(result);
            }
        })
        .catch(error => alert('오류가 발생했습니다: ' + error));
};

function refreshJokboList(btn) {
    // Find the wrapper and reset loaded state to trigger re-fetch on next hover
    // Or just re-fetch immediately
    const wrapper = btn.closest('.jokbo-management-wrapper');
    wrapper.dataset.loaded = 'false';
    // Trigger mouseenter manually or just let user hover again
    // To update immediately:
    const event = new Event('mouseenter');
    wrapper.dispatchEvent(event);
}


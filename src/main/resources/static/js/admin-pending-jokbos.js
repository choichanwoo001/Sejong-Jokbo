window.approveJokbo = function (jokboId) {
    if (!confirm('이 족보를 승인하시겠습니까?')) {
        return;
    }

    fetch(`/admin/jokbo/${jokboId}/approve`, {
        method: 'POST'
    })
        .then(function (response) {
            return response.text();
        })
        .then(function (result) {
            if (result === 'success') {
                alert('족보가 승인되었습니다.');
                location.reload();
            } else {
                alert('승인 중 오류가 발생했습니다: ' + result);
            }
        })
        .catch(function () {
            alert('승인 중 오류가 발생했습니다.');
        });
};

window.rejectJokbo = function (jokboId) {
    if (!confirm('이 족보를 반려하시겠습니까?')) {
        return;
    }

    fetch(`/admin/jokbo/${jokboId}/reject`, {
        method: 'POST'
    })
        .then(function (response) {
            return response.text();
        })
        .then(function (result) {
            if (result === 'success') {
                alert('족보가 반려되었습니다.');
                location.reload();
            } else {
                alert('반려 중 오류가 발생했습니다: ' + result);
            }
        })
        .catch(function () {
            alert('반려 중 오류가 발생했습니다.');
        });
};

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.jokbo-actions [data-action]').forEach((button) => {
        button.addEventListener('click', () => {
            const jokboId = button.getAttribute('data-jokbo-id');
            const action = button.getAttribute('data-action');

            if (!jokboId || !action) {
                return;
            }

            if (action === 'approve') {
                window.approveJokbo(jokboId);
            } else if (action === 'reject') {
                window.rejectJokbo(jokboId);
            }
        });
    });

    if (!window.AdminSearch) {
        return;
    }

    window.AdminSearch.init({
        formId: 'searchForm',
        itemSelector: '.jokbo-item',
        fieldConfigs: [
            { name: 'bookTitle', datasetKey: 'bookTitle', matchType: 'includes' },
            { name: 'uploaderName', datasetKey: 'uploader', matchType: 'includes' },
            { name: 'uploadDate', datasetKey: 'date', matchType: 'equals' }
        ],
        visibleDisplayStyle: 'block',
        noResult: {
            targetSelector: '#jokboList',
            text: '검색 결과가 없습니다.'
        }
    });
});


// SSE (Server-Sent Events) 공통 기능
class SSEManager {
    constructor(type = 'user') {
        this.type = type;
        this.eventSource = null;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectDelay = 5000;
    }
    
    // SSE 연결 설정
    connect() {
        if (this.eventSource) {
            this.eventSource.close();
        }
        
        const url = this.type === 'admin' ? '/api/sse/admin/notifications' : '/api/sse/user/notifications';
        this.eventSource = new EventSource(url);
        
        this.eventSource.onopen = (event) => {
            console.log(`${this.type} SSE 연결이 설정되었습니다.`);
            this.reconnectAttempts = 0;
            
            // 연결 상태를 페이지에 표시 (디버깅용)
            if (this.type === 'user') {
                console.log('사용자 SSE 연결 성공 - 족보 승인 알림을 받을 준비가 되었습니다.');
            }
        };
        
        this.eventSource.onmessage = (event) => {
            console.log('SSE 메시지 수신:', event.data);
        };
        
        this.eventSource.addEventListener('connect', (event) => {
            console.log(`${this.type} SSE 연결 확인:`, event.data);
        });
        
        // 사용자용 이벤트 리스너
        if (this.type === 'user') {
            this.eventSource.addEventListener('jokbo_approved', (event) => {
                console.log('족보 승인 알림 수신:', event.data);
                this.showNotification('족보 승인', event.data, 'success');
                
                // 실시간으로 족보 목록 업데이트
                console.log('실시간 업데이트 시작...');
                this.updateJokboList();
                
                // 실시간 업데이트 실패 시 페이지 새로고침으로 fallback (5초 후)
                setTimeout(() => {
                    console.log('Fallback: 페이지 새로고침 실행');
                    location.reload();
                }, 5000);
            });
        }
        
        // 관리자용 이벤트 리스너
        if (this.type === 'admin') {
            this.eventSource.addEventListener('new_jokbo_request', (event) => {
                console.log('새로운 족보 요청:', event.data);
                this.showNotification('새로운 족보 요청', event.data, 'info');
                
                // 승인 대기 족보 수 업데이트
                this.updatePendingJokbosCount();
            });
            
            this.eventSource.addEventListener('sync', (event) => {
                console.log('동기화 완료:', event.data);
                this.showNotification('동기화 완료', event.data, 'success');
            });
            
            // 오류 이벤트 리스너 추가
            this.eventSource.addEventListener('error', (event) => {
                console.error('SSE 오류 이벤트:', event.data);
                this.showNotification('연결 오류', event.data, 'error');
                
            });
        }
        
        // 사용자용 오류 이벤트 리스너 추가
        if (this.type === 'user') {
            this.eventSource.addEventListener('error', (event) => {
                console.error('SSE 오류 이벤트 발생:', event);
                // event.data는 undefined일 수 있으므로 event 자체를 로깅
                this.showNotification('연결 오류', 'SSE 연결에 문제가 발생했습니다.', 'error');
            });
        }
        
        this.eventSource.onerror = (event) => {
            console.error('SSE 연결 오류:', event);
            this.handleReconnect();
        };
    }
    
    // 재연결 처리
    handleReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            console.log(`SSE 재연결 시도 ${this.reconnectAttempts}/${this.maxReconnectAttempts}`);
            setTimeout(() => {
                this.connect();
            }, this.reconnectDelay);
        } else {
            console.error('SSE 최대 재연결 시도 횟수 초과');
        }
    }
    
    // 알림 표시 함수
    showNotification(title, message, type) {
        // 브라우저 알림 지원 확인
        if ('Notification' in window && Notification.permission === 'granted') {
            new Notification(title, {
                body: message,
                icon: '/favicon.ico'
            });
        }
        
        // 페이지 내 알림 표시
        const notification = document.createElement('div');
        const notificationType = ['success', 'error', 'info'].includes(type) ? type : 'info';
        notification.className = `notification notification-${notificationType}`;
        notification.innerHTML = `
            <strong>${title}</strong><br>
            ${message}
        `;
        
        document.body.appendChild(notification);
        
        // 5초 후 제거
        setTimeout(() => {
            if (notification.parentNode) {
                notification.classList.add('notification-exit');
                notification.addEventListener('animationend', () => {
                    if (notification.parentNode) {
                        notification.parentNode.removeChild(notification);
                    }
                }, { once: true });
            }
        }, 5000);
    }
    
    // 승인 대기 족보 수 업데이트 (관리자용)
    updatePendingJokbosCount() {
        if (this.type === 'admin') {
            fetch('/admin/jokbos/pending/count')
                .then(response => response.json())
                .then(data => {
                    const countElement = document.querySelector('.pending-jokbos .card-number');
                    if (countElement) {
                        countElement.textContent = data.count;
                    }
                })
                .catch(error => console.error('족보 수 업데이트 실패:', error));
        }
    }
    
    // 족보 목록 실시간 업데이트 (사용자용)
    updateJokboList() {
        if (this.type === 'user') {
            console.log('족보 목록 실시간 업데이트 시작');
            
            // 현재 페이지의 책 ID 가져오기 (여러 방법으로 시도)
            let bookId = null;
            
            // 1. data-book-id 속성에서 추출
            const bookDetailElement = document.querySelector('.book-detail');
            if (bookDetailElement) {
                bookId = bookDetailElement.getAttribute('data-book-id');
            }
            
            // 2. URL에서 추출 (fallback)
            if (!bookId) {
                const urlMatch = window.location.pathname.match(/\/book\/(\d+)/);
                if (urlMatch) {
                    bookId = urlMatch[1];
                }
            }
            
            // 3. 페이지 내 숨겨진 요소에서 추출 (fallback)
            if (!bookId) {
                const hiddenBookId = document.querySelector('input[name="bookId"], [data-book-id]');
                if (hiddenBookId) {
                    bookId = hiddenBookId.getAttribute('data-book-id') || hiddenBookId.value;
                }
            }
            
            console.log('추출된 책 ID:', bookId);
            
            // 도서 상세 페이지인 경우 족보 목록 업데이트
            if (bookId) {
                this.refreshJokboList(bookId);
            }
            
            // 홈페이지의 경우 도서별 족보 수 업데이트
            this.updateHomePageJokboCounts();
        }
    }
    
    // 특정 책의 족보 목록 새로고침
    refreshJokboList(bookId) {
        console.log('족보 목록 새로고침 시작, 책 ID:', bookId);
        
        // 현재 활성 탭 확인
        const activeTab = document.querySelector('.jokbo-tab.active');
        const isListTabActive = activeTab && activeTab.textContent.includes('목록');
        
        console.log('족보 목록 탭 활성화 상태:', isListTabActive);
        
        // 족보 목록 탭이 활성화되지 않은 경우, 탭을 자동으로 활성화
        if (!isListTabActive) {
            console.log('족보 목록 탭을 자동으로 활성화합니다.');
            const listTab = document.querySelector('.jokbo-tab[onclick*="list"]');
            if (listTab) {
                listTab.click(); // 탭 클릭으로 활성화
                // 탭 전환 애니메이션을 기다린 후 업데이트
                setTimeout(() => {
                    this.performJokboListUpdate(bookId);
                }, 300);
                return;
            }
        }
        
        // 바로 업데이트 실행
        this.performJokboListUpdate(bookId);
    }
    
    // 실제 족보 목록 업데이트 수행
    performJokboListUpdate(bookId) {
        const currentPage = this.getCurrentPage();
        console.log('실제 업데이트 실행, 현재 페이지:', currentPage);
        
        fetch(`/api/user/books/${bookId}/jokbos?page=${currentPage}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log('족보 목록 데이터 수신:', data);
                this.renderJokboList(data);
            })
            .catch(error => {
                console.error('족보 목록 업데이트 실패:', error);
                // 오류 발생 시 페이지 새로고침으로 fallback
                setTimeout(() => {
                    location.reload();
                }, 1000);
            });
    }
    
    // 현재 페이지 번호 가져오기
    getCurrentPage() {
        const currentPageElement = document.querySelector('.page-btn.current');
        if (currentPageElement) {
            return parseInt(currentPageElement.textContent) - 1;
        }
        return 0;
    }
    
    // 족보 목록 렌더링
    renderJokboList(data) {
        console.log('족보 목록 렌더링 시작');
        
        // 정확한 족보 목록 컨테이너 찾기 (DOM 구조에 맞게)
        let jokboListContainer = document.querySelector('#list .jokbo-list');
        
        if (!jokboListContainer) {
            console.error('족보 목록 컨테이너를 찾을 수 없습니다. (#list .jokbo-list)');
            return;
        }
        
        console.log('족보 목록 컨테이너 찾음:', jokboListContainer);
        
        // 기존 족보 목록 제거
        jokboListContainer.innerHTML = '';
        
        // 새로운 족보 목록 렌더링
        if (data.content && data.content.length > 0) {
            data.content.forEach(jokbo => {
                const jokboItem = this.createJokboItem(jokbo);
                jokboListContainer.appendChild(jokboItem);
            });
            
            // 페이징 업데이트
            this.updatePagination(data);
        } else {
            // 족보가 없는 경우 메시지 표시
            jokboListContainer.innerHTML = '<div class="no-jokbo"><p>등록된 족보가 없습니다.</p></div>';
        }
        
        console.log('족보 목록 렌더링 완료');
    }
    
    // 족보 아이템 생성
    createJokboItem(jokbo) {
        const jokboItem = document.createElement('div');
        jokboItem.className = 'jokbo-item';
        
        const createdAt = new Date(jokbo.createdAt).toLocaleString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
        
        jokboItem.innerHTML = `
            <div class="jokbo-header">
                <span class="jokbo-uploader">${jokbo.uploaderName}</span>
                <span class="jokbo-date">${createdAt}</span>
            </div>
            <div class="jokbo-actions">
                ${jokbo.contentType === 'text' ? 
                    `<a href="/jokbo/view/text/${jokbo.jokboId}" target="_blank" class="btn-view">보기</a>
                     <a href="/jokbo/download/text/${jokbo.jokboId}" class="btn-download">다운로드</a>` :
                    `<a href="/jokbo/view/${jokbo.contentUrl}" target="_blank" class="btn-view">보기</a>
                     <a href="/jokbo/download/${jokbo.contentUrl}" class="btn-download">다운로드</a>`
                }
            </div>
            ${jokbo.comment ? `<div class="jokbo-comment"><strong>코멘트:</strong> <span>${jokbo.comment}</span></div>` : ''}
        `;
        
        return jokboItem;
    }
    
    // 페이징 업데이트
    updatePagination(data) {
        const paginationContainer = document.querySelector('#list .jokbo-pagination');
        if (!paginationContainer) {
            console.log('페이징 컨테이너를 찾을 수 없습니다.');
            return;
        }
        
        console.log('페이징 업데이트 시작');
        
        // 기존 페이징 제거
        paginationContainer.innerHTML = '';
        
        if (data.totalPages > 1) {
            const bookId = document.querySelector('.book-detail').getAttribute('data-book-id');
            
            // 이전 페이지 버튼
            if (data.hasPrevious) {
                const prevBtn = document.createElement('a');
                prevBtn.href = `/book/${bookId}?page=${data.currentPage - 1}&tab=list`;
                prevBtn.className = 'page-btn prev-btn';
                prevBtn.textContent = '이전';
                paginationContainer.appendChild(prevBtn);
            }
            
            // 페이지 번호들
            const pageNumbers = document.createElement('div');
            pageNumbers.className = 'page-numbers';
            
            // 페이지 번호 생성 로직 (기존 페이징과 동일)
            this.generatePageNumbers(pageNumbers, data, bookId);
            
            paginationContainer.appendChild(pageNumbers);
            
            // 다음 페이지 버튼
            if (data.hasNext) {
                const nextBtn = document.createElement('a');
                nextBtn.href = `/book/${bookId}?page=${data.currentPage + 1}&tab=list`;
                nextBtn.className = 'page-btn next-btn';
                nextBtn.textContent = '다음';
                paginationContainer.appendChild(nextBtn);
            }
        }
        
        console.log('페이징 업데이트 완료');
    }
    
    // 페이지 번호 생성
    generatePageNumbers(container, data, bookId) {
        const currentPage = data.currentPage;
        const totalPages = data.totalPages;
        
        // 첫 번째 페이지
        if (currentPage > 3) {
            const firstPage = document.createElement('a');
            firstPage.href = `/book/${bookId}?page=0&tab=list`;
            firstPage.className = 'page-btn';
            firstPage.textContent = '1';
            container.appendChild(firstPage);
        }
        
        // ... 표시
        if (currentPage > 4) {
            const dots = document.createElement('span');
            dots.className = 'page-dots';
            dots.textContent = '...';
            container.appendChild(dots);
        }
        
        // 현재 페이지 주변 번호들
        for (let i = Math.max(0, currentPage - 2); i <= Math.min(totalPages - 1, currentPage + 2); i++) {
            if (i === currentPage) {
                const currentPageSpan = document.createElement('span');
                currentPageSpan.className = 'page-btn current';
                currentPageSpan.textContent = i + 1;
                container.appendChild(currentPageSpan);
            } else {
                const pageBtn = document.createElement('a');
                pageBtn.href = `/book/${bookId}?page=${i}&tab=list`;
                pageBtn.className = 'page-btn';
                pageBtn.textContent = i + 1;
                container.appendChild(pageBtn);
            }
        }
        
        // ... 표시
        if (currentPage < totalPages - 5) {
            const dots = document.createElement('span');
            dots.className = 'page-dots';
            dots.textContent = '...';
            container.appendChild(dots);
        }
        
        // 마지막 페이지
        if (currentPage < totalPages - 4) {
            const lastPage = document.createElement('a');
            lastPage.href = `/book/${bookId}?page=${totalPages - 1}&tab=list`;
            lastPage.className = 'page-btn';
            lastPage.textContent = totalPages;
            container.appendChild(lastPage);
        }
    }
    
    // 홈페이지 족보 수 업데이트
    updateHomePageJokboCounts() {
        console.log('홈페이지 족보 수 업데이트 시작');
        
        // 홈페이지에서만 실행
        const bookItems = document.querySelectorAll('.book-item');
        console.log('발견된 도서 아이템 수:', bookItems.length);
        
        if (bookItems.length > 0) {
            bookItems.forEach((bookItem, index) => {
                const bookId = this.extractBookIdFromElement(bookItem);
                console.log(`도서 ${index + 1} ID:`, bookId);
                
                if (bookId) {
                    fetch(`/api/user/books/${bookId}/jokbos/count`)
                        .then(response => {
                            if (!response.ok) {
                                throw new Error(`HTTP error! status: ${response.status}`);
                            }
                            return response.json();
                        })
                        .then(data => {
                            console.log(`도서 ${bookId} 족보 수:`, data.count);
                            const countElement = bookItem.querySelector('.jokbo-count');
                            if (countElement) {
                                countElement.textContent = `족보: ${data.count}개`;
                                // 업데이트 효과 추가
                                countElement.classList.remove('highlight-animation');
                                void countElement.offsetWidth;
                                countElement.classList.add('highlight-animation');
                                countElement.addEventListener('animationend', () => {
                                    countElement.classList.remove('highlight-animation');
                                }, { once: true });
                            }
                        })
                        .catch(error => {
                            console.error(`도서 ${bookId} 족보 수 업데이트 실패:`, error);
                        });
                }
            });
        } else {
            console.log('홈페이지가 아니거나 도서 아이템을 찾을 수 없습니다.');
        }
    }
    
    // 요소에서 책 ID 추출
    extractBookIdFromElement(element) {
        // data-book-id 속성에서 우선 추출
        const dataBookId = element.getAttribute('data-book-id');
        if (dataBookId) {
            return dataBookId;
        }
        
        // onclick 속성에서 bookId 추출 (fallback)
        const onclick = element.getAttribute('onclick');
        if (onclick) {
            const match = onclick.match(/\/book\/(\d+)/);
            if (match) {
                return match[1];
            }
        }
        
        return null;
    }
    
    // 동기화 요청 (관리자용)
    async sync() {
        if (this.type === 'admin') {
            try {
                const response = await fetch('/api/sse/admin/sync', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                const result = await response.text();
                
                if (result === 'success') {
                    this.showNotification('동기화 성공', '새로운 요청을 확인했습니다.', 'success');
                    this.updatePendingJokbosCount();
                } else {
                    this.showNotification('동기화 실패', result, 'error');
                }
                
                return result;
            } catch (error) {
                console.error('동기화 오류:', error);
                this.showNotification('동기화 오류', '네트워크 오류가 발생했습니다.', 'error');
                throw error;
            }
        }
    }
    
    // 연결 해제
    disconnect() {
        if (this.eventSource) {
            this.eventSource.close();
            this.eventSource = null;
            
            // 서버에 연결 해제 요청
            fetch(`/api/sse/disconnect?type=${this.type}`, { 
                method: 'DELETE' 
            }).catch(error => console.error('연결 해제 요청 실패:', error));
        }
    }
}

// 전역 SSE 매니저 인스턴스들 (사용자와 관리자 분리)
let userSseManager = null;
let adminSseManager = null;

// SSE 초기화 함수
function initSSE(type = 'user') {
    // 브라우저 알림 권한 요청
    if ('Notification' in window && Notification.permission === 'default') {
        Notification.requestPermission();
    }
    
    // 타입에 따라 적절한 매니저 생성
    if (type === 'user') {
        // 기존 사용자 연결이 있으면 해제
        if (userSseManager) {
            userSseManager.disconnect();
        }
        userSseManager = new SSEManager('user');
        userSseManager.connect();
        console.log('사용자용 SSE 매니저 생성 완료');
    } else if (type === 'admin') {
        // 기존 관리자 연결이 있으면 해제
        if (adminSseManager) {
            adminSseManager.disconnect();
        }
        adminSseManager = new SSEManager('admin');
        adminSseManager.connect();
        console.log('관리자용 SSE 매니저 생성 완료');
    }
    
    // 페이지 언로드 시 연결 해제
    window.addEventListener('beforeunload', () => {
        if (userSseManager) {
            userSseManager.disconnect();
        }
        if (adminSseManager) {
            adminSseManager.disconnect();
        }
    });
}

// 동기화 버튼 이벤트 핸들러 (관리자용)
function handleSyncButton(button) {
    if (adminSseManager && adminSseManager.type === 'admin') {
        button.disabled = true;
        button.textContent = '🔄 동기화 중...';
        
        adminSseManager.sync()
            .finally(() => {
                button.disabled = false;
                button.textContent = '🔄 동기화';
            });
    }
}


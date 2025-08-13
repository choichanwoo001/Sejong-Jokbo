-- 관리자 계정 생성
-- 비밀번호는 SHA-256으로 해시화된 값입니다 (원본: admin123)
INSERT INTO admin (admin_name, password, created_at) 
VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', NOW());

-- 추가 관리자 계정 (원본 비밀번호: manager123)
INSERT INTO admin (admin_name, password, created_at) 
VALUES ('manager', 'c4b0e190e5c8b5c4b5c4b5c4b5c4b5c4b5c4b5c4b5c4b5c4b5c4b5c4b5c4b5c4b5', NOW());

-- 동양 도서 데이터 삽입
INSERT INTO book (title, author, publisher, category, image_url, jokbo_count, created_at) VALUES
('성학십도', '이황', '홍익출판사 (2001)', '동양', '/img/east/img-283196314.jpg', 0, NOW()),
('북학의', '박제가', '을유문화사 (2011)', '동양', '/img/east/img-284119835.jpg', 0, NOW()),
('조선상고사', '신채호', '비봉출판사 (2006)', '동양', '/img/east/img-285966877.jpg', 0, NOW()),
('삼국유사', '일연', '한길사 (2006)', '동양', '/img/east/img-286890398.jpg', 0, NOW()),
('논어-슬기바다1', '공자', '홍익출판사 (2005)', '동양', '/img/east/img-287813919.jpg', 0, NOW()),
('맹자-슬기바다2', '맹자', '홍익출판사 (2005)', '동양', '/img/east/img-288737440.jpg', 0, NOW()),
('대학·중용-슬기바다3', '주희', '홍익출판사 (2005)', '동양', '/img/east/img-289660961.jpg', 0, NOW()),
('노자의 목소리로 듣는 도덕경', '노자', '소나무 (2001)', '동양', '/img/east/img-309978423.jpg', 0, NOW()),
('장자', '장자', '현암사 (2010)', '동양', '/img/east/img-310901944.jpg', 0, NOW()),
('사기열전 1', '사마천', '민음사 (2007)', '동양', '/img/east/img-311825465.jpg', 0, NOW()),
('생활의 발견', '린위탕', '문예출판사 (2012)', '동양', '/img/east/img-312748986.jpg', 0, NOW()),
('대동서', '강유위', '을유문화사 (2006)', '동양', '/img/east/img-313672507.jpg', 0, NOW()),
('간디 자서전', '간디', '파주BOOKS (2017)', '동양', '/img/east/img-314596028.jpg', 0, NOW()),
('한 젊은 유학자의 초상', '뚜 웨이밍', '통나무 (1994)', '동양', '/img/east/img-315519549.jpg', 0, NOW()),
('묵자', '묵적', '자유문고 (1995)', '동양', '/img/east/img-316443070.jpg', 0, NOW()),
('춘추좌전(상)', '좌구명', '인간사랑 (2017)', '동양', '/img/east/img-317366591.jpg', 0, NOW()),
('산해경', '장재서', '민음사 (1996)', '동양', '/img/east/img-318290112.jpg', 0, NOW()),
('한비자', '한비', '신원문화사 (2007)', '동양', '/img/east/img-338607574.jpg', 0, NOW()),
('순자', '순자', '을유문화사 (2008)', '동양', '/img/east/img359145655.jpg', 0, NOW()),
('한국인의 신화', '김열규', '일조각 (2005)', '동양', '/img/east/img360069176.jpg', 0, NOW()),
('채근담', '홍자성', '홍익출판미디어그룹 (2022)', '동양', '/img/east/img360992697.jpg', 0, NOW()); 

-- 동서양 도서 데이터 삽입
INSERT INTO book (title, author, publisher, category, image_url, jokbo_count, created_at) VALUES
('젊은 예술가의 초상', '제임스 조이스', '열린책들 (2011)', '동서양', '/img/eastwest/img-197308861.jpg', 0, NOW()),
('구토', '사르트르', '문예출판사 (1999)', '동서양', '/img/eastwest/img-199155903.jpg', 0, NOW()),
('실락원', '밀턴', '일신서적출판사 (1994)', '동서양', '/img/eastwest/img-200079424.jpg', 0, NOW()),
('파우스트', '괴테', '문예출판사 (2010)', '동서양', '/img/eastwest/img-201926466.jpg', 0, NOW()),
('일리아스/오디세이아', '호메로스', '동서문화사 (2007)', '동서양', '/img/eastwest/img-202849987.jpg', 0, NOW()),
('원전으로 읽는 변신이야기', '오비디우스', '솔 (2005)', '동서양', '/img/eastwest/img-203773508.jpg', 0, NOW()),
('인간의 조건', '앙드레 말로', '지식공작소 (2000)', '동서양', '/img/eastwest/img-224090970.jpg', 0, NOW()),
('양철북', '귄터 그라스', '동서문화사 (2010)', '동서양', '/img/eastwest/img-225014491.jpg', 0, NOW()),
('페스트', '알베르 까뮈', '민음사 (2011)', '동서양', '/img/eastwest/img-225938012.jpg', 0, NOW()),
('카라마조프씨네 형제들', '도스토예프스키', '누멘 (2011)', '동서양', '/img/eastwest/img-226861533.jpg', 0, NOW()),
('안나 카레니나', '톨스토이', '혜원 (2008)', '동서양', '/img/eastwest/img-227785054.jpg', 0, NOW()),
('백년동안의 고독', '가브리엘 가르시아', '문학사상사 (2005)', '동서양', '/img/eastwest/img-228708575.jpg', 0, NOW()),
('타이스·붉은 백합', '아나톨 프랑스', '서울대학교출판부 (1997)', '동서양', '/img/eastwest/img-229632096.jpg', 0, NOW()),
('열하일기', '박지원', '동서문화사 (2010)', '동서양', '/img/eastwest/img-230555617.jpg', 0, NOW()),
('서유견문', '유길준', '신원문화사 (2005)', '동서양', '/img/eastwest/img-231479138.jpg', 0, NOW()),
('파한집', '이인로', '신원문화사 (2002)', '동서양', '/img/eastwest/img-252720121.jpg', 0, NOW()),
('난중일기', '이순신', '민음사 (2010)', '동서양', '/img/eastwest/img-253643642.jpg', 0, NOW()),
('천변풍경', '박태원', '문학과지성사 (2005)', '동서양', '/img/eastwest/img-254567163.jpg', 0, NOW()),
('위대한 유산 1,2', '찰스 디킨스', '민음사 (2009)', '동서양', '/img/eastwest/img-255490684.jpg', 0, NOW()),
('닥터 지바고', '보리스 파스테르나크', '동서문화사 (2016)', '동서양', '/img/eastwest/img-256414205.jpg', 0, NOW()),
('변신/인형', '왕멍', '문학과지성사 (2004)', '동서양', '/img/eastwest/img-257337726.jpg', 0, NOW()),
('주홍글씨', '나다니엘 호손', '문예출판사 (2004)', '동서양', '/img/eastwest/img-258261247.jpg', 0, NOW()),
('걸리버여행기', '조너선 스위프트', '문학수첩 (2010)', '동서양', '/img/eastwest/img-259184768.jpg', 0, NOW()),
('모비딕', '허먼 멜빌', '작가정신 (2011)', '동서양', '/img/eastwest/img-260108289.jpg', 0, NOW()),
('픽션들', '호르헤 루이스 보르헤스', '민음사 (2011)', '동서양', '/img/eastwest/img-261031810.jpg', 0, NOW()),
('농담', '밀란 쿤데라', '민음사 (2011)', '동서양', '/img/eastwest/img-281349272.jpg', 0, NOW()),
('삼대', '염상섭', '문학과지성사 (2004)', '동서양', '/img/eastwest/img-282272793.jpg', 0, NOW()),
('당신들의 천국', '이청준', '문학과지성사 (2012)', '동서양', '/img/eastwest/img355451571.jpg', 0, NOW()),
('적과 흑', '스탕달', '동서문화사 (2016)', '동서양', '/img/eastwest/img356375092.jpg', 0, NOW()),
('장미의 이름(상·하)', '움베르토 에코', '열린책들 (2009)', '동서양', '/img/eastwest/img357298613.jpg', 0, NOW()); 

-- 서양 도서 데이터 삽입
INSERT INTO book (title, author, publisher, category, image_url, jokbo_count, created_at) VALUES
('플라톤의 국가', '플라톤', '서광사 (2005)', '서양', '/img/west/img-339531095.jpg', 0, NOW()),
('정치학', '아리스토텔레스', '솔 (2009)', '서양', '/img/west/img-340454616.jpg', 0, NOW()),
('키케로의 의무론', '키케로', '서광사 (2006)', '서양', '/img/west/img-342301658.jpg', 0, NOW()),
('성어거스틴의 고백록', '아우구스티누스', '대한기독교서회 (2003)', '서양', '/img/west/img-343225179.jpg', 0, NOW()),
('신기관', '베이컨', '한길사 (2001)', '서양', '/img/west/img-344148700.jpg', 0, NOW()),
('리바이어던', '홉스', '동서 (2009)', '서양', '/img/west/img-345072221.jpg', 0, NOW()),
('통치론', '로크', '까치 (2007)', '서양', '/img/west/img-345995742.jpg', 0, NOW()),
('사회계약론', '장 자크 루소', '펭귄클래식코리아 (2010)', '서양', '/img/west/img-346919263.jpg', 0, NOW()),
('존 스튜어트 밀 자유론', '밀', '서광사 (2008)', '서양', '/img/west/img-367236725.jpg', 0, NOW()),
('종교의 자연사', '흄', '아카넷 (2004)', '서양', '/img/west/img-368160246.jpg', 0, NOW()),
('프로테스탄트윤리와 자본주의 정신', '막스 베버', '문예출판사 (2010)', '서양', '/img/west/img-369083767.jpg', 0, NOW()),
('미국의 민주주의 1', '토크빌', '한길사 (1997)', '서양', '/img/west/img-370007288.jpg', 0, NOW()),
('역사란 무엇인가', 'E. H. 카', '까치 (2007)', '서양', '/img/west/img-370930809.jpg', 0, NOW()),
('도덕적 인간과 비도덕적 사회', '니버', '문예출판사 (1992)', '서양', '/img/west/img-371854330.jpg', 0, NOW()),
('소유냐 삶이냐', '에리히 프롬', '홍신문화사 (2007)', '서양', '/img/west/img-372777851.jpg', 0, NOW()),
('국화와 칼', '루스 베네딕트', '을유문화사 (2008)', '서양', '/img/west/img-373701372.jpg', 0, NOW()),
('슬픈 열대', '레비스트로스', '한길사 (1998)', '서양', '/img/west/img-374624893.jpg', 0, NOW()),
('자본주의, 사회주의, 민주주의', '슘페터', '한길사 (2011)', '서양', '/img/west/img-375548414.jpg', 0, NOW()),
('서양미술사', '곰브리치', '예경 (2003)', '서양', '/img/west/img-395865876.jpg', 0, NOW()),
('성의 역사 1', '푸코', '나남 (2004)', '서양', '/img/west/img-396789397.jpg', 0, NOW()),
('감시와 처벌', '미셸 푸코', '나남 (2016)', '서양', '/img/west/img-397712918.jpg', 0, NOW()),
('역사의 종말', '프랜시스 후쿠야마', '한마음사 (1992)', '서양', '/img/west/img-398636439.jpg', 0, NOW()),
('종교의 의미와 목적', '윌프레드 캔트웰 스미스', '분도출판사 (1991)', '서양', '/img/west/img-399559960.jpg', 0, NOW()),
('낭만적 거짓과 소설적 진실', '르네 지라르', '한길사 (2001)', '서양', '/img/west/img-400483481.jpg', 0, NOW()),
('오래된 미래', '헬레나 노르베리 호지', '중앙북스 (2015)', '서양', '/img/west/img-401407002.jpg', 0, NOW()),
('예루살렘의 아이히만', '한나 아렌트', '한길사 (2006)', '서양', '/img/west/img-402330523.jpg', 0, NOW()),
('호모루덴스', '호이징하', '까치글방 (1998)', '서양', '/img/west/img-404177565.jpg', 0, NOW()); 

-- 과학 도서 데이터 삽입
INSERT INTO book (title, author, publisher, category, image_url, jokbo_count, created_at) VALUES
('통섭', '장대익, 최재천', '사이언스북스 (2005)', '과학', '/img/science/img-677495277.jpg', 0, NOW()),
('종의 기원', '다윈', '동서문화사 (2009)', '과학', '/img/science/img2009126239.jpg', 0, NOW()),
('부분과 전체', '하이젠베르크', '지식산업사 (2005)', '과학', '/img/science/img2010973281.jpg', 0, NOW()),
('과학혁명의 구조', '쿤', '까치글방 (2005)', '과학', '/img/science/img2011896802.jpg', 0, NOW()),
('카오스', '제임스 글리크', '누림book (2006)', '과학', '/img/science/img2010049760.jpg', 0, NOW()),
('코스모스', '칼 세이건', '사이언스북스 (2010)', '과학', '/img/science/img2014667365.jpg', 0, NOW()),
('총, 균, 쇠', '제레드 다이아몬드', '문학사상사 (2013)', '과학', '/img/science/img2015590886.jpg', 0, NOW()),
('엔트로피', '제레미 리프킨', '세종연구원 (2015)', '과학', '/img/science/img2013743844.jpg', 0, NOW()),
('객관성의 칼날', '찰스 길리스피', '새물결 (2005)', '과학', '/img/science/img2012820323.jpg', 0, NOW()),
('같기도 하고 아니 같기도 하고', '로얼드 호프만', '까치 (1996)', '과학', '/img/science/img2016514407.jpg', 0, NOW()); 

-- 더비 데이터 생성 스크립트

-- 문의 데이터 20개 생성
INSERT INTO inquiry (name, email, message, is_public, created_at) VALUES
('김학생', 'student1@test.com', '족보 업로드 기능이 잘 작동하지 않습니다. 파일 업로드 시 오류가 발생해요.', true, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('이대학생', 'student2@test.com', '특정 책의 족보를 찾을 수 없습니다. 검색 기능을 개선해주세요.', true, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('박연구원', 'researcher1@test.com', '동양 고전 관련 족보가 부족합니다. 더 많은 자료를 추가해주세요.', false, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('최교수', 'professor1@test.com', '족보 품질 관리가 필요합니다. 부정확한 정보가 많이 있어요.', true, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('정학부생', 'student3@test.com', '모바일에서 사이트 접속이 잘 안 됩니다. 반응형 디자인을 개선해주세요.', true, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('한대학원생', 'gradstudent1@test.com', 'PDF 뷰어 기능이 제대로 작동하지 않습니다. 다른 형식도 지원해주세요.', false, DATE_SUB(NOW(), INTERVAL 6 DAY)),
('윤연구원', 'researcher2@test.com', '서양 철학 관련 족보가 필요합니다. 플라톤, 아리스토텔레스 등 추가해주세요.', true, DATE_SUB(NOW(), INTERVAL 7 DAY)),
('임박사', 'phd1@test.com', '족보 검색 시 필터링 기능이 부족합니다. 카테고리별 검색을 추가해주세요.', true, DATE_SUB(NOW(), INTERVAL 8 DAY)),
('조학생', 'student4@test.com', '족보 다운로드 시 파일명이 이상하게 나옵니다. 원본 파일명을 유지해주세요.', false, DATE_SUB(NOW(), INTERVAL 9 DAY)),
('강대학생', 'student5@test.com', '족보 업로드 시 파일 크기 제한이 너무 작습니다. 더 큰 파일도 업로드할 수 있게 해주세요.', true, DATE_SUB(NOW(), INTERVAL 10 DAY)),
('신연구원', 'researcher3@test.com', '과학 관련 족보가 거의 없습니다. 물리학, 화학, 생물학 등 추가해주세요.', true, DATE_SUB(NOW(), INTERVAL 11 DAY)),
('오교수', 'professor2@test.com', '족보 품질 평가 시스템을 도입해주세요. 사용자들이 족보의 품질을 평가할 수 있게 해주세요.', false, DATE_SUB(NOW(), INTERVAL 12 DAY)),
('유학부생', 'student6@test.com', '족보 댓글 기능을 추가해주세요. 다른 학생들과 의견을 나눌 수 있게 해주세요.', true, DATE_SUB(NOW(), INTERVAL 13 DAY)),
('백대학원생', 'gradstudent2@test.com', '족보 북마크 기능이 필요합니다. 나중에 다시 볼 족보를 저장할 수 있게 해주세요.', true, DATE_SUB(NOW(), INTERVAL 14 DAY)),
('남연구원', 'researcher4@test.com', '족보 통계 기능을 추가해주세요. 어떤 족보가 가장 많이 다운로드되는지 알 수 있게 해주세요.', false, DATE_SUB(NOW(), INTERVAL 15 DAY)),
('문박사', 'phd2@test.com', '족보 공유 기능을 개선해주세요. SNS에 공유할 수 있게 해주세요.', true, DATE_SUB(NOW(), INTERVAL 16 DAY)),
('양학생', 'student7@test.com', '족보 프린트 기능이 필요합니다. 인쇄할 때 깔끔하게 나오도록 해주세요.', true, DATE_SUB(NOW(), INTERVAL 17 DAY)),
('구대학생', 'student8@test.com', '족보 알림 기능을 추가해주세요. 새로운 족보가 업로드되면 알림을 받을 수 있게 해주세요.', false, DATE_SUB(NOW(), INTERVAL 18 DAY)),
('라연구원', 'researcher5@test.com', '족보 버전 관리 기능이 필요합니다. 같은 족보의 여러 버전을 관리할 수 있게 해주세요.', true, DATE_SUB(NOW(), INTERVAL 19 DAY)),
('하교수', 'professor3@test.com', '족보 데이터베이스 구조를 개선해주세요. 더 효율적인 검색이 가능하도록 해주세요.', true, DATE_SUB(NOW(), INTERVAL 20 DAY));

-- 족보 데이터 15개 생성 (첫 번째 책 '성학십도'에 대한 족보)
INSERT INTO jokbo (book_id, uploader_name, content_url, content, content_type, comment, status, created_at) VALUES
(1, '김철수', 'jokbo_001.pdf', '성학십도는 조선시대 이황이 지은 교육서로, 성리학의 핵심 개념을 담고 있습니다. 이 책은 인간의 본성과 교육의 중요성을 강조하며, 수신제가치국평천하의 도리를 설명합니다. 주요 내용으로는 격물치지, 성의정심, 수신제가 등이 포함되어 있습니다.', 'text', '성학십도 핵심 요약 및 정리', '승인', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, '이영희', 'jokbo_002.pdf', '성학십도의 주요 개념들을 정리하면 다음과 같습니다:\n\n1. 격물치지: 사물의 이치를 궁구하여 지식을 확장하는 것\n2. 성의정심: 마음을 바르게 하여 의지를 정직하게 하는 것\n3. 수신제가: 몸을 닦아 집안을 다스리는 것\n4. 치국평천하: 나라를 다스려 천하를 평안하게 하는 것\n\n이러한 개념들은 유학의 실천적 가치를 보여줍니다.', 'text', '성학십도 주요 개념 정리', '승인', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, '박민수', 'jokbo_003.jpg', NULL, 'file', '성학십도 시험 대비 족보', '승인', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, '최지영', 'jokbo_004.pdf', '성학십도를 읽고 난 후의 독후감입니다. 이 책은 단순한 교육서가 아니라 인간이 살아가야 할 올바른 길을 제시하는 철학서입니다. 특히 현대 사회에서도 여전히 유효한 가치관과 삶의 태도를 담고 있어 깊은 감명을 받았습니다. 이황의 인간에 대한 깊은 이해와 교육에 대한 진정성 있는 접근이 돋보입니다.', 'text', '성학십도 독후감 및 분석', '승인', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(1, '정현우', 'jokbo_005.png', NULL, 'file', '성학십도 핵심 문장 정리', '승인', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, '한소영', 'jokbo_006.pdf', '성학십도의 시대적 배경과 의미를 분석해보면, 조선 중기라는 특정한 역사적 상황에서 지어진 책임에도 불구하고 보편적 가치를 담고 있습니다. 당시 사림파의 정치적 입장과 성리학의 학문적 발전이 이 책에 반영되어 있으며, 교육을 통한 사회 개혁의 의지가 드러납니다.', 'text', '성학십도 배경 및 시대적 의미', '승인', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(1, '윤준호', 'jokbo_007.pdf', '성학십도를 현대적 관점에서 해석해보면, 다음과 같은 의미를 찾을 수 있습니다:\n\n1. 자기 수양의 중요성: 현대 사회에서도 개인의 도덕적 성장이 필요\n2. 사회적 책임: 개인의 수양이 사회 발전으로 이어지는 가치\n3. 교육의 본질: 지식 전달이 아닌 인격 형성에 중점\n4. 실천적 가치: 이론이 아닌 실천을 통한 가치 실현\n\n이러한 해석은 현대 교육에도 시사하는 바가 큽니다.', 'text', '성학십도 현대적 해석', '승인', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(1, '임수진', 'jokbo_008.jpg', NULL, 'file', '성학십도 주요 인물 정리', '승인', DATE_SUB(NOW(), INTERVAL 8 DAY)),
(1, '조영수', 'jokbo_009.pdf', '성학십도의 철학적 의미를 분석하면, 인간의 본성에 대한 낙관적 견해와 교육을 통한 완성 가능성에 대한 믿음이 핵심입니다. 이는 맹자의 성선설을 바탕으로 하며, 적절한 교육과 수양을 통해 누구나 성인이 될 수 있다는 유교적 이상을 제시합니다. 또한 지식과 실천의 통합을 강조하여 이론과 실천의 조화를 추구합니다.', 'text', '성학십도 철학적 의미 분석', '승인', DATE_SUB(NOW(), INTERVAL 9 DAY)),
(1, '강미영', 'jokbo_010.pdf', '성학십도의 교육적 가치는 다음과 같습니다:\n\n1. 인격 교육의 중시: 지식보다 인격 형성을 우선시\n2. 단계적 교육: 격물치지에서 치국평천하까지 체계적 접근\n3. 실천적 교육: 이론과 실천의 결합 강조\n4. 사회적 책임: 개인 수양이 사회 발전으로 연결되는 가치\n5. 평생 교육: 끊임없는 자기 수양의 중요성\n\n이러한 가치들은 현대 교육에도 중요한 시사점을 제공합니다.', 'text', '성학십도 교육적 가치', '승인', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(1, '신동현', 'jokbo_011.png', NULL, 'file', '성학십도 비교 분석', '승인', DATE_SUB(NOW(), INTERVAL 11 DAY)),
(1, '오지은', 'jokbo_012.pdf', '성학십도의 현대 교육에의 적용 가능성을 살펴보면, 다음과 같은 측면에서 유용합니다:\n\n1. 인성 교육 강화: 지식 교육과 함께 인격 교육의 중요성\n2. 실천 중심 교육: 이론과 실천의 조화로운 교육\n3. 사회적 책임감 함양: 개인 수양이 사회 발전으로 이어지는 가치\n4. 평생학습의 중요성: 끊임없는 자기 수양과 발전\n5. 단계적 교육 방법: 체계적이고 단계적인 교육 접근\n\n이러한 적용은 현대 교육의 문제점을 해결하는 데 도움이 될 수 있습니다.', 'text', '성학십도 현대 교육에의 적용', '승인', DATE_SUB(NOW(), INTERVAL 12 DAY)),
(1, '유준영', 'jokbo_013.jpg', NULL, 'file', '성학십도 핵심 용어 정리', '승인', DATE_SUB(NOW(), INTERVAL 13 DAY)),
(1, '백수진', 'jokbo_014.pdf', '성학십도가 지어진 조선 중기의 시대적 배경을 살펴보면, 사림파의 등장과 성리학의 발전이라는 중요한 변화가 있었습니다. 이 시기는 정치적 혼란과 함께 새로운 사회 질서를 모색하던 시기였으며, 교육을 통한 사회 개혁의 의지가 강하게 나타났습니다. 이황은 이러한 시대적 요구에 응답하여 성학십도를 통해 올바른 교육 방향을 제시했습니다.', 'text', '성학십도 시대적 배경', '승인', DATE_SUB(NOW(), INTERVAL 14 DAY)),
(1, '남영수', 'jokbo_015.pdf', '성학십도의 종합적 정리를 통해 이 책의 전체적인 구조와 의미를 파악할 수 있습니다. 이 책은 단순한 교육서가 아니라 인간의 완성과 사회의 발전을 위한 철학적 가이드라인을 제시합니다. 격물치지에서 시작하여 치국평천하에 이르는 단계적 접근은 개인에서 사회로, 이론에서 실천으로 나아가는 유교적 이상을 보여줍니다.', 'text', '성학십도 종합 정리', '승인', DATE_SUB(NOW(), INTERVAL 15 DAY));

-- 책의 족보 수 업데이트
UPDATE book SET jokbo_count = 15 WHERE book_id = 1;

-- 족보 승인 이력 테이블 데이터 추가 (기존 족보들에 대한 승인 이력)
INSERT INTO jokbo_approval_history (jokbo_id, admin_id, action, previous_status, new_status, comment, created_at) VALUES
(1, 1, '승인', '대기', '승인', '내용이 체계적으로 잘 정리되어 있어 승인합니다.', DATE_SUB(NOW(), INTERVAL 23 HOUR)),
(2, 1, '승인', '대기', '승인', '주요 개념들이 명확하게 정리되어 승인합니다.', DATE_SUB(NOW(), INTERVAL 22 HOUR)),
(3, 1, '승인', '대기', '승인', '시험 대비에 유용한 자료로 판단되어 승인합니다.', DATE_SUB(NOW(), INTERVAL 21 HOUR)),
(4, 1, '승인', '대기', '승인', '독후감과 분석이 잘 작성되어 있어 승인합니다.', DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(5, 1, '승인', '대기', '승인', '핵심 문장들이 잘 정리되어 있어 승인합니다.', DATE_SUB(NOW(), INTERVAL 19 HOUR)),
(6, 1, '승인', '대기', '승인', '시대적 배경 분석이 훌륭하여 승인합니다.', DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(7, 1, '승인', '대기', '승인', '현대적 해석이 적절하여 승인합니다.', DATE_SUB(NOW(), INTERVAL 17 HOUR)),
(8, 1, '승인', '대기', '승인', '주요 인물 정리가 체계적이어서 승인합니다.', DATE_SUB(NOW(), INTERVAL 16 HOUR)),
(9, 1, '승인', '대기', '승인', '철학적 의미 분석이 깊이 있어 승인합니다.', DATE_SUB(NOW(), INTERVAL 15 HOUR)),
(10, 1, '승인', '대기', '승인', '교육적 가치가 잘 설명되어 있어 승인합니다.', DATE_SUB(NOW(), INTERVAL 14 HOUR)),
(11, 1, '승인', '대기', '승인', '비교 분석 자료가 유용하여 승인합니다.', DATE_SUB(NOW(), INTERVAL 13 HOUR)),
(12, 1, '승인', '대기', '승인', '현대 교육에의 적용 방안이 잘 제시되어 승인합니다.', DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(13, 1, '승인', '대기', '승인', '핵심 용어 정리가 체계적이어서 승인합니다.', DATE_SUB(NOW(), INTERVAL 11 HOUR)),
(14, 1, '승인', '대기', '승인', '시대적 배경 설명이 상세하여 승인합니다.', DATE_SUB(NOW(), INTERVAL 10 HOUR)),
(15, 1, '승인', '대기', '승인', '종합적 정리가 완성도 높아 승인합니다.', DATE_SUB(NOW(), INTERVAL 9 HOUR));

-- 새로운 승인 대기 중인 족보들 (다양한 상태를 보여주기 위해)
INSERT INTO jokbo (book_id, uploader_name, content_url, content, content_type, comment, status, created_at) VALUES
(2, '테스트사용자1', NULL, '북학의는 박제가가 저술한 실학서로, 조선 후기의 진보적 사상을 담고 있습니다. 이 책은 중국의 선진 문물을 적극 수용해야 한다는 북학론을 주장하며, 기술과 상업의 발달을 통한 부국강병을 추구했습니다.', 'text', '북학의 기본 개념 정리', '대기', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(3, '테스트사용자2', 'test_file_1.pdf', NULL, 'file', '조선상고사 핵심 내용 요약', '대기', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(4, '테스트사용자3', NULL, '삼국유사는 일연이 편찬한 역사서로, 정사에서 다루지 않은 야사와 설화를 포함하고 있습니다.', 'text', '삼국유사 주요 내용 간단 정리', '대기', DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(1, '테스트사용자4', NULL, '성학십도 재시험 대비 자료입니다. 하지만 내용이 부정확하고 오류가 많아 보입니다.', 'text', '성학십도 재시험 대비 (품질 낮음)', '대기', DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(5, '테스트사용자5', 'test_file_2.jpg', NULL, 'file', '논어 핵심 구절 모음', '대기', DATE_SUB(NOW(), INTERVAL 5 HOUR));

-- 반려된 족보 예시 (관리자가 반려 처리)
INSERT INTO jokbo (book_id, uploader_name, content_url, content, content_type, comment, status, created_at) VALUES
(1, '부적절사용자', NULL, '이 내용은 전혀 관련이 없는 내용입니다. 성학십도와 무관한 개인적인 일기를 올렸습니다.', 'text', '관련 없는 내용', '반려', DATE_SUB(NOW(), INTERVAL 6 HOUR));

-- 반려 이력 추가
INSERT INTO jokbo_approval_history (jokbo_id, admin_id, action, previous_status, new_status, comment, created_at) VALUES
(21, 1, '반려', '대기', '반려', '성학십도와 관련이 없는 내용으로 반려합니다. 관련 있는 내용으로 다시 제출해주세요.', DATE_SUB(NOW(), INTERVAL 6 HOUR));

-- 승인 취소 예시 (승인했다가 다시 취소한 경우)
INSERT INTO jokbo (book_id, uploader_name, content_url, content, content_type, comment, status, created_at) VALUES
(1, '재검토사용자', NULL, '성학십도 내용입니다만, 사실 확인이 필요한 내용이 포함되어 있습니다.', 'text', '성학십도 심화 분석', '대기', DATE_SUB(NOW(), INTERVAL 8 HOUR));

-- 승인 후 취소 이력 
INSERT INTO jokbo_approval_history (jokbo_id, admin_id, action, previous_status, new_status, comment, created_at) VALUES
(22, 1, '승인', '대기', '승인', '초기 검토에서 내용이 적절하다고 판단하여 승인합니다.', DATE_SUB(NOW(), INTERVAL 7 HOUR, 30 MINUTE)),
(22, 2, '승인취소', '승인', '대기', '추가 검토 결과 일부 내용의 정확성에 의문이 있어 재검토를 위해 승인을 취소합니다.', DATE_SUB(NOW(), INTERVAL 7 HOUR));
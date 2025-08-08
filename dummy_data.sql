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

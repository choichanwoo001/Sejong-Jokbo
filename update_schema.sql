-- Jokbo 테이블에 content 컬럼 추가
ALTER TABLE jokbo ADD COLUMN content TEXT COMMENT '텍스트 족보 내용';

-- content_url 컬럼을 nullable로 변경 (텍스트 족보의 경우 파일이 없을 수 있음)
ALTER TABLE jokbo MODIFY COLUMN content_url VARCHAR(500) NULL;

-- Book 테이블에 jokbo_count 컬럼 추가 (없는 경우)
ALTER TABLE book ADD COLUMN jokbo_count INT DEFAULT 0 COMMENT '족보 개수';

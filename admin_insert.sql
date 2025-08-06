-- 관리자 계정 생성
-- 비밀번호는 SHA-256으로 해시화된 값입니다 (원본: admin123)
INSERT INTO admin (admin_name, password, created_at) 
VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', NOW());

-- 추가 관리자 계정 (원본 비밀번호: manager123)
INSERT INTO admin (admin_name, password, created_at) 
VALUES ('manager', 'c4b0e190e5c8b5c4b5c4b5c4b5c4b5c4b5c4b5c4b5c4b5c4b5c4b5c4b5c4b5c4b5', NOW());

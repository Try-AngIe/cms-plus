use cmsplusmain;

-- Vendor 데이터 삽입
INSERT INTO vendor (deleted, created_datetime, vendor_phone, vendor_username, vendor_dept, vendor_name, vendor_email, vendor_password, user_role)
VALUES
    (0, NOW(), '01012345678', 'vendor1', '영업부', '김판매', 'vendor1@example.com', 'password123', 'ROLE_VENDOR'),
    (0, NOW(), '01023456789', 'vendor2', '마케팅부', '이마케팅', 'vendor2@example.com', 'password456', 'ROLE_VENDOR'),
    (0, NOW(), '01034567890', 'vendor3', '고객지원부', '박지원', 'vendor3@example.com', 'password789', 'ROLE_VENDOR');

-- Member 데이터 삽입
INSERT INTO member (deleted, member_auto_billing, member_auto_invoice_send, member_enroll_date, created_datetime, vendor_id, member_phone, member_name, member_email, member_invoice_send_method, member_status)
VALUES
    (0, 1, 1, '2024-01-01', NOW(), 1, '01098765432', '학생1', 'student1@example.com', 'EMAIL', 'ENABLED'),
    (0, 1, 1, '2024-01-02', NOW(), 1, '01087654321', '학생2', 'student2@example.com', 'SMS', 'ENABLED'),
    (0, 1, 1, '2024-01-03', NOW(), 2, '01076543210', '학생3', 'student3@example.com', 'EMAIL', 'ENABLED'),
    (0, 1, 1, '2024-01-04', NOW(), 2, '01065432109', '학생4', 'student4@example.com', 'SMS', 'ENABLED'),
    (0, 1, 1, '2024-01-05', NOW(), 3, '01054321098', '학생5', 'student5@example.com', 'EMAIL', 'ENABLED');

-- Payment 데이터 삽입
INSERT INTO payment (deleted, created_datetime, payment_type, payment_consent_status, payment_status)
VALUES
    (0, NOW(), 'AUTO', 'ACCEPT', 'ENABLED'),
    (0, NOW(), 'AUTO', 'ACCEPT', 'ENABLED'),
    (0, NOW(), 'AUTO', 'ACCEPT', 'ENABLED'),
    (0, NOW(), 'AUTO', 'ACCEPT', 'ENABLED'),
    (0, NOW(), 'AUTO', 'ACCEPT', 'ENABLED');

-- Contract 데이터 삽입
INSERT INTO contract (contract_day, contract_end_date, contract_start_date, deleted, created_datetime, member_id, payment_id, vendor_id, contract_name, contract_status)
VALUES
    (1, '2024-12-31', '2024-01-01', 0, NOW(), 1, 1, 1, '1년 정기 계약', 'ENABLED'),
    (15, '2024-12-31', '2024-01-15', 0, NOW(), 2, 2, 1, '11개월 계약', 'ENABLED'),
    (1, '2024-06-30', '2024-01-01', 0, NOW(), 3, 3, 2, '6개월 계약', 'ENABLED'),
    (1, '2024-12-31', '2024-01-01', 0, NOW(), 4, 4, 2, '1년 특별 계약', 'ENABLED'),
    (1, '2024-12-31', '2024-01-01', 0, NOW(), 5, 5, 3, '1년 표준 계약', 'ENABLED');

-- Product 데이터 삽입
INSERT INTO product (deleted, product_price, created_datetime, vendor_id, product_name, product_status)
VALUES
    (0, 100000, NOW(), 1, '기본 강좌', 'ENABLED'),
    (0, 150000, NOW(), 1, '심화 강좌', 'ENABLED'),
    (0, 80000, NOW(), 2, '온라인 강좌', 'ENABLED'),
    (0, 200000, NOW(), 2, '1:1 튜터링', 'ENABLED'),
    (0, 120000, NOW(), 3, '그룹 스터디', 'ENABLED');

-- Billing_standard 데이터 삽입
INSERT INTO billing_standard (billing_standard_contract_day, deleted, contract_id, created_datetime, billing_standard_status, billing_standard_type)
VALUES
    (1, 0, 1, NOW(), 'ENABLED', 'REGULAR'),
    (15, 0, 2, NOW(), 'ENABLED', 'REGULAR'),
    (1, 0, 3, NOW(), 'ENABLED', 'REGULAR'),
    (1, 0, 4, NOW(), 'ENABLED', 'REGULAR'),
    (1, 0, 5, NOW(), 'ENABLED', 'REGULAR');

-- Billing 데이터 삽입
INSERT INTO billing (billing_date, deleted, billing_standard_id, created_datetime, billing_status)
VALUES
    ('2024-02-01', 0, 1, NOW(), 'CREATED'),
    ('2024-02-15', 0, 2, NOW(), 'CREATED'),
    ('2024-02-01', 0, 3, NOW(), 'CREATED'),
    ('2024-02-01', 0, 4, NOW(), 'CREATED'),
    ('2024-02-01', 0, 5, NOW(), 'CREATED');

-- Billing_product 데이터 삽입
INSERT INTO billing_product (billing_product_price, billing_product_quantity, deleted, billing_standard_id, created_datetime, product_id)
VALUES
    (100000, 1, 0, 1, NOW(), 1),  -- 기본 강좌
    (150000, 1, 0, 1, NOW(), 2),  -- 심화 강좌
    (80000, 1, 0, 2, NOW(), 3),   -- 온라인 강좌
    (200000, 1, 0, 2, NOW(), 4),  -- 1:1 튜터링
    (120000, 1, 0, 3, NOW(), 5),  -- 그룹 스터디
    (100000, 1, 0, 3, NOW(), 1),  -- 기본 강좌
    (150000, 1, 0, 4, NOW(), 2),  -- 심화 강좌
    (200000, 1, 0, 4, NOW(), 4),  -- 1:1 튜터링
    (80000, 2, 0, 5, NOW(), 3),   -- 온라인 강좌 (2개)
    (120000, 1, 0, 5, NOW(), 5);  -- 그룹 스터디

-- payment_method_info 데이터 삽입
INSERT INTO payment_method_info (deleted, created_datetime, payment_method_info_id, payment_method_info_method)
VALUES
    (0, NOW(), 1, 'CARD'),
    (0, NOW(), 2, 'CMS'),
    (0, NOW(), 3, 'CMS');

-- auto_payment 데이터 삽입
INSERT INTO auto_payment (payment_id, payment_method_info_id, payment_simpconsent_request_date, payment_consent_img_url, payment_sign_img_url)
VALUES
    (1, 1, '2024-01-15 10:00:00', 'https://example.com/consent1.jpg', 'https://example.com/sign1.jpg'),
    (2, 2, '2024-01-16 11:00:00', 'https://example.com/consent2.jpg', 'https://example.com/sign2.jpg'),
    (3, 3, '2024-01-17 12:00:00', 'https://example.com/consent3.jpg', 'https://example.com/sign3.jpg');

-- buyer_payment 데이터 삽입
INSERT INTO buyer_payment (payment_id)
VALUES (4), (5);

-- buyer_payment_method 데이터 삽입
INSERT INTO buyer_payment_method (buyer_payment_payment_id, buyer_payment_method)
VALUES
    (4, 'CARD'),
    (4, 'ACCOUNT');



-- card_payment 데이터 삽입
INSERT INTO card_payment (card_info_owner_birth, payment_method_info_id, card_info_number, card_info_owner)
VALUES
    ('1990-01-01', 1, '1234-5678-9012-3456', '홍길동');

-- cms_payment 데이터 삽입
INSERT INTO cms_payment (cms_owner_birth, payment_method_info_id, cms_account_number, cms_account_owner, cms_account_bank)
VALUES
    ('1995-05-05', 2, '987654321', '김철수', '088'),
    ('1995-05-05', 3, '987654321', '김철수', '004');

-- setting_simpconsent 데이터 삽입
INSERT INTO setting_simpconsent (deleted, created_datetime)
VALUES
    (0, NOW()),
    (0, NOW()),
    (0, NOW());

-- simpconsent_vendor_auto_payment_method 데이터 삽입
INSERT INTO simpconsent_vendor_auto_payment_method (simp_consent_setting_setting_simpconsent_id, simpconsent_auto_payment_method)
VALUES
    (1, 'CARD'),
    (1, 'CMS'),
    (2, 'CMS'),
    (3, 'CARD');

-- contract_product 데이터 삽입
INSERT INTO contract_product (contract_product_price, contract_product_quantity, deleted, contract_id, created_datetime, product_id)
VALUES
    (100000, 1, 0, 1, NOW(), 1),
    (150000, 1, 0, 2, NOW(), 2),
    (80000, 2, 0, 3, NOW(), 3),
    (200000, 1, 0, 4, NOW(), 4),
    (120000, 1, 0, 5, NOW(), 5);

-- simpconsent_vendor_product 데이터 삽입
INSERT INTO simpconsent_vendor_product (product_id, setting_simpconsent_id)
VALUES
    (1, 1),
    (2, 1),
    (3, 2),
    (4, 2),
    (5, 3);

-- virtual_account_payment 데이터 삽입
INSERT INTO virtual_account_payment (payment_id, virtual_payment_account_number, virtual_payment_bank_code, virtual_payment_account_owner)
VALUES
    (5, '1234567890123', '088', '이가상');
-- Seed cards (only insert if table is empty)
INSERT INTO cards (card_name, last_four_digits, expiry_date, overseas_enabled, daily_limit, balance, status)
SELECT * FROM (SELECT 'Premier World Card' AS card_name, '4521' AS last_four_digits, '2027-12-31' AS expiry_date, true AS overseas_enabled, 5000.00 AS daily_limit, 15000.00 AS balance, 'ACTIVE' AS status) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM cards LIMIT 1);

INSERT INTO cards (card_name, last_four_digits, expiry_date, overseas_enabled, daily_limit, balance, status)
SELECT * FROM (SELECT 'Debit Card', '8834', '2025-06-30', false, 2000.00, 800.00, 'ACTIVE') AS tmp
WHERE (SELECT COUNT(*) FROM cards) = 1;

INSERT INTO cards (card_name, last_four_digits, expiry_date, overseas_enabled, daily_limit, balance, status)
SELECT * FROM (SELECT 'Expired Card', '1102', '2024-01-15', true, 3000.00, 5000.00, 'EXPIRED') AS tmp
WHERE (SELECT COUNT(*) FROM cards) = 2;

INSERT INTO cards (card_name, last_four_digits, expiry_date, overseas_enabled, daily_limit, balance, status)
SELECT * FROM (SELECT 'Frozen Card', '7766', '2028-03-20', true, 4000.00, 10000.00, 'FROZEN') AS tmp
WHERE (SELECT COUNT(*) FROM cards) = 3;

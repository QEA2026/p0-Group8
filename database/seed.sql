/*
    We can use this file for test data I havent done anything yet :D
*/

-- 1. Wipe out test data from before in reverse dependency order
-- DELETE FROM approvals;
-- DELETE FROM expenses;
-- DELETE FROM users;
-- 1.1 Table deletion handled by schema.sql

-- 2. Insert test users
-- INSERT INTO users (username, password, role) VALUES
-- ('brian', 'password','Employee'),
-- ('landon', 'passwerd','Employee'),
-- ('siri', 'password','Manager');
-- 2.1 This section is now handled dynamically by the service layer, so no static inserts are needed


-- 3. Insert Expenses
INSERT INTO expenses (userId, amount, description, category, date) VALUES
(2, 199.99, 'A desk','Utilities', '2026-06-19'),
(1, 57.77, 'Poke-buzz-balls', 'Food', '2026-06-20'),
(2, 23.88, 'Chipotle lunch w/ guacamole', 'Food', '2026-06-23'),
(1, 49.99, 'Database streaming service expense', 'Entertainment', '2026-06-24');


--4. Insert Approvals
INSERT INTO approvals (expenseId, status, reviewer, comment, review_date) VALUES
(1, 'approved', 3, 'Approved, needs a desk to work at work.', '2026-06-19'),
(2,'pending', NULL, NULL, NULL ),
(3, 'denied', 3, 'Denied. Missing itemized receipt.', '2026-06-24'),
(4,'approved',3, 'Approved, cannot miss the latest Data Island episodes.', '2026-06-24');
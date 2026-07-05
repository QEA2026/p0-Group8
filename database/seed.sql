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

INSERT INTO users (id, username, password, role) VALUES
    (1, 'manager1', 'managerpass', 'manager'),
    (2, 'manager2', 'managerpass2', 'manager'),
    (3, 'alice', 'alicepass', 'employee'),
    (4, 'bob', 'bobpass', 'employee'),
    (5, 'carol', 'carolpass', 'employee');

INSERT INTO expenses (id, userId, amount, description, category, date) VALUES
    (1, 3, 125.40, 'Client lunch with receipts', 'MEALS', '2026-06-10'),
    (2, 3, 480.00, 'Hotel stay for conference', 'TRAVEL', '2026-06-12'),
    (3, 4, 89.99, 'Printer ink refill', 'OFFICE_SUPPLIES', '2026-06-15'),
    (4, 4, 42.50, 'Team coffee meeting', 'MEALS', '2026-06-18'),
    (5, 5, 310.25, 'Rideshare to airport and hotel', 'TRAVEL', '2026-06-21'),
    (6, 5, 199.95, 'Monitor arm for workstation', 'EQUIPMENT', '2026-06-25');

INSERT INTO approvals (id, expenseId, status, reviewer, comment, review_date) VALUES
    (1, 1, 'pending', NULL, NULL, NULL),
    (2, 2, 'approved', 1, 'Conference travel approved.', '2026-06-13'),
    (3, 3, 'pending', NULL, NULL, NULL),
    (4, 4, 'denied', 2, 'Missing itemized receipt.', '2026-06-19'),
    (5, 5, 'pending', NULL, NULL, NULL),
    (6, 6, 'approved', 1, 'Necessary workstation upgrade.', '2026-06-26');
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

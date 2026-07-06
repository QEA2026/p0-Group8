INSERT INTO expenses (userId, amount, description, category, date) VALUES
((SELECT id FROM users WHERE username = 'alice'), 125.40, 'Client lunch with receipts', 'MEALS', '2026-06-10'),
((SELECT id FROM users WHERE username = 'alice'), 480.00, 'Hotel stay for conference', 'TRAVEL', '2026-06-12'),
((SELECT id FROM users WHERE username = 'bob'), 89.99, 'Printer ink refill', 'OFFICE_SUPPLIES', '2026-06-15'),
((SELECT id FROM users WHERE username = 'bob'), 42.50, 'Team coffee meeting', 'MEALS', '2026-06-18'),
((SELECT id FROM users WHERE username = 'carol'), 310.25, 'Rideshare to airport and hotel', 'TRAVEL', '2026-06-21'),
((SELECT id FROM users WHERE username = 'carol'), 199.95, 'Monitor arm for workstation', 'EQUIPMENT', '2026-06-25'),
((SELECT id FROM users WHERE username = 'alice'), 59.99, 'Online training course', 'TRAINING', '2026-06-27'),
((SELECT id FROM users WHERE username = 'bob'), 24.99, 'Project software subscription', 'SOFTWARE', '2026-06-28');

INSERT INTO approvals (expenseId, status, reviewer, comment, review_date) VALUES
(1, 'pending', NULL, NULL, NULL),
(2, 'approved', (SELECT id FROM users WHERE username = 'manager1'), 'Conference travel approved.', '2026-06-13'),
(3, 'pending', NULL, NULL, NULL),
(4, 'denied', (SELECT id FROM users WHERE username = 'manager2'), 'Missing itemized receipt.', '2026-06-19'),
(5, 'pending', NULL, NULL, NULL),
(6, 'approved', (SELECT id FROM users WHERE username = 'manager1'), 'Necessary workstation upgrade.', '2026-06-26'),
(7, 'denied', (SELECT id FROM users WHERE username = 'manager1'), 'Training was not pre-approved.', '2026-06-28'),
(8, 'pending', NULL, NULL, NULL);
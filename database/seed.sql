-- ==========================
-- EXPENSES
-- ==========================

INSERT INTO expenses (userId, amount, description, category, date) VALUES
(1, 23.75, 'Lunch with client', 'MEALS', '2026-07-01'),
(1, 145.00, 'Hotel for technology conference', 'LODGING', '2026-07-02'),
(1, 49.99, 'Wireless mouse for workstation', 'EQUIPMENT', '2026-07-03'),
(1, 18.40, 'Airport parking', 'TRAVEL', '2026-07-04'),

(2, 82.15, 'Printer paper and office supplies', 'OFFICE_SUPPLIES', '2026-07-02'),
(2, 299.99, 'Adobe Creative Cloud subscription', 'SOFTWARE', '2026-07-03'),
(2, 12.80, 'Coffee meeting with client', 'MEALS', '2026-07-04'),
(2, 525.00, 'AWS Cloud Practitioner training', 'TRAINING', '2026-07-05');

-- ==========================
-- APPROVALS
-- reviewer = 3 (Siri)
-- ==========================

INSERT INTO approvals (expenseId, status, reviewer, comment, review_date) VALUES
(1, 'approved', 3, 'Approved. Business expense verified.', '2026-07-02'),
(2, 'approved', 3, 'Conference travel approved.', '2026-07-03'),
(3, 'denied', 3, 'Please provide a more detailed business justification.', '2026-07-04');

INSERT INTO approvals (expenseId, status) VALUES
(4, 'pending');

INSERT INTO approvals (expenseId, status, reviewer, comment, review_date) VALUES
(5, 'approved', 3, 'Office supplies approved.', '2026-07-03');

INSERT INTO approvals (expenseId, status) VALUES
(6, 'pending');

INSERT INTO approvals (expenseId, status, reviewer, comment, review_date) VALUES
(7, 'denied', 3, 'Meal exceeded reimbursement policy.', '2026-07-05');

INSERT INTO approvals (expenseId, status) VALUES
(8, 'pending');

/* 
schema.sql contains the users table, expenses table, and approvals table

the way I thought about it is that a user will submit an expense right and then 
recieved by approvals so thats how the relationships work
*/

DROP TABLE IF EXISTS approvals;
DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT, -- sql auto generates the ids
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL
);

CREATE TABLE expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userId INTEGER NOT NULL,
    amount REAL NOT NULL,
    description TEXT NOT NULL,
    category TEXT NOT NULL,
    date TEXT NOT NULL,
    FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE approvals (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    expenseId INTEGER NOT NULL,
    status TEXT NOT NULL, -- pending, approved or denied?
    reviewer INTEGER,
    comment TEXT,
    review_date TEXT,
    FOREIGN KEY (expenseId) REFERENCES expenses(id),
    FOREIGN KEY (reviewer) REFERENCES users(id)
);
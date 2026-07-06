import sqlite3
from pathlib import Path

import pytest

from main import app as flask_app


ROOT_DIR = Path(__file__).resolve().parents[2]
SCHEMA_PATH = ROOT_DIR / "database" / "schema.sql"


@pytest.fixture()
def app():
    flask_app.config["TESTING"] = True
    return flask_app


@pytest.fixture()
def client(app):
    return app.test_client()


@pytest.fixture()
def temp_db_path(tmp_path):
    db_path = tmp_path / "expense_manager_test.db"

    with sqlite3.connect(db_path) as conn:
        conn.executescript(SCHEMA_PATH.read_text())
        conn.execute(
            "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)",
            (1, "alice", "alicepass", "Employee"),
        )
        conn.execute(
            "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)",
            (2, "bob", "bobpass", "Employee"),
        )
        conn.execute(
            "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)",
            (3, "siri", "managerpass", "Manager"),
        )
        conn.execute(
            "INSERT INTO expenses (id, userId, amount, description, category, date) VALUES (?, ?, ?, ?, ?, ?)",
            (1, 1, 100.0, "Old pending meal", "MEALS", "2026-07-01"),
        )
        conn.execute(
            "INSERT INTO expenses (id, userId, amount, description, category, date) VALUES (?, ?, ?, ?, ?, ?)",
            (2, 1, 200.5, "New approved trip", "TRAVEL", "2026-07-03"),
        )
        conn.execute(
            "INSERT INTO expenses (id, userId, amount, description, category, date) VALUES (?, ?, ?, ?, ?, ?)",
            (3, 2, 50.0, "Office supplies", "OFFICE_SUPPLIES", "2026-07-02"),
        )
        conn.execute(
            "INSERT INTO approvals (id, expenseId, status, reviewer, comment, review_date) VALUES (?, ?, ?, ?, ?, ?)",
            (1, 1, "pending", None, None, None),
        )
        conn.execute(
            "INSERT INTO approvals (id, expenseId, status, reviewer, comment, review_date) VALUES (?, ?, ?, ?, ?, ?)",
            (2, 2, "approved", 3, "Looks good", "2026-07-04"),
        )
        conn.execute(
            "INSERT INTO approvals (id, expenseId, status, reviewer, comment, review_date) VALUES (?, ?, ?, ?, ?, ?)",
            (3, 3, "denied", 3, "Missing receipt", "2026-07-03"),
        )
        conn.commit()

    return str(db_path)

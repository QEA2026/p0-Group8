import sqlite3
import sys
from pathlib import Path

# Configure imports so this script can use the Employee App services
base_dir = Path(__file__).parent
sys.path.append(str(base_dir.parent))
sys.path.append(str(base_dir.parent / "employee_app"))

from employee_app.repository.user_repository import UserRepository
from employee_app.service.authentication_service import AuthenticationService

db_path = base_dir / "expense_manager.db"


def run_sql_file(cursor, filename):
    sql_file = base_dir / filename

    if not sql_file.exists():
        raise FileNotFoundError(f"{filename} was not found at {sql_file}")

    cursor.executescript(sql_file.read_text())
    print(f"Executed {filename}")


# Step 1: Rebuild the database schema
with sqlite3.connect(db_path) as conn:
    conn.execute("PRAGMA foreign_keys = ON")
    cursor = conn.cursor()

    run_sql_file(cursor, "schema.sql")
    conn.commit()


# Step 2: Seed users through the application layer
# This ensures passwords are hashed before being stored.
user_repo = UserRepository(str(db_path))
auth_service = AuthenticationService(
    user_repository=user_repo,
    jwt_secret="seed-only-unused-secrety",
    token_expiration_hours=24,
)

auth_service.register_user("brian", "password", "employee")
auth_service.register_user("landon", "password", "employee")
auth_service.register_user("siri", "password", "manager")

print("Secure hashed users seeded successfully.")


# Step 3: Seed expenses and approvals
with sqlite3.connect(db_path) as conn:
    conn.execute("PRAGMA foreign_keys = ON")
    cursor = conn.cursor()

    run_sql_file(cursor, "seed.sql")
    conn.commit()

print("Database initialized successfully.")

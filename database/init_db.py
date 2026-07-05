import sqlite3
import sys
from pathlib import Path

# Clean Pathing: Injects two directories so app-internal imports work 
base_dir = Path(__file__).parent
sys.path.append(str(base_dir.parent))  # Allows finding employee_app
sys.path.append(str(base_dir.parent / "employee_app"))  # Allows employee_app internal files to find 'repository'

from employee_app.repository.user_repository import UserRepository
from employee_app.service.authentication_service import AuthenticationService

db_path = base_dir / "expense_manager.db"

def run_sql_file(cursor, filename):
    sql_file = base_dir / filename
    if sql_file.exists():
        cursor.executescript(sql_file.read_text())
        print(f"Executed {filename}")

# Build and Seed the Database
with sqlite3.connect(db_path) as conn:
    cursor = conn.cursor()
    
    # Step A: Create tables, others will be destroyed and recreated as per schema.sql
    run_sql_file(cursor, "schema.sql")
    
    # Step B: Seed secure users via the application layer into the freshly created tables
    user_repo = UserRepository(str(db_path))
    auth_service = AuthenticationService(user_repo, "seed-secret-key")
    
    auth_service.register_user('brian', 'password', 'Employee')
    auth_service.register_user('landon', 'passwerd', 'Employee')
    auth_service.register_user('siri', 'password', 'Manager')
    print("Secure hashed users seeded successfully.")
    
    # Step C: Populate business expenses and approvals
    run_sql_file(cursor, "seed.sql")
print("Database initialized and user data seeded successfully.")
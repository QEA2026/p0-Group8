import sqlite3
import os

db_name = "expense_manager.db"

# Connect to the database
conn = sqlite3.connect(db_name)
cursor = conn.cursor()

# Run schema sql
if os.path.exists("schema.sql"):
    with open("schema.sql", "r") as f:
        cursor.executescript(f.read())
    print("Initialized database from schema.sql")

# Run seed.sql
if os.path.exists("seed.sql"):
    with open("seed.sql", "r") as f:
        cursor.executescript(f.read())
    print("Ran seed.sql")

# Commit changes and close the connection
conn.commit()
conn.close()
print("Database initialized and seeded.")
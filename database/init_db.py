import sqlite3
from pathlib import Path

BASE_DIR = Path(__file__).parent
DB_PATH = BASE_DIR / "expense_manager.db"
SCHEMA_PATH = BASE_DIR / "schema.sql"
SEED_PATH = BASE_DIR / "seed.sql"


def main():
    conn = sqlite3.connect(DB_PATH)

    with open(SCHEMA_PATH, "r") as schema_file:
        conn.executescript(schema_file.read())

    with open(SEED_PATH, "r") as seed_file:
        conn.executescript(seed_file.read())

    conn.commit()
    conn.close()

    print(f"Database initialized at {DB_PATH}")


if __name__ == "__main__":
    main()

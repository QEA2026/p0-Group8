# Connection Factory
import sqlite3
import os
from typing import Optional

# Initiate a stateless & context managed connection to the database 
class ConnectToDB:

    def __init__(self, db_path: Optional[str] = None):
        self.db_path = db_path or "database/expense_manager.db"
        # print(os.getenv("DB_PATH")) //Optional: Use environment variable for database path if using .env file

    def get_connection(self) -> sqlite3.Connection:
        conn = sqlite3.connect(self.db_path)
        
        conn.row_factory = sqlite3.Row  # Enable named column access, 
        # hands the data back as an easy-to-read Python dictionary 
        # (like row['username']) instead of a tuple (like row[1])

        conn.execute("PRAGMA foreign_keys = ON") # force the database to respect the rules in schema.sql
        # will prevent someone from creating an expense for a userId that doesn't exist
        return conn
    

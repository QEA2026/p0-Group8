# Connection Factory
import sqlite3
import os
from typing import Optional

# Initiate a stateless & context managed connection to the database 
class ConnectToDB:

    def __init__(self, db_path: Optional[str] = None):
        if db_path:
            self.db_path = db_path

        # Locates the database file relative to the project structure if no path is provided
        else:
            # Get the directory where database.py itself is saved (./employee-app)
            current_dir = os.path.dirname(os.path.abspath(__file__))
            
            # Go up one level to the root folder, then go down into the database folder
            root_dir = os.path.dirname(current_dir)
            self.db_path = os.path.join(root_dir, "database", "expense_manager.db")

        # Optional: Use environment variable for database path if using .env file
        # os.getenv('DATABASE_PATH', 'expense_manager.db')
        # print(os.getenv("DB_PATH")) 

    def get_connection(self) -> sqlite3.Connection:
        conn = sqlite3.connect(self.db_path)
        
        conn.row_factory = sqlite3.Row  # Enable named column access, 
        # hands the data back as an easy-to-read Python dictionary 
        # (like row['username']) instead of a tuple (like row[1])

        conn.execute("PRAGMA foreign_keys = ON") # force the database to respect the rules in schema.sql
        # will prevent someone from creating an expense for a userId that doesn't exist
        return conn
    

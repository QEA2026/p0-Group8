# Contains the UserRepository class for interacting with the users table in the database.
from .database import ConnectToDB
from .user_model import User
from typing import Optional

class UserRepository:
    def __init__(self, db_path: Optional[str] = None):
        self.db = ConnectToDB(db_path)
    
    def find_by_username(self, username: str) -> Optional[User]:
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT id, username, password, role FROM users WHERE username = ?", (username,))
            row = cursor.fetchone()
            if row:
                return User(id=row["id"], username=row["username"], password=row["password"], role=row["role"])
            return None
        
    def find_by_id(self, user_id: int) -> Optional[User]:
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT id, username, password, role FROM users WHERE id = ?", (user_id,))
            row = cursor.fetchone()
            if row:
                return User(id=row["id"], username=row["username"], password=row["password"], role=row["role"])
            return None
        
    def create_user(self, user: User) -> User:
        # Inserts a new user into the database and returns the user with their new ID.
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(
                "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
                (user.username, user.password, user.role)
            )
            # Grab the auto-generated ID from the database and assign it to the model
            user.id = cursor.lastrowid
            return user
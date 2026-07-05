# Model for the User table in the database
from dataclasses import dataclass
from typing import Optional

@dataclass
class User:
    """Represents a user in the system, either an employee or a manager."""
    id: Optional[int] = None
    username: str = ""
    password: str = ""
    role: str = ""  # Default role is Employee

    def __post_init__(self):
        if not self.username:
            raise ValueError("Username cannot be empty.")
        if not self.password:
            raise ValueError("Password cannot be empty.")
        if not self.role:
            raise ValueError("Role cannot be empty.")
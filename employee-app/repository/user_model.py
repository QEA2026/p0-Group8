# Model for the User table in the database
from dataclasses import dataclass
from typing import Optional

@dataclass
class User:
    id: Optional[int] = None
    username: str = ""
    password: str = ""
    email: str = ""

    def __post_init__(self):
        if not self.username:
            raise ValueError("Username cannot be empty.")
        if not self.password:
            raise ValueError("Password cannot be empty.")
        if not self.email:
            raise ValueError("Email cannot be empty.")
        if self.role != "Employee":
            raise ValueError("Role must be 'Employee' to access the application.")
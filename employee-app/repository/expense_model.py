# Model for the expense table in the database

from dataclasses import dataclass
from typing import Optional

@dataclass
class Expense:
    id: Optional[int] = None
    user_id: int
    amount: float 
    description: str
    date: str # Store date as a string in 'YYYY-MM-DD' format
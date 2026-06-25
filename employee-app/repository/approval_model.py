# Model for the approval table in the database

from dataclasses import dataclass
from typing import Optional

@dataclass
class Approval:
    id: Optional[int] = None
    expense_id: int
    status: str  # e.g., "Pending", "Approved", "Rejected"
    approver_id: int
    date: str  # Store date as a string in 'YYYY-MM-DD' format
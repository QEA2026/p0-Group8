# Read model for employee ledger rows built from expenses + approvals join.

from dataclasses import dataclass
from typing import Optional

@dataclass
class LedgerEntry:
    """A read-only DTO combining an Expense and its Approval status."""
    expense_id: int
    user_id: int
    amount: float
    description: str
    category: str
    expense_date: str  # Store date as a string in 'YYYY-MM-DD' format
    status: str  # "Pending", "Approved", or "Denied"
    manager_comment: Optional[str] = None
    review_date: Optional[str] = None  # Store date as a string in 'YYYY-MM-DD' format

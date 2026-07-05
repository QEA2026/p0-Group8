# Contains the ApprovalRepository class for interacting with the approvals table in the database.
from .database import ConnectToDB   
from .expense_model import Expense
from .approval_model import Approval
from typing import List, Optional

class ApprovalRepository:
    def __init__(self, db_path: Optional[str] = None):
        self.db = ConnectToDB(db_path)

    def find_by_expense_id(self, expense_id: int) -> Optional[Approval]:
        """Retrieve a single approval by its expense ID which may or may not exist in the database. If it does not exist, return None."""
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT id, expenseId, status, reviewer, comment,review_date FROM approvals WHERE expenseId = ?", (expense_id,))
            row = cursor.fetchone()
            if row:
                return Approval(
                    id=row["id"], 
                    expense_id=row["expenseId"], 
                    status=row["status"], 
                    reviewer=row["reviewer"], 
                    comment=row["comment"],
                    review_date=row["review_date"]
                )
        return None
    
    def find_expenses_with_status_for_user(self, user_id: int, status: str) -> List[tuple]:
        """Retrieve all expenses for a specific user with a given approval status. Returns a list of tuples containing Expense and Approval objects."""
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(
                '''
                SELECT e.id, e.userId, e.amount, e.description, e.category, e.date, 
                       a.id AS approvalId, a.status, a.reviewer, a.comment, a.review_date
                FROM expenses e
                JOIN approvals a ON e.id = a.expenseId
                WHERE e.userId = ?
                ORDER BY e.date DESC
            ''', (user_id,))
            rows = cursor.fetchall()
            return [
            (
                Expense(
                    id=row["id"], 
                    user_id=row["userId"], 
                    amount=row["amount"], 
                    description=row["description"], 
                    category=row["category"], 
                    date=row["date"]
                ),
                Approval(
                    id=row["approvalId"],
                    expense_id=row["id"],
                    status=row["status"],
                    reviewer=row["reviewer"], 
                    comment=row["comment"],
                    review_date=row["review_date"]
                )
            )
            for row in rows
        ]
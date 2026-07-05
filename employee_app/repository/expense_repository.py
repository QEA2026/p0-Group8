# Contains the ExpenseRepository class for interacting with the expenses table in the database.
from .database import ConnectToDB
from .expense_model import Expense
from .ledger_entry_model import LedgerEntry
from typing import List, Optional

class ExpenseRepository:
    def __init__(self, db_path: Optional[str] = None):
        self.db = ConnectToDB(db_path)

    def create_expense(self, expense):
        """Create a new expense in the database and return the Expense object with its new ID."""
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            # Insert the expense into the expenses table
            cursor.execute(
                "INSERT INTO expenses (userId, amount, description, category, date) VALUES (?, ?, ?, ?, ?)",
                (expense.user_id, expense.amount, expense.description, expense.category, expense.date)
            )
            # Grab the last inserted expense ID to use for the approval entry
            expense.id = cursor.lastrowid
            
            # Insert a corresponding entry into the approvals table with a default status of "Pending"
            cursor.execute( 
                "INSERT INTO approvals (expenseId, status) VALUES (?,'Pending')",
                (expense.id,)
            )

            conn.commit()
        return expense
    
    """ Legacy method, kept for reference, but not currently used in the application. """
    # def find_by_id(self, expense_id: int) -> Optional[Expense]:
    #     """Retrieve a single expense by its ID. Returns None if the expense does not exist."""
    #     with self.db.get_connection() as conn:
    #         cursor = conn.cursor()
    #         cursor.execute("SELECT id, userId, amount, description, category, date FROM expenses WHERE id = ?", (expense_id,))
    #         row = cursor.fetchone()
    #         if row:
    #             return Expense(
    #                 id=row["id"], 
    #                 user_id=row["userId"], 
    #                 amount=row["amount"], 
    #                 description=row["description"], 
    #                 category=row["category"],
    #                 date=row["date"]
    #             )
    #     return None
    
    def find_by_user_id(self, user_id: int) -> List[Expense]:
        """Retrieve all expenses for a specific user by their user ID. Returns a list of Expense objects."""
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT id, userId, amount, category, description, date FROM expenses WHERE userId = ?", (user_id,))
            rows = cursor.fetchall()
            return [Expense(
                    id=row["id"], 
                    user_id=row["userId"], 
                    amount=row["amount"], 
                    description=row["description"], 
                    category=row["category"],
                    date=row["date"]
                ) for row in rows]

    def get_expenses_by_user(self, user_id: int) -> List[LedgerEntry]:
        """ Retrieve ledger rows for a specific user by joining expenses with approvals.
         The returned read model is used by service-layer grouping (pending vs history)."""
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(
                '''
                SELECT
                    e.id,
                    e.userId,
                    e.amount,
                    e.description,
                    e.category,
                    e.date,
                    a.status,
                    a.comment,
                    a.review_date
                FROM expenses e
                LEFT JOIN approvals a ON e.id = a.expenseId
                WHERE e.userId = ?
                ORDER BY e.date DESC, e.id DESC
                ''',
                (user_id,)
            )
            rows = cursor.fetchall()

            return [
                LedgerEntry(
                    expense_id=row["id"],
                    user_id=row["userId"],
                    amount=row["amount"],
                    description=row["description"],
                    category=row["category"],
                    expense_date=row["date"],
                    status=row["status"] if row["status"] else "Pending",
                    manager_comment=row["comment"],
                    review_date=row["review_date"],
                )
                for row in rows
            ]

    def find_expense_with_status(self, expense_id: int) -> Optional[LedgerEntry]:
        """Retrieve a single expense joined with approval fields to support guarded edits/deletes."""
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(
                '''
                SELECT
                    e.id,
                    e.userId,
                    e.amount,
                    e.description,
                    e.category,
                    e.date,
                    a.status,
                    a.comment,
                    a.review_date
                FROM expenses e
                LEFT JOIN approvals a ON e.id = a.expenseId
                WHERE e.id = ?
                ''',
                (expense_id,)
            )
            row = cursor.fetchone()

            if not row:
                return None

            return LedgerEntry(
                expense_id=row["id"],
                user_id=row["userId"],
                amount=row["amount"],
                description=row["description"],
                category=row["category"],
                expense_date=row["date"],
                status=row["status"] if row["status"] else "Pending",
                manager_comment=row["comment"],
                review_date=row["review_date"],
            )
    

    def update_expense(self, expense_id: int, amount: float, description: str) -> bool:
        """Update editable expense fields in the database. Returns True if one row was updated."""
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(
                "UPDATE expenses SET amount = ?, description = ? WHERE id = ?",
                (amount, description, expense_id)
            )
            conn.commit()
            return cursor.rowcount > 0
    
    def delete_expense(self, expense_id: int) -> bool:
        """Delete an expense from the database by its ID. Returns True if the deletion was successful, False otherwise."""
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            # Delete the expense from the approvals table
            cursor.execute("DELETE FROM approvals WHERE expenseId = ?", (expense_id,))
            # Delete the expense from the expenses table
            cursor.execute("DELETE FROM expenses WHERE id = ?", (expense_id,))
            conn.commit()
            return cursor.rowcount > 0  # Returns True if a row was deleted, False otherwise
        

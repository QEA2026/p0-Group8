# Contains the ExpenseRepository class for interacting with the expenses table in the database.
from .database import ConnectToDB
from .expense_model import Expense
from typing import List, Optional

class ExpenseRepository:
    def __init__(self, db_path: Optional[str] = None):
        self.db = ConnectToDB(db_path)

    def create_expense(self, expense):
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
    
    # Retrieve a single expense by its ID which may or may not exist in the database. If it does not exist, return None.
    def find_by_id(self, expense_id: int) -> Optional[Expense]:
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT id, userId, amount, description, category, date FROM expenses WHERE id = ?", (expense_id,))
            row = cursor.fetchone()
            if row:
                return Expense(
                    id=row["id"], 
                    user_id=row["userId"], 
                    amount=row["amount"], 
                    description=row["description"], 
                    category=row["category"],
                    date=row["date"]
                )
        return None
    
    # Retrieve all expenses for a specific user by their user ID. Returns a list of Expense objects.
    def find_by_user_id(self, user_id: int) -> List[Expense]:
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
    
    # Update existing expense details in the database. Returns the updated Expense object.
    def update_expense(self, expense: Expense) -> Expense:
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(
                "UPDATE expenses SET amount = ?, description = ?, category = ?, date = ? WHERE id = ?",
                (expense.amount, expense.description, expense.category, expense.date, expense.id)
            )
            conn.commit()
        return expense
    
    # Delete an expense from the database by its ID. Returns True if the deletion was successful, False otherwise.
    def delete_expense(self, expense_id: int) -> bool:
        with self.db.get_connection() as conn:
            cursor = conn.cursor()
            # Delete the expense from the approvals table
            cursor.execute("DELETE FROM approvals WHERE expenseId = ?", (expense_id,))
            # Delete the expense from the expenses table
            cursor.execute("DELETE FROM expenses WHERE id = ?", (expense_id,))
            conn.commit()
            return cursor.rowcount > 0  # Returns True if a row was deleted, False otherwise
        

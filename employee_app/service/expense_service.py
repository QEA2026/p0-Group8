# Service layer for handling expense-related business logic

from repository.expense_model import Expense
from repository.ledger_entry_model import LedgerEntry
from repository.expense_repository import ExpenseRepository
from datetime import datetime
from decimal import Decimal, InvalidOperation
from enum import Enum
from typing import Dict, List


# A list of allowed categories for expenses as a constant set for fast RDBM lookups
# Define an explicit Enum for Categories
class ExpenseCategory(str, Enum):
    TRAVEL = 'TRAVEL'
    MEALS = 'MEALS'
    LODGING = 'LODGING'
    OFFICE_SUPPLIES = 'OFFICE_SUPPLIES'
    EQUIPMENT = 'EQUIPMENT'
    SOFTWARE = 'SOFTWARE'
    TRAINING = 'TRAINING'
    OTHER = 'OTHER'

class ExpenseService:
    def __init__(self, expense_repository: ExpenseRepository):
        self.expense_repo = expense_repository

    @staticmethod
    def _parse_currency_amount(amount) -> Decimal:
        """Parse and validate money as a positive Decimal with max 2 fraction digits."""
        try:
            parsed_amount = Decimal(str(amount).strip())
        except (InvalidOperation, ValueError, TypeError):
            raise ValueError("Expense amount must be a valid number.")

        if parsed_amount <= 0:
            raise ValueError("Expense amount must be greater than zero.")

        # Accept values like 45 or 45.5, but block >2 decimal places (e.g., 45.555)
        if parsed_amount.as_tuple().exponent < -2:
            raise ValueError("Expense amount must have at most 2 decimal places.")

        # Normalize accepted amounts to two decimal places (e.g., 46 -> 46.00)
        return parsed_amount.quantize(Decimal("0.01"))

    @staticmethod
    def format_currency_amount(amount) -> str:
        """Return money as a 2-decimal string for stable API responses."""
        normalized_amount = ExpenseService._parse_currency_amount(amount)
        return f"{normalized_amount:.2f}"
    
    def create_expense(self, user_id: int, amount: float, description: str, category: str, expense_date: str) -> Expense: 
        """ Validates input, checks date format, and saves the new expense. """

        # 1. Number Validation
        clean_amount = self._parse_currency_amount(amount)
        
        # 2. Description Validation
        if not description or not description.strip():
            raise ValueError("A desciption is required for the expense.")
        
        # 3. Category Validation
        if not category or not category.strip():
            raise ValueError("A category is required for the expense.")
        # Normalize the category to uppercase and remove extra spaces
        clean_category = category.strip().upper()
        if clean_category not in ExpenseCategory.__members__:
            # Creating the printed allowed list dynamically from the Enum
            allowed_list = ", ".join(sorted([e.value for e in ExpenseCategory]))
            raise ValueError(f"Invalid category. Must be one of: {allowed_list}")
        
        # 4. Date Validation (The UAT Fix)
        try:
            # This checks if the string perfectly matches 'YYYY-MM-DD'
            datetime.strptime(expense_date, '%Y-%m-%d')
        except ValueError:
            raise ValueError("Date must be in YYYY-MM-DD format (e.g., 2026-06-25).")
        
        # 5. Assemble the Model
        new_expense = Expense(
            id=None,
            user_id=user_id,
            amount=float(clean_amount),
            description=description.strip(),
            category=clean_category,
            date=expense_date
        )

        # 6. Pass down to the Repository
        return self.expense_repo.create_expense(new_expense)

    def get_user_ledger(self, user_id: int) -> Dict[str, List[dict]]:
        """Builds the employee ledger response split into pending and history views."""
        # 1. Pull the joined ledger rows from the repository layer
        ledger_rows = self.expense_repo.get_expenses_by_user(user_id)

        # 2. Prepare the two business-facing sections required by User Story 3
        pending_expenses: List[dict] = []
        expense_history: List[dict] = []

        # 3. Translate and sort each row into the right section
        for entry in ledger_rows:
            # Convert dataclass to JSON-friendly dict for controller response
            entry_dict = self._serialize_ledger_entry(entry)
            # Normalize status so mixed-case DB values still behave correctly
            normalized_status = (entry.status or "").strip().lower()

            if normalized_status == "pending":
                pending_expenses.append(entry_dict)
            else:
                # Approved/Denied and any non-pending final statuses go to history
                expense_history.append(entry_dict)

        # 4. Return a stable API contract for the controller/client layer
        return {
            "pending_expenses": pending_expenses,
            "expense_history": expense_history,
        }

    def get_pending_expenses(self, user_id: int) -> List[dict]:
        """Returns only pending expenses for the logged-in employee."""
        ledger_rows = self.expense_repo.get_expenses_by_user(user_id)
        pending_expenses: List[dict] = []

        for entry in ledger_rows:
            normalized_status = (entry.status or "").strip().lower()
            if normalized_status == "pending":
                pending_expenses.append(self._serialize_ledger_entry(entry))

        return pending_expenses

    def update_pending_expense(self, user_id: int, expense_id: int, amount, description: str) -> None:
        """Updates amount/description only when expense is owned by user and still pending."""
        # 1. Validate the requested field values first
        clean_amount = self._parse_currency_amount(amount)
        if not description or not description.strip():
            raise ValueError("A desciption is required for the expense.")

        # 2. Pull the current expense+status snapshot and enforce authorization rules
        expense_row = self.expense_repo.find_expense_with_status(expense_id)
        if not expense_row:
            raise ValueError("Expense not found.")
        if expense_row.user_id != user_id:
            raise ValueError("You can only edit your own expenses.")

        normalized_status = (expense_row.status or "").strip().lower()
        if normalized_status != "pending":
            raise ValueError("Only pending expenses can be edited.")

        # 3. Persist the allowed mutation
        updated = self.expense_repo.update_expense(
            expense_id=expense_id,
            amount=float(clean_amount),
            description=description.strip(),
        )
        if not updated:
            raise ValueError("Expense update failed.")

    def delete_pending_expense(self, user_id: int, expense_id: int) -> None:
        """Deletes an expense only when it is owned by user and still pending."""
        # 1. Pull the current expense+status snapshot and enforce authorization rules
        expense_row = self.expense_repo.find_expense_with_status(expense_id)
        if not expense_row:
            raise ValueError("Expense not found.")
        if expense_row.user_id != user_id:
            raise ValueError("You can only delete your own expenses.")

        normalized_status = (expense_row.status or "").strip().lower()
        if normalized_status != "pending":
            raise ValueError("Only pending expenses can be deleted.")

        # 2. Delete the expense and linked approval row
        deleted = self.expense_repo.delete_expense(expense_id)
        if not deleted:
            raise ValueError("Expense delete failed.")

    def _serialize_ledger_entry(self, entry: LedgerEntry) -> dict:
        """Convert a LedgerEntry read model into a JSON-safe dictionary."""
        # Keep key names explicit and stable for downstream CLI/client rendering
        return {
            "expense_id": entry.expense_id,
            "user_id": entry.user_id,
            "amount": self.format_currency_amount(entry.amount),
            "description": entry.description,
            "category": entry.category,
            "expense_date": entry.expense_date,
            "status": entry.status,
            "manager_comment": entry.manager_comment,
            "review_date": entry.review_date,
        }

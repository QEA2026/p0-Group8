# Service layer for handling expense-related business logic

from repository.expense_model import Expense
from repository.expense_repository import ExpenseRepository
from datetime import datetime
from enum import Enum


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
    
    def create_expense(self, user_id: int, amount: float, description: str, category: str, expense_date: str) -> Expense: 
        """ Validates input, checks date format, and saves the new expense. """

        # 1. Number Validation
        try:
            amount = float(amount)
        except ValueError:
            raise ValueError("Expense amount must be a valid number.")
        
        if amount <= 0:
            raise ValueError("Expense amount must be greater than zero.")
        
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
            amount=amount,
            description=description.strip(),
            category=clean_category,
            date=expense_date
        )

        # 6. Pass down to the Repository
        return self.expense_repo.create_expense(new_expense)

from datetime import datetime

CATEGORIES = [
    "TRAVEL",
    "MEALS",
    "LODGING",
    "OFFICE_SUPPLIES",
    "EQUIPMENT",
    "SOFTWARE",
    "TRAINING",
    "OTHER"
]

def select_category():
    print("\n=== Categories ===")
    for i, cat in enumerate(CATEGORIES, start=1):
        print(f"{i}. {cat}")

    while True:
        choice = input("Select category number: ").strip()

        if choice.isdigit():
            index = int(choice) - 1
            if 0 <= index < len(CATEGORIES):
                return CATEGORIES[index]

        print("Invalid selection. Try again.")

def get_non_empty(prompt):
    while True:
        value = input(prompt).strip()
        if value:
            return value
        print("Input cannot be empty.")

def validate_required(field_name, value):
    value = value.strip()
    if not value:
        print(f"{field_name} is required.")
        return None
    return value

def validate_amount(value):
    try:
        amount = float(value)
        if amount <= 0:
            print("Amount must be greater than 0.")
            return None
        return amount
    except ValueError:
        print("Amount must be a number.")
        return None

def validate_date(value):
    try:
        datetime.strptime(value, "%Y-%m-%d")
        return value
    except ValueError:
        print("Date must be in YYYY-MM-DD format.")
        return None
    
def show_expense(exp):
    print("--------------------------------------------------")
    print(f"ID: {exp.get('expense_id')}")
    print(f"Amount: ${exp.get('amount')}")
    print(f"Category: {exp.get('category')}")
    print(f"Status: {exp.get('status')}")
    print(f"Date: {exp.get('expense_date')}")
    print(f"Description: {exp.get('description')}")

    if exp.get("manager_comment"):
        print(f"Manager Comment: {exp.get('manager_comment')}")

    print("--------------------------------------------------")

def print_expense_list(expenses):
    print("\n=== PENDING EXPENSES ===")
    for exp in expenses:
        print(
            f"ID: {exp.get('expense_id')} | "
            f"${exp.get('amount')} | "
            f"{exp.get('category')} | "
            f"{exp.get('expense_date')}"
        )
    print("=================================\n")


def select_expense_from_list(expenses):
    """
    Displays a list of expenses and prompts user to select a valid ID.
    Returns the selected expense_id or None if invalid.
    """

    if not expenses:
        print("No expenses available.")
        return None

    print_expense_list(expenses)

    valid_ids = {str(exp.get("expense_id")) for exp in expenses}

    while True:
        expense_id = input("Enter Expense ID: ").strip()

        if expense_id in valid_ids:
            return expense_id

        print("Invalid ID. Please choose from the list above.")
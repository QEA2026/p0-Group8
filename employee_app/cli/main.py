from auth import login_flow
from datetime import datetime

from api_client import (
    submit_expense,
    get_ledger,
    get_pending,
    update_expense,
    delete_expense,
    logout
)

from utils import (
    validate_required,
    validate_amount,
    validate_date,
    select_category,
    show_expense,
    print_expense_list,
    select_expense_from_list
)

def show_menu():
    print("\n--- Employee Menu ---")
    print("1. Submit Expense")
    print("2. View Ledger")
    print("3. View Pending Expenses")
    print("4. Edit Expense")
    print("5. Delete Expense")
    print("6. Logout")
    print("0. Exit")

def main():
    #print("Starting Employee CLI...")

    if not login_flow():
        print("Login failed. Exiting.")
        return

    while True:
        show_menu()
        choice = input("Select option: ").strip()

        #submit expenses
        if choice == "1":
            while True:
                amount_input = input("Amount: ")
                amount = validate_amount(amount_input)
                if amount is not None:
                    break

            while True:
                description_input = input("Description: ")
                description = validate_required("Description", description_input)
                if description:
                    break

            while True:
                category = select_category()
                if category:
                    break

            while True:
                date_input = input("Date (YYYY-MM-DD): ")
                expense_date = validate_date(date_input)
                if expense_date:
                    break

            response = submit_expense(amount, description, category, expense_date)

            print(response.json())

        #view ledger
        elif choice == "2":
            response = get_ledger()
            data = response.json()

            print("\n========== PENDING EXPENSES ==========\n")
            for exp in data.get("pending_expenses", []):
                show_expense(exp)

            print("\n========== EXPENSE HISTORY ==========\n")
            for exp in data.get("expense_history", []):
                show_expense(exp)

        #view any pending
        elif choice == "3":
            response = get_pending()
            data = response.json()

            print("\n========== PENDING ONLY ==========\n")
            for exp in data.get("pending_expenses", []):
                show_expense(exp)

        #edit expense
        elif choice == "4":
            response = get_pending()
            data = response.json()

            expense_id = select_expense_from_list(data.get("pending_expenses", []))

            if not expense_id:
                continue

            amount = input("New amount (leave blank to keep): ")
            description = input("New description (leave blank): ")

            response = update_expense(expense_id, amount, description)
            print(response.json())

        #delete expense
        elif choice == "5":
            response = get_pending()
            data = response.json()

            expense_id = select_expense_from_list(data.get("pending_expenses", []))

            if not expense_id:
                continue

            response = delete_expense(expense_id)
            print(response.json())
        #logout
        elif choice == "6":
            #logout()
            print("Logged out.")
            return 1
            # break

        elif choice == "0":
            print("Exiting...")
            return 0

        else:
            print("Invalid option")


if __name__ == "__main__":
    exit(main())
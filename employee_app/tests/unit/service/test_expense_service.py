# 7 Expense service unit tests: 4 Positive and 3 Negative
import pytest

from repository.expense_model import Expense
from repository.ledger_entry_model import LedgerEntry
from service.expense_service import ExpenseService


def _entry(expense_id: int, user_id: int, status: str, amount: float = 10.0) -> LedgerEntry:
    # Helper Function: Creates a LedgerEntry instance with the given parameters and default values for other fields
    return LedgerEntry(
        expense_id=expense_id,
        user_id=user_id,
        amount=amount,
        description="sample",
        category="MEALS",
        expense_date="2026-07-05",
        status=status,
        manager_comment=None,
        review_date=None,
    )


def test_format_currency_amount_normalizes_to_two_decimals():
    # Act & Assert
    assert ExpenseService.format_currency_amount("46") == "46.00"
    assert ExpenseService.format_currency_amount("46.5") == "46.50"


def test_create_expense_calls_repo_with_clean_values(mocker):
    # Arrange: Mock setup and stubbed repo return value
    repo = mocker.Mock()
    service = ExpenseService(repo)

    repo.create_expense.return_value = Expense(
        id=99,
        user_id=1,
        amount=46.5,
        description="Lunch",
        category="MEALS",
        date="2026-07-05",
    )

    # Act: Create a new expense using the service
    result = service.create_expense(
        user_id=1,
        amount="46.5",
        description="  Lunch  ",
        category=" meals ",
        expense_date="2026-07-05",
    )

    # Assert: Verify created expense has the expected values
    assert result.id == 99
    created_expense = repo.create_expense.call_args.args[0]
    assert created_expense.amount == 46.5
    assert created_expense.description == "Lunch"
    assert created_expense.category == "MEALS"


def test_create_expense_rejects_more_than_two_decimals(mocker):
    # Arrange: Mock setup
    service = ExpenseService(mocker.Mock())

    # Act & Assert: Attempt to create an expense with more than two decimal places and expect a ValueError
    with pytest.raises(ValueError, match="at most 2 decimal places"):
        service.create_expense(
            user_id=1,
            amount="46.555",
            description="Lunch",
            category="MEALS",
            expense_date="2026-07-05",
        )


def test_get_user_ledger_splits_pending_and_history(mocker):
    # Arrange: Mock setup, service instance, and stubbed repo return values
    repo = mocker.Mock()
    service = ExpenseService(repo)

    repo.get_expenses_by_user.return_value = [
        _entry(expense_id=3, user_id=1, status="Approved", amount=50),
        _entry(expense_id=2, user_id=1, status="pending", amount=10),
        _entry(expense_id=1, user_id=1, status="Denied", amount=25),
    ]

    # Act: Call the method under test
    ledger = service.get_user_ledger(user_id=1)

    # Assert: Verify the ledger splits pending and history correctly
    assert len(ledger["pending_expenses"]) == 1
    assert len(ledger["expense_history"]) == 2
    assert ledger["pending_expenses"][0]["expense_id"] == 2


def test_update_pending_expense_rejects_non_owner(mocker):
    # Arrange: Mock setup, service instance, and stubbed repo return value
    repo = mocker.Mock()
    service = ExpenseService(repo)

    repo.find_expense_with_status.return_value = _entry(
        expense_id=10,
        user_id=2,
        status="Pending",
        amount=10,
    )

    # Act & Assert: Attempt to update a pending expense as a non-owner and expect a ValueError
    with pytest.raises(ValueError, match="only edit your own"):
        service.update_pending_expense(
            user_id=1,
            expense_id=10,
            amount="12.00",
            description="Updated",
        )



def test_update_pending_expense_updates_owned_pending(mocker):
    # Arrange: Mock setup, service instance and stubbed repo return values
    repo = mocker.Mock()
    service = ExpenseService(repo)
    
    repo.find_expense_with_status.return_value = _entry(
        expense_id=10,
        user_id=1,
        status="Pending",
        amount=10,
    )
    repo.update_expense.return_value = True

    # Act: Update the pending expense owned by the user
    service.update_pending_expense(
        user_id=1,
        expense_id=10,
        amount="12.50",
        description="Updated",
    )

    # Assert: Behavior Verification - ensure the update_expense method was called with the correct parameters
    repo.update_expense.assert_called_once_with(
        expense_id=10,
        amount=12.5,
        description="Updated",
    )



def test_delete_pending_expense_rejects_non_pending(mocker):
    # Arrange: Mock setup, service instance, and expected return values
    repo = mocker.Mock()
    service = ExpenseService(repo)

    repo.find_expense_with_status.return_value = _entry(
        expense_id=10,
        user_id=1,
        status="Approved",
        amount=10,
    )

    # Act & Assert: Attempt to delete a non-pending expense and expect a ValueError
    with pytest.raises(ValueError, match="Only pending expenses"):
        service.delete_pending_expense(user_id=1, expense_id=10)

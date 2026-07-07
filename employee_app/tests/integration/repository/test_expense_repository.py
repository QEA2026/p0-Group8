# 4 Expense repository tests: 4 positive
from repository.expense_repository import ExpenseRepository


def test_get_expenses_by_user_returns_sorted_joined_rows(temp_db_path):
    # Arrange: Create an instance of the ExpenseRepository with the temporary database path
    repo = ExpenseRepository(temp_db_path)

    # Act: Retrieve expenses for the user with ID 1
    rows = repo.get_expenses_by_user(1)

    # Assert: Verify that the expenses are sorted by status and contain the expected data
    assert [row.expense_id for row in rows] == [2, 1]
    assert rows[0].status == "approved"
    assert rows[1].status == "pending"


def test_find_expense_with_status_returns_joined_data(temp_db_path):
    # Arrange: Set up the ExpenseRepository
    repo = ExpenseRepository(temp_db_path)

    # Act: Use tested method to find the expense with status for the expense ID 2
    row = repo.find_expense_with_status(2)

    # Assert: Verify that the returned row contains the expected joined data
    assert row is not None
    assert row.expense_id == 2
    assert row.user_id == 1
    assert row.status == "approved"
    assert row.manager_comment == "Looks good"
    assert row.review_date == "2026-07-04"


def test_update_expense_persists_amount_and_description(temp_db_path):
    # Arrange: Set up the ExpenseRepository instance
    repo = ExpenseRepository(temp_db_path)

    # Act: Use tested method to update the expense with ID 1
    updated = repo.update_expense(1, 111.11, "Updated meal")

    # Assert: Verify that the update was successful and the changes are persisted
    assert updated is True
    row = repo.find_expense_with_status(1)
    assert row is not None
    assert row.amount == 111.11
    assert row.description == "Updated meal"


def test_delete_expense_removes_expense_and_approval(temp_db_path):
    # Arrange: Set up the ExpenseRepository instance
    repo = ExpenseRepository(temp_db_path)

    # Act: Use tested method to remove the expense with ID 1
    deleted = repo.delete_expense(1)

    # Assert: Verify deleted returns successfully and the ID is None
    assert deleted is True
    assert repo.find_expense_with_status(1) is None

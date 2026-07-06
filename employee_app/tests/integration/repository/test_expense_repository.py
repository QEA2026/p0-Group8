from repository.expense_repository import ExpenseRepository


def test_get_expenses_by_user_returns_sorted_joined_rows(temp_db_path):
    repo = ExpenseRepository(temp_db_path)

    rows = repo.get_expenses_by_user(1)

    assert [row.expense_id for row in rows] == [2, 1]
    assert rows[0].status == "approved"
    assert rows[1].status == "pending"


def test_find_expense_with_status_returns_joined_data(temp_db_path):
    repo = ExpenseRepository(temp_db_path)

    row = repo.find_expense_with_status(2)

    assert row is not None
    assert row.expense_id == 2
    assert row.user_id == 1
    assert row.status == "approved"
    assert row.manager_comment == "Looks good"
    assert row.review_date == "2026-07-04"


def test_update_expense_persists_amount_and_description(temp_db_path):
    repo = ExpenseRepository(temp_db_path)

    updated = repo.update_expense(1, 111.11, "Updated meal")

    assert updated is True
    row = repo.find_expense_with_status(1)
    assert row is not None
    assert row.amount == 111.11
    assert row.description == "Updated meal"


def test_delete_expense_removes_expense_and_approval(temp_db_path):
    repo = ExpenseRepository(temp_db_path)

    deleted = repo.delete_expense(1)

    assert deleted is True
    assert repo.find_expense_with_status(1) is None

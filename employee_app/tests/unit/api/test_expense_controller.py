# 6 Expense Controller tests: 5 Positive and 1 Negative (has 4 test negative test cases)
from datetime import timedelta

from repository.expense_model import Expense
from repository.user_model import User


def _login_employee(client, app, mocker):
    # Helper function: Gives a tokened user and an auth service instance to use RESTAPIs
    auth_service = mocker.Mock()
    auth_service.login.return_value = User(id=1, username="brian", password="hashed", role="Employee")
    auth_service.generate_jwt_token.return_value = "test-jwt-token"
    auth_service.token_expiry = timedelta(hours=24)
    app.auth_service = auth_service

    login_response = client.post(
        "/auth/login",
        json={"username": "brian", "password": "password"},
    )
    assert login_response.status_code == 200

    return auth_service


def _employee_user():
    # Helper Function: Returns specified user from _login_employee for asserting correct user return object
    return User(id=1, username="brian", password="hashed", role="Employee")


def test_submit_expense_succeeds_for_logged_in_employee(client, app, mocker):
    # Arrange: Login user, mock setup, initialize services, and expected return values
    _login_employee(client, app, mocker)

    expense_service = mocker.Mock()
    expense_service.create_expense.return_value = Expense(
        id=10,
        user_id=1,
        amount=22.0,
        description="Lunch",
        category="OTHER",
        date="2026-04-01",
    )
    expense_service.format_currency_amount.return_value = "22.00"
    app.expense_service = expense_service
    app.auth_service.parse_token.return_value = _employee_user()

    # Act: Target the submit endpoint with expense json
    response = client.post(
        "/expenses/submit",
        json={
            "amount": "22",
            "description": "Lunch",
            "category": "OTHER",
            "expense_date": "2026-04-01",
        },
    )

    # Assert: Verify success status code and return payload values
    assert response.status_code == 201
    payload = response.get_json()
    assert payload["expense_id"] == 10
    assert payload["amount"] == "22.00"


def test_ledger_returns_pending_and_history_lists(client, app, mocker):
    # Arrange: Login user, mock setup, initialize services, and expected return values
    _login_employee(client, app, mocker)

    expense_service = mocker.Mock()
    expense_service.get_user_ledger.return_value = {
        "pending_expenses": [
            {"expense_id": 1, "amount": "22.00", "status": "Pending"}
        ],
        "expense_history": [
            {"expense_id": 2, "amount": "10.00", "status": "Approved"}
        ],
    }
    app.expense_service = expense_service
    app.auth_service.parse_token.return_value = _employee_user()

    # Act: Hit ledger endpoint
    response = client.get("/expenses/ledger")

    # Assert: Verify success status code, payload contains pending and history, and correct payload length
    assert response.status_code == 200
    payload = response.get_json()
    assert "pending_expenses" in payload
    assert "expense_history" in payload
    assert len(payload["pending_expenses"]) == 1
    assert len(payload["expense_history"]) == 1


def test_pending_returns_only_pending_items(client, app, mocker):
    # Arrange: Login user, mock setup, initialize services, and expected return values
    _login_employee(client, app, mocker)

    expense_service = mocker.Mock()
    expense_service.get_pending_expenses.return_value = [
        {"expense_id": 1, "status": "Pending"},
        {"expense_id": 3, "status": "Pending"},
    ]
    app.expense_service = expense_service
    app.auth_service.parse_token.return_value = _employee_user()

    # Act: Hit pending expenses endpoint
    response = client.get("/expenses/pending")

    # Assert: Verify status success code and payload contains pending expense object with correct data length
    assert response.status_code == 200
    payload = response.get_json()
    assert list(payload.keys()) == ["pending_expenses"]
    assert len(payload["pending_expenses"]) == 2


def test_update_allows_owned_pending_expense(client, app, mocker):
    # Arrange: Login user, mock setup, initialize services, and expected return values
    _login_employee(client, app, mocker)

    expense_service = mocker.Mock()
    app.expense_service = expense_service
    app.auth_service.parse_token.return_value = _employee_user()

    # Act: Hit put expense endpoint for updating specified expense
    response = client.put(
        "/expenses/1",
        json={"amount": "24.50", "description": "Updated lunch"},
    )

    # Assert: Verify success status code, return message, and data was sent to mock db
    assert response.status_code == 200
    assert response.get_json()["message"] == "Expense updated successfully."
    expense_service.update_pending_expense.assert_called_once_with(
        user_id=1,
        expense_id=1,
        amount="24.50",
        description="Updated lunch",
    )


def test_delete_allows_owned_pending_expense(client, app, mocker):
    # Arrange: Login user, mock setup, and initialize services  
    _login_employee(client, app, mocker)

    expense_service = mocker.Mock()
    app.expense_service = expense_service
    app.auth_service.parse_token.return_value = _employee_user()

    # Act: Hit expense endpoint with DELETE for ID 1 
    response = client.delete("/expenses/1")

    # Assert: Verify success status code, return message, and data was changed in mock db
    assert response.status_code == 200
    assert response.get_json()["message"] == "Expense deleted successfully."
    expense_service.delete_pending_expense.assert_called_once_with(user_id=1, expense_id=1)


def test_update_and_delete_reject_non_owner_or_non_pending_expense(client, app, mocker):
    # Arrange: Login user, mock setup, initialize services, and expected return values
    _login_employee(client, app, mocker)

    expense_service = mocker.Mock()
    app.expense_service = expense_service
    app.auth_service.parse_token.return_value = _employee_user()

    # Act: Trigger 4 different failure scenarios
    expense_service.update_pending_expense.side_effect = ValueError("You can only edit your own expenses.")
    update_response = client.put(
        "/expenses/2",
        json={"amount": "24.50", "description": "Updated lunch"},
    )

    expense_service.update_pending_expense.side_effect = ValueError("Only pending expenses can be edited.")
    non_pending_update_response = client.put(
        "/expenses/1",
        json={"amount": "24.50", "description": "Updated lunch"},
    )

    expense_service.delete_pending_expense.side_effect = ValueError("You can only delete your own expenses.")
    delete_response = client.delete("/expenses/2")

    expense_service.delete_pending_expense.side_effect = ValueError("Only pending expenses can be deleted.")
    non_pending_delete_response = client.delete("/expenses/1")

    # Assert: Verify bad request status code for all 4 scenarios
    assert update_response.status_code == 400
    assert non_pending_update_response.status_code == 400
    assert delete_response.status_code == 400
    assert non_pending_delete_response.status_code == 400

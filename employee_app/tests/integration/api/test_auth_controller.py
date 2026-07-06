from datetime import timedelta

from repository.user_model import User


def test_login_succeeds_for_valid_employee(client, app, mocker):
    auth_service = mocker.Mock()
    auth_service.login.return_value = User(id=1, username="brian", password="hashed", role="Employee")
    auth_service.generate_jwt_token.return_value = "test-jwt-token"
    auth_service.token_expiry = timedelta(hours=24)
    app.auth_service = auth_service

    response = client.post(
        "/auth/login",
        json={"username": "brian", "password": "password"},
    )

    assert response.status_code == 200
    payload = response.get_json()
    assert payload["message"] == "Welcome back, brian!"
    assert payload["user"]["username"] == "brian"
    assert "jwt_token=test-jwt-token" in response.headers.get("Set-Cookie", "")


def test_login_rejects_bad_credentials(client, app, mocker):
    auth_service = mocker.Mock()
    auth_service.login.side_effect = ValueError("Invalid username or password.")
    auth_service.token_expiry = timedelta(hours=24)
    app.auth_service = auth_service

    response = client.post(
        "/auth/login",
        json={"username": "brian", "password": "wrong"},
    )

    assert response.status_code == 401
    assert response.get_json()["error"] == "Invalid username or password."


def test_logout_clears_jwt_cookie(client):
    response = client.post("/auth/logout")

    assert response.status_code == 200
    assert response.get_json()["message"] == "Logged out successfully"
    assert "jwt_token=;" in response.headers.get("Set-Cookie", "")

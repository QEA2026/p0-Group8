# 5 Authentication service unit tests: 2 Positive and 3 Negative
import bcrypt
import pytest

from repository.user_model import User
from service.authentication_service import AuthenticationService


TEST_JWT_SECRET = "unit-test-secret-key-at-least-32-chars"
TEST_TOKEN_EXPIRATION_HOURS = 24


def _hashed(password: str) -> str:
    # Helper Function: Hashes the given password using bcrypt and returns the hashed string
    return bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")


def test_login_returns_employee_on_valid_credentials(mocker):
    # Arrange: Mock setup, service instance, and stubbed repo return values
    repo = mocker.Mock()
    service = AuthenticationService(
        repo,
        jwt_secret=TEST_JWT_SECRET,
        token_expiration_hours=TEST_TOKEN_EXPIRATION_HOURS,
    )

    user = User(id=1, username="brian", password=_hashed("password"), role="Employee")
    repo.find_by_username.return_value = user

    # Act: Call the login method with valid credentials
    result = service.login("brian", "password")

    # Assert: Verify that the returned user has the expected username
    assert result.username == "brian"


def test_login_raises_value_error_for_unknown_username(mocker):
    # Arrange: Mock setup, service instance, and stubbed repo return values
    repo = mocker.Mock()
    service = AuthenticationService(
        repo,
        jwt_secret=TEST_JWT_SECRET,
        token_expiration_hours=TEST_TOKEN_EXPIRATION_HOURS,
    )
    repo.find_by_username.return_value = None

    # Act & Assert: Attempt to login with an unknown username and expect a ValueError
    with pytest.raises(ValueError, match="Invalid username or password"):
        service.login("missing", "password")


def test_login_raises_value_error_for_bad_password(mocker):
    # Arrange: Mock setup, service instance, and expected return values
    repo = mocker.Mock()
    service = AuthenticationService(
        repo,
        jwt_secret=TEST_JWT_SECRET,
        token_expiration_hours=TEST_TOKEN_EXPIRATION_HOURS,
    )

    user = User(id=1, username="brian", password=_hashed("password"), role="Employee")
    repo.find_by_username.return_value = user

    # Act & Assert: Attempt to login with an incorrect password and expect a ValueError
    with pytest.raises(ValueError, match="Invalid username or password"):
        service.login("brian", "wrong")


def test_login_blocks_manager_accounts(mocker):
    # Arrange: Mock setup, service instance, and stubbed repo return values
    repo = mocker.Mock()
    service = AuthenticationService(
        repo,
        jwt_secret=TEST_JWT_SECRET,
        token_expiration_hours=TEST_TOKEN_EXPIRATION_HOURS,
    )

    manager = User(id=3, username="siri", password=_hashed("password"), role="Manager")
    repo.find_by_username.return_value = manager

    # Act & Assert: Attempt to login with a manager account and expect a PermissionError
    with pytest.raises(PermissionError, match="Managers"):
        service.login("siri", "password")


def test_generate_and_verify_jwt_token_round_trip(mocker):
    # Arrange: Mock setup, service instance, and stubbed repo return values
    repo = mocker.Mock()
    service = AuthenticationService(
        repo,
        jwt_secret=TEST_JWT_SECRET,
        token_expiration_hours=TEST_TOKEN_EXPIRATION_HOURS,
    )

    user = User(id=1, username="brian", password=_hashed("password"), role="Employee")

    # Act: Generate a JWT token for the user
    token = service.generate_jwt_token(user)
    payload = service.verify_jwt_token(token)

    # Assert: Verify that the payload contains the expected user information
    assert payload is not None
    assert payload["user_id"] == 1
    assert payload["username"] == "brian"
    
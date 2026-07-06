import bcrypt

from repository.user_model import User
from service.authentication_service import AuthenticationService


TEST_JWT_SECRET = "unit-test-secret-key-at-least-32-chars"


def _hashed(password: str) -> str:
    return bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")


def test_login_returns_employee_on_valid_credentials(mocker):
    repo = mocker.Mock()
    service = AuthenticationService(repo, jwt_secret=TEST_JWT_SECRET)

    user = User(id=1, username="brian", password=_hashed("password"), role="Employee")
    repo.find_by_username.return_value = user

    result = service.login("brian", "password")

    assert result.username == "brian"


def test_login_raises_value_error_for_unknown_username(mocker):
    repo = mocker.Mock()
    service = AuthenticationService(repo, jwt_secret=TEST_JWT_SECRET)
    repo.find_by_username.return_value = None

    try:
        service.login("missing", "password")
        assert False, "Expected ValueError for unknown user"
    except ValueError as exc:
        assert "Invalid username or password" in str(exc)


def test_login_raises_value_error_for_bad_password(mocker):
    repo = mocker.Mock()
    service = AuthenticationService(repo, jwt_secret=TEST_JWT_SECRET)

    user = User(id=1, username="brian", password=_hashed("password"), role="Employee")
    repo.find_by_username.return_value = user

    try:
        service.login("brian", "wrong")
        assert False, "Expected ValueError for invalid password"
    except ValueError as exc:
        assert "Invalid username or password" in str(exc)


def test_login_blocks_manager_accounts(mocker):
    repo = mocker.Mock()
    service = AuthenticationService(repo, jwt_secret=TEST_JWT_SECRET)

    manager = User(id=3, username="siri", password=_hashed("password"), role="Manager")
    repo.find_by_username.return_value = manager

    try:
        service.login("siri", "password")
        assert False, "Expected PermissionError for manager login"
    except PermissionError as exc:
        assert "Managers" in str(exc)


def test_generate_and_verify_jwt_token_round_trip(mocker):
    repo = mocker.Mock()
    service = AuthenticationService(repo, jwt_secret=TEST_JWT_SECRET)

    user = User(id=1, username="brian", password=_hashed("password"), role="Employee")

    token = service.generate_jwt_token(user)
    payload = service.verify_jwt_token(token)

    assert payload is not None
    assert payload["user_id"] == 1
    assert payload["username"] == "brian"

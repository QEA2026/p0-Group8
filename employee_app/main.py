
from api.auth_controller import auth_bp
from api.expense_controller import expense_bp
from config.app_config import AppConfig
from flask import Flask
from repository.expense_repository import ExpenseRepository
from repository.user_repository import UserRepository
from service.authentication_service import AuthenticationService
from service.expense_service import ExpenseService

config = AppConfig()

# 1. Initialize the Flask Application
app = Flask(__name__)

# 2. Set up the dependency injection for the UserRepository and AuthenticationService
# Create the database repository object
user_repo = UserRepository()

# Pass the repository, JWT secret, and JWT expiration to auth service
auth_service = AuthenticationService(
    user_repository=user_repo,
    jwt_secret=config.jwt_secret,
    token_expiration_hours=config.jwt_expiration_hours
)

# Similarly, set up the dependency injection for the ExpenseRepository and ExpenseService
expense_repo = ExpenseRepository()
expense_service = ExpenseService(expense_repository=expense_repo)

# 3. Attach the live service to the app (The Power Strip)
# This is what makes `current_app.auth_service` work in auth.py and auth_controller.py
app.auth_service = auth_service
app.expense_service = expense_service

# 4. Register your web routes (Blueprints)
app.register_blueprint(auth_bp)
app.register_blueprint(expense_bp)


# 5. Turn the server on!
if __name__ == "__main__":
    print("🚀 Starting the Employee Portal Backend on http://127.0.0.1:5000")
    app.run(port=5000, debug=True)

from flask import Flask
from api.auth_controller import auth_bp
from repository.user_repository import UserRepository
from service.authentication_service import AuthenticationService

# 1. Initialize the Flask Application
app = Flask(__name__)

#2. Set up the dependency injection for the UserRepository and AuthenticationService
# Create the database repository object
user_repo = UserRepository()

# Then pass the repository into the service, along with a secret key for JWTs
# (TODO: Hide this secret key in a .env file)
jwt_secret = "super_secret_development_key_123!"
auth_service = AuthenticationService(user_repository=user_repo, jwt_secret=jwt_secret)

# 3. Attach the live service to the app
# This is what makes `current_app.auth_service` work in auth.py and auth_controller.py
app.auth_service = auth_service

# 4. Register your web routes (Blueprints)
app.register_blueprint(auth_bp)

# 5. Turn the server on!
if __name__ == '__main__':
    print("🚀 Starting the Employee Portal Backend on http://127.0.0.1:5000")
    app.run(port=5000, debug=True)
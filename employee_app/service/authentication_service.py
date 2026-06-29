# Authentication service for handling user authentication and JWT token generation
from repository.user_model import User
from repository.user_repository import UserRepository
import jwt
import os
import bcrypt
from datetime import datetime, timedelta, timezone
from typing import Optional, Dict, Any



class AuthenticationService:
    def __init__(self, user_repository: UserRepository, jwt_secret: str, jwt_algorithm: str = "HS256"):
        self.user_repository = user_repository
        # Best Practice: Pull secret key from environment variables, fallback to default for dev
        self.jwt_secret = jwt_secret or os.environ.get("JWT_SECRET", 'dev-secret-key')
        self.jwt_algorithm = jwt_algorithm
        try:
            hours_config = int(os.environ.get("TOKEN_EXPIRY_HOURS", 24))
        except (TypeError, ValueError):
            hours_config = 24
        self.token_expiry_hours = hours_config
        self.token_expiry = timedelta(hours=hours_config)

    # Registers a new user by hashing their password and saving to the database (used by seed.sql dynamic seeding).
    def register_user(self, username: str, raw_password: str, role: str) -> User:
        """Hashes the password with bcrypt and saves a new user to the database."""
        salt = bcrypt.gensalt()
        # Bcrypt requires bytes, so we encode, hash, and decode back to a string for the DB
        hashed_password = bcrypt.hashpw(raw_password.encode('utf-8'), salt).decode('utf-8')
        
        new_user = User(id=None, username=username, password=hashed_password, role=role)
        return self.user_repository.create_user(new_user)
    
    def authenticate_user(self, username: str, password: str) -> Optional[User]:
        user = self.user_repository.find_by_username(username)
        
        # Check if user exists and if the raw password bytes match the hashed password bytes
        if user and bcrypt.checkpw(password.encode('utf-8'), user.password.encode('utf-8')):
            return user
        return None
    
    def get_user_by_id(self, user_id: int) -> Optional[User]:
        return self.user_repository.find_by_id(user_id)
    
    # Generates a JWT token for the authenticated user
    def generate_jwt_token(self, user: User) -> str:
        now = datetime.now(timezone.utc) # Modernized timezone handling
        payload = {
            'user_id': user.id,
            'username': user.username,
            'role': user.role,
            'exp': now + self.token_expiry,
            'iat': now
        }
        return jwt.encode(payload, self.jwt_secret, algorithm=self.jwt_algorithm)
    
    # Verifies a JWT token and returns the payload if valid
    def verify_jwt_token(self, token: str) -> Optional[Dict[str, Any]]:
        try:
            return jwt.decode(token, self.jwt_secret, algorithms=[self.jwt_algorithm])
        except (jwt.ExpiredSignatureError, jwt.InvalidTokenError):
            # Grouped exceptions together
            return None

    # Validates a JWT token and returns the payload if valid
    def parse_token(self, token: str) -> Optional[User]:
        payload = self.verify_jwt_token(token)
        if payload:
            return self.get_user_by_id(payload['user_id'])
        return None
    
    # Handles the login process for a user, including authentication and role-based access control
    def login(self, username: str, raw_password: str) -> User:
        # 1. Fetch the user from the repository
        user = self.user_repository.find_by_username(username)
        if not user:
            raise ValueError("Invalid username or password.")
            
        # 2. Verify the bcrypt password hash
        if not bcrypt.checkpw(raw_password.encode('utf-8'), user.password.encode('utf-8')):
            raise ValueError("Invalid username or password.")
            
        # 3. THE BLOCK: Intercept managers and route them away
        if user.role.lower() == 'manager':
            raise PermissionError(
                "Access Denied: Managers must manage tasks and authenticate "
                "exclusively through the corporate Job Application portal."
            )
            
        # 4. If they are an Employee, let them pass and return their session token/data
        return user
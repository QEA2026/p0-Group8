# Authentication module for verifying and managing JWT tokens for user authentication.

from functools import wraps
from flask import request, jsonify, current_app
from service.authentication_service import AuthenticationService

def get_auth_service() -> 'AuthenticationService':
    return current_app.auth_service

def require_employee_auth(f):
    """Decorator to require employee authentication for a route. 
    Verifies the JWT token and ensures the user has an 'Employee' role.
    Returns 401 if the token is missing or invalid, 403 if the user is not an employee.
    """
    @wraps(f)
    def decorated_function(*args, **kwargs):
        token = request.cookies.get('jwt_token')

        if not token:
            return jsonify({'error': 'Authentication token is missing'}), 401
        auth_service = get_auth_service()
        
        user = auth_service.parse_token(token)
        if not user:
            return jsonify({'error': 'Invalid or expired authentication token'}), 401
            
        if user.role.lower() != 'employee':
            return jsonify({'error': 'Access forbidden: Employee role required'}), 403
            
        request.current_user = user

        return f(*args, **kwargs)
    return decorated_function

def get_current_user():
    """Helper function to get the currently authenticated user based on the JWT token."""
    return getattr(request, 'current_user', None)
# Authentication controller for handling API requests related to user authentication, such as login and token validation
from flask import Blueprint, request, jsonify, make_response, current_app
from service.authentication_service import AuthenticationService


auth_bp = Blueprint('auth', __name__, url_prefix='/auth')

def get_auth_service() -> AuthenticationService:
    return current_app.auth_service

@auth_bp.route('/login', methods=['POST'])
def login():
    """Employee login endpoint. Expects JSON payload with 'username' and 'password'(raw)."""
    try:
        data = request.get_json()
        if not data or not data.get('username') or not data.get('password'):
            return jsonify({"error": "Username and password are required."}), 400
        
        username = data.get('username')
        password = data.get('password')

        auth_service = get_auth_service()
        user = auth_service.login(username, password)

        token = auth_service.generate_jwt_token(user)

        response_data = {
            'message': f'Welcome back, {user.username}!',
            'user' : {
                'id': user.id,
                'username': user.username,
                'role': user.role
            }
        }
        response = make_response(jsonify(response_data))

        response.set_cookie(
            'jwt_token', token, httponly=True, secure=False, samesite='Lax',max_age=int(auth_service.token_expiry.total_seconds())
        )

        return response
    
    except ValueError as ve:
        # Catches wrong password or username
        return jsonify({'error': str(ve)}), 401
    except PermissionError as pe:
        # Catches the Manager trying to log in
        return jsonify({'error': str(pe)}), 403
    except Exception as e:
        return jsonify({'error': 'Login failed', 'details': str(e)}), 500

@auth_bp.route('/logout', methods=['POST'])
def logout():
    """Employee logout endpoint. Clears the JWT token cookie. """
    response = make_response(jsonify({'message': 'Logged out successfully'}))
    
    response.set_cookie('jwt_token', '', expires=0, httponly=True, samesite='lax')
    
    return response


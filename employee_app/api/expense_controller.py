from flask import Blueprint, request, jsonify, current_app
from api.auth import require_employee_auth

# 1. Create the Blueprint (The routing map)
expense_bp = Blueprint('expense', __name__, url_prefix = '/expenses')

# 2. Define the route and apply the Bouncer
@expense_bp.route('/submit', methods=['POST'])
@require_employee_auth
def submit_expense():
    """Handles incoming expense JSON, passes it to the service, and returns a response."""
    
    # A. Who did the bouncer let in? (Attached in auth.py)
    user = request.current_user

    # B. Open the JSON package from the CLI/Postman (See below for format)
    data = request.get_json()

    # C. Hand off to the Service layer
    try:
        # Grab the service from the live app (Thise is wired through main.py as per service layer convention)
        expense_service = current_app.expense_service
        
        # Pass the unpacked data to the business logic
        new_expense = expense_service.create_expense(
            user_id=user.id,
            amount=data.get('amount'),
            description=data.get('description'),
            category=data.get('category'),
            expense_date=data.get('expense_date')
        )
        
        # D. Success! Send the receipt back.
        return jsonify({
            "message": "Expense submitted successfully!",
            "expense_id": new_expense.id
        }), 201

    except ValueError as e:
        # If the Service catches bad data (e.g., negative amount, invalid category),
        # it raises a ValueError and returns a 400 Bad Request.
        return jsonify({"error": str(e)}), 400
        
    except Exception as e:
        # A catch-all for any unexpected database crashes
        return jsonify({"error": "An internal server error occurred.", "details": str(e)}), 500


@expense_bp.route('/ledger', methods=['GET'])
@require_employee_auth
def get_ledger():
    """Returns grouped ledger data for the authenticated employee."""
    # A. Identify the logged-in employee attached by the auth decorator
    user = request.current_user

    try:
        # B. Resolve the live service from Flask app context
        expense_service = current_app.expense_service

        # C. Ask the service for grouped ledger views (pending + history)
        ledger_data = expense_service.get_user_ledger(user.id)

        # D. Return the grouped payload for CLI/client rendering
        return jsonify(ledger_data), 200

    except ValueError as e:
        # Service-level validation/processing errors
        return jsonify({"error": str(e)}), 400

    except Exception as e:
        # Catch-all for unexpected failures
        return jsonify({"error": "An internal server error occurred.", "details": str(e)}), 500
    

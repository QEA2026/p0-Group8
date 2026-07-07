import requests
import config

session = requests.Session()


#auth 
def login(username, password):
    payload = {
        "username": username,
        "password": password
    }

    response = session.post(config.LOGIN_URL, json=payload)
    return response


def logout():
    return session.post(config.LOGOUT_URL)


#expenses
def submit_expense(amount, description, category, expense_date):
    payload = {
        "amount": amount,
        "description": description,
        "category": category,
        "expense_date": expense_date
    }

    return session.post(config.SUBMIT_EXPENSE_URL, json=payload)


def get_ledger():
    return session.get(config.LEDGER_URL)


def get_pending():
    return session.get(config.PENDING_URL)


def update_expense(expense_id, amount, description):
    payload = {
        "amount": amount,
        "description": description
    }

    url = f"{config.BASE_URL}/expenses/{expense_id}"
    return session.put(url, json=payload)


def delete_expense(expense_id):
    url = f"{config.BASE_URL}/expenses/{expense_id}"
    return session.delete(url)
from api_client import login
from utils import get_non_empty

def login_flow():
    print("\n--- Login ---")

    username = get_non_empty("Username: ")
    password = get_non_empty("Password: ")

    response = login(username, password)

    if response.status_code == 200:
        print("Login successful!")
        return True
    else:
        print("Login failed:", response.json().get("error"))
        return False
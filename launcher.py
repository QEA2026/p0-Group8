import subprocess

def run_employee():
    result = subprocess.run(
        ["python", "employee_app/cli/main.py"]
    )

    return result.returncode

def run_manager():
    result = subprocess.run(
        [
            "mvn",
            "exec:java",
            "-Dexec.mainClass=com.revature.expensemanager.cli.MainCLI"
        ],
        cwd="manager-app"
    )

    return result.returncode


while True:

    print("\n==============================")
    print(" Revature Expense Manager ")
    print("==============================")
    print("1. Login as Employee")
    print("2. Login as Manager")
    print("0. Exit Program")

    choice = input("Choose: ")

    if choice == "1":
        result = run_employee()
        if result == 0:
            print("Goodbye!")
            break
        elif result == 1:
            continue

    elif choice == "2":
        result = run_manager()
        if result == 0:
            print("Goodbye!")
            break

    elif choice == "0":
        print("goodbye.")
        break

    else:
        print("Invalid choice, try again.")
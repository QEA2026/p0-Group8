# Revature Expense Manager
![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

The Revature Expense Manager is a dual-application platform that separates employee and manager experiences into purpose-built clients while sharing one secure data backbone.

## 1. Overview 📌
> Revature Expense Manager delivers a unified expense workflow through two coordinated applications: a Python Employee App for submission and self-service updates, and a Java Manager App for review, approval, and reporting. Both applications operate against a shared SQLite database to keep records consistent across the full lifecycle.

## 2. Features & Stretch Goals ✅
- Unified launcher via `launcher.py` to route users into Employee or Manager workflows from one entry point.
- JWT-based session handling in both app contexts for secure, role-aligned access control.
- Password hashing with bcrypt before persisted user credentials are stored.
- Structured Java logging (console + file appender) for operational visibility during live runs.
- Cross-app testing strategy with Python's pytest + mock for integration & unit tests and Java Junit + mockito tests for service layer logic.
- Shared schema design to enable coordinated approvals, auditability, and downstream export/reporting paths.

## 3. User Flow 👥
- Employee App (Python): authenticate, submit expenses, view ledger, view pending items, edit pending expenses, delete pending expenses.
- Manager App (Java): authenticate as manager, review pending submissions, approve or deny expenses, and generate manager-facing reporting outputs.

## 4. Tech Stack & Architecture 🏗️
- Employee Client: Python CLI utilizing the requests library to interact with Flask-backed API modules.
- Manager Client: Java (Maven) CLI application utilizing Javalin for HTTP routing and Jackson for JSON/CSV serialization.
- Data Layer: Shared SQLite database (`database/expense_manager.db`) used by both applications.
- Security: JWT authentication and bcrypt password hashing.
- Testing: Integration and unit testing powered by pytest + pytest-mock (Python) and JUnit 5 + Mockito (Java).
- Observability: Logback-backed Java logs written to `manager-app/logs/manager-app.log`.

## 5. Database Schema 🗃️
<details>
<summary><strong>Expand schema tables</strong></summary>

### users
| Column | Type | Constraints | Description |
|---|---|---|---|
| id | INTEGER | PRIMARY KEY, AUTOINCREMENT | Unique user identifier |
| username | TEXT | UNIQUE, NOT NULL | Login username |
| password | TEXT | NOT NULL | Hashed password value |
| role | TEXT | NOT NULL | User role (`employee` or `manager`) |

### expenses
| Column | Type | Constraints | Description |
|---|---|---|---|
| id | INTEGER | PRIMARY KEY, AUTOINCREMENT | Unique expense identifier |
| userId | INTEGER | NOT NULL, FK -> users.id | Expense owner |
| amount | REAL | NOT NULL | Expense amount |
| description | TEXT | NOT NULL | Expense narrative |
| category | TEXT | NOT NULL, CHECK | Expense classification |
| date | TEXT | NOT NULL | Expense date (ISO string) |

Allowed `category` values:
- `TRAVEL`
- `MEALS`
- `LODGING`
- `OFFICE_SUPPLIES`
- `EQUIPMENT`
- `SOFTWARE`
- `TRAINING`
- `OTHER`

### approvals
| Column | Type | Constraints | Description |
|---|---|---|---|
| id | INTEGER | PRIMARY KEY, AUTOINCREMENT | Unique approval identifier |
| expenseId | INTEGER | NOT NULL, UNIQUE, FK -> expenses.id | Target expense |
| status | TEXT | NOT NULL | Approval state (`pending`, `approved`, `denied`) |
| reviewer | INTEGER | FK -> users.id | Manager reviewer id |
| comment | TEXT | Nullable | Manager decision note |
| review_date | TEXT | Nullable | Review timestamp/date |

</details>

## 6. Setup & Installation ⚙️
<details>
<summary><strong>Expand setup instructions</strong></summary>

### Prerequisites
- Python 3.11+ recommended
- Java 17+ recommended
- Maven 3.9+

### Step 1: Configure environment files
Create the employee environment file at `employee_app/.env`:

```env
JWT_SECRET=replace-with-your-secret-employee
JWT_EXPIRATION_HOURS=24
```

Create the manager environment file at `manager-app/.env`:

```env
JWT_SECRET=replace-with-your-secret-manager
JWT_EXPIRATION_HOURS=24
```

### Step 2: Install Python dependencies
From the workspace root:

```bash
pip install -r requirements.txt
```

### Step 3: Initialize and seed the shared database
From the workspace root:

```bash
python database/init_db.py
```

### Step 4: Resolve Java dependencies
From `manager-app`:

```bash
mvn -q validate
```

</details>

## 7. API Reference 🔌

<details>
<summary><strong>Expand API Endpoints & JSON Shapes</strong></summary>

*Select an application below to view its specific routing and JSON schemas:*


<details>
<summary><strong>Employee App API (Python Flask)</strong></summary>

### Employee App API (Python Flask)

Base route prefixes are defined in blueprints:
- Auth: `/auth`
- Expenses: `/expenses`

#### POST /auth/login
- Description: Authenticates an employee and sets `jwt_token` as an HTTP-only cookie.
- Request Body JSON:

```json
{
	"username": "string",
	"password": "string"
}
```

- Success Response JSON (200):

```json
{
	"message": "Welcome back, <username>!",
	"user": {
		"id": 1,
		"username": "string",
		"role": "Employee"
	}
}
```

#### POST /auth/logout
- Description: Clears the employee `jwt_token` cookie.
- Request Body JSON: None
- Success Response JSON (200):

```json
{
	"message": "Logged out successfully"
}
```

#### POST /expenses/submit
- Description: Creates a new expense for the authenticated employee.
- Request Body JSON:

```json
{
	"amount": "number|string",
	"description": "string",
	"category": "TRAVEL|MEALS|LODGING|OFFICE_SUPPLIES|EQUIPMENT|SOFTWARE|TRAINING|OTHER",
	"expense_date": "YYYY-MM-DD"
}
```

- Success Response JSON (201):

```json
{
	"message": "Expense submitted successfully and is now pending manager review.",
	"next_step": "string",
	"expense_id": 10,
	"amount": "22.00"
}
```

#### GET /expenses/ledger
- Description: Returns pending expenses and non-pending history for the authenticated employee.
- Request Body JSON: None
- Success Response JSON (200):

```json
{
	"pending_expenses": [
		{
			"expense_id": 1,
			"user_id": 1,
			"amount": "22.00",
			"description": "string",
			"category": "string",
			"expense_date": "YYYY-MM-DD",
			"status": "pending|approved|denied",
			"manager_comment": "string|null",
			"review_date": "YYYY-MM-DD|null"
		}
	],
	"expense_history": [
		{
			"expense_id": 2,
			"user_id": 1,
			"amount": "10.00",
			"description": "string",
			"category": "string",
			"expense_date": "YYYY-MM-DD",
			"status": "approved|denied",
			"manager_comment": "string|null",
			"review_date": "YYYY-MM-DD|null"
		}
	],
	"message": "Ledger retrieved successfully. Pending and history are included.",
	"summary": {
		"pending_count": 1,
		"history_count": 1
	},
	"next_step": "string"
}
```

#### GET /expenses/pending
- Description: Returns only pending expenses for the authenticated employee.
- Request Body JSON: None
- Success Response JSON (200):

```json
{
	"pending_expenses": [
		{
			"expense_id": 1,
			"user_id": 1,
			"amount": "22.00",
			"description": "string",
			"category": "string",
			"expense_date": "YYYY-MM-DD",
			"status": "pending",
			"manager_comment": "string|null",
			"review_date": "YYYY-MM-DD|null"
		}
	]
}
```

#### PUT /expenses/{expense_id}
- Description: Updates amount and description of an owned pending expense.
- Request Body JSON:

```json
{
	"amount": "number|string",
	"description": "string"
}
```

- Success Response JSON (200):

```json
{
	"message": "Expense updated successfully.",
	"next_step": "string"
}
```

#### DELETE /expenses/{expense_id}
- Description: Deletes an owned pending expense.
- Request Body JSON: None
- Success Response JSON (200):

```json
{
	"message": "Expense deleted successfully.",
	"next_step": "string"
}
```

</details>

&nbsp;

<details>
<summary><strong>Manager App API (Java Javalin)</strong></summary>

### Manager App API (Java Javalin)

Routes are defined in `manager-app/src/main/java/com/revature/expensemanager/Main.java`.

Auth behavior:
- `Authorization: Bearer <token>` with a manager role is required for `/expenses/*` and `/reports/*`.

#### POST /login
- Description: Authenticates manager credentials and returns a manager JWT.
- Request Body JSON (`LoginRequest`):

```json
{
	"username": "string",
	"password": "string"
}
```

- Success Response JSON (200) (`LoginResponse`):

```json
{
	"id": 1,
	"username": "string",
	"role": "manager",
	"token": "jwt-string"
}
```

#### GET /expenses/pending
- Description: Returns all pending expenses for manager review.
- Request Body JSON: None
- Success Response JSON (200):

```json
[
	{
		"id": 1,
		"userId": 2,
		"amount": 49.99,
		"description": "string",
		"category": "string",
		"date": "YYYY-MM-DD"
	}
]
```

#### PUT /expenses/{id}/review
- Description: Approves or denies a specific expense.
- Request Body JSON (`ApprovalRequest`):

```json
{
	"status": "approved|denied",
	"comment": "string"
}
```

- Success Response JSON (200):

```json
"Expense reviewed successfully"
```

#### GET /reports/employee?userId={id}[&export=true]
- Description: Returns expenses for one employee by user id. When `export=true`, response includes `Report-File` header with CSV path.
- Request Body JSON: None
- Success Response JSON (200):

```json
[
	{
		"id": 1,
		"userId": 2,
		"amount": 49.99,
		"description": "string",
		"category": "string",
		"date": "YYYY-MM-DD"
	}
]
```

#### GET /reports/category?category={value}[&export=true]
- Description: Returns expenses for one category. When `export=true`, response includes `Report-File` header.
- Request Body JSON: None
- Success Response JSON (200): array of `Expense` objects (same shape as above).

#### GET /reports/date?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD[&export=true]
- Description: Returns expenses within inclusive date range. When `export=true`, response includes `Report-File` header.
- Request Body JSON: None
- Success Response JSON (200): array of `Expense` objects (same shape as above).

#### GET /employees
- Description: Returns employee lookup values used for manager reporting workflows.
- Request Body JSON: None
- Success Response JSON (200) (`EmployeeSummary[]`):

```json
[
	{
		"id": 1,
		"username": "string"
	}
]
```

</details>

</details>

## 8. Usage & Testing 🧪
### Running the Main app
From the workspace root:

```bash
python launcher.py
```
This entrypoint displays the role selector and dispatches users into based on choice:
- Employee flow (Python CLI)
- Manager flow (Java Maven CLI)

When successfully logged in, users are directed into their respective CLI environments seamlessly:

**👤 Employee Menu Options (Python CLI):**
- Submit a new expense
- View complete expense ledger
- View only pending expenses
- Edit a pending expense
- Delete a pending expense

<br>

**👔 Manager Menu Options (Java CLI):**
- View all pending employee expenses
- Review (Approve/Deny) an expense with mandatory comments
- Generate Expense Reports (Filter by Employee, Category, or Date)
- Export reports to CSV

### Run tests
Python test suite (workspace root):

```bash
python -m pytest -q
```

Java test suite (`manager-app`):

```bash
mvn test
```

### Find Java logs
- Runtime logs are written to `manager-app/logs/manager-app.log`.
- Logging configuration is defined in `manager-app/src/main/resources/logback.xml`.

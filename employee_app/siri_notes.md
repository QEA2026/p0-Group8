# Siri CLI Handoff Notes

This note is a quick handoff guide for building the CLI side against the current employee backend.

As a major note: you will not be importing or calling a single Python function from the Service or Repository layers. 
All should be accomplishable through calling the Flask methods and *maybe* adding another layer of input validation for handling.
Only if you feel it necessary. (Possibly for: Menu Selections, Login Component (Empty Inputs), Date Formatting (YYYY-MM-DD)).
Meaning the explanations of service and repository function below aren't essential, only for your reference

However there is one case you will run into: expense creation. Right now, validation only happens at the ENTIRE payload delivery (bc create_expense in expense_service checks it as a whole, not part by part. But it does tell you what in the payload is wrong but only one at a time. If multiple things are wrong it will start with what it first checks). Meaning, users will have to retype their whole expense rather than just the part that is wrong (annoying if you have a long description or something). Unless, you choose to handle expense creation in steps with your own validation logic at each step which lines up with accepted payload standards AND THEN send it over so it's certain to not bounce back.

## Big Picture Workflow

1. User logs in with username/password.
2. Backend sets an HTTP-only cookie (`jwt_token`).
3. CLI reuses that session cookie for all protected expense endpoints.
4. User can:
	 - submit expenses,
	 - view full ledger (pending + history),
	 - view pending-only list,
	 - update pending expenses,
	 - delete pending expenses.
5. Approval/denial transitions are manager-side (not controlled from this employee API).

## Endpoints You Will Use (CLI Side)

### Auth

- `POST /auth/login`
	- Purpose: start authenticated session via cookie.
	- Body: `{"username": "...", "password": "..."}`
	- Success: includes user payload and sets cookie.

- `POST /auth/logout`
	- Purpose: clear auth cookie and end session.

### Employee Expense Actions

- `POST /expenses/submit`
	- Purpose: create new expense.
	- Body fields used: `amount`, `description`, `category`, `expense_date`.
	- `user_id` from payload is ignored; backend uses logged-in user from auth.
	- Success now returns formatted amount (example: `"46.50"`).

- `GET /expenses/ledger`
	- Purpose: read grouped data for display:
		- `pending_expenses`
		- `expense_history`

- `GET /expenses/pending`
	- Purpose: fetch pending-only rows for edit/delete selection.
	- Best endpoint for the "Eraser" CLI menu.

- `PUT /expenses/<expense_id>`
	- Purpose: update pending expense fields.
	- Editable fields: `amount`, `description` only.
	- Backend guards:
		- must exist,
		- must belong to logged-in user,
		- must be `pending`.

- `DELETE /expenses/<expense_id>`
	- Purpose: delete a pending expense.
	- Same guards as update.

## Backend Functions (What/Where/Why) (Service/Repository)

### Service Layer (business rules)

File: `employee_app/service/expense_service.py`

- `create_expense(...)`
	- Why it matters for CLI: enforces input rules (amount/category/date/description).

- `get_user_ledger(user_id)`
	- Why it matters for CLI: returns the two display sections directly for your ledger screen.

- `get_pending_expenses(user_id)`
	- Why it matters for CLI: ideal source list before showing "pick expense id to edit/delete".

- `update_pending_expense(...)`
	- Why it matters for CLI: all permission/state checks happen here; CLI should show returned errors cleanly.

- `delete_pending_expense(...)`
	- Why it matters for CLI: same guard logic, so no client-side trust assumptions needed.

- `format_currency_amount(amount)`
	- Why it matters for CLI: backend returns consistent 2-decimal formatting; display directly.

### Repository Layer (SQL behavior)

File: `employee_app/repository/expense_repository.py`

- `get_expenses_by_user(user_id)`
	- Uses joined expense+approval rows.
	- Sorted by newest first: `date DESC, id DESC`.

- `find_expense_with_status(expense_id)`
	- Used to enforce pending + ownership checks before mutation.

- `update_expense(expense_id, amount, description)`
	- SQL update is intentionally limited to these two fields.

- `delete_expense(expense_id)`
	- Deletes approval row first, then expense row.

## CLI Implementation Suggestions

1. Use `requests.Session()` and keep one shared session object after login.
2. Build a simple menu flow:
	 - Login
	 - Submit expense
	 - View ledger
	 - Edit pending expense
	 - Delete pending expense
	 - Logout/Exit
3. For edit/delete:
	 - call `GET /expenses/pending`,
	 - render IDs + key fields,
	 - prompt user to choose an ID,
	 - call `PUT` or `DELETE`.
4. Always print backend error messages from JSON (`error`) for fast debugging.

## Input/Output Notes

- Amount rules:
	- accepted: `46`, `46.5`, `46.50`
	- rejected: `<= 0` and `> 2` decimal places
- Categories must be one of:
	- `TRAVEL`, `MEALS`, `LODGING`, `OFFICE_SUPPLIES`, `EQUIPMENT`, `SOFTWARE`, `TRAINING`, `OTHER`
- Date format must be `YYYY-MM-DD`.

## Known Boundaries

- Employee app cannot approve/deny expenses.
- To test history transitions end-to-end, you need manager-side actions (or direct DB setup for local testing).

## Quick Starter Payloads

### Login

```json
{
	"username": "brian",
	"password": "password"
}
```

### Submit

```json
{
	"amount": "22",
	"description": "Lost in the jungle and ordered a machete on amazon.",
	"category": "OTHER",
	"expense_date": "2026-04-01"
}
```

### Update

```json
{
	"amount": "24.50",
	"description": "Updated description from CLI"
}
```

If anything returns `401`, session cookie was likely lost or login was not completed in the same session.

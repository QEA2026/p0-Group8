package com.revature.expensemanager;

import com.revature.expensemanager.controller.AuthController;
import com.revature.expensemanager.controller.ExpenseController;
import com.revature.expensemanager.controller.ReportController;
import com.revature.expensemanager.dao.ApprovalDAO;
import com.revature.expensemanager.dao.ExpenseDAO;
import com.revature.expensemanager.dao.JdbcApprovalDAO;
import com.revature.expensemanager.dao.JdbcExpenseDAO;
import com.revature.expensemanager.dao.JdbcReportDAO;
import com.revature.expensemanager.dao.JdbcUserDAO;
import com.revature.expensemanager.dao.ReportDAO;
import com.revature.expensemanager.dao.UserDAO;
import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.service.AuthService;
import com.revature.expensemanager.service.ExpenseService;
import com.revature.expensemanager.service.ReportService;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.servlet.http.HttpSession;

public class Main {

    public static void main(String[] args) {
        UserDAO userDAO = new JdbcUserDAO();
        ExpenseDAO expenseDAO = new JdbcExpenseDAO();
        ApprovalDAO approvalDAO = new JdbcApprovalDAO();
        ReportDAO reportDAO = new JdbcReportDAO();

        AuthService authService = new AuthService(userDAO);
        ExpenseService expenseService = new ExpenseService(expenseDAO, approvalDAO);
        ReportService reportService = new ReportService(reportDAO);

        AuthController authController = new AuthController(authService);
        ExpenseController expenseController = new ExpenseController(expenseService);
        ReportController reportController = new ReportController(reportService);

        Javalin app = Javalin.create(config -> {
            config.routes.post("/login", authController::login);

            config.routes.before("/expenses/*", Main::requireManager);
            config.routes.before("/reports/*", Main::requireManager);

            config.routes.get("/expenses/pending", expenseController::getPendingExpenses);
            config.routes.put("/expenses/{id}/review", expenseController::reviewExpense);

            config.routes.get("/reports/employee", reportController::getExpensesByEmployee);
            config.routes.get("/reports/category", reportController::getExpensesByCategory);
            config.routes.get("/reports/date", reportController::getExpensesByDateRange);

            config.routes.exception(RuntimeException.class, (e, ctx) -> {
                e.printStackTrace();
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .json(new ErrorResponse("Internal server error."));
            });
        });

        app.start(7000);
    }

    private static void requireManager(Context ctx) {
        HttpSession session = ctx.req().getSession(false);

        if (session == null) {
            ctx.status(HttpStatus.UNAUTHORIZED)
                    .json(new ErrorResponse("Login required."));
            ctx.skipRemainingHandlers();
            return;
        }
    }
}

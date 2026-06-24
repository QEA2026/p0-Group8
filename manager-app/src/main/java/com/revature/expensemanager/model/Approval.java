package com.revature.expensemanager.model;

public class Approval {
    private int id;
    private int expenseId; // FK -> expenses.id
    private String status;
    private Integer reviewer; // FK -> users.id (null if not reviewed)
    private String comment;
    private String review_date;

    // No-args constructor
    public Approval() {
    }

    // Constructor for creating a new approval record
    public Approval(int expenseId, String status, Integer reviewer, String comment, String review_date) {
        this.expenseId = expenseId;
        this.status = status;
        this.reviewer = reviewer;
        this.comment = comment;
        this.review_date = review_date;
    }

    // Constructor for existing approvals from database
    public Approval(int id, int expenseId, String status, Integer reviewer, String comment, String review_date) {
        this.id = id;
        this.expenseId = expenseId;
        this.status = status;
        this.reviewer = reviewer;
        this.comment = comment;
        this.review_date = review_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getReviewer() {
        return reviewer;
    }

    public void setReviewer(Integer reviewer) {
        this.reviewer = reviewer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReview_date() {
        return review_date;
    }

    public void setReview_date(String review_date) {
        this.review_date = review_date;
    }

    @Override
    public String toString() {
        return "Approval [id=" + id + ", expenseId=" + expenseId + ", status=" + status + ", reviewer=" + reviewer
                + ", comment=" + comment + ", review_date=" + review_date + "]";
    }
}

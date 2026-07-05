package com.revature.expensemanager.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.revature.expensemanager.model.Expense;

public class ReportExportService {
    private static final String REPORTS_DIRECTORY = "reports";
    private static final DateTimeFormatter FILE_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public String exportExpensesToCsv(List<Expense> expenses, String reportName) {
        String fileName = reportName + "_"
                + LocalDateTime.now().format(FILE_TIMESTAMP_FORMAT)
                + ".csv";

        try {
            Files.createDirectories(Path.of(REPORTS_DIRECTORY));

            File file = Path.of(REPORTS_DIRECTORY, fileName).toFile();

            CsvMapper csvMapper = new CsvMapper();

            CsvSchema schema = csvMapper.schemaFor(Expense.class).withHeader();

            csvMapper.writer(schema).writeValue(file, expenses);

            return file.getPath();
        } catch (IOException e) {
            throw new RuntimeException("Error exporting report to CSV file.", e);
        }
    }
}

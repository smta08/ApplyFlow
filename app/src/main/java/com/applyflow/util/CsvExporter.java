package com.applyflow.util;

import android.content.Context;

import com.applyflow.data.db.ApplicationEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class CsvExporter {

    private CsvExporter() {
    }

    private static final String HEADER =
            "Company,Role,Status,Priority,Date applied,Location,Salary,Source,"
                    + "Contact name,Contact email,Link,Notes,Job description,Created at";

    public static File export(Context context, List<ApplicationEntity> applications) throws IOException {
        File dir = new File(context.getCacheDir(), "exports");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "applyflow_export.csv");
        try (OutputStreamWriter writer =
                     new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(buildCsv(applications));
        }
        return file;
    }

    public static String buildCsv(List<ApplicationEntity> applications) {
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append("\r\n");
        if (applications != null) {
            for (ApplicationEntity a : applications) {
                sb.append(escape(a.company)).append(',')
                        .append(escape(a.role)).append(',')
                        .append(escape(a.status)).append(',')
                        .append(escape(priorityText(a.priority))).append(',')
                        .append(escape(a.dateApplied)).append(',')
                        .append(escape(a.location)).append(',')
                        .append(escape(a.salary)).append(',')
                        .append(escape(a.source)).append(',')
                        .append(escape(a.contactName)).append(',')
                        .append(escape(a.contactEmail)).append(',')
                        .append(escape(a.link)).append(',')
                        .append(escape(a.notes)).append(',')
                        .append(escape(a.jobDescription)).append(',')
                        .append(escape(a.createdAt)).append("\r\n");
            }
        }
        return sb.toString();
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static String priorityText(int priority) {
        switch (priority) {
            case Constants.PRIORITY_HIGH:
                return "High";
            case Constants.PRIORITY_TOP:
                return "Top";
            default:
                return "Normal";
        }
    }
}

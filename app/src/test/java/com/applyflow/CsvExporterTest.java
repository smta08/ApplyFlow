package com.applyflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.applyflow.data.db.ApplicationEntity;
import com.applyflow.util.CsvExporter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CsvExporterTest {

    @Test
    public void escapesPlainValueUnchanged() {
        assertEquals("Acme", CsvExporter.escape("Acme"));
    }

    @Test
    public void escapesNullToEmpty() {
        assertEquals("", CsvExporter.escape(null));
    }

    @Test
    public void quotesValueWithComma() {
        assertEquals("\"Acme, Inc\"", CsvExporter.escape("Acme, Inc"));
    }

    @Test
    public void doublesInnerQuotes() {
        assertEquals("\"a\"\"b\"", CsvExporter.escape("a\"b"));
    }

    @Test
    public void buildCsvHasHeaderAndRow() {
        List<ApplicationEntity> apps = new ArrayList<>();
        apps.add(new ApplicationEntity("Acme, Inc", "Engineer", null,
                "APPLIED", null, "2026-06-01", "2026-06-01T00:00:00"));

        String csv = CsvExporter.buildCsv(apps);

        assertTrue(csv.startsWith("Company,Role,Status"));
        assertTrue(csv.contains("\"Acme, Inc\""));
        assertTrue(csv.contains("Engineer"));
    }
}

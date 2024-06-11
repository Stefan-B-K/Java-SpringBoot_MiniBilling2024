package com.istef.minibilling2024.service.billingService;


import com.istef.minibilling2024.entity.Line;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BillingAppTest {
    private final static String PATH_TESTS = "/Users/stefan_kozuharov/УПРАЖНЕНИЯ/JAVA/MiniBilling2024/src/test/resources";

    private BillingService billingService;


    void deleteJson(String testPath) {
        File invoicesJson = new File(testPath + "/invoices.json");
        File linesJson = new File(testPath + "/lines.json");
        if (invoicesJson.exists()) invoicesJson.delete();
        if (linesJson.exists()) linesJson.delete();
    }


    @Test
    public void generateInvoicesSc5() {
        String testPath = PATH_TESTS + "/sc5";

        this.billingService = new BillingService(testPath);
        List<Line> invoiceLines = billingService
                .generateInvoices("23-11")
                .get(0)
                .getLines();

        File outCsv = new File(testPath + "/out.csv");
        try (CSVReader csvReader = new CSVReader(new FileReader(outCsv), ',', '"')) {
            String[] row;
            int lineIndex = 0;
            while ((row = csvReader.readNext()) != null) {
                ZonedDateTime rowFirstDate = parseZonedToUTC(row[0]);
                ZonedDateTime rowSecondDate = parseZonedToUTC(row[1]);
                BigDecimal rowQuantity = BigDecimal.valueOf(Double.parseDouble(row[2]));
                double rowPrice = Double.parseDouble(row[3]);

                assertEquals(rowFirstDate, invoiceLines.get(lineIndex).getStartDateUtc());
                assertEquals(rowSecondDate, invoiceLines.get(lineIndex).getEndDateUtc());
                assertEquals(rowQuantity, invoiceLines.get(lineIndex).getQuantity());
                assertEquals(rowPrice, invoiceLines.get(lineIndex++).getPrice());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        deleteJson(testPath);
    }

    private static ZonedDateTime parseZonedToUTC(String dateTime) {
        return ZonedDateTime.parse(dateTime)
                .withZoneSameInstant(ZoneOffset.UTC);
    }

}

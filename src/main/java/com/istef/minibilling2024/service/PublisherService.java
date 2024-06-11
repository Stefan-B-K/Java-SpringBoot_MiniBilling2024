package com.istef.minibilling2024.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.istef.minibilling2024.entity.Invoice;
import com.istef.minibilling2024.jackson.OutputViews;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class PublisherService {
    private static final String FILE_EXT = "json";

    private final String outputPath;
    private final ObjectMapper objectMapper;
    private YearMonth yearMonth;

    public PublisherService(String outputPath) {
        this.outputPath = outputPath;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public void publishToJsonFolders(String yearMonth, List<Invoice> invoices) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM");
        this.yearMonth = YearMonth.parse(yearMonth, formatter);

        for (Invoice invoice : invoices) {
            saveToJson(invoice);
        }
    }

    private void saveToJson(Invoice invoice) {
        File json = invoiceJson(invoice);

        try {
            if (!json.getParentFile().exists() && !json.getParentFile().mkdirs())
                throw new IOException("Failed to create folder " + json.getParentFile());

            if (!json.exists() && !json.createNewFile())
                throw new IOException("Failed to create file " + json.getName());

            objectMapper
                    .writerWithView(OutputViews.Publish.class)
                    .writeValue(json, invoice);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private File invoiceJson(Invoice invoice) {
        String userNames = invoice.getConsumer();
        String refNumber = invoice.getUser().getRefNumber();
        String userFolder = userNames + '-' + refNumber;

        DateTimeFormatter shortFormat = DateTimeFormatter
                .ofPattern("MMMM-yy")
                .localizedBy(new Locale("bg", "BG"));
        String invoiceNumber = invoice.getDocumentNumber();
        String shortDate = yearMonth.format(shortFormat);
        String file = invoiceNumber + '-' + shortDate + '.' + FILE_EXT;

        return Path.of(outputPath, userFolder, file).toFile();
    }
}

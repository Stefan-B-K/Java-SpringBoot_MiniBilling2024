package com.istef.minibilling2024.dao.json;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.istef.minibilling2024.dao.InvoiceDAO;
import com.istef.minibilling2024.entity.Invoice;
import com.istef.minibilling2024.entity.User;
import com.istef.minibilling2024.jackson.OutputViews;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InvoiceJsonDAO implements InvoiceDAO {
    private static final String FILE_NAME = "invoices.json";
    private final File json;
    private List<Invoice> invoiceList;

    private final ObjectMapper objectMapper;

    public InvoiceJsonDAO(String jsonPath) {
        this.json = Path.of(jsonPath, FILE_NAME).toFile();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Override
    public long findLastDocNumber() {
        List<Invoice> all = findAll();
        if (all.isEmpty()) return FIRST_DOC_REF;

        return all.stream()
                .mapToLong(invoice -> Long.parseLong(invoice.getDocumentNumber()))
                .max()
                .getAsLong();
    }

    @Override
    public Optional<Invoice> findLastInvoiceByUser(User user) {
        List<Invoice> all = findAll();
        if (all.isEmpty()) return Optional.empty();

        List<Invoice> invoices = all.stream()
                .filter(invoice -> invoice
                        .getUser().getRefNumber().equals(user.getRefNumber()))
                .collect(Collectors.toList());
        if (invoices.isEmpty()) return Optional.empty();

        return Optional.of(invoices.get(invoices.size() - 1));
    }

    @Override
    public List<Invoice> findAll() {
        if (this.invoiceList != null) return this.invoiceList;
        if (!json.exists()) return new ArrayList<>();

        ObjectReader invoiceListReader = objectMapper.readerForListOf(Invoice.class);
        try {
            invoiceList = invoiceListReader.readValue(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return invoiceList;
    }

    @Override
    public Invoice save(Invoice invoice) {

        try {
            List<Invoice> invoiceList = findAll();
            invoiceList.add(invoice);

            if (!json.getParentFile().exists() && !json.getParentFile().mkdirs())
                throw new IOException("Failed to create folder " + json.getParentFile());

            if (!json.exists() && !json.createNewFile())
                throw new IOException("Failed to create file " + json.getName());

            objectMapper
                    .writerWithView(OutputViews.Store.class)
                    .writeValue(json, invoiceList);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    @Override
    public List<Invoice> findAllByUser(User user) {
        return null;
    }


}

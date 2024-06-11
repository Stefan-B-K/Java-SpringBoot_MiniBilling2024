package com.istef.minibilling2024.dao.json;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.istef.minibilling2024.dao.LineDAO;
import com.istef.minibilling2024.entity.Invoice;
import com.istef.minibilling2024.entity.Line;
import com.istef.minibilling2024.entity.Product;
import com.istef.minibilling2024.jackson.OutputViews;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class LineJsonDAO implements LineDAO {
    private static final String FILE_NAME = "lines.json";
    private final File json;
    private List<Line> lineList;

    private final ObjectMapper objectMapper;

    public LineJsonDAO(String jsonPath) {
        this.json = Path.of(jsonPath, FILE_NAME).toFile();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Override
    public List<Line> findAllByInvoiceAndProduct(final Invoice invoice, final Product product) {
        return findAll().stream()
                .filter(line -> line.getInvoiceNumber().equals(invoice.getDocumentNumber())
                            && line.getProduct() == product)
                .collect(Collectors.toList());
    }

    @Override
    public Line save(Line line) {

        try {
            lineList = findAll();
            lineList.add(line);

            if (!json.getParentFile().exists() && !json.getParentFile().mkdirs())
                throw new IOException("Failed to create folder " + json.getParentFile());

            if (!json.exists() && !json.createNewFile())
                throw new IOException("Failed to create file " + json.getName());

            objectMapper
                    .writerWithView(OutputViews.Store.class)
                    .writeValue(json, lineList);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    @Override
    public List<Line> findAll() {
        if (this.lineList != null) return this.lineList;
        if (!json.exists()) return new ArrayList<>();

        ObjectReader lineListReader = objectMapper.readerForListOf(Line.class);
        try {
            lineList = lineListReader.readValue(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lineList;
    }

}

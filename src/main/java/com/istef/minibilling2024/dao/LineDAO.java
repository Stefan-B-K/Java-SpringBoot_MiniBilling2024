package com.istef.minibilling2024.dao;

import com.istef.minibilling2024.entity.Invoice;
import com.istef.minibilling2024.entity.Line;
import com.istef.minibilling2024.entity.Product;

import java.util.List;

public interface LineDAO {
    List<Line> findAllByInvoiceAndProduct(Invoice invoice, Product product);

    Line save(Line line);

    List<Line> findAll();
}

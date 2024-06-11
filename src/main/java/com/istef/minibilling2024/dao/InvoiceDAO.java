package com.istef.minibilling2024.dao;

import com.istef.minibilling2024.entity.Invoice;
import com.istef.minibilling2024.entity.User;

import java.util.List;
import java.util.Optional;

public interface InvoiceDAO {
    long FIRST_DOC_REF = 10000;

    long findLastDocNumber();

    Optional<Invoice> findLastInvoiceByUser(User user);

    List<Invoice> findAll();

    Invoice save(Invoice invoice);

    List<Invoice> findAllByUser(User user);
}

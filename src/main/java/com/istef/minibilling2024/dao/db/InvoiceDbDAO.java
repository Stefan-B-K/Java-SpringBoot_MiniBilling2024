package com.istef.minibilling2024.dao.db;

import com.istef.minibilling2024.dao.InvoiceDAO;
import com.istef.minibilling2024.entity.Invoice;
import com.istef.minibilling2024.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceDbDAO extends JpaRepository<Invoice, Long>, InvoiceDAO {
    List<Invoice> findAllByUser(User user);

    @Override
    default long findLastDocNumber() {
        List<Invoice> all = findAll();
        if (all.isEmpty()) return FIRST_DOC_REF;

        return all.stream()
                .mapToLong(invoice -> Long.parseLong(invoice.getDocumentNumber()))
                .max()
                .getAsLong();
    }

    @Override
    default Optional<Invoice> findLastInvoiceByUser(User user) {
        List<Invoice> invoices = findAllByUser(user);
        if (invoices.isEmpty()) return Optional.empty();

        return  Optional.of(invoices.get(invoices.size() - 1));
    }
}

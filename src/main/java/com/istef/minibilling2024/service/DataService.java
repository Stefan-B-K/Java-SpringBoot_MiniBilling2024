package com.istef.minibilling2024.service;

import com.istef.minibilling2024.dao.*;
import com.istef.minibilling2024.dao.csv.PriceCsvDAO;
import com.istef.minibilling2024.dao.csv.ReadingCsvDAO;
import com.istef.minibilling2024.dao.csv.UserCsvDAO;
import com.istef.minibilling2024.dao.json.InvoiceJsonDAO;
import com.istef.minibilling2024.dao.json.LineJsonDAO;
import com.istef.minibilling2024.entity.*;
import com.istef.minibilling2024.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DataService {

    private final UserDAO userDAO;
    private final ReadingDAO readingDAO;
    private final PriceDAO priceDAO;
    private final LineDAO lineDAO;
    private final InvoiceDAO invoiceDAO;

    @Autowired
    public DataService(UserDAO userDAO,
                       ReadingDAO readingDAO,
                       PriceDAO priceDAO,
                       LineDAO lineDAO,
                       InvoiceDAO invoiceDAO) {
        this.userDAO = userDAO;
        this.readingDAO = readingDAO;
        this.priceDAO = priceDAO;
        this.lineDAO = lineDAO;
        this.invoiceDAO = invoiceDAO;
    }

    // constructor for CLI
    public DataService(String inputsPath) {
        this.userDAO = new UserCsvDAO(inputsPath);
        this.readingDAO = new ReadingCsvDAO(inputsPath);
        this.priceDAO = new PriceCsvDAO(inputsPath);
        this.lineDAO = new LineJsonDAO(inputsPath);
        this.invoiceDAO = new InvoiceJsonDAO(inputsPath);
    }


    public static ResponseEntity<Object> responseForPostSave(String id) {
        if (id == null) return ResponseEntity.status(500).body(null);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).body(id);
    }

    public List<User> listAllUsers() {
        return userDAO.findAll();
    }

    public User getOneUser(String refNumber) {
        User user = userDAO.findByRefNumber(refNumber);
        if (user == null) throw new EntityNotFoundException(
                "No user found for refNumber: " + refNumber);

        return user;
    }

    public String addUser(User user) {
        return userDAO.save(user).getRefNumber();
    }


    public long getNextDocRef() {
        return invoiceDAO.findLastDocNumber() + 1;
    }

    public List<Reading> listReadingsForUser(String userRef) {
        User user = getOneUser(userRef);
        return readingDAO.findAllByUser(user);
    }

    public List<Reading> listReadingsForMonth(ZonedDateTime afterDate,
                                              ZonedDateTime beforeDate,
                                              User user,
                                              Product product) {
        return readingDAO
                .findAllInDateRangeByUserAndProduct(afterDate,
                        beforeDate,
                        user,
                        product);
    }

    public Reading getFirstReading(User user, Product product) {

        return readingDAO.findFirstByUserAndProduct(user, product);
    }

    public Reading getReadingForDate(User user,
                                     Product product,
                                     ZonedDateTime previousDate) {
        return readingDAO
                .findByUserAndProductAndDate(user, product, previousDate);
    }

    public Long addReading(Reading reading, String userRef) {
        getOneUser(userRef);
        reading.setUserRef(userRef);

        return readingDAO.save(reading).getId();
    }

    public List<Price> getPriceListForUserId(String userRef) {
        List<Price> priceList = new ArrayList<>();

        User user = userDAO.findByRefNumber(userRef);
        if (user == null) return priceList;

        return priceDAO.findAllByPriceListId(user.getPriceListId());
    }

    public List<Invoice> listInvoicesForUser(String userRef) {
        User user = getOneUser(userRef);

        return invoiceDAO.findAllByUser(user);
    }

    public List<Line> listLinesFromLastInvoice(User user, Product product) {
        Invoice lastInvoice = invoiceDAO
                .findLastInvoiceByUser(user)
                .orElse(null);
        if (lastInvoice == null) return new ArrayList<>();

        return lineDAO
                .findAllByInvoiceAndProduct(lastInvoice, product);
    }

    public void saveInvoices(List<Invoice> invoices) {
        for (Invoice invoice : invoices) {
            invoiceDAO.save(invoice);
            for (Line line : invoice.getLines()) {
                lineDAO.save(line);
            }
        }
    }

}

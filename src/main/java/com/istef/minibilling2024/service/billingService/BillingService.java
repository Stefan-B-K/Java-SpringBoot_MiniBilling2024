package com.istef.minibilling2024.service.billingService;

import com.istef.minibilling2024.BillingApp;
import com.istef.minibilling2024.entity.*;
import com.istef.minibilling2024.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BillingService {
    private final DataService dataService;
    private final LinesGenerator linesGenerator;

    private ZonedDateTime monthEnd;

    @Autowired
    public BillingService(DataService dataService) {
        this.dataService = dataService;
        this.linesGenerator = new LinesGenerator();
    }

    // constructor for CLI
    public BillingService(String inputsPath) {
        this.dataService = new DataService(inputsPath);
        this.linesGenerator = new LinesGenerator();
    }

    public List<Invoice> generateInvoices(String yearMonth) {
        this.monthEnd = setInvoiceMonthEnd(yearMonth);

        List<User> users = dataService.listAllUsers();
        long nextDocRef = dataService.getNextDocRef();

        List<Invoice> invoices = new ArrayList<>();
        for (User user : users) {
            Invoice invoice = generateInvoiceForUser(user, nextDocRef, null);
            if (invoice == null) continue;

            invoices.add(invoice);
            nextDocRef++;
        }

        if (!invoices.isEmpty()) dataService.saveInvoices(invoices);

        return invoices;
    }

    public Invoice generateLiveBillForUser(String userRef, Map<String, String> inputReadings) {
        ZonedDateTime date = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC);
        this.monthEnd = YearMonth.now()
                .atEndOfMonth()
                .atTime(LocalTime.parse("23:59:59"))
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC);
        User user = dataService.getOneUser(userRef);

        List<Reading> liveReadings = new ArrayList<>();
        for (Map.Entry<String, String> entry : inputReadings.entrySet()) {
            liveReadings.add(new Reading(
                    user.getRefNumber(),
                    Product.init(entry.getKey()),
                    date,
                    Double.parseDouble(entry.getValue()))
            );
        }

        return generateInvoiceForUser(user, null, liveReadings);
    }

    private Invoice generateInvoiceForUser(User user, Long nextDocRef, List<Reading> liveReadings) {
        String documentNumber = nextDocRef == null ? null : String.valueOf(nextDocRef);
        Invoice invoice = new Invoice(
                ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC),
                documentNumber,
                user.getNames(),
                user);

        String consumerRef = user.getRefNumber();
        List<Price> priceList = dataService.getPriceListForUserId(consumerRef);

        List<Line> newLines = generateLinesForInvoice(user, priceList, invoice, liveReadings);
        if (newLines.isEmpty()) return null;

        invoice.setLines(newLines);
        return invoice;
    }

    private List<Line> generateLinesForInvoice(User user,
                                               List<Price> priceList,
                                               Invoice invoice,
                                               List<Reading> liveReadings) {
        List<Line> resultLines = new ArrayList<>();

        int index = 1;
        for (Product product : Product.values()) {
            List<Price> priceListForProduct = priceList.stream()
                    .filter(price -> price.getProduct().equals(product))
                    .collect(Collectors.toList());
            List<Line> lines =
                    generateLinesForProduct(index, user, product, priceListForProduct, invoice, liveReadings);

            if (!lines.isEmpty()) {
                resultLines.addAll(lines);
                index += lines.size();
            }
        }

        return resultLines;
    }

    private List<Line> generateLinesForProduct(int index, User user, Product product,
                                               List<Price> priceList, Invoice invoice,
                                               List<Reading> liveReadings) {

        List<Line> resultLines = new ArrayList<>();

        Reading prevReading = getPreviousReading(user, product);
        if (prevReading == null) return resultLines;

        List<Reading> newReadings = dataService
                .listReadingsForMonth(prevReading.getDate(), monthEnd, user, product);

        if (liveReadings != null && !liveReadings.isEmpty()) newReadings.addAll(liveReadings);
        if (newReadings.isEmpty()) return resultLines;

        List<Line> linesForProduct = linesGenerator
                .generateLines(index, prevReading, newReadings, priceList, product, invoice);

        resultLines.addAll(linesForProduct);

        return resultLines;
    }

    private Reading getPreviousReading(User user, Product product) {
        List<Line> lastInvoiceLines =
                dataService.listLinesFromLastInvoice(user, product);
        if (lastInvoiceLines.isEmpty())
            return dataService.getFirstReading(user, product);

        ZonedDateTime previousDate = null;
        for (Line line : lastInvoiceLines) {
            if (line.getProduct() == product) {
                ZonedDateTime endDateUTC = line.getEndDateUtc();
                if (previousDate == null) {
                    previousDate = endDateUTC;
                    continue;
                }
                if (previousDate.isBefore(endDateUTC)) {
                    previousDate = endDateUTC;
                }
            }
        }
        return dataService.getReadingForDate(user, product, previousDate);
    }

    private ZonedDateTime setInvoiceMonthEnd(String yearMonth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM");
        return YearMonth.parse(yearMonth, formatter)
                .atEndOfMonth()
                .atTime(LocalTime.parse("23:59:59"))
                .atZone(BillingApp.PRICELIST_ZONE_ID)
                .withZoneSameInstant(ZoneOffset.UTC);
    }

}

package com.istef.minibilling2024;

import com.istef.minibilling2024.entity.Invoice;
import com.istef.minibilling2024.service.PublisherService;
import com.istef.minibilling2024.service.billingService.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;

@Component
public class BillingApp {
    public final static ZoneId PRICELIST_ZONE_ID = ZoneId.of("Europe/Sofia");
    public final static String OUTPUT_PATH_DEFAULT = "/Users/stefan_kozuharov/УПРАЖНЕНИЯ/JAVA/MiniBilling2024/src/main/resources/output";

    private final BillingService billingService;
    private final PublisherService publisherService;


    @Autowired
    public BillingApp(BillingService billingService) {
        this.billingService = billingService;
        this.publisherService = new PublisherService(OUTPUT_PATH_DEFAULT);
    }

    // constructor for CLI
    public BillingApp(String inputsPath, String outputsPath) {
        this.billingService = new BillingService(inputsPath);
        this.publisherService = new PublisherService(outputsPath);
    }


    public void run(String yearMonth) {

        List<Invoice> newInvoices = billingService.generateInvoices(yearMonth);

        if (!newInvoices.isEmpty())
            publisherService.publishToJsonFolders(yearMonth, newInvoices);
    }
}

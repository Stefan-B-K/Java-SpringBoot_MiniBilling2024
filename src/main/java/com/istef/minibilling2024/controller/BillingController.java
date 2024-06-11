package com.istef.minibilling2024.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.istef.minibilling2024.entity.Invoice;
import com.istef.minibilling2024.jackson.OutputViews;
import com.istef.minibilling2024.service.DataService;
import com.istef.minibilling2024.service.billingService.BillingService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


@RestController
public class BillingController {
    private final BillingService billingService;
    private final DataService dataService;

    public BillingController(BillingService billingService, DataService dataService) {
        this.billingService = billingService;
        this.dataService = dataService;
    }

    @PostMapping("billing/{yearMonth}")
    @ApiResponse(responseCode = "200", description = "OK<br>Returns the number of generated invoices",
            content = @Content(schema = @Schema(type = "String", example = "3"))
    )
    public ResponseEntity<String> generateInvoices(@Parameter(description = "In a 'YY-MM' format.", example = "24-03")
                                                   @Schema(type = "string", example = "24-03")
                                                   @PathVariable String... yearMonth) {
        if (yearMonth == null) {
            yearMonth = new String[]{
                    DateTimeFormatter
                            .ofPattern("yy-MM")
                            .format(LocalDateTime.now())
            };
        }

        int newInvoicesCount = billingService.generateInvoices(yearMonth[0]).size();

        return ResponseEntity.status(201).body("Invoices generated: " + newInvoicesCount);
    }

    @GetMapping("users/{ref}/invoices")
    @JsonView(OutputViews.Publish.class)
    public List<Invoice> getInvoicesForUser(@Parameter(description = "User ID")
                                            @Schema(type = "string", example = "4")
                                            @PathVariable int ref) {

        return dataService.listInvoicesForUser(String.valueOf(ref));
    }

    @GetMapping("users/{ref}/live")
    @JsonView(OutputViews.Live.class)
    public Invoice getCurrentBill(@Parameter(description = "User ID")
                                  @Schema(type = "string", example = "4")
                                  @PathVariable int ref,
                                  @Parameter(description = "Product and value<br>Example: users/4/live?gas=1544&elec=2340")
                                  @Schema(example = "{\n   \"gas\": \"1544\",\n   \"elec\": \"2340\"\n}")
                                  @RequestParam Map<String, String> queryParameters
    ) {

        return billingService.generateLiveBillForUser(String.valueOf(ref), queryParameters);
    }
}

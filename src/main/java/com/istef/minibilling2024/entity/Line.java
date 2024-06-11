package com.istef.minibilling2024.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.istef.minibilling2024.jackson.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity(name = "lines")
@JsonPropertyOrder({"invoiceNumber", "index", "quantity", "startDateUtc", "endDateUtc", "product", "price", "priceListId", "amount"})
@JsonView(OutputViews.All.class)
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JsonProperty("invoiceNumber")
    @JsonView(OutputViews.Store.class)
    private Invoice invoice;

    private int index;

    @JsonSerialize(using = BigDecimalRoundSerializer.class)
    @Precision(precision = 2)
    private BigDecimal quantity;

    private double price;

    @JsonDate
    @JsonProperty("lineStart")
    private ZonedDateTime startDateUtc;

    @JsonDate
    @JsonProperty("lineEnd")
    private ZonedDateTime endDateUtc;

    @Enumerated(EnumType.STRING)
    @NotNull
    @JsonDeserialize(using = ProductDeserializer.class)
    private Product product;

    @JsonProperty("priceList")
    private int priceListId;

    @JsonSerialize(using = BigDecimalRoundSerializer.class)
    @Precision(precision = 2)
    private BigDecimal amount;


    public Line() {
    }

    public Line(int index,
                BigDecimal quantity,
                ZonedDateTime startDateUTC,
                ZonedDateTime endDateUTC,
                double price,
                Product product,
                int priceListId,
                Invoice invoice) {
        this.index = index;
        this.quantity = quantity;
        this.startDateUtc = startDateUTC;
        this.endDateUtc = endDateUTC;
        this.product = product;
        this.price = price;
        this.priceListId = priceListId;
        this.invoice = invoice;
        setAmount();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        if (this.price != 0) setAmount();
    }

    public ZonedDateTime getStartDateUtc() {
        return startDateUtc;
    }

    public void setStartDateUtc(ZonedDateTime startDateUTC) {
        this.startDateUtc = startDateUTC;
    }

    public ZonedDateTime getEndDateUtc() {
        return endDateUtc;
    }

    public void setEndDateUtc(ZonedDateTime endDateUTC) {
        this.endDateUtc = endDateUTC;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        if (this.quantity.compareTo(BigDecimal.ZERO) != 0) setAmount();
    }

    public int getPriceListId() {
        return priceListId;
    }

    public void setPriceListId(int priceListId) {
        this.priceListId = priceListId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getInvoiceNumber() {
        return this.invoice.getDocumentNumber();
    }

    private void setAmount() {
        this.amount = this.quantity.multiply(BigDecimal.valueOf(this.price));
    }
}

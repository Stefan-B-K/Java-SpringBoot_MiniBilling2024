package com.istef.minibilling2024.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.istef.minibilling2024.jackson.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.List;

@Entity(name = "invoices")
@JsonPropertyOrder({"documentDate", "documentNumber", "consumer", "reference", "totalAmount", "lines"})
@JsonView(OutputViews.All.class)
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @JsonDate
    @JsonProperty("documentDate")
    private ZonedDateTime dateUtc;

    @JsonView({OutputViews.Publish.class, OutputViews.Store.class})
    private String documentNumber;

    private String consumer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty("reference")
    @JsonSerialize(using = UserRefSerializer.class)
    private User user;

    @JsonSerialize(using = BigDecimalRoundSerializer.class)
    @Precision(precision = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    @JsonView({OutputViews.Publish.class, OutputViews.Live.class})
    private List<Line> lines;

    public Invoice() {
    }

    public Invoice(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Invoice(ZonedDateTime dateUTC, String documentNumber, String consumer, User user) {
        this.dateUtc = dateUTC;
        this.documentNumber = documentNumber;
        this.consumer = consumer;
        this.user = user;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public ZonedDateTime getDateUtc() {
        return dateUtc;
    }

    public void setDateUtc(ZonedDateTime dateUTC) {
        this.dateUtc = dateUTC;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = List.copyOf(lines);

        this.totalAmount = this.lines.stream()
                .map(line -> line.getAmount().setScale(2, RoundingMode.UP))
                .reduce(BigDecimal::add)
                .get();
    }

}



package com.istef.minibilling2024.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Entity(name = "users")
public class User {
    @Id
    @NotBlank
    private String refNumber;

    @NotBlank
    private String names;

    @NotNull
    @Positive
    private Integer priceListId;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Reading> readings;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Invoice> invoices;


    public User() {
    }

    public User(String refNumber) {
        this.refNumber = refNumber;
    }

    public User(String names, String refNumber, int priceListId) {
        this.names = names;
        this.refNumber = refNumber;
        this.priceListId = priceListId;
    }


    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    public int getPriceListId() {
        return priceListId;
    }

    public void setPriceListId(int priceListId) {
        this.priceListId = priceListId;
    }

    public List<Reading> getReadings() {
        return readings;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }
}

package com.istef.minibilling2024.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.istef.minibilling2024.jackson.DateZonedToZuluDeserializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity(name = "readings")
public class Reading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    @Column (columnDefinition = "varchar")
    @NotNull
    private Product product;

    @NotNull
    @JsonDeserialize(using = DateZonedToZuluDeserializer.class)
    private ZonedDateTime date;

    @NotNull
    private Double value;

    public Reading() {
    }


    public Reading(String userRef, Product product, ZonedDateTime date, Double value) {
        this.user = new User(userRef);
        this.product = product;
        this.date = date;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public String getUserRef() {
        return this.user.getRefNumber();
    }

    public void setUserRef(String userRef) {
        this.user = new User(userRef);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = Product.init(product);
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}

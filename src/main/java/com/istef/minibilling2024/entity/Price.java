package com.istef.minibilling2024.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.istef.minibilling2024.jackson.DateToZuluEndOfDayDeserializer;
import com.istef.minibilling2024.jackson.DateToZuluStartOfDayDeserializer;
import com.istef.minibilling2024.jackson.ProductDeserializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity(name = "prices")
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonDeserialize(using = ProductDeserializer.class)
    private Product product;

    @NotNull
    @JsonDeserialize(using = DateToZuluStartOfDayDeserializer.class)
    private ZonedDateTime validFromDateUTC;

    @NotNull
    @JsonDeserialize(using = DateToZuluEndOfDayDeserializer.class)
    private ZonedDateTime validToDateUTC;

    @NotNull
    private Double value;

    @NotNull
    private Integer priceListId;

    public Price() {
    }

    public Price(Product product, ZonedDateTime validFromDateUTC, ZonedDateTime validToDateUTC, double value, int priceListId) {
        this.product = product;
        this.validFromDateUTC = validFromDateUTC;
        this.validToDateUTC = validToDateUTC;
        this.value = value;
        this.priceListId = priceListId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return Double.compare(value, price.value) == 0
               && priceListId == price.priceListId
               && product == price.product
               && Objects.equals(validFromDateUTC, price.validFromDateUTC)
               && Objects.equals(validToDateUTC, price.validToDateUTC);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, validFromDateUTC, validToDateUTC, value, priceListId);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ZonedDateTime getValidFromDateUTC() {
        return validFromDateUTC;
    }

    public void setValidFromDateUTC(ZonedDateTime validFromDateUTC) {
        this.validFromDateUTC = validFromDateUTC;
    }

    public ZonedDateTime getValidToDateUTC() {
        return validToDateUTC;
    }

    public void setValidToDateUTC(ZonedDateTime validToDateUTC) {
        this.validToDateUTC = validToDateUTC;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getPriceListId() {
        return priceListId;
    }

    public void setPriceListId(int priceListId) {
        this.priceListId = priceListId;
    }

    public Long getId() {
        return id;
    }
}

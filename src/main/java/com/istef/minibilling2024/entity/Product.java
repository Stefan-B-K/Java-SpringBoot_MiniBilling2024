package com.istef.minibilling2024.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Product {
    GAS("gas"),
    ELECTRICITY("elec");

    private final String name;

    Product(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public static Product init(String value) {
        value = value.trim();
        if (value.equals(GAS.getName())) return GAS;
        if (value.equals(ELECTRICITY.getName())) return ELECTRICITY;
        throw new NumberFormatException("Invalid value: " + value);
    }

}
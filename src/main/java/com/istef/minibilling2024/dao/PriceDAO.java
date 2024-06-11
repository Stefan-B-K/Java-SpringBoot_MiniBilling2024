package com.istef.minibilling2024.dao;

import com.istef.minibilling2024.entity.Price;

import java.util.List;

public interface PriceDAO {

    List<Price> findAllByPriceListId(int priceListId);
}

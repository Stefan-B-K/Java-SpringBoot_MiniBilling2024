package com.istef.minibilling2024.dao.db;

import com.istef.minibilling2024.dao.PriceDAO;
import com.istef.minibilling2024.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceDbDAO extends JpaRepository<Price, Long>, PriceDAO {

}

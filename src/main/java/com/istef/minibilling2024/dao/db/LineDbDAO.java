package com.istef.minibilling2024.dao.db;

import com.istef.minibilling2024.dao.LineDAO;
import com.istef.minibilling2024.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineDbDAO extends JpaRepository<Line, Long>, LineDAO {
}

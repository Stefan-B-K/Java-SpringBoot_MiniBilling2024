package com.istef.minibilling2024.dao.db;

import com.istef.minibilling2024.dao.ReadingDAO;
import com.istef.minibilling2024.entity.Product;
import com.istef.minibilling2024.entity.Reading;
import com.istef.minibilling2024.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

public interface ReadingDbDAO extends JpaRepository<Reading, Long>, ReadingDAO {
    List<Reading> findAllByUserAndProduct(User user, Product product);

    @Override
    default List<Reading> findAllInDateRangeByUserAndProduct(ZonedDateTime afterDate,
                                                             ZonedDateTime beforeDate,
                                                             User user,
                                                             Product product) {

        List<Reading> readings = findAllByUserAndProduct(user, product);

        return readings.stream().filter(reading -> {
            if (afterDate != null && !reading.getDate().isAfter(afterDate)) return false;
            return reading.getDate().isBefore(beforeDate);
        }).collect(Collectors.toList());
    }
}

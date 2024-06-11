package com.istef.minibilling2024.dao;

import com.istef.minibilling2024.entity.Product;
import com.istef.minibilling2024.entity.Reading;
import com.istef.minibilling2024.entity.User;

import java.time.ZonedDateTime;
import java.util.List;

public interface ReadingDAO {
    List<Reading> findAllByUser(User user);

    List<Reading> findAllInDateRangeByUserAndProduct(ZonedDateTime afterDate,
                                                     ZonedDateTime beforeDate,
                                                     User user,
                                                     Product product);

    Reading findByUserAndProductAndDate(User user,
                                        Product product,
                                        ZonedDateTime date);

    Reading findFirstByUserAndProduct(User user, Product product);

    Reading save(Reading reading);
}

package com.istef.minibilling2024.dao.csv;


import com.istef.minibilling2024.BillingApp;
import com.istef.minibilling2024.dao.PriceDAO;
import com.istef.minibilling2024.entity.Price;
import com.istef.minibilling2024.entity.Product;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class PriceCsvDAO implements PriceDAO {
    private static final String FILE_NAME = "prices";
    private static final String FILE_EXT = "csv";
    private final String csvPath;

    public PriceCsvDAO(String csvPath) {
        this.csvPath = csvPath;
    }

    @Override
    public List<Price> findAllByPriceListId(int priceListId) {

        String file = FILE_NAME + '-' + priceListId + '.' + FILE_EXT;
        File csv = Path.of(csvPath, file).toFile();

        List<Price> priceList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(csv), ',', '"')) {
            String[] row;
            while ((row = csvReader.readNext()) != null) {

                priceList.add(new Price(
                        Product.init(row[0]),
                        parseDateToStartOfDayUTC(row[1], BillingApp.PRICELIST_ZONE_ID),
                        parseDateToEndOfDayUTC(row[2], BillingApp.PRICELIST_ZONE_ID),
                        Double.parseDouble(row[3]),
                        priceListId));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return priceList;
    }


    private static ZonedDateTime parseDateToStartOfDayUTC(String date, ZoneId zoneId) {
        return LocalDate.parse(date)
                .atStartOfDay(zoneId)
                .withZoneSameInstant(ZoneOffset.UTC);
    }

    private static ZonedDateTime parseDateToEndOfDayUTC(String date, ZoneId zoneId) {
        return LocalDate.parse(date)
                .atTime(LocalTime.parse("23:59:59"))
                .atZone(zoneId)
                .withZoneSameInstant(ZoneOffset.UTC);
    }

}

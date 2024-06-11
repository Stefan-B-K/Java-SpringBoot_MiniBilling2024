package com.istef.minibilling2024.dao.csv;


import com.istef.minibilling2024.dao.ReadingDAO;
import com.istef.minibilling2024.entity.Product;
import com.istef.minibilling2024.entity.Reading;
import com.istef.minibilling2024.entity.User;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReadingCsvDAO implements ReadingDAO {
    private static final String FILE_NAME = "readings.csv";
    private final File csv;


    public ReadingCsvDAO(String csvPath) {
        this.csv = Path.of(csvPath, FILE_NAME).toFile();
    }


    @Override
    public List<Reading> findAllInDateRangeByUserAndProduct(ZonedDateTime afterDate,
                                                            ZonedDateTime beforeDate,
                                                            User user,
                                                            Product product) {
        List<Reading> readingsList = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(csv), ',', '"')) {
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                ZonedDateTime readingDate = parseZonedToUTC(row[2]);

                if (afterDate != null && !readingDate.isAfter(afterDate)) continue;
                if (!readingDate.isBefore(beforeDate)) continue;

                String userId = user.getRefNumber();
                if (row[0].equals(userId) && row[1].equals(product.getName()))
                    readingsList.add(new Reading(
                            userId,
                            Product.init(row[1]),
                            readingDate,
                            Double.parseDouble(row[3])));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return readingsList;
    }

    @Override
    public Reading findByUserAndProductAndDate(User user,
                                               Product product,
                                               ZonedDateTime date) {
        String userRefNumber = user.getRefNumber();

        try (CSVReader csvReader = new CSVReader(new FileReader(csv), ',', '"')) {
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                ZonedDateTime readingDate = parseZonedToUTC(row[2]);

                if (row[0].equals(userRefNumber)
                    && row[1].equals(product.getName())
                    && readingDate.isEqual(date)
                )
                    return new Reading(
                            userRefNumber,
                            Product.init(row[1]),
                            readingDate,
                            Double.parseDouble(row[3]));
            }

            throw new RuntimeException("Reading not found (for date from previous invoice)");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Reading findFirstByUserAndProduct(User user, Product product) {
        String userRefNumber = user.getRefNumber();

        try (CSVReader csvReader = new CSVReader(new FileReader(csv), ',', '"')) {
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                if (row[0].equals(userRefNumber) && row[1].equals(product.getName()))

                    return new Reading(userRefNumber,
                            product,
                            parseZonedToUTC(row[2]),
                            Double.parseDouble(row[3]));
            }

            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static ZonedDateTime parseZonedToUTC(String dateTime) {
        return ZonedDateTime.parse(dateTime)
                .withZoneSameInstant(ZoneOffset.UTC);
    }

    @Override
    public Reading save(Reading reading) {
        return null;
    }

    @Override
    public List<Reading> findAllByUser(User user) {
        return null;
    }
}

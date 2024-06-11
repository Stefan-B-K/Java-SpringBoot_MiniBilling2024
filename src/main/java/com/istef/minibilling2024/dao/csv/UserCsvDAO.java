package com.istef.minibilling2024.dao.csv;


import com.istef.minibilling2024.dao.UserDAO;
import com.istef.minibilling2024.entity.User;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UserCsvDAO implements UserDAO {
    private static final String FILE_NAME = "users.csv";
    private final File csv;


    public UserCsvDAO(String csvPath) {
        this.csv = Path.of(csvPath, FILE_NAME).toFile();
    }


    @Override
    public User findByRefNumber(String refNumber) {

        try (CSVReader csvReader = new CSVReader(new FileReader(csv), ',', '"')) {
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                String readRefNumber = row[1];
                if (readRefNumber.equals(refNumber))
                   return new User(
                           readRefNumber,
                           row[1],
                           Integer.parseInt(row[2])) ;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> userList = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(csv), ',', '"')) {
            String[] row;
            while ((row = csvReader.readNext()) != null)
                userList.add(new User(row[0], row[1], Integer.parseInt(row[2])));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return userList;
    }

    @Override
    public User save(User user) {
        return null;
    }

}

package com.example.finalproject.DBConnection;

import com.example.finalproject.dtos.RegisterDto;
import org.springframework.stereotype.Service;

import java.sql.Statement;

@Service
public class DBQueriesService {

    Statement statement;


    public void saveUser(RegisterDto user) throws Exception {
        String sql = String.format(
                "INSERT INTO users(first_name, last_name,email) VALUES('%s', '%s', %s)",
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );

        Statement statement = DBConnection.getConnection().createStatement();
        statement.execute(sql);
    }
}
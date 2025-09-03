package com.brokersystems.brokerapp.server.utils;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by HP on 7/31/2017.
 */
@Component
public class DatabaseUtilities {

    @Autowired
    private DataSource dataSource;

    public String getDbName(){
        String url = null;
        Connection connection = null;
        try {
             connection = dataSource.getConnection();
            url =connection.getMetaData().getURL().toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        String[] urlArray = url.split(":");
        String db_name = urlArray[1];
        return db_name;
    }
}

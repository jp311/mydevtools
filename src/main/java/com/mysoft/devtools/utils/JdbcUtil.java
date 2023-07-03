package com.mysoft.devtools.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * @author hezd   2023/6/5
 */
public class JdbcUtil {
    public final static String MYSQL = "MySql";
    public final static String DM = "DM";

    static final String MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";

    static final String DM_JDBC_DRIVER = "dm.jdbc.driver.DmDriver";

    public static boolean test(String dbType, String ip, String port, String dbName, String userName, String password) {
        try {
            String url = getUrl(dbType, ip, port, dbName);
            if (Objects.equals(dbType, MYSQL)) {
                Class.forName(MYSQL_JDBC_DRIVER);
            } else {
                Class.forName(DM_JDBC_DRIVER);
            }
            Connection conn = DriverManager.getConnection(url, userName, password);

            Statement statement = conn.createStatement();
            statement.execute("select 1");

            statement.close();
            conn.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUrl(String dbType, String ip, String port, String dbName) {
        String url;
        if (Objects.equals(dbType, MYSQL)) {
            if (dbName == null || dbName.isBlank()) {
                url = MessageFormat.format("jdbc:mysql://{0}:{1}", ip, port);
            } else {
                url = MessageFormat.format("jdbc:mysql://{0}:{1}/{2}", ip, port, dbName);
            }
        } else {
            if (dbName == null || dbName.isBlank()) {
                url = MessageFormat.format("jdbc:dm://{0}:{1}", ip, port);
            } else {
                url = MessageFormat.format("jdbc:dm://{0}:{1}?schema={2}", ip, port, dbName);
            }
        }
        return url;
    }
}

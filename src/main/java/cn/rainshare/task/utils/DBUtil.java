package cn.rainshare.task.utils;

import java.sql.*;
import java.util.ResourceBundle;

public class DBUtil {

    private DBUtil(){

    }

    /**
     * 注册驱动
     */
    static {

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
            Class.forName(bundle.getString("driver"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @return SQL连接对象
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
        String url = bundle.getString("url");
        String username = bundle.getString("username");
        String passwd = bundle.getString("passwd");
        return DriverManager.getConnection(url,username,passwd);

    }

    /**
     * 关闭资源
     * @param connection
     * @param statement
     * @param resultSet
     */
    public static void close(Connection connection,Statement statement,ResultSet resultSet){

        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        if(statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }



}

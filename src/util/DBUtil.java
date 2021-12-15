package util;

import java.sql.*;

/**
 * @author Starset
 * @version 1.0
 * @date 2021/11/26 17:24
 */
public class DBUtil {

    private static Statement statement;

    public static boolean initConnection(String ip,String port,String userName,String password){//连接数据库
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/midnightChat?user=" + userName + "&password=" + password + "&&characterEncoding=UTF-8");
            statement = connection.createStatement();
        } catch (Exception e) {
            return false;
        }
        keepAlive();
        return true;
    }

    public static boolean authenticate(String userName,String userPass){//验证用户
        StringBuilder builder = new StringBuilder()
                .append("select * from user where userName =")
                .append('\'').append(userName)
                .append('\'')
                .append(" and password=")
                .append('\'')
                .append(userPass)
                .append('\'')
                .append(';');
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(builder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int createUser(String userName, String userPass) {//创建用户
        StringBuilder builder = new StringBuilder()
                .append("insert into user(userName,password) values(")
                .append('\'')
                .append(userName)
                .append('\'')
                .append(',')
                .append('\'')
                .append(userPass)
                .append('\'')
                .append(");");
        try {
            return statement.executeUpdate(builder.toString());
        }catch (Exception e){
            return -1;
        }
    }

    @SuppressWarnings("SqlDialectInspection")
    public static void keepAlive(){//保活
        Thread thread = new Thread(() -> {
            while (true){
                try {
                    Thread.sleep(1000 * 60 *2);
                    statement.executeQuery("select version()");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

}

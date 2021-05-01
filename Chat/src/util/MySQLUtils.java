package util;

import java.sql.*;

public class MySQLUtils {
    private MySQLUtils() {}

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接对象
     *
     * @return 连接对象
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/chatroom?serverTimezone=GMT%2B8", "root",
                "0204");

    }

    /**
     * 关闭资源
     *
     * @param conn      连接对象
     * @param statement 数据库操作对象
     * @param resultSet 结果集
     */
    public static void close(Connection conn, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /**
     * 登录，用户名和密码是否匹配
     * @param name 用户名
     * @param password 密码
     * @return 匹配结果
     */
    public static boolean matches(String name, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "select * from registeredUser where name = ? and password = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public static void login(String name, String ip, int port) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();
            String sql = "insert into onlineUser values(?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, ip);
            ps.setInt(3, port);
            ps.setString(4, "Online");
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * 检查是否存在重名用户
     * @return true为有，false为无
     */
    public static boolean hasSameUser(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "select * from registeredUser where name = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next())
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * 注册
     * @param name 用户名
     * @param password 密码
     */
    public static void register(String name, String password) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();
            String sql = "insert into registeredUser values(?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, password);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void deleteClientByIP(String ip) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();
            String sql = "delete from onlineUser where ip = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, ip);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * 查询用户是否存在
     * @param ip ip地址
     * @return 查询结果
     */
    public static boolean hasUserByIP(String ip) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "select * from onlineUser where ip = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, ip);
            rs = ps.executeQuery();
            if (rs.next())
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public static boolean hasUserByName(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "select * from onlineUser where name = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next())
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * 根据名字更改用户状态
     * @param name 名字
     * @param status 状态
     */
    public static void changeStatusByName(String name, String status) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();
            String sql = "update onlineUser set status = ? where name = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * 根据ip更改用户状态
     * @param ip ip地址
     * @param status 状态
     */
    public static void changeStatusByIP(String ip, String status) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();
            String sql = "update onlineUser set status = ? where ip = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, ip);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * 根据名字查询用户是否在线
     * @param name 名字
     * @return 结果
     */
    public static boolean isOnlineByName(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "select * from onlineUser where name = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next())
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * 根据名字获取用户状态
     * @param name 名字
     * @return
     */
    public static String getStatusByName(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String status = null;

        try {
            conn = getConnection();
            String sql = "select * from onlineUser where name = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next())
                status = rs.getString("status");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return status;
    }

    /**
     * 根据IP获取用户名
     * @param ip ip地址
     * @return 用户名
     */
    public static String getNameByIP(String ip) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String name = null;

        try {
            conn = getConnection();
            String sql = "select * from onlineUser where ip = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, ip);
            rs = ps.executeQuery();
            if (rs.next())
                name = rs.getString("name");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return name;
    }

    /**
     * 获取所有在线用户
     * @return 查询结果集
     */
    public static ResultSet getAllOnlineUser() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "select * from onlineUser";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    /**
     * 根据名字获取IP地址
     * @param name 用户名
     * @return
     */
    public static String getIPByName(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String ip = null;

        try {
            conn = getConnection();
            String sql = "select * from onlineUser where name = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next())
                ip = rs.getString("ip");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ip;
    }
}

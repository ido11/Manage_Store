package presistence.dao.employees;

import java.sql.*;
import logic.employees.models.User;
import logic.employees.models.User;
import presistence.dao.DAO;
import presistence.dao.Result;

import java.util.LinkedList;

public class UserDAO implements DAO<User, String, Integer, Integer> {

    private Connection conn = null; //Connection instance

    @Override
    public Result update(User user, String key, Integer dummy1, Integer dummy2) {
        String sql = "UPDATE users SET username = ?, password = ?, id =?, " +
                    "firstname=?, lastname=? WHERE username = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setInt(3, user.getId());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getLastName());
            pstmt.setString(6, key);
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result delete(User user) {
        if(findByKey(user) == null)
            return Result.FAIL;
        String sql = "DELETE FROM users WHERE username = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result insert(User user) {
        if(findByKey(user) != null)
            return Result.FAIL;
        String sql = "INSERT INTO users(username, password, id, firstname, lastname)" +
                " VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setInt(3, user.getId());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getLastName());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public User findByKey(User user) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                return new User(rs.getString("username"),
                                rs.getString("password"),
                                rs.getInt("id"),
                                rs.getString("firstname"),
                                rs.getString("lastname"));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    public void setConnection(Connection conn)
    {
        this.conn = conn;
    }

    @Override
    public LinkedList<User> findByVal(Object id) {
        return null;
    }


}

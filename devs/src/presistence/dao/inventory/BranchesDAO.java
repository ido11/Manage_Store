package presistence.dao.inventory;

import logic.inventory.models.StoreBranch;
import presistence.dao.DAO;
import presistence.dao.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BranchesDAO implements DAO<StoreBranch, Integer, Integer, Integer> {

    private Connection conn = null; //Connection instance

    @Override
    public Result update(StoreBranch branch, Integer key, Integer dummy, Integer dummy2) {
        String sql = "UPDATE discounts SET name = ? WHERE branchID = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, branch.getName());
            pstmt.setInt(2, key);
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result delete(StoreBranch branch) {
        if(findByKey(branch) == null)
            return Result.FAIL;
        String sql = "DELETE FROM branches WHERE branchID = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, branch.getBranchID());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result insert(StoreBranch branch) {
        String sql = "INSERT INTO branches(name) VALUES (?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, branch.getName());
            pstmt.executeUpdate();
            conn.commit();

            pstmt = conn.prepareStatement("SELECT branchID FROM  branches WHERE name = ?");
            pstmt.setString(1, branch.getName());
            ResultSet rs = pstmt.executeQuery();
            StoreBranch inserted = null;
            if(rs.next())
                inserted = new StoreBranch(rs.getInt("branchID"));

            sql =   "INSERT INTO locations" +
                    "(locationID, branchID, physical_place, place_identifier)" +
                    " VALUES (-1, ?, 'Warehouse', 'empty1')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, inserted.getBranchID());
            pstmt.executeUpdate();

            sql =   "INSERT INTO locations" +
                    "(locationID, branchID, physical_place, place_identifier)" +
                    " VALUES (-2, ?, 'Store', 'empty1')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, inserted.getBranchID());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public StoreBranch findByKey(StoreBranch branch) {
        String sql = "SELECT * FROM branches WHERE branchID = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, branch.getBranchID());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
                return new StoreBranch(rs.getInt("branchID"),
                                        rs.getString("name"));
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public StoreBranch findByName(StoreBranch branch) {
        String sql = "SELECT * FROM branches WHERE name = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, branch.getName());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
                return new StoreBranch(rs.getInt("branchID"),
                                        rs.getString("name"));
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<StoreBranch> findAll()
    {
        List<StoreBranch> products = new ArrayList<>();;
        String sql = "SELECT * FROM branches";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next())
                products.add(new StoreBranch(rs.getInt("branchID"),
                                            rs.getString("name")));
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return products;
    }

    @Override
    public void setConnection(Connection conn)
    {
        this.conn = conn;
    }

    @Override
    public LinkedList<StoreBranch> findByVal(Object id) {
        return null;
    }
}

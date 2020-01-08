package presistence.dao.suppliers;

import logic.suppliers.models.Discount;
import presistence.dao.DAO;
import presistence.dao.Result;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DiscountDAO implements DAO<Discount, String, String, String> {

    private Connection conn = null; //Connection instance

    @Override
    public Result update(Discount discount, String key, String dummy1, String dummy2) {
        String sql = "UPDATE supplierproduct SET supplierNum = ?, code = ?, minAmount = ?, discountPer =?, " +
                "WHERE supplierNum = ? AND code = ?" ;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, discount.getSupplierNum());
            pstmt.setString(2, discount.getCode());
            pstmt.setInt(3, discount.getMinAmount());
            pstmt.setInt(4, discount.getDiscount());

            pstmt.setInt(5, Integer.parseInt(key));
            pstmt.setString(6, discount.getCode());
            pstmt.executeUpdate();
            conn.commit();

            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result delete(Discount discount) {
        String sql = "DELETE FROM supplierdiscount WHERE supplierNum = ? AND code = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, discount.getSupplierNum());
            pstmt.setString(2, discount.getCode());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result insert(Discount discount) {
        if(findByKey(discount) != null)
            return Result.FAIL;
        String sql = "INSERT INTO supplierdiscount(supplierNum, code, minAmount, discountPer)" +
                " VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, discount.getSupplierNum());
            pstmt.setString(2, discount.getCode());
            pstmt.setInt(3, discount.getMinAmount());
            pstmt.setInt(4, discount.getDiscount());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Discount findByKey(Discount discount) {
        String sql = "SELECT * FROM supplierdiscount WHERE supplierNum = ? AND code = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, discount.getSupplierNum());
            pstmt.setString(2, discount.getCode());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                return new Discount(rs.getInt("supplierNum"),
                        rs.getString("code"),
                        rs.getInt("minAmount"),
                        rs.getInt("discountPer"));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Discount> findByKeys(Discount discount) {
        String sql = "SELECT * FROM supplierdiscount WHERE supplierNum = ?";
        try {
            List disc = new LinkedList<Discount>();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, discount.getSupplierNum());
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                disc.add(new Discount(rs.getInt("supplierNum"),
                    rs.getString("code"),
                    rs.getInt("minAmount"),
                    rs.getInt("discountPer")));
            }
            rs.close();
            return disc;
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
    public LinkedList<Discount> findByVal(Object id) {
        return null;
    }


}

package presistence.dao.suppliers;

import logic.suppliers.models.Supplier;
import logic.suppliers.models.SupplierType2;
import presistence.dao.DAO;
import presistence.dao.Result;

import java.sql.*;
import java.util.LinkedList;

public class SupplierType2DAO implements DAO<SupplierType2, String, String, String> {

    private Connection conn = null; //Connection instance



    @Override
    public presistence.dao.Result update(SupplierType2 supplier, String key, String dummy1, String dummy2) {
        String sql = "UPDATE suppliers SET supplierNum = ?, name = ?, bankAccount = ?, paymentCond =?, " +
                "phoneNum=? WHERE supplierNum = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, supplier.getSupplierNum());
            pstmt.setString(2, supplier.getName());
            pstmt.setString(3, supplier.getBankAccount());
            pstmt.setString(4, supplier.getPaymentCond());
            pstmt.setString(5, supplier.getPhoneNum());
            pstmt.setInt(6, Integer.parseInt(key));
            pstmt.executeUpdate();
            conn.commit();
            return presistence.dao.Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return presistence.dao.Result.FAIL;
    }

    @Override
    public presistence.dao.Result delete(SupplierType2 supplier) {
        if(findByKey(supplier) == null)
            return presistence.dao.Result.FAIL;
        String sql = "DELETE FROM suppliers WHERE supplierNum = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, supplier.getSupplierNum());
            pstmt.executeUpdate();
            conn.commit();
            return presistence.dao.Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return presistence.dao.Result.FAIL;
    }

    @Override
    public presistence.dao.Result insert(SupplierType2 supplier) {
        if(findByKey(supplier) != null)
            return presistence.dao.Result.FAIL;
        String sql = "INSERT INTO suppliers(supplierNum, name, bankAccount, paymentCond, phoneNum)" +
                " VALUES (?, ?, ?, ?, ?)";
        String sql1 = "INSERT INTO suppliersType2(supplierNum)" +
                " VALUES (?)";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, supplier.getSupplierNum());
            pstmt.setString(2, supplier.getName());
            pstmt.setString(3, supplier.getBankAccount());
            pstmt.setString(4, supplier.getPaymentCond());
            pstmt.setString(5, supplier.getPhoneNum());
            pstmt.executeUpdate();

            PreparedStatement pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setInt(1, supplier.getSupplierNum());
            pstmt1.executeUpdate();

            conn.commit();
            return presistence.dao.Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public SupplierType2 findByKey(SupplierType2 supplier) {
        String sql = "SELECT s.supplierNum, name, bankAccount, paymentCond, phoneNum, address FROM suppliersType2 s2 JOIN suppliers s ON s.supplierNum = s2.supplierNum WHERE s.supplierNum = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, supplier.getSupplierNum());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                return new SupplierType2(rs.getInt("supplierNum"),
                        rs.getString("name"),
                        rs.getString("bankAccount"),
                        rs.getString("paymentCond"),
                        rs.getString("phoneNum"));
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
    public LinkedList<SupplierType2> findByVal(Object id) {
        return null;
    }


    public boolean isSupplier2(Supplier supplier) {
        String sql = "SELECT * FROM suppliersType2 WHERE supplierNum = ?";
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, supplier.getSupplierNum());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return true;
            }
            rs.close();
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

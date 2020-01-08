package presistence.dao.suppliers;

import logic.suppliers.models.Supplier;
import logic.suppliers.models.SupplierType1;
import presistence.dao.DAO;
import presistence.dao.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SupplierType1DAO implements DAO<SupplierType1, String, String, String> {

    private Connection conn = null; //Connection instance



    @Override
    public presistence.dao.Result update(SupplierType1 supplier, String key, String dummy1, String dummy2) {
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

    public void updateDays(SupplierType1 supplier, LinkedList<String> prevDays) {
        String sql = "UPDATE suppliersType1 SET day =? WHERE supplierNum = ? AND day = ?";
        String sql2 = "INSERT INTO suppliersType1(supplierNum, day)" +
                " VALUES (?, ?)";
        String sql3 = "DELETE FROM suppliersType1 WHERE supplierNum = ? AND day = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            PreparedStatement pstmt3 = conn.prepareStatement(sql3);
            int prevSize = prevDays.size();
            int curSize = supplier.getDays().size();
            for (int i = 0; i < prevSize; i++) {
                pstmt3.setInt(1, supplier.getSupplierNum());
                pstmt3.setString(2, prevDays.get(i));
                pstmt3.executeUpdate();
            }
            conn.commit();
            for (int i = 0; i < curSize; i++) {
                pstmt2.setInt(1, supplier.getSupplierNum());
                pstmt2.setString(2, supplier.getDays().get(i));
                pstmt2.executeUpdate();
            }
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




        @Override
    public presistence.dao.Result delete(SupplierType1 supplier) {
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
    public presistence.dao.Result insert(SupplierType1 supplier) {
        if(findByKey(supplier) != null)
            return presistence.dao.Result.FAIL;
        String sql = "INSERT INTO suppliers(supplierNum, name, bankAccount, paymentCond, phoneNum)" +
                " VALUES (?, ?, ?, ?, ?)";


        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, supplier.getSupplierNum());
            pstmt.setString(2, supplier.getName());
            pstmt.setString(3, supplier.getBankAccount());
            pstmt.setString(4, supplier.getPaymentCond());
            pstmt.setString(5, supplier.getPhoneNum());
            pstmt.executeUpdate();
            conn.commit();
            for(int i=0 ; i<supplier.getDays().size();i++) {
                String sql2 = "INSERT INTO suppliersType1(supplierNum, day)" +
                        " VALUES (?, ?)";
                pstmt = conn.prepareStatement(sql2);
                pstmt.setInt(1, supplier.getSupplierNum());
                pstmt.setString(2, supplier.getDays().get(i));
                pstmt.executeUpdate();
            }
            conn.commit();
            return presistence.dao.Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public SupplierType1 findByKey(SupplierType1 supplier) {
        String sql = "SELECT * FROM suppliers WHERE supplierNum = ?";
        String sql1 = "SELECT * FROM suppliersType1 WHERE supplierNum = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, supplier.getSupplierNum());
            ResultSet rs = pstmt.executeQuery();
            PreparedStatement pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setInt(1, supplier.getSupplierNum());
            ResultSet rs1 = pstmt1.executeQuery();
            LinkedList<String> days = new LinkedList<>();
            if(rs1.next()){
                days.add(rs1.getString("day"));
            }

                if(rs.next())
            {
                return new SupplierType1(rs.getInt("supplierNum"),
                        rs.getString("name"),
                        rs.getString("bankAccount"),
                        rs.getString("paymentCond"),
                        rs.getString("phoneNum"),
                        days);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<SupplierType1> findAllBySupplyDay(String day)
    {
        List<SupplierType1> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliersType1 WHERE day = ?";
        try {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, day);
        ResultSet rs = pstmt.executeQuery();
        while(rs.next())
            suppliers.add(new SupplierType1(rs.getInt("supplierNum")));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return suppliers;
    }

    public boolean isSupplier1(Supplier supplier){
        String sql = "SELECT * FROM suppliersType1 WHERE supplierNum = ?";
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

    public void setConnection(Connection conn)
    {
        this.conn = conn;
    }

    @Override
    public LinkedList<SupplierType1> findByVal(Object id) {
        return null;
    }


}

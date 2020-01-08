package presistence.dao.suppliers;

import logic.suppliers.models.Product;
import logic.suppliers.models.SupplierType1;
import presistence.dao.DAO;
import presistence.dao.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ProductDAO implements DAO<Product, String, String, String> {

    private Connection conn = null; //Connection instance

    @Override
    public Result update(Product product, String key, String dummy1, String dummy2) {
        String sql = "UPDATE supplierproduct SET description = ?, price =?, " +
                "amount=? WHERE supplierNum = ? AND code = ?" ;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, product.getDesc());
            pstmt.setInt(2, product.getPrice());
            pstmt.setInt(3, product.getAmount());

            pstmt.setInt(4, Integer.parseInt(key));
            pstmt.setString(5, product.getCode());
            pstmt.executeUpdate();
            conn.commit();

            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result delete(Product product) {
        if(findByKey(product) == null)
            return Result.FAIL;
        String sql = "DELETE FROM supplierproduct WHERE supplierNum = ? AND code = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, product.getSupplierNum());
            pstmt.setString(2, product.getCode());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result insert(Product product) {
        if(findByKey(product) != null)
            return Result.FAIL;
        String sql = "INSERT INTO supplierproduct(supplierNum, code, description, price, amount)" +
                " VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, product.getSupplierNum());
            pstmt.setString(2, product.getCode());
            pstmt.setString(3, product.getDesc());
            pstmt.setInt(4, product.getPrice());
            pstmt.setInt(5, product.getAmount());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Product findByKey(Product product) {
        String sql = "SELECT * FROM supplierproduct WHERE supplierNum = ? AND code = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, product.getSupplierNum());
            pstmt.setString(2, product.getCode());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                return new Product(rs.getString("code"),
                        rs.getString("description"),
                        rs.getInt("supplierNum"),
                        rs.getInt("price"),
                        rs.getInt("amount"));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Product> findByBarcode(int barcode) {
        List<Product> prod_list = new ArrayList<>();
        String sql = "SELECT * FROM supplierproduct WHERE code = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, barcode+"");
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                prod_list.add(new Product(rs.getString("code").trim(),
                        rs.getString("description"),
                        rs.getInt("supplierNum"),
                        rs.getInt("price"),
                        rs.getInt("amount")));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return prod_list;
    }

    public List<Product> findByKeys(Product product) {
        String sql = "SELECT * FROM supplierproduct WHERE supplierNum = ?";
        try {
            List prod = new LinkedList<Product>();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, product.getSupplierNum());
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                 prod.add(new Product(rs.getString("code"),
                        rs.getString("description"),
                        rs.getInt("supplierNum"),
                        rs.getInt("price"),
                        rs.getInt("amount")));
            }
            rs.close();
            return prod;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Product> findBySupplier(SupplierType1 sup) {
        String sql = "SELECT * FROM supplierproduct WHERE supplierNum = ?";
        try {
            List prod = new LinkedList<Product>();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sup.getSupplierNum());
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                prod.add(new Product(rs.getString("code").trim(),
                        rs.getString("description"),
                        rs.getInt("supplierNum"),
                        rs.getInt("price"),
                        rs.getInt("amount")));
            }
            rs.close();
            return prod;
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
    public LinkedList<Product> findByVal(Object id) {
        return null;
    }


}

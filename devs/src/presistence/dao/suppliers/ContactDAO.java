package presistence.dao.suppliers;

import logic.suppliers.models.Contact;
import presistence.dao.DAO;
import presistence.dao.Result;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class ContactDAO implements DAO<Contact, String, String, String> {

    private Connection conn = null; //Connection instance

    @Override
    public Result update(Contact product, String key, String dummy1, String dummy2) {
        String sql = "UPDATE contacts SET supplierNum = ?, cellPhone = ?, name = ? " +
                " WHERE supplierNum = ? AND cellPhone = ?" ;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, product.getSupplierNum());
            pstmt.setString(2, product.getPhoneNum());
            pstmt.setString(3, product.getName());


            pstmt.setInt(4, Integer.parseInt(key));
            pstmt.setString(5, product.getPhoneNum());
            pstmt.executeUpdate();
            conn.commit();

            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result delete(Contact contact) {
        if(findByKey(contact) == null)
            return Result.FAIL;
        String sql = "DELETE FROM contacts WHERE supplierNum = ? AND cellPhone = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, contact.getSupplierNum());
            pstmt.setString(2, contact.getPhoneNum());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result insert(Contact contact) {
        if(findByKey(contact) != null)
            return Result.FAIL;
        String sql = "INSERT INTO contacts(supplierNum, cellPhone, name)" +
                " VALUES (?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, contact.getSupplierNum());
            pstmt.setString(2, contact.getPhoneNum());
            pstmt.setString(3, contact.getName());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Contact findByKey(Contact contact) {
        String sql = "SELECT * FROM contacts WHERE supplierNum = ? AND cellPhone = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, contact.getSupplierNum());
            pstmt.setString(2, contact.getPhoneNum());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                return new Contact(rs.getInt("supplierNum"),
                        rs.getString("cellPhone"),
                        rs.getString("name"));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Contact> findByKeys(Contact contact) {
        String sql = "SELECT * FROM contacts WHERE supplierNum = ?";
        try {
            List cont = new LinkedList<Contact>();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, contact.getSupplierNum());
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                cont.add(new Contact(rs.getInt("supplierNum"),
                        rs.getString("cellphone"),
                        rs.getString("name")));
            }
            rs.close();
            return cont;
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
    public LinkedList<Contact> findByVal(Object id) {
        return null;
    }


}

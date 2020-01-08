package presistence.dao.deliveries;

import logic.deliveries.models.Product;
import logic.deliveries.models.Supplier;
import logic.deliveries.models.Site;
import presistence.ConnectionHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

public class SupplierDAO {
    private ConnectionHandler connectionHandler = new ConnectionHandler();
    private ProductDAO prodDAO = new ProductDAO();
    private final String tablename = "suppliers";
    private final String siteTablename = "sites";
    private final String sup2prodTable = "supp2prod";

    public Supplier findID(int ID){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format("SELECT {1}.address, {1}.phoneNumber, " +
                    "{1}.contactPerson, {1}.area, {0}.supplierNum " +
                    "FROM {0} join {1} ON {0}.address={1}.address WHERE {0}.supplierNum = ?",tablename,siteTablename));
            ps.setInt(1,ID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Supplier sup = new Supplier(rs.getString(1),rs.getString(2),
                        rs.getString(3),rs.getString(4),rs.getInt(5));
                rs.close();
                return sup;
            }
            else return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public Site findAddrss(String addrss){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format("SELECT {1}.address, {1}.phoneNumber, " +
                    "{1}.contactPerson, {1}.area, {0}.supplierNum " +
                    " FROM {0} join {1} ON {0}.address={1}.address WHERE {0}.address = ?",tablename,siteTablename));
            ps.setString(1,addrss);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Supplier sup = new Supplier(rs.getString(1),rs.getString(2),
                        rs.getString(3),rs.getString(4),rs.getInt(5));
                rs.close();
                return sup;
            }
            else return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connectionHandler.closeConnection();
        }
    }


    public void insert(Supplier s){
        try {
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("INSERT INTO {0}(address,phoneNumber,contactPerson,area) " +
                    "VALUES (?,?,?,?)",siteTablename));
            ps.setString(1,s.getAddress());
            ps.setString(2,s.getPhoneNumber());
            ps.setString(3,s.getContactPerson());
            ps.setString(4,s.getArea());
            ps.executeUpdate();
            ps.close();
            ps = conn.prepareStatement(MessageFormat.format("UPDATE {0} SET address = ?" +
                    "WHERE supplierNum = ?",tablename));
            ps.setString(1,s.getAddress());
            ps.setInt(2,s.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void update(Supplier s){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "DELETE FROM {0} WHERE supplierNum=?",
                    sup2prodTable));
            ps.setInt(1,s.getId());
            ps.executeUpdate();
            ps.close();
            ps = conn.prepareStatement(MessageFormat.format(
                    "UPDATE {0} SET phoneNumber=?,contactPerson=?,area=? WHERE address=?",
                    siteTablename));
            ps.setString(1,s.getPhoneNumber());
            ps.setString(2,s.getContactPerson());
            ps.setString(3,s.getArea());
            ps.setString(4,s.getAddress());
            ps.executeUpdate();
            ps.close();
            ps = conn.prepareStatement(MessageFormat.format(
                    "UPDATE {0} SET address=? WHERE supplierNum=?",
                    tablename));
            ps.setString(1,s.getAddress());
            ps.setInt(2,s.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void delete(Supplier s){
        try{
            Connection conn = connectionHandler.connect();

            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "DELETE FROM {0} WHERE supplierNum=?",
                    sup2prodTable));
            ps.setInt(1,s.getId());
            ps.executeUpdate();
            ps.close();

            ps = conn.prepareStatement(MessageFormat.format(
                    "DELETE FROM {0} WHERE supplierNum=?",
                    tablename));
            ps.setInt(1,s.getId());
            ps.executeUpdate();
            ps.close();
            ps = conn.prepareStatement(MessageFormat.format(
                    "DELETE FROM {0} WHERE address=?",
                    siteTablename));
            ps.setString(1,s.getAddress());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }
/*
    public Supplier findByAreaAndProduct(String area, int id) {
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format("SELECT {1}.address, {1}.phoneNumber, " +
                    "{1}.contactPerson, {1}.area, {0}.supplierNum, {0}.independent, {0}.prod_id " +
                    "FROM {0} join {1} ON {0}.address={1}.address WHERE {1}.area = ? and {0}.supplierNum in (Select supplierNum From {2} Where prod_id=?)",tablename,siteTablename,sup2prodTable));
            ps.setString(1,area);
            ps.setInt(2,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Product prod = prodDAO.findID(rs.getInt(6));
                Supplier sup = new Supplier(rs.getString(1),rs.getString(2),
                        rs.getString(3),rs.getString(4),rs.getInt(5),
                        rs.getBoolean(6),prodDAO.findSup(rs.getInt(5)));
                rs.close();
                return sup;
            }
            else return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public boolean getIndependent(String area, int id) {
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format("SELECT * " +
                    "FROM {0} join {1} ON {0}.address={1}.address " +
                    "WHERE {1}.area = ? and {0}.supplierNum in (Select supplierNum From {2} Where prod_id=?) and {0}.independent=? ",tablename,siteTablename,sup2prodTable));
            ps.setString(1,area);
            ps.setInt(2,id);
            ps.setBoolean(3,true);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                rs.close();
                return true;
            }
            else {
                rs.close();
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        } finally {
            connectionHandler.closeConnection();
        }
    }
 */
}

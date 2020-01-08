package presistence.dao.deliveries;

import logic.deliveries.models.Branch;
import presistence.ConnectionHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

public class BranchDAO {

    private ConnectionHandler connectionHandler=new ConnectionHandler();
    private final String tablename = "branches";
    private final String siteTablename = "sites";

    public Branch findID(int ID){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format("SELECT {1}.address, {1}.phoneNumber, " +
                    "{1}.contactPerson, {1}.area, {0}.branchID " +
                    "FROM {0} join {1} ON {0}.address={1}.address WHERE {0}.branchID = ?",tablename,siteTablename));
            ps.setInt(1,ID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Branch branch = new Branch(rs.getString(1),rs.getString(2),
                        rs.getString(3),rs.getString(4),rs.getInt(5));
                rs.close();
                return branch;
            }
            else return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public Branch findAddrss(String addrss){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format("SELECT {1}.address, {1}.phoneNumber, " +
                    "{1}.contactPerson, {1}.area, {0}.branchID " +
                    "FROM {0} join {1} ON {0}.address={1}.address WHERE {1}.address = ?",tablename,siteTablename));
            ps.setString(1,addrss);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Branch branch = new Branch(rs.getString(1),rs.getString(2),
                        rs.getString(3),rs.getString(4),rs.getInt(5));
                rs.close();
                return branch;
            }
            else return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connectionHandler.closeConnection();
        }
    }


    public void insert(Branch branch){
        try {
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("INSERT INTO {0}(address,phoneNumber,contactPerson,area) " +
                    "VALUES (?,?,?,?)",siteTablename));
            ps.setString(1,branch.getAddress());
            ps.setString(2,branch.getPhoneNumber());
            ps.setString(3,branch.getContactPerson());
            ps.setString(4,branch.getArea());
            ps.executeUpdate();
            ps.close();
            ps = conn.prepareStatement(MessageFormat.format("INSERT INTO {0}(branchID,address) " +
                    "VALUES (?,?)",tablename));
            ps.setInt(1,branch.getId());
            ps.setString(2,branch.getAddress());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void update(Branch branch){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "UPDATE {0} SET phoneNumber=?,contactPerson=?,area=? WHERE address=?",
                    siteTablename));
            ps.setString(1,branch.getPhoneNumber());
            ps.setString(2,branch.getContactPerson());
            ps.setString(3,branch.getArea());
            ps.setString(4,branch.getAddress());
            ps.executeUpdate();
            ps.close();
            ps = conn.prepareStatement(MessageFormat.format(
                    "UPDATE {0} SET address=? WHERE branchID=?",
                    tablename));
            ps.setString(1,branch.getAddress());
            ps.setInt(2,branch.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void delete(Branch branch){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "DELETE FROM {0} WHERE branchID=?",
                    tablename));
            ps.setInt(1,branch.getId());
            ps.executeUpdate();
            ps.close();
            ps = conn.prepareStatement(MessageFormat.format(
                    "DELETE FROM {0} WHERE address=?",
                    siteTablename));
            ps.setString(1,branch.getAddress());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

}

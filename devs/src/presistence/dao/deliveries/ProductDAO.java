package presistence.dao.deliveries;

import logic.deliveries.models.Product;
import presistence.ConnectionHandler;

import java.sql.*;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

public class ProductDAO {
    private ConnectionHandler connectionHandler=new ConnectionHandler();
    private final String tablename = "delivery_products";
    private final String sup2prodTable = "supp2prod";

    public Product findID(int ID){
        try {
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("SELECT prod_id, name, weight FROM {0} WHERE prod_id = ?",tablename));
            ps.setInt(1, ID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Product prod = new Product(rs.getInt(1),rs.getString(2),rs.getDouble(3));
                rs.close();
                ps.close();
                connectionHandler.closeConnection();
                return prod;

            }
            else {
                connectionHandler.closeConnection();
                return null;
            }
        }catch (SQLException e) {
            connectionHandler.closeConnection();
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void insert(Product p){
        try {
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("INSERT INTO {0}(prod_id,name,weight) VALUES (?,?,?)", tablename));
            ps.setInt(1, p.getId());
            ps.setString(2, p.getName());
            ps.setDouble(3, p.getWeight());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }
    public void update(Product p){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("UPDATE {0} " +
                    "SET name = ?, weight = ? WHERE prod_id = ?",tablename));
            ps.setString(1,p.getName());
            ps.setDouble(2,p.getWeight());
            ps.setInt(3,p.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }
    public void delete(Product p){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("DELETE FROM {0} WHERE prod_id = ?",tablename));
            ps.setInt(1,p.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }

    }

    public List<Product> findSup(int ID) {
        try {
            LinkedList<Product> productLinkedList = new LinkedList<>();
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("SELECT {0}.prod_id, name, weight FROM {0} join {1} ON {0}.prod_id = {1}.prod_id WHERE {1}.supp_id = ?",tablename,sup2prodTable));
            ps.setInt(1, ID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Product prod = new Product(rs.getInt(1),rs.getString(2),rs.getDouble(3));
                productLinkedList.add(prod);
            }
            rs.close();
            ps.close();
            connectionHandler.closeConnection();
            return productLinkedList;
        }catch (SQLException e) {
            connectionHandler.closeConnection();
            System.out.println(e.getMessage());
            return null;
        }
    }
}

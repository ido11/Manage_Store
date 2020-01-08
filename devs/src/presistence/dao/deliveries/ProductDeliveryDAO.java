package presistence.dao.deliveries;

import logic.deliveries.models.*;
import logic.deliveries.models.ProductDelivery;
import presistence.ConnectionHandler;
import presistence.dao.inventory.ProductsDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

public class ProductDeliveryDAO {


    private ConnectionHandler connectionHandler = new ConnectionHandler();
    private SiteDAO siteDAO = new SiteDAO();
    private BranchDAO branchDAO = new BranchDAO();
    private SupplierDAO supplierDAO = new SupplierDAO();
    private ProductsDAO productDAO = new  presistence.dao.inventory.ProductsDAO();
    private final String tablename = "productsDeliveries";

    public ProductDelivery findID(int deliveryID, String suppAddrs, String brenchAddrs, int prodID){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format(
                    "SELECT delivery_id,supp_addrs, prod_id,count,dest_brench_address " +
                    "FROM {0} WHERE delivery_id=? and supp_addrs=? and prod_id=? and dest_brench_address=?",tablename));
            ps.setInt(1,deliveryID);
            ps.setString(2,suppAddrs);
            ps.setInt(3,prodID);
            ps.setString(4,brenchAddrs);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Site sup = siteDAO.findAddrs(rs.getString(2));
                Site branch = siteDAO.findAddrs(rs.getString(5));
                logic.inventory.models.Product p = productDAO.findByKey(new logic.inventory.models.Product(rs.getInt(3)));
                ProductDelivery pd = new ProductDelivery(rs.getInt(1),sup,branch,p,rs.getInt(4));
                rs.close();
                return pd;
            }
            else return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void insert(ProductDelivery pd){
        try {
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "INSERT INTO {0}(delivery_id,supp_addrs, prod_id,count,dest_brench_address) " +
                    "VALUES (?,?,?,?,?)",tablename));
            ps.setInt(1,pd.getDeliveryID());
            ps.setString(2,pd.getSuppSite().getAddress());
            ps.setInt(3,pd.getProduct().getBarcode());
            ps.setInt(4,pd.getCountOfProducts());
            ps.setString(5,pd.getBranchSite().getAddress());
            ps.executeUpdate();
            ps.close();
            if(siteDAO.findAddrs(pd.getSuppSite().getAddress())==null)
                siteDAO.insert(pd.getSuppSite());
            if(siteDAO.findAddrs(pd.getBranchSite().getAddress())==null)
                siteDAO.insert(pd.getBranchSite());
            if(productDAO.findByKey(new logic.inventory.models.Product(pd.getProduct().getBarcode()))==null)
            productDAO.insert(pd.getProduct());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void update(ProductDelivery pd){
        try{
            //siteDAO.update(pd.getBranchSite());
            //siteDAO.update(pd.getSuppSite());
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "UPDATE {0} SET count=? " +
                            "WHERE delivery_id=? and supp_addrs=? and prod_id=? and dest_brench_address=?",
                    tablename));
            ps.setInt(1,pd.getCountOfProducts());
            ps.setInt(2,pd.getDeliveryID());
            ps.setString(3,pd.getSuppSite().getAddress());
            ps.setInt(4,pd.getProduct().getBarcode());
            ps.setString(5,pd.getBranchSite().getAddress());
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void delete(ProductDelivery pd){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "DELETE FROM {0} " +
                            "WHERE delivery_id=? and supp_addrs=? and prod_id=? and dest_brench_address=?",
                    tablename));
            ps.setInt(1,pd.getDeliveryID());
            ps.setString(2,pd.getSuppSite().getAddress());
            ps.setInt(3,pd.getProduct().getBarcode());
            ps.setString(4,pd.getBranchSite().getAddress());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public List<ProductDelivery> findSite(Site s) {
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps;
            if(s instanceof Supplier) {
                ps = conn.prepareStatement(MessageFormat.format(
                        "SELECT delivery_id,supp_addrs, prod_id,count,dest_brench_address " +
                                "FROM {0} WHERE supp_addrs=?", tablename));
            }
            else if(s instanceof Branch) {
                ps = conn.prepareStatement(MessageFormat.format(
                        "SELECT delivery_id,supp_addrs, prod_id,count,dest_brench_address " +
                                "FROM {0} WHERE dest_brench_address=?", tablename));
            }
            else return null;
            ps.setString(1,s.getAddress());
            ResultSet rs = ps.executeQuery();
            LinkedList<ProductDelivery> productDeliveries = new LinkedList<>();
            while(rs.next()){
                Site sup = siteDAO.findAddrs(rs.getString(2));
                Site branch = siteDAO.findAddrs(rs.getString(5));
                logic.inventory.models.Product p = productDAO.findByKey(new logic.inventory.models.Product(rs.getInt(3)));
                ProductDelivery pd = new ProductDelivery(rs.getInt(1),sup,branch,p,rs.getInt(4));
                productDeliveries.add(pd);
            }
            rs.close();
            return productDeliveries;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connectionHandler.closeConnection();
        }
    }
}

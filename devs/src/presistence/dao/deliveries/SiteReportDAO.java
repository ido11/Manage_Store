package presistence.dao.deliveries;

import logic.deliveries.models.*;
import logic.deliveries.models.SiteReport;
import presistence.ConnectionHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

public class SiteReportDAO {

    private ConnectionHandler connectionHandler = new ConnectionHandler();
    private SiteDAO siteDAO = new SiteDAO();
    private ProductDeliveryDAO productDeliveryDAO = new ProductDeliveryDAO();
    private final String tablename = "site_report";

    public SiteReport findID(int deliveryID, String siteAddrss){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format(
                    "SELECT  delivery_id,site_addrs,weight_on_site,deliveryOrder" +
                    " FROM {0} WHERE delivery_id = ? and site_addrs = ?",
                    tablename));
            ps.setInt(1,deliveryID);
            ps.setString(2,siteAddrss);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Site s = siteDAO.findAddrs(rs.getString(2));
                List<ProductDelivery> pd = productDeliveryDAO.findSite(s);
                SiteReport sr = new SiteReport(deliveryID, s,rs.getDouble(3),rs.getInt(4),pd);
                rs.close();
                return sr;
            }
            else return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connectionHandler.closeConnection();
        }
    }



    public void insert(SiteReport s){
        try {
            if(siteDAO.findAddrs(s.getSite().getAddress())==null)
                siteDAO.insert(s.getSite());
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("INSERT INTO {0}(delivery_id,site_addrs,weight_on_site,deliveryOrder) " +
                    "VALUES (?,?,?,?)",tablename));
            ps.setInt(1,s.getDeliveryID());
            ps.setString(2,s.getSite().getAddress());
            ps.setDouble(3,s.getWeightOnSite());
            ps.setInt(4,s.getOrder());
            ps.executeUpdate();
            ps.close();
            for(ProductDelivery pd:s.getProductDelivery()){
                if(productDeliveryDAO.findID(pd.getDeliveryID(),pd.getSuppSite().getAddress(),pd.getBranchSite().getAddress(),pd.getProduct().getBarcode())==null)
                    productDeliveryDAO.insert(pd);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void update(SiteReport s){
        try{
            for(ProductDelivery pd:s.getProductDelivery()){
                productDeliveryDAO.update(pd);
            }
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "UPDATE {0} SET weight_on_site=?,deliveryOrder=? WHERE delivery_id = ? and site_addrs = ?",
                    tablename));
            ps.setDouble(1,s.getWeightOnSite());
            ps.setInt(2,s.getOrder());
            ps.setInt(3,s.getDeliveryID());
            ps.setString(4,s.getSite().getAddress());
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void delete(SiteReport s){
        try{
            if(s.getProductDelivery()!=null) {
                for (ProductDelivery pd : s.getProductDelivery()) {
                    if (pd != null) productDeliveryDAO.delete(pd);
                }
            }
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "DELETE FROM {0} WHERE delivery_id = ? and site_addrs = ?",
                    tablename));
            ps.setInt(1,s.getDeliveryID());
            ps.setString(2,s.getSite().getAddress());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public List<SiteReport> findByDelivery(int deliveryID) {
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format(
                    "SELECT  delivery_id,site_addrs,weight_on_site,deliveryOrder " +
                            "FROM {0} WHERE delivery_id = ?",
                    tablename));
            ps.setInt(1,deliveryID);
            ResultSet rs = ps.executeQuery();
            LinkedList<SiteReport> siteReports = new LinkedList<>();
            while(rs.next()){
                Site s = siteDAO.findAddrs(rs.getString(2));
                List<ProductDelivery> pd = productDeliveryDAO.findSite(s);
                SiteReport sr = new SiteReport(deliveryID, s,rs.getDouble(3),rs.getInt(4),pd);
                siteReports.addLast(sr);
            }
            rs.close();
            return siteReports;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connectionHandler.closeConnection();
        }
    }
}

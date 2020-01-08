package presistence.dao.deliveries;

import logic.deliveries.models.Driver;
import logic.deliveries.models.DeliveryDoc;
import logic.deliveries.models.SiteReport;
import logic.deliveries.models.Truck;
import presistence.ConnectionHandler;

import java.sql.*;
import java.text.MessageFormat;
import java.util.List;

public class DeliveryDocDAO {


    private ConnectionHandler connectionHandler = new ConnectionHandler();
    private DriverDAO driverDAO = new DriverDAO();
    private TruckDAO truckDAO = new TruckDAO();
    private SiteReportDAO siteReportDAO = new SiteReportDAO();
    private final String tablename = "deliveryDocs";

    public DeliveryDoc findID(int ID){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format("SELECT delivery_id,driver_id, truck_id " +
                    "FROM {0} WHERE delivery_id=? ",tablename));
            ps.setInt(1,ID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Driver d = driverDAO.findID(rs.getInt(2));
                Truck t = truckDAO.findlicense(rs.getString(3));
                List<SiteReport> siteReports = siteReportDAO.findByDelivery(rs.getInt(1));
                DeliveryDoc dd = new DeliveryDoc(rs.getInt(1),d,t,siteReports);
                rs.close();
                return dd;
            }
            else return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void insert(DeliveryDoc dd){
        try {
            if (driverDAO.findID(dd.getDriver().getId()) == null)
                driverDAO.insert(dd.getDriver());
            if(truckDAO.findlicense(dd.getTruck().getLicensePlate())==null)
                truckDAO.insert(dd.getTruck());
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("INSERT INTO {0}(delivery_id,driver_id,truck_id) " +
                    "VALUES (?,?,?)",tablename));
            ps.setInt(1,dd.getDeliveryID());
            ps.setInt(2,dd.getDriver().getId());
            ps.setString(3,dd.getTruck().getLicensePlate());
            ps.executeUpdate();
            ps.close();
            for(SiteReport s:dd.getSiteReports()){
                if(siteReportDAO.findID(dd.getDeliveryID(),s.getSite().getAddress())==null)
                    siteReportDAO.insert(s);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void update(DeliveryDoc dd){
        try{
            driverDAO.update(dd.getDriver());
            truckDAO.update(dd.getTruck());
            for(SiteReport s:dd.getSiteReports())
                siteReportDAO.update(s);
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "UPDATE {0} SET driver_id=?,truck_id=? WHERE delivery_id=?",
                    tablename));
            ps.setInt(1,dd.getDriver().getId());
            ps.setString(2,dd.getTruck().getLicensePlate());
            ps.setInt(3,dd.getDeliveryID());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void delete(DeliveryDoc dd){
        try{
            for(SiteReport s:dd.getSiteReports())
                siteReportDAO.delete(s);
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "DELETE FROM {0} WHERE delivery_id=?",
                    tablename));
            ps.setInt(1,dd.getDeliveryID());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }
}

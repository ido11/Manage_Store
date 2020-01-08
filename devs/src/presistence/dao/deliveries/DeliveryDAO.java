package presistence.dao.deliveries;

import logic.deliveries.models.DeliveryDoc;
import logic.deliveries.models.Delivery;
import presistence.ConnectionHandler;
import presentation.deliveries.DeliveriesPrinter;
import logic.deliveries.models.Delivery;

import java.sql.*;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class DeliveryDAO {

    private ConnectionHandler connectionHandler = new ConnectionHandler();
    private DeliveryDocDAO deliveryDocDAO = new DeliveryDocDAO();
    private final String tablename = "deliveries";
    private final String deliveryDocsTable = "deliveryDocs";

    public Delivery findID(int ID){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format("SELECT delivery_id,start_date,start_time " +
                    "FROM {0} WHERE delivery_id=? ",tablename));
            ps.setInt(1,ID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                DeliveryDoc dd = deliveryDocDAO.findID(rs.getInt(1));
                Delivery d = new Delivery(rs.getInt(1),dd,rs.getDate(2),rs.getTime(3));
                rs.close();
                return d;
            }
            else return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void insert(Delivery d){
        try {
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("INSERT INTO {0}(delivery_id,start_date,start_time) " +
                    "VALUES (?,?,?)",tablename));
            ps.setInt(1,d.getDeliveryID());
            ps.setDate(2,d.getStartDate());
            ps.setTime(3,d.getStartTime());
            ps.executeUpdate();
            ps.close();
            if (d.getDeliveryDoc()!=null&&deliveryDocDAO.findID(d.getDeliveryDoc().getDeliveryID()) == null)
                deliveryDocDAO.insert(d.getDeliveryDoc());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void update(Delivery d){
        try{
            if(d.getDeliveryDoc()!=null)
                deliveryDocDAO.update(d.getDeliveryDoc());
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "UPDATE {0} SET start_date=?,start_time=? WHERE delivery_id=?",
                    tablename));
            ps.setDate(1,d.getStartDate());
            ps.setTime(2,d.getStartTime());
            ps.setInt(3,d.getDeliveryID());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void delete(Delivery d){
        try{
            if(d.getDeliveryDoc()!=null)
                deliveryDocDAO.delete(d.getDeliveryDoc());
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "DELETE FROM {0} WHERE delivery_id=?",
                    tablename));
            ps.setInt(1,d.getDeliveryID());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public List<Delivery> findAvailableDeliveries() {
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format("SELECT delivery_id,start_date,start_time " +
                    "FROM {0} WHERE start_date > ? and start_date <= ? order by start_date",tablename));
            ps.setDate(1,new Date(Calendar.getInstance().getTime().getTime()));
            ps.setDate(2,(new Date(Calendar.getInstance().getTime().getTime()+604800000)));
            ResultSet rs = ps.executeQuery();
            LinkedList<Delivery> dlist = new LinkedList<Delivery>();
            while(rs.next()){
                DeliveryDoc dd = deliveryDocDAO.findID(rs.getInt(1));
                Delivery d = new Delivery(rs.getInt(1),dd,rs.getDate(2),rs.getTime(3));
                dlist.addLast(d);
            }
            rs.close();
            return dlist;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public int getNewID() {
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format("SELECT MAX(delivery_id) " +
                    "FROM {0}",tablename));
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int maxID = rs.getInt(1);
                rs.close();
                return maxID+1;
            }
            else return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public boolean isAvailable(int driverId, String shiftType, Date date) {
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format("SELECT * " +
                    "FROM {0} join {1} on {0}.delivery_id = {1}.delivery_id " +
                    "WHERE {1}.driver_id=? and {0}.start_date = ? and {0}.start_time{2}? ",
                    tablename,deliveryDocsTable,shiftType.equals("Morning")?"<":">="));
            ps.setInt(1,driverId);
            ps.setDate(2,date);
            ps.setTime(3,Time.valueOf("16:00:00"));
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                rs.close();
                return false;
            }
            else {
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public Delivery findDriverID(int driverID){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format("SELECT {0}.delivery_id " +
                    " FROM {0} inner join {1} on {0}.delivery_id = {1}.delivery_id " +
                    " WHERE {1}.driver_id=? and {0}.start_date >= ? order by start_date",tablename, deliveryDocsTable));
            ps.setInt(1,driverID);
            ps.setDate(2,new Date(Calendar.getInstance().getTime().getTime()));
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Delivery d = findID(rs.getInt(1));
                rs.close();
                return d;
            }
            else return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connectionHandler.closeConnection();
        }
    }
}

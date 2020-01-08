package presistence.dao.deliveries;

import logic.deliveries.models.Truck;
import presistence.ConnectionHandler;

import java.sql.*;
import java.text.MessageFormat;

public class TruckDAO {

    private ConnectionHandler connectionHandler = new ConnectionHandler();
    private final String tablename = "trucks";
    private final  String deliveriesTable = "deliveries";
    private final String deliverieDocTable = "deliveryDocs";

    public Truck findlicense(String lp){
        try {
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "SELECT license_plate, type, base_weight,max_weight FROM {0} WHERE license_plate = ?",
                    tablename));
            ps.setString(1, lp);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Truck t = new Truck(rs.getString(1),rs.getString(2),rs.getDouble(3),rs.getDouble(4));
                rs.close();
                ps.close();
                connectionHandler.closeConnection();
                return t;

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

    public void insert(Truck t){
        try {
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("INSERT INTO {0}(license_plate, type, base_weight,max_weight) VALUES (?,?,?,?)", tablename));
            ps.setString(1, t.getLicensePlate());
            ps.setString(2, t.getType());
            ps.setDouble(3, t.getBaseWeight());
            ps.setDouble(4, t.getMaxWeight());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }
    public void update(Truck t){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("UPDATE {0} " +
                    "SET type = ?, base_weight = ?,max_weight=? WHERE license_plate = ?",tablename));
            ps.setString(1,t.getType());
            ps.setDouble(2,t.getBaseWeight());
            ps.setDouble(3,t.getMaxWeight());
            ps.setString(4,t.getLicensePlate());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }
    public void delete(Truck t){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("DELETE FROM {0} WHERE license_plate = ?",tablename));
            ps.setString(1,t.getLicensePlate());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }

    }



    public Truck findAvailable(Date startDate, String shift, double v) {
        try {
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "SELECT license_plate, type, base_weight,max_weight FROM {0} " +
                            "WHERE license_plate not in (" +
                            "SELECT truck_id from {1} join {2} on {1}.delivery_id={2}.delivery_id" +
                            " WHERE start_date=? and start_time{3}?) " +
                            "ORDER BY max_weight ASC"
                            ,tablename,deliveriesTable,deliverieDocTable,shift.equals("morning")?"<":">="));
            ps.setDate(1,startDate);
            ps.setTime(2,Time.valueOf("16:00:00"));
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Truck t = new Truck(rs.getString(1),rs.getString(2),rs.getDouble(3),rs.getDouble(4));
                if(t.getMaxWeight()-t.getBaseWeight()>=v){
                    rs.close();
                    ps.close();
                    connectionHandler.closeConnection();
                    return t;
                }
            }
            rs.close();
            ps.close();
            connectionHandler.closeConnection();
            return null;
        }catch (SQLException e) {
            connectionHandler.closeConnection();
            System.out.println(e.getMessage());
            return null;
        }
    }
}

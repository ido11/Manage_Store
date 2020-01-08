package presistence.dao.deliveries;

import logic.deliveries.models.DriverShift;
import logic.deliveries.models.Driver;
import presistence.ConnectionHandler;

import java.sql.*;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class DriverShiftDAO {

    private ConnectionHandler connectionHandler = new ConnectionHandler();
    private DriverDAO driverDAO = new DriverDAO();
    private DeliveryDAO deliveryDAO = new DeliveryDAO();
    private final String tablename = "drivers_shifts";

    public DriverShift findID(int driverID, Date shiftDate, String shiftType){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format("SELECT shift_date, driver_id, type " +
                    "FROM {0} " +
                    "WHERE shift_date=? and driver_id=? and type=? ",tablename));
            ps.setDate(1,shiftDate);
            ps.setInt(2,driverID);
            ps.setString(3,shiftType);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Driver d = driverDAO.findID(rs.getInt(2));
                DriverShift ds = new DriverShift(rs.getDate(1),d,rs.getString(3));
                rs.close();
                return ds;
            }
            else return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public void insert(DriverShift ds){
        try {
            if (driverDAO.findID(ds.getDriver().getId()) == null)
                driverDAO.insert(ds.getDriver());
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("INSERT INTO {0}(shift_date, driver_id, type) " +
                    "VALUES (?,?,?)",tablename));
            ps.setDate(1,ds.getDate());
            ps.setInt(2,ds.getDriver().getId());
            ps.setString(3,ds.getType());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }


    public void delete(DriverShift ds){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                    "DELETE FROM {0} WHERE shift_date=? and driver_id=? and type=?",
                    tablename));
            ps.setDate(1,ds.getDate());
            ps.setInt(2,ds.getDriver().getId());
            ps.setString(3,ds.getType());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }

    public List<DriverShift> getAvailable() {
        LinkedList<DriverShift> driverShifts = new LinkedList<>();
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps =  conn.prepareStatement(MessageFormat.format("SELECT shift_date, driver_id, type " +
                    "FROM {0} WHERE shift_date>?",tablename));
            ps.setDate(1,new Date(Calendar.getInstance().getTime().getTime()));
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Driver d = driverDAO.findID(rs.getInt(2));
                DriverShift ds = new DriverShift(rs.getDate(1),d,rs.getString(3));
                if(deliveryDAO.isAvailable(d.getId(),ds.getType(),ds.getDate()))
                    driverShifts.add(ds);
            }
            rs.close();
            return driverShifts;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connectionHandler.closeConnection();
        }
    }
}

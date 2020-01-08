package presistence.dao.deliveries;

import logic.deliveries.models.Driver;
import presistence.dao.employees.EmployeeDAO;
import presistence.ConnectionHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DriverDAO {

    private ConnectionHandler connectionHandler=new ConnectionHandler();
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private final String tablename = "drivers";
    private final String EmpTable = "employees";

    public Driver findID(int ID){
        try {
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("SELECT {1}.id, {1}.firstname, {1}.lastname, {1}.salary, {1}.firstEmployed, {1}.employmentCond, {0}.license FROM {0} join {1} ON {0}.ID = {1}.id WHERE {0}.ID = ?",tablename,EmpTable));
            ps.setInt(1, ID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Driver driver = new Driver(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4),df.parse(rs.getString(5)),rs.getString(6),rs.getString(7));
                rs.close();
                ps.close();
                connectionHandler.closeConnection();
                return driver;

            }
            else {
                connectionHandler.closeConnection();
                return null;
            }
        }catch (SQLException | ParseException e) {
            connectionHandler.closeConnection();
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void insert(Driver d){
        try {
            Connection conn = connectionHandler.connect();
            employeeDAO.setConnection(conn);
            employeeDAO.insert(d);
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("INSERT INTO {0}(ID, license) VALUES (?,?)", tablename));
            ps.setInt(1, d.getId());
            ps.setString(2, d.getLicense());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }
    public void update(Driver d){
        try{
            Connection conn = connectionHandler.connect();
            employeeDAO.setConnection(conn);
            employeeDAO.update(d,d.getKey(), 0,0);
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("UPDATE {0} " +
                    "SET license = ? WHERE ID = ?",tablename));
            ps.setString(1,d.getLicense());
            ps.setInt(2,d.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }
    }
    public void delete(Driver d){
        try{
            Connection conn = connectionHandler.connect();
            PreparedStatement ps = conn.prepareStatement(MessageFormat.format("DELETE FROM {0} WHERE ID = ?",tablename));
            ps.setInt(1,d.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connectionHandler.closeConnection();
        }

    }
}

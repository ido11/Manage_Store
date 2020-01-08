package presistence.dao.employees;

import logic.deliveries.models.Branch;
import logic.deliveries.models.Truck;
import logic.employees.models.Shift;
import logic.employees.models.ShiftAssigning;
import presistence.ConnectionHandler;
import presistence.dao.DAO;
import presistence.dao.Result;

import java.sql.*;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ShiftsAssigningDAO implements DAO<ShiftAssigning, Integer, Integer, Integer> {

    private Connection conn = null; //Connection instance

    final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");


    @Override
    public Result update(ShiftAssigning shiftAssigning, Integer empID, Integer dummy1, Integer dummy2) {
    /*    String sql = "UPDATE shiftsAssigning SET empID = ? WHERE shiftID = ? AND roleID = ? AND empID = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, shiftAssigning.getEmpID());
            pstmt.setInt(2, shiftAssigning.getShiftID());
            pstmt.setInt(3, shiftAssigning.getRoleID());
            pstmt.setInt(4, empID);
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL; */
    return null;
    }

    @Override
    public Result delete(ShiftAssigning shiftAssigning) {
        String sql = "DELETE FROM shiftsAssigning WHERE shiftID = ? AND roleID = ? AND empID = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, shiftAssigning.getShiftID());
            pstmt.setInt(2, shiftAssigning.getRoleID());
            pstmt.setInt(3, shiftAssigning.getEmpID());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result insert(ShiftAssigning shiftAssigning) {
        if(findByKey(shiftAssigning) != null)
            return Result.FAIL;
        String sql = "INSERT INTO shiftsAssigning(shiftID, empID, roleID)" +
                " VALUES (?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, shiftAssigning.getKey());
            pstmt.setInt(2, shiftAssigning.getEmpID());
            pstmt.setInt(3, shiftAssigning.getRoleID());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public ShiftAssigning findByKey(ShiftAssigning shiftAssigning) {
        return null;
    }

    @Override
    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public LinkedList<ShiftAssigning> findByVal(Object id) {
        LinkedList<ShiftAssigning> toRet= new LinkedList<ShiftAssigning>();
        if (id.equals("")) {
            String sql = "SELECT shiftID, sha.roleID, description, empID" +
                    " FROM shiftsAssigning sha JOIN roles ro ON sha.roleID = ro.roleID ";

            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
                while(rs.next())
                {
                    int shiftID = rs.getInt("shiftID");
                    int roleID = rs.getInt("roleID");
                    int empID = rs.getInt("empID");
                    String roleDesc = rs.getString("description");
                    toRet.add(new ShiftAssigning(shiftID, roleID, empID, roleDesc));
                }
                if (toRet != null) {
                    return toRet;
                }
                rs.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return null;
        }

        else {
           /* String sql = "    SELECT shiftID, sha.roleID, description, empID" +
                    " FROM shiftsAssigning sha JOIN roles ro ON sha.roleID = ro.roleID" +
                    " WHERE sha.shiftID = ? ";
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, (int) id);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int shiftID = rs.getInt("shiftID");
                    int roleID = rs.getInt("roleID");
                    int empID = rs.getInt("empID");
                    String roleDesc = rs.getString("description");
                    toRet.add(new ShiftAssigning(shiftID, roleID, empID, roleDesc));
                }
                if (!toRet.isEmpty())
                    return toRet;
                rs.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            return null;*/
            String sql = "    SELECT shiftID, sha.roleID, description, empID" +
                    " FROM shiftsAssigning sha JOIN roles ro ON sha.roleID = ro.roleID" +
                    " WHERE sha.empID = ? ";
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, (int) id);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int shiftID = rs.getInt("shiftID");
                    int roleID = rs.getInt("roleID");
                    int empID = rs.getInt("empID");
                    String roleDesc = rs.getString("description");
                    toRet.add(new ShiftAssigning(shiftID, roleID, empID, roleDesc));
                }
                if (!toRet.isEmpty())
                    return toRet;
                rs.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            return null;
        }
    }

    public List<ShiftAssigning> getAvailableDrivers() {
        Connection conn2 = (new ConnectionHandler()).connect();
        LinkedList<ShiftAssigning> toRet= new LinkedList<ShiftAssigning>();
        String sql = "SELECT sha.shiftID, sha.roleID, description, empID, dateTime" +
                " FROM shiftsAssigning sha JOIN roles ro JOIN shifts ON sha.roleID = ro.roleID AND sha.shiftID = shifts.shiftID" +
                " WHERE description = ?";

        try {
            PreparedStatement pstmt = conn2.prepareStatement(sql);
            pstmt.setString(1,"driver");
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                if(df.parse(rs.getString(5)).before(Calendar.getInstance().getTime())||
                        df.parse(rs.getString(5)).after(new java.util.Date(Calendar.getInstance().getTime().getTime()+604800000)))
                    continue;
                int shiftID = rs.getInt("shiftID");
                int roleID = rs.getInt("roleID");
                int empID = rs.getInt("empID");
                String roleDesc = rs.getString("description");
                toRet.add(new ShiftAssigning(shiftID, roleID, empID, roleDesc));
            }
            rs.close();
            if (toRet != null) {
                toRet = findAvailables(toRet);
                return toRet;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;

    }

    private LinkedList<ShiftAssigning> findAvailables(LinkedList<ShiftAssigning> toRet) {
        LinkedList<ShiftAssigning> output = new LinkedList<>();
        try {
            Connection conn = (new ConnectionHandler()).connect();
            for (ShiftAssigning ss:toRet){
                Shift s = (new ShiftsDAO()).findByID(ss.getShiftID());
                String shift = s.getShiftTime();
                java.sql.Date shiftDate = new java.sql.Date(s.getDateTime().getTime());
                PreparedStatement ps = conn.prepareStatement(MessageFormat.format(
                        "SELECT * FROM {0} d join {1} dd on d.delivery_id = dd.delivery_id " +
                                " WHERE start_date=? and start_time{2}? "
                        ,"deliveries","deliveryDocs",shift.equals("morning")?"<":">="));
                ps.setDate(1,shiftDate);
                ps.setTime(2,Time.valueOf("16:00:00"));
                ResultSet rs = ps.executeQuery();
                if(!rs.next()){
                    output.add(ss);
                }
                rs.close();
                ps.close();
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return output;
    }

    public List<ShiftAssigning> getAvailableStockKeepers(Branch b) {
        Connection conn2 = (new ConnectionHandler()).connect();
        LinkedList<ShiftAssigning> toRet= new LinkedList<ShiftAssigning>();
        String sql = "SELECT sha.shiftID, sha.roleID, description, empID, dateTime" +
                " FROM shiftsAssigning sha JOIN roles ro JOIN shifts ON sha.roleID = ro.roleID AND sha.shiftID = shifts.shiftID" +
                " WHERE description = ? AND branch_id = ?";

        try {
            PreparedStatement pstmt = conn2.prepareStatement(sql);
            pstmt.setString(1,"stock keeper");
            pstmt.setInt(2, b.getId());
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                if(df.parse(rs.getString(5)).before(Calendar.getInstance().getTime())||
                        df.parse(rs.getString(5)).after(new java.util.Date(Calendar.getInstance().getTime().getTime()+604800000)))
                    continue;
                int shiftID = rs.getInt("shiftID");
                int roleID = rs.getInt("roleID");
                int empID = rs.getInt("empID");
                String roleDesc = rs.getString("description");
                toRet.add(new ShiftAssigning(shiftID, roleID, empID, roleDesc));
            }
            rs.close();
            if (toRet != null) {
                return toRet;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;

    }

    public boolean AvailableSK(java.util.Date date, String time, Branch branch) {
        Connection conn2 = (new ConnectionHandler()).connect();
        String sql = "SELECT shifts.shiftID, sha.roleID, description, empID, dateTime, shiftTime" +
                " FROM shiftsAssigning sha JOIN roles ro JOIN shifts ON sha.roleID = ro.roleID AND sha.shiftID = shifts.shiftID" +
                " WHERE description = ? AND branch_id = ?";
        boolean output = false;
        try {
            PreparedStatement pstmt = conn2.prepareStatement(sql);
            pstmt.setString(1,"stock keeper");
            pstmt.setInt(2,branch.getId());
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                if(df.parse(rs.getString("dateTime")).compareTo(date)==0 && rs.getString("shiftTime").equals(time)) {
                    output = true;
                    break;
                }
            }
            rs.close();
            return output;
        } catch (SQLException | ParseException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
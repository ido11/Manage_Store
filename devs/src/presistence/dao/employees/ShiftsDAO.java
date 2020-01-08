package presistence.dao.employees;

import presistence.ConnectionHandler;
import presistence.ConnectionHandler;
import logic.employees.models.Shift;
import presistence.dao.DAO;
import presistence.dao.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class ShiftsDAO implements DAO<Shift, Integer, Integer, Integer> {

    final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    private Connection conn = null; //Connection instance

    @Override
    public Result update(Shift shift, Integer key, Integer dummy1, Integer dummy2) {
        return null;
    }

    @Override
    public Result delete(Shift shift) {
        if(findByKey(shift) == null)
            return Result.FAIL;
        String sql = "DELETE FROM shifts WHERE shiftID = ? ";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, shift.getShiftID());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result insert(Shift shift) {
        if(findByKey(shift) != null)
        return Result.FAIL;
        String sql = "INSERT INTO shifts(managerId, datetime, shiftTime)" +
                " VALUES (?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, shift.getManagerID());
            String dateStr = df.format(shift.getDateTime());
            pstmt.setString(2, dateStr);
            pstmt.setString(3, shift.getShiftTime());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Shift findByKey(Shift shift) {
        String sql = "SELECT * FROM shifts WHERE shiftID = ? ";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, shift.getShiftID());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                int MID = rs.getInt("managerId");
                Date date = df.parse(rs.getString("dateTime"));
                int SID = rs.getInt("shiftID");
                String shiftTime = rs.getString("shiftTime");
                return new Shift(MID, date,SID, shiftTime,rs.getInt("branch_id"));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public LinkedList<Shift> findByVal(Object id) {
        if (id.equals("")){
            String sql = "SELECT * FROM shifts ";
            LinkedList<Shift> toRet= new LinkedList<Shift>();
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
                while(rs.next())
                {
                    int MID = rs.getInt("managerId");
                    String dateStr = rs.getString("dateTime");
                    Date date = df.parse(dateStr);
                    int SID = rs.getInt("shiftID");
                    String shiftTime = rs.getString("shiftTime");
                    toRet.add(new Shift(MID, date, SID, shiftTime,rs.getInt("branch_id")));
                }
                if (toRet != null) {
                    return toRet;
                }
                rs.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
        return null;
    }

    public Shift findByID(int shiftID) {
        Connection conn2 = (new ConnectionHandler()).connect();
        String sql = "SELECT * FROM shifts WHERE shiftID = ? ";
        try {
            PreparedStatement pstmt = conn2.prepareStatement(sql);
            pstmt.setInt(1, shiftID);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                int MID = rs.getInt("managerId");
                Date date = df.parse(rs.getString("dateTime"));
                int SID = rs.getInt("shiftID");
                String shiftTime = rs.getString("shiftTime");
                Shift sh = new Shift(MID, date,SID, shiftTime,rs.getInt("branch_id"));
                rs.close();
                return sh;
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }


}

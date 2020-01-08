package presistence.dao.employees;


import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

import logic.employees.models.Constraints;
import presistence.dao.DAO;
import presistence.dao.Result;


public class ConstraintsDAO implements DAO<Constraints, String[], Integer, Integer> {

    final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    final SimpleDateFormat dfH = new SimpleDateFormat("hh:mm:ss");
    private Connection conn = null; //Connection instance

    @Override
    public Result update(Constraints constraints, String key[], Integer dummy1, Integer dummy2) {
      /*  String sql = "UPDATE constraints SET empID = ?, start = ?, finish = ? ";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, constraints.getKey().getFirst());
            pstmt.setDate(2, (java.sql.Date) constraints.getKey().getSecond());
            pstmt.setDate(1, (java.sql.Date) constraints.getEnd());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;*/
      return null;
    }

    @Override
    public Result delete(Constraints constraints) {
        if(findByKey(constraints) == null)
            return Result.FAIL;
        String sql = "DELETE FROM constraints WHERE empID = ? AND shiftTime = ? AND shiftDate = ? ";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, constraints.getEmpID());
            pstmt.setString(2, constraints.getShiftTime());
            String st = df.format(constraints.getDate());
            pstmt.setString(3, st);
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result insert(Constraints constraints) {
        if(findByKey(constraints) != null)
            return Result.FAIL;
        String sql = "INSERT INTO constraints(empID, shiftTime, shiftDate)" +
                " VALUES (?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, constraints.getEmpID());
            pstmt.setString(2, constraints.getShiftTime());
            String st = df.format(constraints.getDate());
            pstmt.setString(3, st);
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Constraints findByKey(Constraints constraints) {
       String sql = "SELECT * FROM constraints WHERE empID = ? AND shiftTime = ? AND shiftDate = ? ";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, constraints.getEmpID());
            pstmt.setString(2, constraints.getShiftTime());
            String sd = df.format(constraints.getDate());
            pstmt.setString(3, sd);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                int empID = rs.getInt("empID");
                String shiftTime  = rs.getString("shiftTime");
                String shiftString  = rs.getString("shiftDate");
                Date shiftDate = df.parse(shiftString);
                return new Constraints(empID, shiftTime, shiftDate);
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

    public LinkedList<Constraints> findByVal(Object id) {
        if (id != "") {
            String sql = "SELECT * FROM constraints WHERE empID = ?";
            LinkedList<Constraints> toRet = new LinkedList<Constraints>();
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, (Integer) id);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int empId = rs.getInt("empID");
                    String shiftTime = rs.getString("shiftTime");
                    String stringDate = rs.getString("shiftDate");
                    Date shiftDate = df.parse(stringDate);
                    toRet.add(new Constraints(empId,shiftTime,shiftDate));
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
        else{
            String sql = "SELECT * FROM constraints ";
            LinkedList<Constraints> toRet = new LinkedList<Constraints>();
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int empId = rs.getInt("empID");
                    String shiftTime = rs.getString("shiftTime");
                    String stringDate = rs.getString("shiftDate");
                    Date shiftDate = df.parse(stringDate);
                    toRet.add(new Constraints(empId,shiftTime,shiftDate));
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
    }
}

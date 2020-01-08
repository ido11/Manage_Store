package presistence.dao.employees;

import logic.employees.models.EmpRoles;
import logic.employees.models.Pair;
import presistence.dao.DAO;
import presistence.dao.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class EmpRolesDescDAO implements DAO<EmpRoles, Pair<Integer,Integer>, Integer, Integer> {

    private Connection conn = null; //Connection instance

    @Override
    public Result update(EmpRoles empRoles, Pair<Integer, Integer> key, Integer dummy1, Integer dummy2) {
        // the system cannot update employees' roles in this version
        return null;
    }

    @Override
    public Result delete(EmpRoles empRoles) {
        // the system cannot delete employees' roles in this version
        return null;
    }

    @Override
    public Result insert(EmpRoles empRoles) {
        String sql = "INSERT INTO empRoles(empID, roleID)" +
                " VALUES (?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, empRoles.getEmpId());
            pstmt.setInt(2, empRoles.getRoleId());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public EmpRoles findByKey(EmpRoles empRoles) {
        return null;
    }

    @Override
    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public LinkedList<EmpRoles> findByVal(Object id) {
        String sql = " SELECT em.empID, em.roleID, ro.description" +
                " FROM empRoles em JOIN roles ro ON em.roleID = ro.roleID"+
                " WHERE em.empID = ?";
        EmpRoles currEmp;
        LinkedList<EmpRoles> toRet = new LinkedList<>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, (int)id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                currEmp = new EmpRoles();
                currEmp.setEmpID((int)id);
                currEmp.setRoleID(rs.getInt("roleID"));
                currEmp.setRoleDescription(rs.getString("description"));
                toRet.add(currEmp);
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

    public LinkedList<Integer> findRules(int empID){
        String sql = "SELECT empRoles.roleID FROM empRoles WHERE empID = ?";
        LinkedList<Integer> toRet = new LinkedList<>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, empID);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                int roleId = rs.getInt("roleID") ;
                toRet.add(roleId);
            }
            rs.close();
            return toRet;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
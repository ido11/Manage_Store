package presistence.dao.employees;

import logic.employees.models.Employee;
import logic.employees.models.Role;
import presistence.dao.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import presistence.dao.Result;
import java.util.LinkedList;

public class RoleDAO implements DAO<Role, Integer, Integer, Integer> {

    private Connection conn = null; //Connection instance

    @Override
    public Result update(Role role, Integer key, Integer dummy1, Integer dummy2) {
        return null;
    }

    @Override
    public Result delete(Role role) {
        return null;
    }

    @Override
    public Result insert(Role role) {
        return null;
    }

    @Override
    public Role findByKey(Role role) {
        return null;
    }

    @Override
    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public LinkedList<Role> findByVal(Object emp) {
        String sql = "SELECT * FROM empRoles e JOIN roles r ON e.roleID = r.roleID WHERE empID = ?";
        LinkedList<Role> toRet = new LinkedList<Role>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, ((Employee)emp).getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int roleID = rs.getInt("roleID");
                String description = rs.getString("description");
                toRet.add(new Role(roleID, description));
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

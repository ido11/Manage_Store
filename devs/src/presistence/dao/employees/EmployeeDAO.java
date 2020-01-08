package presistence.dao.employees;

import logic.employees.models.Employee;
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

public class EmployeeDAO implements DAO<Employee, Integer, Integer, Integer> {

    final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private Connection conn = null; //Connection instance

    @Override
    public Result update(Employee emp, Integer key, Integer dummy1, Integer dummy2) {
        String sql = "UPDATE employees SET firstname = ?, lastname = ?, salary = ?, firstEmployed = ?, employmentCond =? WHERE id = ? ";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(6, key.intValue());
            pstmt.setString(1, emp.getFirstName());
            pstmt.setString(2, emp.getLastName());
            pstmt.setInt(3, emp.getSalary());
            String d = df.format(emp.getFirstEmployed());
            pstmt.setString(4, d);
            pstmt.setString(5, emp.getEmploymentCond());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result delete(Employee emp) {
        if(findByKey(emp) == null)
            return Result.FAIL;
        String sql = "DELETE FROM employees WHERE id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, emp.getKey());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result insert(Employee emp) {
        if(findByKey(emp) != null)
            return Result.FAIL;
        String sql = "INSERT INTO employees(id, firstname, lastname, salary, firstEmployed, employmentCond)" +
                " VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, emp.getKey());
            pstmt.setString(2, emp.getFirstName());
            pstmt.setString(3, emp.getLastName());
            pstmt.setInt(4, emp.getSalary());
            String d = df.format(emp.getFirstEmployed());
            pstmt.setString(5, d);
            pstmt.setString(6, emp.getEmploymentCond());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Employee findByKey(Employee emp) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, emp.getKey());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                int key = rs.getInt("id");
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                int salary = rs.getInt("salary");
                String firstEmployedStr = rs.getString("firstEmployed");
                Date firstEmployed = df.parse(firstEmployedStr);
                String employmentCond = rs.getString("employmentCond");

                return new Employee(key, firstname, lastname, salary, firstEmployed, employmentCond);
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

    public LinkedList<Employee> findByVal(Object id) {
        LinkedList<Employee> toRet = new LinkedList<>();
        Employee currEmp;
        if (!(id instanceof  Integer)) return null;
        if (((Integer)id) == 0) {
            String sql = "SELECT * FROM employees WHERE id != 0";
            Employee emp = new Employee();

            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
                while(rs.next())
                {
                    currEmp = new Employee();
                    currEmp.setId(rs.getInt("id"));
                    currEmp.setFirstName(rs.getString("firstname"));
                    currEmp.setLastName( rs.getString("lastname"));
                    currEmp.setSalary(rs.getInt("salary"));
                    String firstEmployedStr = rs.getString("firstEmployed");
                    Date firstEmployed = df.parse(firstEmployedStr);
                    currEmp.setFirstEmployed(firstEmployed);
                    currEmp.setEmploymentCond(rs.getString("employmentCond"));
                    toRet.add(currEmp);
                }
                rs.close();
                return toRet;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return null;
        }
        String sql = "SELECT * FROM employees WHERE id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, (Integer)id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                currEmp = new Employee();
                currEmp.setId((int)id);
                currEmp.setFirstName(rs.getString("firstname"));
                currEmp.setFirstName( rs.getString("lastname"));
                currEmp.setSalary(rs.getInt("salary"));
                String firstEmployedStr = rs.getString("firstEmployed");
                Date firstEmployed = df.parse(firstEmployedStr);
                currEmp.setFirstEmployed(firstEmployed);
                currEmp.setEmploymentCond(rs.getString("employmentCond"));
                toRet.add(currEmp);
            }
            rs.close();
            return toRet;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
package presistence.dao.suppliers;

import logic.suppliers.models.Company;
import presistence.dao.DAO;
import presistence.dao.Result;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class CompanyDAO implements DAO<Company, String, String, String> {

    private Connection conn = null; //Connection instance


    @Override
    public Result update(Company company, String key, String dummy1, String dummy2) {
        String sql = "UPDATE company SET companyNum = ? , supplierNum = ? , name = ? WHERE companyNum = ? AND supplierNum = ?" ;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, company.getCompanyNum());
            pstmt.setInt(2, company.getSupplierNum());
            pstmt.setString(3, company.getName());
            pstmt.setInt(4, company.getCompanyNum());
            pstmt.setInt(5, company.getSupplierNum());
            pstmt.executeUpdate();
            conn.commit();

            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result delete(Company company) {
        if(findByKey(company) == null)
            return Result.FAIL;
        String sql = "DELETE FROM company WHERE companyNum = ? AND supplierNum = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, company.getCompanyNum());
            pstmt.setInt(2, company.getSupplierNum());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Result insert(Company company) {
        if(findByKey(company) != null)
            return Result.FAIL;
        String sql = "INSERT INTO company(companyNum,supplierNum, name)" +
                " VALUES (?, ? , ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, company.getCompanyNum());
            pstmt.setInt(2, company.getSupplierNum());
            pstmt.setString(3, company.getName());
            pstmt.executeUpdate();
            conn.commit();
            return Result.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Result.FAIL;
    }

    @Override
    public Company findByKey(Company company) {
        String sql = "SELECT * FROM company WHERE companyNum = ? AND supplierNum = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, company.getCompanyNum());
            pstmt.setInt(2, company.getSupplierNum());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                return new Company(rs.getInt("companyNum"),
                        rs.getInt("supplierNum"),
                        rs.getString("name"));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Company> findByKeys(Company company) {
        String sql = "SELECT * FROM company WHERE supplierNum = ?";
        try {
            List comp = new LinkedList<Company>();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, company.getCompanyNum());
            pstmt.setInt(1, company.getSupplierNum());
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                comp.add(new Company(rs.getInt("companyNum"),
                        rs.getInt("supplierNum"),
                        rs.getString("name")));
            }
            rs.close();
            return comp;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void setConnection(Connection conn)
    {
        this.conn = conn;
    }

    @Override
    public LinkedList<Company> findByVal(Object id) {
        return null;
    }


}

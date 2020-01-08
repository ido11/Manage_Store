package presistence.dao.deliveries;

import logic.deliveries.models.Branch;
import logic.deliveries.models.Site;
import logic.deliveries.models.Supplier;
import presistence.ConnectionHandler;
import logic.deliveries.models.Site;


public class SiteDAO {

    private BranchDAO branchDAO = new BranchDAO();
    private SupplierDAO supplierDAO = new SupplierDAO();

    public Site findAddrs(String addrss){
        Site s = branchDAO.findAddrss(addrss);
        if(s==null) s = supplierDAO.findAddrss(addrss);
        return s;
    }

    public void insert(Site s) throws Exception {
        if(s instanceof Branch)
            branchDAO.insert((Branch)s);
        else if(s instanceof Supplier)
            supplierDAO.insert((Supplier)s);
        else
            throw new Exception("All sites must be branches or suppliers");
    }

    public void update(Site s) throws Exception {
        if(s instanceof Branch)
            branchDAO.update((Branch)s);
        else if(s instanceof Supplier)
            supplierDAO.update((Supplier)s);
        else
            throw new Exception("All sites must be branches or suppliers");
    }

    public void delete(Site s) throws Exception {
        if(s instanceof Branch)
            branchDAO.delete((Branch)s);
        else if(s instanceof Supplier)
            supplierDAO.delete((Supplier)s);
        else
            throw new Exception("All sites must be branches or suppliers");
    }

}

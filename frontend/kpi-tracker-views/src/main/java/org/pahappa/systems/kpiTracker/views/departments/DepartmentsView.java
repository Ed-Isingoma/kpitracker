package org.pahappa.systems.kpiTracker.views.departments;


import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.security.PermissionConstants;
import org.pahappa.systems.kpiTracker.views.dialogs.DepartmentFormDialog;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.SessionExpiredException;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.pahappa.systems.kpiTracker.security.HyperLinks.DEPARTMENTS_VIEW;

@ManagedBean(name = "departmentsView")
@ViewScoped
@ViewPath(path = DEPARTMENTS_VIEW)
public class DepartmentsView extends PaginatedTableView<Department, DepartmentsView, DepartmentsView> {

//    private DepartmentService departmentService;
    // Standard Getters and Setters
    @Setter
    @Getter
    private String searchTerm;

    @Getter
    @Setter
    @ManagedProperty("#{departmentFormDialog}")
    private DepartmentFormDialog departmentFormDialog;

    @PostConstruct
    public void init() {
//        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        super.setMaximumresultsPerpage(10);
        try {
            super.enforceSecurity(null, true, new String[]{PermissionConstants.PERM_VIEW_DEPARTMENTS});
        } catch (IOException | SessionExpiredException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) throws Exception {
        Search search = new Search().setFirstResult(offset).setMaxResults(limit);
        // You can add search logic here based on the 'searchTerm'
//        super.setDataModels(departmentService.getInstances(search, offset, limit));
    }

    @Override
    public List<org.sers.webutils.server.core.service.excel.reports.ExcelReport> getExcelReportModels() {
        return null; // Implement if Excel export is needed
    }

    @Override
    public String getFileName() {
        return null; // Implement if Excel export is needed
    }

    public void deleteDepartment(Department department) throws Exception {
        try {
//            this.departmentService.deleteInstance(department);
            super.reloadFilterReset();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Action Successful", "Department deleted successfully."));
        } catch (OperationFailedException e) {
            // Use standard JSF FacesMessage for errors
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Action Failed", e.getMessage()));
        }
    }

    @Override
    public List<Department> load(int i, int i1, Map<String, SortMeta> map, Map<String, FilterMeta> map1) {
        return Collections.emptyList();
    }
}
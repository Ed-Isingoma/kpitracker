package org.pahappa.systems.kpiTracker.views.departments;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DepartmentFormDialog;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "departmentsView")
@ViewScoped
@ViewPath(path = HyperLinks.DEPARTMENTS_VIEW)
@Getter
@Setter
public class DepartmentsView extends PaginatedTableView<Department, DepartmentsView, DepartmentsView> {

    private static final long serialVersionUID = 1L;
    private DepartmentService departmentService;

    @ManagedProperty(value = "#{departmentFormDialog}")
    private DepartmentFormDialog departmentFormDialog;

    @PostConstruct
    public void init() {
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        super.setMaximumresultsPerpage(12);
        reloadFilterReset();
    }

    public void reloadFilterReset(){
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) {
        Search search = new Search().addSortDesc("dateCreated");
        super.setTotalRecords(departmentService.countInstances(search));
        super.setDataModels(departmentService.getInstances(search, offset, limit));
    }

    public void deleteDepartment(Department department) {
        try {
            this.departmentService.deleteInstance(department);
            reloadFilterReset();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Department deleted."));
        } catch (OperationFailedException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", e.getMessage()));
        }
    }

    @Override
    public List<Department> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        reloadFromDB(first, pageSize, null);
        this.setRowCount(super.getTotalRecords());
        return super.getDataModels();
    }

    @Override
    public List<ExcelReport> getExcelReportModels() { return null; }

    @Override
    public String getFileName() { return null; }
}
package org.pahappa.systems.kpiTracker.views.departments;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DepartmentFormDialog;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
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
    private String searchTerm;

    @ManagedProperty(value = "#{departmentFormDialog}")
    private DepartmentFormDialog departmentFormDialog;

    @PostConstruct
    public void init() {
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        super.setMaximumresultsPerpage(12);
        this.reloadFilterReset();
    }

    public void reloadFilterReset() {
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) throws Exception {
        Search search = new Search();
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.setFirstResult(offset);
        search.setMaxResults(limit);
        search.addSortDesc("dateCreated");

        if (StringUtils.isNotBlank(this.searchTerm)) {
            search.addFilter(Filter.or(
                    Filter.ilike("name", "%" + this.searchTerm + "%"),
                    Filter.ilike("description", "%" + this.searchTerm + "%")
            ));
        }

        super.setTotalRecords(this.departmentService.countInstances(search));
        super.setDataModels(this.departmentService.getInstances(search, offset, limit));
    }

    public void deleteDepartment(Department department) {
        try {
            this.departmentService.deleteInstance(department);
            reloadFilterReset();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Department deleted successfully."));
        } catch (OperationFailedException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", e.getLocalizedMessage()));
        }
    }

    @Override
    public List<Department> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        try {
            reloadFromDB(first, pageSize, null);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data Error", "Failed to load data."));
            e.printStackTrace();
        }
        this.setRowCount(super.getTotalRecords());
        return super.getDataModels();
    }

    public void reloadView(){

    }

    @Override
    public List<ExcelReport> getExcelReportModels() { return null; }

    @Override
    public String getFileName() { return null; }
}
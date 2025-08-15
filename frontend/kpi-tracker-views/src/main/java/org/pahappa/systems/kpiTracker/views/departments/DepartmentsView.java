package org.pahappa.systems.kpiTracker.views.departments;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DepartmentFormDialog;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.core.utils.SystemCrashHandler;
import org.sers.webutils.server.shared.SharedAppData;
import org.springframework.web.servlet.support.RequestContext;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@ManagedBean(name = "departmentsView")
@ViewScoped // Changed to ViewScoped for correct state management with the view
@ViewPath(path = "/pages/admin/departments/departmentsView.xhtml")
@Getter
@Setter
public class DepartmentsView extends PaginatedTableView<Department, DepartmentsView, DepartmentsView> {

    private DepartmentService departmentService;
    private String searchTerm;

    // FIX: Inject the dialog bean so the view can access it.
    @ManagedProperty(value = "#{departmentFormDialog}")
    private DepartmentFormDialog departmentFormDialog;

    @PostConstruct
    public void init() {
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        super.setMaximumresultsPerpage(10); // Sets the number of rows per page
        reloadFilterReset();
    }

    @Override
    public void reloadFilterReset(){
        super.setTotalRecords(departmentService.countInstances(new Search()));
        try{
            super.reloadFilterReset();
        }catch(Exception e){
            UiUtils.ComposeFailure("Error",e.getLocalizedMessage());
        }

    }

    /**
     * This is the core method for lazy loading. It's called by the p:dataTable
     * whenever it needs to fetch data (on page load, pagination, sorting, etc.).
     */
    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) {
        // FIX: Added a try-catch block to expose any hidden exceptions during data loading.
        try {
            Search search = new Search();
            search.setFirstResult(offset);
            search.setMaxResults(limit);

            if (StringUtils.isNotBlank(this.searchTerm)) {
                search.addFilter(Filter.or(
                        Filter.ilike("name", "%" + this.searchTerm + "%"),
                        Filter.ilike("description", "%" + this.searchTerm + "%")
                ));
            }

            super.setDataModels(this.departmentService.getInstances(search, offset, limit));
            super.setTotalRecords(this.departmentService.countInstances(search));

        } catch (Exception e) {
            System.err.println("AN ERROR OCCURRED IN 'reloadFromDB':");
            e.printStackTrace(); // This will print the full error to your server console.
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading data", e.getMessage()));
        }
    }

    public void deleteDepartment(Department department) {
        try {
            this.departmentService.deleteInstance(department);
            // FIX: Use UiUtils to show messages, as the base class doesn't have these methods.
        } catch (OperationFailedException e) {
            // FIX: Use UiUtils for error messages and log the exception.
            SystemCrashHandler.reportSystemCrash(e, SharedAppData.getLoggedInUser());
        }
    }

    /**
     * This method is used by a search button to trigger a table refresh.
     */
    public void search() {
        // The lazy table will automatically call reloadFromDB when its parent form is updated.
        // This method is kept for explicit search button actions.
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return null; // Implement for Excel export if needed
    }

    @Override
    public String getFileName() {
        return null; // Implement for Excel export if needed
    }

    @Override
    public void forEach(Consumer<? super Department> action) {
        super.forEach(action);
    }

    @Override
    public List<Department> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        reloadFromDB(first, pageSize, null);
        return super.getDataModels();
    }

}
package org.pahappa.systems.kpiTracker.views.dialogs;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.primefaces.PrimeFaces;
import org.sers.webutils.client.views.presenters.DialogForm;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.List;

@ManagedBean(name = "departmentFormDialog")
@Getter
@Setter
@SessionScoped
public class DepartmentFormDialog extends DialogForm<Department> {

    private transient DepartmentService departmentService;
    private transient EmployeeUserService employeeUserService;
    private List<EmployeeUser> users;

    public DepartmentFormDialog() {
        // The first argument is the 'name' which is also the 'widgetVar'
        super("departmentFormDialogWidget", 600, 350);
    }

    @PostConstruct
    public void init() {
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.employeeUserService = ApplicationContextProvider.getBean(EmployeeUserService.class);
        this.users = this.employeeUserService.getAllInstances();
    }

    @Override
    public void persist() throws Exception {
        this.departmentService.saveInstance(super.getModel());
    }

    @Override
    public void save() {
        try {
            this.persist();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Action Successful", "Department saved successfully."));
            PrimeFaces.current().ajax().addCallbackParam("saved", true);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Action Failed", e.getLocalizedMessage()));
            PrimeFaces.current().ajax().addCallbackParam("saved", false);
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.setModel(new Department());
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
    }

    // --- SOLUTION TO PropertyNotFoundException ---
    // Explicitly override the getters and setters for the model.
    // This makes them directly available to the bean and solves the proxy issue.
    @Override
    public Department getModel() {
        return super.getModel();
    }

    @Override
    public void setModel(Department model) {
        super.setModel(model);
    }
    // --- END OF SOLUTION ---

    @Override
    public void beanInit() {}

    @Override
    public void pageLoadInit() {}
}
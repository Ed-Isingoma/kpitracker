package org.pahappa.systems.kpiTracker.views.dialogs;

import lombok.Getter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.sers.webutils.client.views.presenters.DialogForm;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.List;

@ManagedBean(name = "departmentFormDialog")
@SessionScoped
public class DepartmentFormDialog extends DialogForm<Department> {

    private transient DepartmentService departmentService;
    private transient UserService userService;
    // Getters and Setters
    @Getter
    private List<User> users;

    public DepartmentFormDialog() {
        super("Department Form", 600, 350); // Dialog title, width, height
    }

    @PostConstruct
    public void init() throws OperationFailedException {
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        this.users = this.userService.getUsers(); // Load users for the dropdown
    }

    @Override
    public void persist() throws Exception {
        this.departmentService.saveInstance(super.model);
    }

    @Override
    public void save() {
        try {
            this.persist();
            super.resetModal();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Action Successful", "Record saved successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Action Failed", e.getMessage()));
        }
    }

    @Override
    public Department getModel() {
        return super.getModel();
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Department();
    }

    @Override
    public void setFormProperties() {
        // This is called when a user clicks 'edit' on an existing department
        // No extra properties to set for now
    }

    @Override
    public void beanInit() {

    }

    @Override
    public void pageLoadInit() {

    }

}
package org.pahappa.systems.kpiTracker.views.dialogs;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.primefaces.PrimeFaces;
import org.sers.webutils.client.views.presenters.DialogForm;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
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
    private List<EmployeeUser> users; // For the dropdown

    public DepartmentFormDialog() {
        super("departmentFormDialogWidget", 600, 350);
    }

    @PostConstruct
    public void init() {
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.employeeUserService = ApplicationContextProvider.getBean(EmployeeUserService.class);
        // Load the list of users once for the dropdown
        this.users = this.employeeUserService.getAllInstances();
    }

    /**
     * This method is called by the inherited save() method.
     * It contains the core logic for saving the entity.
     */
    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        this.departmentService.saveInstance(super.getModel());
    }

    /**
     * Resets the form to its initial state for a new entry.
     */
    @Override
    public void resetModal() {
        super.resetModal();
        super.setModel(new Department());
    }

    /**
     * This is called by the framework when a model is set for editing.
     */
    @Override
    public void setFormProperties() {
        super.setFormProperties();
    }

    @Override
    public void beanInit() {

    }

    @Override
    public void pageLoadInit() {

    }

    // Explicitly override getModel to ensure proper access
    @Override
    public Department getModel() {
        return super.getModel();
    }

    // Explicitly override setModel to ensure proper access
    @Override
    public void setModel(Department model) {
        super.setModel(model);
    }

    @Override
    public void save(){
        try {
            this.persist(); // This calls the method below to do the actual saving

            // Add a success message to the growl
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Action Successful", "Department saved successfully."));

            // This parameter is used by the oncomplete attribute in the XHTML to know it can close the dialog.
            PrimeFaces.current().ajax().addCallbackParam("validationFailed", false);

        } catch (ValidationFailedException | OperationFailedException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Action Failed", e.getLocalizedMessage()));
            // This parameter tells the oncomplete in the XHTML to keep the dialog open.
            PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "A fatal error occurred", "See server logs for details."));
            e.printStackTrace();
            PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);
        }
    }
}
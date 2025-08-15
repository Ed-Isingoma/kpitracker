package org.pahappa.systems.kpiTracker.views.dialogs;

import lombok.Getter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.Team;
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

@ManagedBean(name = "teamFormDialog")
@SessionScoped
public class TeamFormDialog extends DialogForm<Team> {

    private transient TeamService teamService;
    private transient EmployeeUserService userService;
    private transient DepartmentService departmentService;

    // Getters
    @Getter
    private List<EmployeeUser> users;
    @Getter
    private List<Department> departments;

    public TeamFormDialog() {
        super("teamFormDialogWidget", 600, 400);
    }

    @PostConstruct
    public void init(){
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        this.userService = ApplicationContextProvider.getBean(EmployeeUserService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);

        this.users = this.userService.getAllInstances();
        this.departments = this.departmentService.getAllInstances();
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        this.teamService.saveInstance(super.getModel());
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Team();
    }

    @Override
    public Team getModel(){
        return super.getModel();
    }

    @Override
    public void setFormProperties() {
        // No extra properties to set for now
    }

    @Override
    public void beanInit() {

    }

    @Override
    public void pageLoadInit() {

    }
@Override
public void save() {
    try {
        this.persist(); // Calls the method above to do the actual saving

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Action Successful", "Team saved successfully."));

        PrimeFaces.current().ajax().addCallbackParam("validationFailed", false);

    } catch (ValidationFailedException | OperationFailedException e) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Action Failed", e.getLocalizedMessage()));
        PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);
    } catch (Exception e) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_FATAL, "A fatal error occurred", "See server logs for details."));
        e.printStackTrace();
        PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);
    }
}

}
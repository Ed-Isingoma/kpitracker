package org.pahappa.systems.kpiTracker.views.users;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.Team;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.core.utils.SystemCrashHandler;
import org.sers.webutils.server.shared.SharedAppData;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.*;
import java.util.logging.Logger;

@ManagedBean(name = "userFormDialog")
@Getter
@Setter
@SessionScoped
public class UserFormDialog extends DialogForm<EmployeeUser> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UserFormDialog.class.getSimpleName());

    private transient DepartmentService departmentService;
    private transient TeamService teamService;
    private transient EmployeeUserService employeeUserService;
    private transient RoleService roleService;

    private List<Gender> listOfGenders;
    private List<Role> databaseRoles;
    private List<Role> userRoles ;
    private List<Department> departments;
    private List<Team> teams;
    private boolean edit;

    @PostConstruct
    public void init() {
        this.employeeUserService = ApplicationContextProvider.getBean(EmployeeUserService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        this.roleService = ApplicationContextProvider.getBean(RoleService.class);

        this.listOfGenders = Arrays.asList(Gender.values());
        this.databaseRoles = roleService.getRoles();
        this.departments = departmentService.getAllInstances();
        this.teams = teamService.getAllInstances();
        resetModal();
    }

    public UserFormDialog() {
        super(HyperLinks.USER_FORM_DIALOG, 700, 600);
    }

    @Override
    public void persist() throws ValidationFailedException {
        // Cast to your custom user type before setting department/team if needed,
        // but since super.model is already of the correct type, this is direct.
    }

    @Override
    public void resetModal() {
        super.resetModal();
        // Instantiate your custom User class
        super.model = new EmployeeUser();
        this.userRoles = new ArrayList<>();
        this.teams = new ArrayList<>();
        setEdit(false);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null) {
            setEdit(true);
            // Correctly load the user's existing roles into the Set for the checkbox
            if (super.model.getRoles() != null) {
                this.userRoles = new ArrayList<>(super.model.getRoles());
            } else {
                this.userRoles = new ArrayList<>();
            }
        }
    }

    /**
     * Action listener for adding a new user.
     * It resets the form and then shows the dialog.
     */
    public void addUser() {
        this.setModel(new EmployeeUser());
        super.show(null);
    }

    /**
     * Action listener for editing an existing user.
     * It sets the model with the selected user's data and then shows the dialog.
     * @param user The user to be edited.
     */
    public void editUser(EmployeeUser user) {
        this.setModel(user);
        super.show(null);
    }


    @Override
    public EmployeeUser getModel(){
        return super.getModel();
    }


    @Override
    public void setModel(EmployeeUser model) {
        super.setModel(model);
    }

    /**
     * This "smart getter" is the safeguard against the validation error.
     * It ensures that if the validation phase runs before the model is updated,
     * the list of available teams is still correct.
     */
    public List<Team> getTeams() {
        // If the teams list is null or empty, and a department is selected, try to populate it.
        // This acts as a safeguard for the JSF validation phase.
        if (super.getModel() != null && super.getModel().getDepartment() != null) {
            this.teams = teamService.getInstances(new Search().addFilterEqual("department", super.getModel().getDepartment()), 0, 0);
        } else{
            // Ensure it's never null to prevent NullPointerExceptions in the view.
            this.teams = new ArrayList<>();
        }
        return this.teams;
    }

    public void onDepartmentChange() {
        if (super.model.getDepartment() != null) {
            this.teams = teamService.getInstances(new Search().addFilterEqual("department", super.model.getDepartment()), 0, 0);
        } else {
            this.teams = new ArrayList<>();
        }
        super.model.setTeam(null); // Reset team selection
    }

    @Override
    public void save() {
        try {
            super.model.setRoles(new HashSet<>(this.userRoles));
            this.employeeUserService.saveUser(super.model);
            super.hide(); // Use the inherited hide method
            UiUtils.showMessageBox("Success", "User saved successfully.");
        } catch (Exception e) {
            UiUtils.ComposeFailure("Save Failed", e.getMessage());
            SystemCrashHandler.reportSystemCrash(e, SharedAppData.getLoggedInUser());
        }
    }

}
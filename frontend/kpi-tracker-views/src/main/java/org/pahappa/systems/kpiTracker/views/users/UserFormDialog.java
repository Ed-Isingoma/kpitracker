package org.pahappa.systems.kpiTracker.views.users;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.Team;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
// Use your custom User model that has Department and Team fields
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@ManagedBean(name = "userFormDialog")
@Getter
@Setter
@SessionScoped
public class UserFormDialog extends DialogForm<EmployeeUser> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UserFormDialog.class.getSimpleName());

    private transient UserService userService;
    private transient DepartmentService departmentService;
    private transient TeamService teamService;

    private List<Gender> listOfGenders;
    private List<Role> databaseRoles;
    private Set<Role> userRoles = new HashSet<>();
    private List<Department> departments;
    private List<Team> teams;
    private boolean edit;

    @PostConstruct
    public void init() {
        this.userService = ApplicationContextProvider.getBean(EmployeeUserService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);

        this.listOfGenders = Arrays.asList(Gender.values());
        this.databaseRoles = userService.getRoles();
        this.departments = departmentService.getAllInstances();
        this.teams = teamService.getAllInstances();
    }

    public UserFormDialog() {
        super(HyperLinks.USER_FORM_DIALOG, 700, 600);
    }

    @Override
    public void persist() throws ValidationFailedException {
        // Cast to your custom user type before setting department/team if needed,
        // but since super.model is already of the correct type, this is direct.
        super.model.setRoles(userRoles);
        this.userService.saveUser(super.model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        // Instantiate your custom User class
        super.model = new EmployeeUser();
        this.userRoles = new HashSet<>();
        setEdit(false);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null) {
            setEdit(true);
            // Correctly load the user's existing roles into the Set for the checkbox
            if (super.model.getRoles() != null) {
                this.userRoles = new HashSet<>(super.model.getRoles());
            } else {
                this.userRoles = new HashSet<>();
            }
        }
    }
}
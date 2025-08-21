package org.pahappa.systems.kpiTracker.views.users;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService; // Import the specific service
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.Team;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.utils.GeneralSearchUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.model.utils.SearchField;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean(name = "usersView")
@Getter
@Setter
@ViewScoped
@Component
@ViewPath(path = HyperLinks.USERS_VIEW)
public class UsersView extends PaginatedTableView<EmployeeUser, UsersView, UsersView> {

    private static final long serialVersionUID = 1L;
    // Change the type to the specific service interface to avoid ambiguity
    private EmployeeUserService userService;
    // 1. Inject the required services
    private DepartmentService departmentService;
    private TeamService teamService;

    private RoleService roleService;
    private String searchTerm;
    private int total;
    private int noMale;
    private int noFemale;
    private int noUnknown;
    private Department selectedDepartment;
    private Team selectedTeam;
    private Role selectedRole;
    private List<Gender> genders = new ArrayList<>();
    private Gender selectedGender;
    private Date createdFrom, createdTo;
    private List<SearchField> searchFields;
    private List<Department> departments;
    private List<Team> teams;
    private List<Role> roles;

    // 1. Add a property to hold the selected user
    private EmployeeUser selectedUser;

    @ManagedProperty(value = "#{userFormDialog}")
    private UserFormDialog userFormDialog;

    @Override
    public void init() {
        super.init(); // Keep this call
    }

    @PostConstruct
    public void beanInit() {
        // Request the specific bean to resolve the ambiguity
        this.userService = ApplicationContextProvider.getBean(EmployeeUserService.class);
        this.roleService = ApplicationContextProvider.getBean(RoleService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        this.departments = departmentService.getInstances(new Search(), 0, 0);
        this.teams = teamService.getInstances(new Search(), 0, 0);
        this.roles = roleService.getRoles();
        this.genders = Arrays.asList(Gender.values());
        this.searchFields = Arrays.asList(
                new SearchField("First Name", "firstName"),
                new SearchField("Last Name", "lastName"),
                new SearchField("Username", "username"),
                new SearchField("Email Address", "emailAddress")
        );
        this.reloadFilterReset();
    }



    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) {
        Search search = createFullSearch();
        super.setTotalRecords(this.userService.countInstances(search));
        super.setDataModels(this.userService.getInstances(search, offset, limit));
    }

    /**
     * This method is called by the PrimeFaces lazy-loading DataTable.
     * It's responsible for fetching the data for the current page.
     */
    @Override
    public List<EmployeeUser> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        Search search = createFullSearch();
        super.setTotalRecords(this.userService.countInstances(search));
        return this.userService.getInstances(search, first, pageSize);
    }

    @Override
    public void reloadFilterReset() {
        // Reset search criteria and reload the first page
        Search search = createFullSearch();
        super.setTotalRecords(this.userService.countInstances(search));
        this.total = super.getTotalRecords();
        this.noMale = this.userService.countInstances(search.copy().addFilterEqual("gender", Gender.MALE));
        this.noFemale = this.userService.countInstances(search.copy().addFilterEqual("gender", Gender.FEMALE));
        this.noUnknown = this.userService.countInstances(search.copy().addFilterEqual("gender", Gender.UNKNOWN));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            Logger.getLogger(UsersView.class.getName()).log(Level.SEVERE, null, e);
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }

    public void deleteSelectedUser(User user) {
        try {
            this.userService.deleteUser(user);
            UiUtils.showMessageBox("Action successful", "User has been deactivated.");
            this.reloadFilterReset();
        } catch (OperationFailedException ex) {
            UiUtils.ComposeFailure("Action failed", ex.getLocalizedMessage());
            Logger.getLogger(UsersView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    // 3. Add the navigation method that will be called on row-click
    public void viewUserProfile() {
        if (selectedUser != null) {
            try {// Get the application's context path (e.g., "/kpi-tracker")
                String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();

                // Construct the full, correct URL for the redirect
                String url = contextPath + "/pages/users/UserProfileView.xhtml?faces-redirect=true&userId=" + selectedUser.getId();

                FacesContext.getCurrentInstance().getExternalContext().redirect(url);
            } catch (IOException e) {
                // Log the error and show a message to the user
                e.printStackTrace();
                UiUtils.ComposeFailure("Navigation Error", "Could not redirect to the user profile.");
            }
        }
    }


            @Override
    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }

    @Override
    public String getFileName() {
        return "Users_Report";
    }

        private Search createFullSearch() {
            // Start with the base search from the utility class
            Search search = GeneralSearchUtils.composeUsersSearch(this.searchFields, this.searchTerm, this.selectedGender, this.createdFrom, this.createdTo);

            // =================== FIX START ===================
            // Explicitly set the search to use the EmployeeUser class.
            // This tells the DAO to look for properties like 'department' on EmployeeUser, not the base User.
            search.setSearchClass(EmployeeUser.class);
            // =================== FIX END =====================

            // Now, add the new filters for Department, Team, and Role
            if (selectedDepartment != null) {
                search.addFilterEqual("department", selectedDepartment);
            }
            if (selectedTeam != null) {
                search.addFilterEqual("team", selectedTeam);
            }
            if (selectedRole != null) {
                search.addFilterSome("roles", Filter.equal("id", this.selectedRole.getId()));
            }
            return search;
        }

    public Department getSelectedDepartment() { return selectedDepartment; }
    public void setSelectedDepartment(Department selectedDepartment) { this.selectedDepartment = selectedDepartment; }

    public Team getSelectedTeam() { return selectedTeam; }
    public void setSelectedTeam(Team selectedTeam) { this.selectedTeam = selectedTeam; }

    public Role getSelectedRole() { return selectedRole; }
    public void setSelectedRole(Role selectedRole) { this.selectedRole = selectedRole; }

    public List<Department> getDepartments() { return departments; }
    public List<Team> getTeams() { return teams; }
    public List<Role> getRoles() { return roles; }

}
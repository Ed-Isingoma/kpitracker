package org.pahappa.systems.kpiTracker.views.users;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService; // Import the specific service
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

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean(name = "usersView")
@Getter
@Setter
@SessionScoped
@ViewPath(path = HyperLinks.USERS_VIEW)
public class UsersView extends PaginatedTableView<User, UsersView, UsersView> {

    private static final long serialVersionUID = 1L;
    // Change the type to the specific service interface to avoid ambiguity
    private EmployeeUserService userService;

    private RoleService roleService;
    private String searchTerm;
    private int total;
    private int noMale;
    private int noFemale;
    private int noUnknown;
    private List<Gender> genders = new ArrayList<>();
    private Gender selectedGender;
    private Date createdFrom, createdTo;
    private List<SearchField> searchFields;

    @PostConstruct
    public void init() {
        // Request the specific bean to resolve the ambiguity
        this.userService = ApplicationContextProvider.getBean(EmployeeUserService.class);
        this.roleService = ApplicationContextProvider.getBean(RoleService.class);
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
        Search search = GeneralSearchUtils.composeUsersSearch(this.searchFields, this.searchTerm, this.selectedGender, this.createdFrom, this.createdTo);
        super.setTotalRecords(this.userService.countUsers(search));
        super.setDataModels(this.userService.getUsers(search, offset, limit));
    }

    /**
     * This method is called by the PrimeFaces lazy-loading DataTable.
     * It's responsible for fetching the data for the current page.
     */
    @Override
    public List<User> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        Search search = GeneralSearchUtils.composeUsersSearch(this.searchFields, this.searchTerm, this.selectedGender, this.createdFrom, this.createdTo);
        super.setTotalRecords(this.userService.countUsers(search));
        return this.userService.getUsers(search, first, pageSize);
    }

    @Override
    public void reloadFilterReset() {
        // Reset search criteria and reload the first page
        Search search = GeneralSearchUtils.composeUsersSearch(this.searchFields, this.searchTerm, this.selectedGender, this.createdFrom, this.createdTo);
        super.setTotalRecords(this.userService.countUsers(search));
        this.total = super.getTotalRecords();
        this.noMale = this.userService.countUsers(search.copy().addFilterEqual("gender", Gender.MALE));
        this.noFemale = this.userService.countUsers(search.copy().addFilterEqual("gender", Gender.FEMALE));
        this.noUnknown = this.userService.countUsers(search.copy().addFilterEqual("gender", Gender.UNKNOWN));
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

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }

    @Override
    public String getFileName() {
        return "Users_Report";
    }
}
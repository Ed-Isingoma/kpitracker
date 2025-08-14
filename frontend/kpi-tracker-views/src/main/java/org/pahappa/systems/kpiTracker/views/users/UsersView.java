package org.pahappa.systems.kpiTracker.views.users;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.models.security.PermissionConstants;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.SessionExpiredException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "usersView")
@ViewScoped
@ViewPath(path = HyperLinks.USERS_VIEW)
@Getter
@Setter
public class UsersView extends PaginatedTableView<User, UsersView, UsersView> {

    private static final long serialVersionUID = 1L;
    private transient UserService userService;
    private String searchTerm;
    private Search search;

    // Dashboard card values
    private int total;
    private int noMale;
    private int noFemale;
    private int noUnknown;

    private List<Gender> genders;
    private Gender selectedGender;
    private Date createdFrom, createdTo;

    @ManagedProperty("#{userFormDialog}")
    private UserFormDialog userFormDialog;

    @PostConstruct
    public void init() {
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        this.genders = List.of(Gender.values());
        super.setMaximumresultsPerpage(10);
        reloadFilterReset();
        try {
            super.enforceSecurity(null, true, new String[]{PermissionConstants.PERM_VIEW_USERS});
        } catch (IOException | SessionExpiredException e) {
            e.printStackTrace();
        }
    }

    private Search composeSearch() {
        Search search = new Search();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            search.addFilterOr(
                    new com.googlecode.genericdao.search.Filter("username", "%" + searchTerm + "%", com.googlecode.genericdao.search.Filter.OP_ILIKE),
                    new com.googlecode.genericdao.search.Filter("firstName", "%" + searchTerm + "%", com.googlecode.genericdao.search.Filter.OP_ILIKE),
                    new com.googlecode.genericdao.search.Filter("lastName", "%" + searchTerm + "%", com.googlecode.genericdao.search.Filter.OP_ILIKE),
                    new com.googlecode.genericdao.search.Filter("emailAddress", "%" + searchTerm + "%", com.googlecode.genericdao.search.Filter.OP_ILIKE)
            );
        }
        if (selectedGender != null) {
            search.addFilterEqual("gender", selectedGender);
        }
        if (createdFrom != null) {
            search.addFilterGreaterOrEqual("dateCreated", createdFrom);
        }
        if (createdTo != null) {
            search.addFilterLessOrEqual("dateCreated", createdTo);
        }
        return search;
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) {
        this.search = composeSearch();
        this.search.setFirstResult(offset).setMaxResults(limit);
        super.setDataModels(userService.getUsers(this.search, offset, limit));
    }

    public void reloadFilterReset() {
        try {
            this.search = composeSearch();
            int totalRecords = userService.countUsers(this.search);
            super.setTotalRecords(totalRecords);

            // Dashboard counts
            this.total = totalRecords;
            this.noMale = userService.countUsers(this.search.copy().addFilterEqual("gender", Gender.MALE));
            this.noFemale = userService.countUsers(this.search.copy().addFilterEqual("gender", Gender.FEMALE));
            this.noUnknown = userService.countUsers(this.search.copy().addFilterEqual("gender", Gender.UNKNOWN));

            super.reloadFilterReset();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to reload data. " + e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return null;
    }

    @Override
    public String getFileName() {
        return null;
    }

    public void deleteUser(User user) {
        try {
            this.userService.deleteUser(user);
            reloadFilterReset();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Action Successful", "User deleted successfully."));
        } catch (OperationFailedException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Action Failed", e.getMessage()));
        }
    }

    /**
     * ✅ Always prepares the dialog for adding a new user.
     */
    public void prepareNewUser() {
        userFormDialog.resetModal(); // Fresh model
        userFormDialog.setEditMode(false);
    }

    /**
     * ✅ Prepares the dialog for editing an existing user.
     */
    public void prepareEditUser(User user) {
        userFormDialog.setModel(user); // Load existing model
        userFormDialog.setFormProperties(); // Load roles & edit flag
    }

    @Override
    public List<User> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        reloadFromDB(first, pageSize, null);
        this.setRowCount(super.getTotalRecords());
        return super.getDataModels();
    }
}

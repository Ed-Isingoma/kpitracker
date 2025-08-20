package org.pahappa.systems.kpiTracker.views.users;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.primefaces.event.FileUploadEvent;
import org.sers.webutils.client.views.presenters.WebFormView;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.core.utils.SystemCrashHandler;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "profileView")
@SessionScoped
@Getter
@Setter
public class ProfileView extends WebFormView<EmployeeUser, ProfileView, ProfileView> {

    private transient EmployeeUserService employeeUserService;
    private String confirmPassword;

    @PostConstruct
    public void beanInit() {
        // FIX: Request the specific bean 'EmployeeUserService.class' to resolve the ambiguity.
        this.employeeUserService = ApplicationContextProvider.getBean(EmployeeUserService.class);
                // 1. Get the generic User object from the session.
                User loggedInUser = SharedAppData.getLoggedInUser();

        if (loggedInUser != null) {
            // 2. Use its ID to fetch the full, correctly-typed EmployeeUser from the database.
            // This avoids the ClassCastException.
            super.model = this.employeeUserService.getInstanceByID(loggedInUser.getId());
        }
        // --- FIX END ---
    }

    @Override
    public void persist() throws ValidationFailedException {
        // The saveUser method is overridden with more logic
    }

    public void saveUser() {
        try {
            if (this.model.hasNewPassword() && !this.model.getClearTextPassword().equals(this.confirmPassword)) {
                throw new ValidationFailedException("Passwords do not match.");
            }
            this.employeeUserService.saveUser(this.model);
            UiUtils.showMessageBox("Success", "Profile updated successfully.");
        } catch (Exception e) {
            SystemCrashHandler.reportSystemCrash(e, SharedAppData.getLoggedInUser());
            UiUtils.ComposeFailure("Action Failed", e.getMessage());
        }
    }

    public void fileUploadEvent(FileUploadEvent event) {
        // Handle file upload logic here if needed
        UiUtils.showMessageBox("Info", "File Uploaded: " + event.getFile().getFileName());
    }

    @Override
    public void pageLoadInit() {
        // Called after the model is set.
    }

    @Override
    public String getViewUrl() {
        return "/pages/users/ProfileView.xhtml?faces-redirect=true";
    }
}
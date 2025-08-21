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
@Getter
@Setter
@ViewScoped
public class ProfileView extends WebFormView<EmployeeUser, ProfileView, ProfileView> {

    private transient EmployeeUserService employeeUserService;
    private String confirmPassword;
    private EmployeeUser employeeUser;

    // ADD THIS CONSTRUCTOR
    public ProfileView() {
        System.out.println("DEBUG: ProfileView constructor called.");
    }


    @PostConstruct
    public void beanInit() {
        // This method now correctly loads the user data every time the page is viewed.
        this.employeeUserService = ApplicationContextProvider.getBean(EmployeeUserService.class);
        User loggedInUser = SharedAppData.getLoggedInUser();

        if (loggedInUser != null) {
            // FIX: Fetch the full EmployeeUser from the database using the ID from the session user.
            // This avoids ClassCastExceptions and ensures the 'model' property is set correctly.
            super.model = this.employeeUserService.getInstanceByID(loggedInUser.getId());
        }
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
    public EmployeeUser getModel(){
        return super.getModel();
    }

    @Override
    public String getViewUrl() {
        return "/pages/users/ProfileView.xhtml?faces-redirect=true";
    }
}
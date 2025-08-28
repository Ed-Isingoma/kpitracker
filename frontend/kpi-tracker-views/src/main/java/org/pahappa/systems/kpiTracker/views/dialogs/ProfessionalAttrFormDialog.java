package org.pahappa.systems.kpiTracker.views.dialogs;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.ProfessionalAttrCategoryService;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttrCategory;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "professionalAttrFormDialog")
@SessionScoped
@Getter
@Setter
public class ProfessionalAttrFormDialog implements Serializable {

    private static final long serialVersionUID = 1L;
    private ProfessionalAttrCategory model;
    private ProfessionalAttrCategoryService professionalAttrCategoryService;
    private String updateTarget;
    private List<User> allUsers;
    private UserService userService;

    @PostConstruct
    public void init() throws OperationFailedException {
        this.professionalAttrCategoryService = ApplicationContextProvider.getBean(ProfessionalAttrCategoryService.class);
        this.model = new ProfessionalAttrCategory();
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        this.allUsers = this.userService.getUsers();
    }

    /**
     * Prepares the dialog for creating a new attribute for a given cycle.
     * This is called by the "Add" button.
     * @param goalCycle The cycle to which the new attribute will belong.
     */
    public void prepareNewProfessionalAttr(GoalCycle goalCycle) {
        this.model = new ProfessionalAttrCategory();
        this.model.setGoalCycle(goalCycle);
    }

    /**
     * Saves the current attribute (both new and edited instances).
     * This is called by the "Save" button in the dialog.
     */
    public void save() {
        try {
            if (this.model.getTitle() == null || this.model.getTitle().trim().isEmpty()) {
                throw new OperationFailedException("Attribute Title is required.");
            }
            if (this.model.getContributionWeight() <= 0 || this.model.getContributionWeight() > 100) {
                throw new OperationFailedException("Contribution Weight must be between 1 and 100.");
            }

            professionalAttrCategoryService.saveInstance(this.model);

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Professional Attribute saved successfully.");
            FacesContext.getCurrentInstance().addMessage(null, message);

        } catch (Exception e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
}

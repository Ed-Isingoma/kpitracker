package org.pahappa.systems.kpiTracker.views.dialogs;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.core.services.KpiService;
import org.pahappa.systems.kpiTracker.models.Goal;
import org.pahappa.systems.kpiTracker.models.KPI;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.service.impl.UserServiceImpl;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "kpiFormDialog")
@ViewScoped
@Getter
@Setter
public class KpiFormDialog implements Serializable {

    private static final long serialVersionUID = 1L;
    private KPI model;
    private String updateTarget;

    private KpiService kpiService;
    private GoalService goalService;
    private UserService userService;

    private List<Goal> allGoals;
    private List<User> allUsers;

    @PostConstruct
    public void init() {
        this.kpiService = ApplicationContextProvider.getBean(KpiService.class);
        this.goalService = ApplicationContextProvider.getBean(GoalService.class);

        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        this.userService = context.getBean("userService", UserService.class);

        this.model = new KPI();
        this.allGoals = this.goalService.getAllInstances();

        try {
            this.allUsers = this.userService.getUsers();
        } catch (OperationFailedException e) {
            System.out.println("OPERATION-FAILED-EXCEPTION: " + e.getMessage());
        }
    }

    /**
     * Resets the form for creating a new KPI.
     */
    public void newKPI() {
        this.model = new KPI();
    }

    /**
     * Saves the current KPI.
     */
    public void save() {
        try {
            if (this.model.getTitle() == null || this.model.getTitle().trim().isEmpty()) {
                throw new RuntimeException("A Title is required for the KPI.");
            }
            if (this.model.getGoal() == null) {
                throw new RuntimeException("A parent Goal must be selected.");
            }
            if (this.model.getOwner() == null) {
                throw new RuntimeException("An Owner must be selected.");
            }

            kpiService.saveInstance(this.model);

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "KPI saved successfully.");
            FacesContext.getCurrentInstance().addMessage(null, message);

        } catch (Exception e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
}
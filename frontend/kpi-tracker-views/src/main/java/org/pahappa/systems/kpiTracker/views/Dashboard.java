package org.pahappa.systems.kpiTracker.views;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.core.services.GoalCycleService;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.security.HyperLinks;

import org.primefaces.model.chart.PieChartModel;

import org.sers.webutils.client.controllers.WebAppExceptionHandler;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.security.User;
import org.sers.webutils.model.utils.SortField;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@ManagedBean(name = "dashboard")
@ViewScoped
@ViewPath(path = HyperLinks.DASHBOARD)
public class Dashboard extends WebAppExceptionHandler implements Serializable {

    private static final long serialVersionUID = 1L;
    private User loggedinUser;

    Search search = new Search();
    private String searchTerm;
    private SortField selectedSortField;

    private int totalEmployees;
    private int totalDepartments;
    private int cycleCompletionPercentage;
    private int totalCycles;

    private EmployeeUserService employeeUserService;
    private DepartmentService departmentService;
    private GoalCycleService goalCycleService;

    private PieChartModel pieModel;
    private int businessGoals = 70;
    private int professionalAttributes = 30;

    @PostConstruct
    public void init() {
        loggedinUser = SharedAppData.getLoggedInUser();
        employeeUserService = ApplicationContextProvider.getBean(EmployeeUserService.class);
        departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        goalCycleService = ApplicationContextProvider.getBean(GoalCycleService.class);
        loadDepartmentSummaryData();
        loadCurrentCycleProgress();
        createPieModel();
    }

    private void createPieModel() {
        pieModel = new PieChartModel();
        pieModel.set("Business Goals", this.businessGoals);
        pieModel.set("Professional Attributes", this.professionalAttributes);
        pieModel.setShowDataLabels(true);
        pieModel.setDiameter(120);
        pieModel.setSeriesColors("007bff, 28a745"); // Blue, Green (hex colors without #)

    }

    public String getViewPath() {
        return Dashboard.class.getAnnotation(ViewPath.class).path();
    }

    private void loadDepartmentSummaryData() {
        this.totalEmployees = employeeUserService.countInstances(search);
        this.totalDepartments = departmentService.countInstances(search);
        this.totalCycles = goalCycleService.countInstances(search);
    }

    private void loadCurrentCycleProgress() {
        GoalCycle currentCycle = goalCycleService.findCurrentCycle();

        if (currentCycle == null) {
            this.cycleCompletionPercentage = 0;
            return;
        }

        Date startDate = currentCycle.getStartDate();
        Date endDate = currentCycle.getEndDate();
        Date today = new Date();

        long totalDuration = endDate.getTime() - startDate.getTime();
        if (totalDuration <= 0) {
            this.cycleCompletionPercentage = 100;
            return;
        }

        long elapsedDuration = today.getTime() - startDate.getTime();

        double percentage = ((double) elapsedDuration / totalDuration) * 100.0;
        this.cycleCompletionPercentage = (int) Math.round(percentage);
    }

}

package org.pahappa.systems.kpiTracker.views.departments;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.core.services.GoalCycleService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.models.Team;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.MeterGaugeChartModel;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.client.views.presenters.WebFormView;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "departmentDetailsView")
@ViewScoped
@Getter @Setter
@ViewPath(path = HyperLinks.DEPARTMENT_DETAILS_VIEW)
public class DepartmentDetailsView extends WebFormView<Department, DepartmentDetailsView, DepartmentsView> {

    private static final long serialVersionUID = 1L;
    private transient DepartmentService departmentService;
    private transient GoalCycleService goalCycleService;
    private transient EmployeeUserService employeeUserService;
    private transient TeamService teamService;

    private String departmentId;
    private Department department;
    private List<GoalCycle> goalCycles;
    private GoalCycle selectedCycle;

    private List<EmployeeUser> departmentMembers;
    private List<Team> departmentTeams;

    private DonutChartModel kpiPerformanceModel;
    private MeterGaugeChartModel professionalRatingsModel;

    @Override
    @PostConstruct
    public void beanInit() {
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.goalCycleService = ApplicationContextProvider.getBean(GoalCycleService.class);
        this.employeeUserService = ApplicationContextProvider.getBean(EmployeeUserService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        this.goalCycles = goalCycleService.getAllInstances();
    }

    @Override
    public void pageLoadInit() {
        if (departmentId != null) {
            this.department = departmentService.getInstanceByID(departmentId);
            if (this.department != null) {
                // Fetch the real data for the tabs
                this.departmentMembers = employeeUserService.getEmployeesInDepartment(this.department);
                this.departmentTeams = teamService.getTeamsInDepartment(this.department);
                createDummyCharts();
            }
        }
    }

    private void createDummyCharts() {
        // --- CORRECTED KPI Performance Donut Chart (Using OLD Chart Model) ---
        kpiPerformanceModel = new DonutChartModel();
        Map<String, Number> circle = new LinkedHashMap<>();
        circle.put("Achieved", 72);
        circle.put("Remaining", 28); // 100 - 72

        // The addCircle method takes a Map
        kpiPerformanceModel.addCircle(circle);

        kpiPerformanceModel.setSeriesColors("4CAF50,E0E0E0"); // Green for achieved, Grey for remaining
        kpiPerformanceModel.setShowDataLabels(false);
        kpiPerformanceModel.setLegendPosition("e"); // Position east (right), then we hide it with extender
        kpiPerformanceModel.setExtender("kpiPerformanceExtender");


        // --- Professional Attributes Meter Gauge Chart (Using OLD Chart Model) ---
        List<Number> intervals = new ArrayList<>();
        intervals.add(40);
        intervals.add(70);
        intervals.add(100);

        professionalRatingsModel = new MeterGaugeChartModel(65, intervals);
        professionalRatingsModel.setSeriesColors("F44336,FFC107,4CAF50"); // Red, Yellow, Green
        professionalRatingsModel.setGaugeLabel("Avg. Rating");
        professionalRatingsModel.setGaugeLabelPosition("bottom");
        professionalRatingsModel.setShowTickLabels(false);
    }

    // --- Unused but required abstract methods ---
    @Override
    public void persist() throws Exception {}
    @Override
    public void resetModal() {}
    @Override
    public void setFormProperties() {}
    @Override
    public String getViewUrl() {
        return HyperLinks.DEPARTMENTS_VIEW;
    }
}
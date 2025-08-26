package org.pahappa.systems.kpiTracker.views.teams;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
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

@ManagedBean(name = "teamDetailsView")
@ViewScoped
@Getter @Setter
@ViewPath(path = HyperLinks.TEAM_DETAILS_VIEW)
public class TeamDetailsView extends WebFormView<Team, TeamDetailsView, TeamView> {

    private static final long serialVersionUID = 1L;
    private transient TeamService teamService;
    private transient EmployeeUserService employeeUserService;

    private String teamId;
    private Team team;
    private List<EmployeeUser> teamMembers;

    private DonutChartModel kpiPerformanceModel;
    private MeterGaugeChartModel professionalRatingsModel;

    @Override
    @PostConstruct
    public void beanInit() {
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        this.employeeUserService = ApplicationContextProvider.getBean(EmployeeUserService.class);

        this.kpiPerformanceModel = new DonutChartModel();
        this.professionalRatingsModel = new MeterGaugeChartModel();
    }

    @Override
    public void pageLoadInit() {
        if (teamId != null) {
            this.team = teamService.getInstanceByID(teamId);
            if (this.team != null) {
                this.teamMembers = employeeUserService.getEmployeesInTeam(this.team);
                createDummyCharts();
            }
        }
    }

    private void createDummyCharts() {
        // KPI Performance Chart
        Map<String, Number> circle = new LinkedHashMap<>();
        circle.put("Achieved", 85);
        circle.put("Remaining", 15);
        kpiPerformanceModel.addCircle(circle);
        kpiPerformanceModel.setSeriesColors("4CAF50,E0E0E0");
        kpiPerformanceModel.setShowDataLabels(false);
        kpiPerformanceModel.setExtender("kpiPerformanceExtender");

        // Professional Attributes Chart
        List<Number> intervals = new ArrayList<>();
        intervals.add(40);
        intervals.add(70);
        intervals.add(100);
        professionalRatingsModel = new MeterGaugeChartModel(78, intervals);
        professionalRatingsModel.setSeriesColors("F44336,FFC107,4CAF50");
        professionalRatingsModel.setGaugeLabel("Avg. Rating");
        professionalRatingsModel.setGaugeLabelPosition("bottom");
        professionalRatingsModel.setShowTickLabels(false);
    }

    @Override
    public void persist() throws Exception {}
    @Override
    public void resetModal() {}
    @Override
    public void setFormProperties() {}

    @Override
    public String getViewUrl() {
        return HyperLinks.TEAMS_VIEW;
    }
}
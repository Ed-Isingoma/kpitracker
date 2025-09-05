package org.pahappa.systems.kpiTracker.views.kpis;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GoalCycleService;
import org.pahappa.systems.kpiTracker.core.services.KpiService;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.models.KPI;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.KpiFormDialog;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "kpiView")
@ViewScoped
@Getter
@Setter
@ViewPath(path = HyperLinks.KPI_VIEW)
public class KpiView implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient KpiService kpiService;
    private transient GoalCycleService goalCycleService;

    private List<GoalCycle> allGoalCycles;
    private GoalCycle selectedGoalCycle;
    private List<KPI> kpiList;

    private KpiFormDialog kpiFormDialog;

    @PostConstruct
    public void init() {
        this.kpiService = ApplicationContextProvider.getBean(KpiService.class);
        this.goalCycleService = ApplicationContextProvider.getBean(GoalCycleService.class);
        this.kpiFormDialog = new KpiFormDialog();
        this.allGoalCycles = this.goalCycleService.getAllInstances();
        this.selectedGoalCycle = this.goalCycleService.findCurrentCycle();

        loadKpisForSelectedCycle();
    }

    /**
     * Helper method to fetch KPIs based on the currently selectedGoalCycle.
     */
    public void loadKpisForSelectedCycle() {
        this.kpiList = this.kpiService.getKpisForCycle(this.selectedGoalCycle);
    }

    /**
     * Prepares the dialog for creating a new KPI.
     */
    public void prepareNewKpi() {
        kpiFormDialog.newKPI();
        kpiFormDialog.setUpdateTarget(":kpiForm:kpiTable");
    }

}
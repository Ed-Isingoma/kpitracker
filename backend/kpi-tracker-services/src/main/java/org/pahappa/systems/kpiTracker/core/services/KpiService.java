package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.core.services.base.GenericService;
import org.pahappa.systems.kpiTracker.models.Goal;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.models.KPI;

import java.util.List;

public interface KpiService extends GenericService<KPI> {
    List<KPI> getKpisForCycle(GoalCycle goalCycle);
    List<KPI> getKpisForCycleAndGoal(GoalCycle goalCycle, Goal goal);

}

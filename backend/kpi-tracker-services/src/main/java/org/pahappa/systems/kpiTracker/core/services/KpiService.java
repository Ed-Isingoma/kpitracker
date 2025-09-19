package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.core.services.base.GenericService;
import org.pahappa.systems.kpiTracker.models.*;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;

import java.util.List;

public interface KpiService extends GenericService<KPI> {
    List<KPI> getKpisForCycle(GoalCycle goalCycle);

    List<KPI> getKpisForCycleAndGoal(GoalCycle goalCycle, Goal goal);

    double calculateOverallWeightedAchievement(List<KPI> kpis);
}

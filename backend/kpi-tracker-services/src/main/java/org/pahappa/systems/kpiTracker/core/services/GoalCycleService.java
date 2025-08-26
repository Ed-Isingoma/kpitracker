package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.core.services.base.GenericService;
import org.pahappa.systems.kpiTracker.models.GoalCycle;

public interface GoalCycleService extends GenericService<GoalCycle> {

    GoalCycle findCurrentCycle();
}
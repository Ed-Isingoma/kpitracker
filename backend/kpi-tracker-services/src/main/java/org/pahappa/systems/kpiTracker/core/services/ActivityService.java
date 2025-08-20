package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.core.services.base.GenericService;
import org.pahappa.systems.kpiTracker.models.Activity;
import org.pahappa.systems.kpiTracker.models.Goal;

import java.util.List;

public interface ActivityService extends GenericService<Activity> {
    List<Activity> getActivitiesForGoal(Goal selectedGoal);
}
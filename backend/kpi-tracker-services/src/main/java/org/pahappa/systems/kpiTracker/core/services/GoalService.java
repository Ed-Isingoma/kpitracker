package org.pahappa.systems.kpiTracker.core.services;
import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.core.services.base.GenericService;
import org.pahappa.systems.kpiTracker.models.Goal;
import org.pahappa.systems.kpiTracker.models.GoalCycle;

import java.util.List;

public interface GoalService extends GenericService<Goal> {
    int countGoals(Search search);
    List<Goal> getGoals(Search search, int offset, int limit);
    List<Goal> getGoalsByLevel(GoalLevel level);
    List<Goal> getChildGoals(Goal parentGoal);

    // NEW: Method to get organisational goals for a specific cycle
    List<Goal> getOrganisationalGoalsForCycle(GoalCycle cycle);
}

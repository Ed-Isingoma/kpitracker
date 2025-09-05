package org.pahappa.systems.kpiTracker.core.services;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.core.services.base.GenericService;
import org.pahappa.systems.kpiTracker.models.Goal;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.sers.webutils.model.security.User;

import java.util.List;

public interface GoalService extends GenericService<Goal> {
    List<Goal> getGoalsByLevel(GoalLevel level);
    List<Goal> getChildGoals(Goal parentGoal);
    List<Goal> getOrganisationalGoalsForCycle(GoalCycle cycle);
    SearchResult<Goal> searchAndCountRelevantGoals(EmployeeUser employeeUser, GoalCycle cycle, int first, int pageSize);

}

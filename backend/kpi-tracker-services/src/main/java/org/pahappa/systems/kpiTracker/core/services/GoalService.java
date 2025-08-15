package org.pahappa.systems.kpiTracker.core.services;
import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.base.GenericService;
import org.pahappa.systems.kpiTracker.models.Goal;

import java.util.List;

public interface GoalService extends GenericService<Goal> {
    int countGoals(Search search);
    List<Goal> getGoals(Search search, int offset, int limit);
}

package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.KpiService;
import org.pahappa.systems.kpiTracker.core.services.impl.base.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.Goal;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.models.KPI;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class KpiServiceImpl extends GenericServiceImpl<KPI> implements KpiService {

    public List<KPI> getKpisForCycle(GoalCycle goalCycle) {
        if (goalCycle == null) {
            return Collections.emptyList();
        }
        Search search = new Search(KPI.class);
        search.addFilterEqual("goal.goalCycle", goalCycle);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        return super.search(search);
    }

    public boolean isDeletable(KPI instance) throws OperationFailedException {
        return true;
    }

    public KPI saveInstance(KPI kpi) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(kpi, "KPI cannot be null");
        return super.save(kpi);
    }

    public List<KPI> getKpisForCycleAndGoal(GoalCycle goalCycle, Goal goal) {
        if (goalCycle == null || goal == null) {
            return Collections.emptyList();
        }
        Search search = new Search(KPI.class);
        search.addFilterEqual("goal", goal);
        search.addFilterEqual("goal.goalCycle", goalCycle);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        return super.search(search);
    }
}

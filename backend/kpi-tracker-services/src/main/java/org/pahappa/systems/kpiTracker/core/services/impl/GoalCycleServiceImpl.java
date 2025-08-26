package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.Search;
import org.hibernate.Hibernate;
import org.pahappa.systems.kpiTracker.core.services.GoalCycleService;
import org.pahappa.systems.kpiTracker.core.services.impl.base.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service("goalCycleService")
@Transactional
public class GoalCycleServiceImpl extends GenericServiceImpl<GoalCycle> implements GoalCycleService {

    @Override
    public boolean isDeletable(GoalCycle instance) throws OperationFailedException {
        // Add logic here if a cycle cannot be deleted, e.g., if it's active.
        return true;
    }

    public GoalCycle findCurrentCycle() {
        Search search = new Search(GoalCycle.class);
        Date today = new Date();

        search.addFilterLessOrEqual("startDate", today);
        search.addFilterGreaterOrEqual("endDate", today);

//         search.addFilterEqual("status", GoalStatus.ACTIVE);

        search.setMaxResults(1);

        return (GoalCycle) this.searchUnique(search);
    }

    @Override
    public GoalCycle saveInstance(GoalCycle goalCycle) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(goalCycle, "Goal Cycle details cannot be null");
        Validate.hasText(goalCycle.getTitle(), "Goal Cycle title is required");
        Validate.notNull(goalCycle.getStartDate(), "Start date is required");
        Validate.notNull(goalCycle.getEndDate(), "End date is required");

        if(goalCycle.getEndDate().before(goalCycle.getStartDate())){
            throw new ValidationFailedException("End date cannot be before the start date.");
        }

        return super.save(goalCycle);
    }

}
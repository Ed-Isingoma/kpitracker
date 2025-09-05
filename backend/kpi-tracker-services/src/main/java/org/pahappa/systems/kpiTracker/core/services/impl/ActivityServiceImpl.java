package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.impl.base.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.Activity;
import org.pahappa.systems.kpiTracker.models.Goal;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service("activityService")
@Transactional
public class ActivityServiceImpl extends GenericServiceImpl<Activity> implements ActivityService {

    @Override
    public boolean isDeletable(Activity instance) throws OperationFailedException {
        return true; // Activities can always be deleted for now
    }

    @Override
    public Activity saveInstance(Activity activity) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(activity, "Activity details cannot be null");
        Validate.hasText(activity.getTitle(), "Activity title is required");
        Validate.notNull(activity.getGoal(), "Activity must be linked to a goal");
        Validate.notNull(activity.getAssignedUser(), "Activity must be assigned to a user");
        Validate.notNull(activity.getStartDate(), "Start date is required");
        Validate.notNull(activity.getEndDate(), "End date is required");

        return super.save(activity);
    }

}
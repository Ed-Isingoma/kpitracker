package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.ProfessionalAttrCategoryService;
import org.pahappa.systems.kpiTracker.core.services.impl.base.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttrCategory;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProfessionalAttrCategoryServiceImpl extends GenericServiceImpl<ProfessionalAttrCategory> implements ProfessionalAttrCategoryService {

    public List<ProfessionalAttrCategory> getAttributesForCycle(GoalCycle goalCycle) {
        Search search = new Search();
        search.addFilterEqual("goalCycle", goalCycle);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        return super.search(search);
    }

    public boolean isDeletable(ProfessionalAttrCategory professionalAttrCategory) throws OperationFailedException {
        return true;
    }

    public ProfessionalAttrCategory saveInstance(ProfessionalAttrCategory professionalAttrCategory) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(professionalAttrCategory, "Category cannot be null");
//        Validate.hasText(activity.getTitle(), "Activity title is required");
//        Validate.notNull(activity.getGoal(), "Activity must be linked to a goal");
//        Validate.notNull(activity.getAssignedUser(), "Activity must be assigned to a user");
//        Validate.notNull(activity.getStartDate(), "Start date is required");
//        Validate.notNull(activity.getEndDate(), "End date is required");

        return super.save(professionalAttrCategory);
    }
}

package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.core.services.impl.base.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.Goal;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service // Ensure the bean name matches what might be looked up
@Transactional
public class GoalServiceImpl extends GenericServiceImpl<Goal> implements GoalService {

    @Override
    public boolean isDeletable(Goal goal) throws OperationFailedException {
        // You can add logic here later, e.g., cannot delete a completed goal.
        return true;
    }

    @Override
    public Goal saveInstance(Goal entityInstance) throws ValidationFailedException, OperationFailedException {
        // Implement the save logic by calling the parent's save method
        if (entityInstance == null) {
            throw new ValidationFailedException("Goal instance cannot be null");
        }
        // Add any validation logic here before saving
        return super.save(entityInstance);
    }

    /**
     * Correctly implements the count method by delegating to the generic parent.
     */
    @Override
    public int countGoals(Search search) {
        return super.count(search);
    }

    /**
     * Correctly implements the get method by delegating to the generic parent.
     */
    @Override
    public List<Goal> getGoals(Search search, int offset, int limit) {
        // The parent's search method handles setting the offset and limit
        return super.search(search.setFirstResult(offset).setMaxResults(limit));
    }
}
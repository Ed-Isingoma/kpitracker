package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.core.services.impl.base.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.*;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class GoalServiceImpl extends GenericServiceImpl<Goal> implements GoalService {

    @Override
    public boolean isDeletable(Goal goal) throws OperationFailedException {
        // You can add logic here later, e.g., cannot delete a completed goal.
        return true;
    }

    @Override
    public SearchResult<Goal> searchAndCountRelevantGoals(EmployeeUser employeeUser, GoalCycle cycle, int first, int pageSize) {
        if (employeeUser == null || cycle == null) {
            return new SearchResult<>();
        }

        Search search = new Search(Goal.class);
        search.addFilterEqual("goalCycle", cycle);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.setDistinct(true);

        List<Filter> relevantFilters = new ArrayList<>();
        relevantFilters.add(Filter.equal("goalLevel", GoalLevel.ORGANISATIONAL));
        if (employeeUser.getDepartment() != null) {
            relevantFilters.add(Filter.equal("department", employeeUser.getDepartment()));
        }
        if (employeeUser.getTeam() != null) {
            relevantFilters.add(Filter.equal("team", employeeUser.getTeam()));
        }
        search.addFilter(Filter.or(relevantFilters.toArray(new Filter[0])));

        search.setFirstResult(first);
        search.setMaxResults(pageSize);

        return super.searchAndCount(search);
    }

    @Override
    public Goal saveInstance(Goal goal) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(goal, "Goal details cannot be null");
        Validate.hasText(goal.getTitle(), "Goal title is required");
        Validate.notNull(goal.getGoalLevel(), "Goal level is required");
        Validate.isTrue(goal.getWeight() != null && goal.getWeight() > 0, "Goal weight must be greater than 0.");
        if (goal.getGoalLevel() == GoalLevel.ORGANISATIONAL) {
            Validate.notNull(goal.getGoalCycle(), "Organisational goal must be linked to a cycle.");
            Validate.isTrue(goal.getWeight() != null && goal.getWeight() > 0, "Organisational goal weight must be greater than 0.");

            // --- FIX: Replaced Java 8 Streams with a traditional for-loop for compatibility ---
            int totalWeightInCycle = 0;
            for (Goal g : getOrganisationalGoalsForCycle(goal.getGoalCycle())) {
                if (!g.getId().equals(goal.getId())) {
                    if (g.getWeight() != null){
                    totalWeightInCycle += g.getWeight();
                    }
                }
            }
            Validate.isTrue(totalWeightInCycle + goal.getWeight() <= 100, "Total weight of organisational goals in a cycle cannot exceed 100.");

            // Validate department contribution weights for this goal
            if (goal.getDepartmentAssignments() != null && !goal.getDepartmentAssignments().isEmpty()) {
                // --- FIX: Replaced Java 8 Streams with a traditional for-loop for compatibility ---
                int totalDepartmentWeight = 0;
                for(BusinessGoalDepartmentAssignment assignment : goal.getDepartmentAssignments()){
                    totalDepartmentWeight += assignment.getContributionWeight();
                }
                Validate.isTrue(totalDepartmentWeight == 100, "The sum of department contribution weights for this goal must be exactly 100.");

                // --- FIX: Replaced Java 8 Lambda with a traditional for-loop for compatibility ---
                for (BusinessGoalDepartmentAssignment assignment : goal.getDepartmentAssignments()) {
                    assignment.setGoal(goal);
                }
            }
        } else { // For Department and Team goals
            Validate.notNull(goal.getParentGoal(), "A parent goal is required for Department and Team goals.");
            // FIX: Add validation for Team goals
        }

        return super.save(goal);
    }

    public List<Goal> getGoalsByLevel(GoalLevel level) {
        Search search = new Search();
        search.addFilterEqual("goalLevel", level);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        return super.search(search);
    }

    public List<Goal> getChildGoals(Goal parentGoal) {
        Search search = new Search();
        search.addFilterEqual("parentGoal", parentGoal);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        return super.search(search);
    }


    @Override
    public List<Goal> getOrganisationalGoalsForCycle(GoalCycle cycle) {
        if (cycle == null) {
            return Collections.emptyList();
        }
        Search search = new Search();
        search.addFilterEqual("goalCycle", cycle);
        search.addFilterEqual("goalLevel", GoalLevel.ORGANISATIONAL);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        return super.search(search);
    }
}
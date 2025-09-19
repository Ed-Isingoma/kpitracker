package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.core.services.KpiService;
import org.pahappa.systems.kpiTracker.core.services.impl.base.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.*;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class KpiServiceImpl extends GenericServiceImpl<KPI> implements KpiService {

    @Autowired
    private EmployeeUserService employeeUserService;

    @Override
    public double calculateOverallWeightedAchievement(List<KPI> kpis) {
        if (kpis == null || kpis.isEmpty()) { return 0.0; }
        double totalWeightedAchieved = 0;
        int totalWeight = 0;
        for (KPI kpi : kpis) {
            if (kpi.isAchieved()) {
                totalWeightedAchieved += kpi.getWeight();
            }
            totalWeight += kpi.getWeight();
        }
        if (totalWeight == 0) { return 0.0; }
        return (totalWeightedAchieved / totalWeight) * 100;
    }

    @Override
    public List<KPI> getKpisForCycle(GoalCycle goalCycle) {
        EmployeeUser currentUser = employeeUserService.getCurrentUser();
        boolean isAdministrator = false;
        boolean isDepartmentLead = false;
        boolean isTeamLead = false;
        for (Role role : currentUser.getRoles()) {
            if (role.getName().equals("ROLE_ADMINISTRATOR")) { isAdministrator = true; break; }
            if (role.getName().equals("Department Lead")) { isDepartmentLead = true; }
            if (role.getName().equals("Team Lead")) { isTeamLead = true; }
        }
        if (isAdministrator) { return getAllKpisForCycle(goalCycle); }
        else if (isDepartmentLead) { return getKpisForDepartmentAndCycle(currentUser.getDepartment(), goalCycle); }
        else if (isTeamLead) { return getKpisForTeamAndCycle(currentUser.getTeam(), goalCycle); }
        else { return getKpisForUserAndCycle(currentUser, goalCycle); }
    }

    @Override
    public List<KPI> getKpisForCycleAndGoal(GoalCycle goalCycle, Goal goal) {
        if (goalCycle == null || goal == null) { return Collections.emptyList(); }
        Search search = new Search(KPI.class);
        search.addFilterEqual("goal", goal);
        search.addFilterEqual("goal.goalCycle", goalCycle);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        return super.search(search);
    }

    private List<KPI> getAllKpisForCycle(GoalCycle goalCycle) {
        if (goalCycle == null) { return Collections.emptyList(); }
        Search search = new Search(KPI.class);
        search.addFilterEqual("goal.goalCycle", goalCycle);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        return super.search(search);
    }

    private List<KPI> getKpisForDepartmentAndCycle(Department department, GoalCycle goalCycle) {
       if (department == null || goalCycle == null) { return Collections.emptyList(); }
       Search search = new Search(KPI.class);
       search.addFilterEqual("goal.goalCycle", goalCycle);
       search.addFilterEqual("owner.department", department);
       search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
       return super.search(search);
    }

    private List<KPI> getKpisForTeamAndCycle(Team team, GoalCycle goalCycle) {
        if (team == null || goalCycle == null) { return Collections.emptyList(); }
        Search search = new Search(KPI.class);
        search.addFilterEqual("goal.goalCycle", goalCycle);
        search.addFilterEqual("owner.team", team);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        return super.search(search);
    }

    private List<KPI> getKpisForUserAndCycle(EmployeeUser user, GoalCycle goalCycle) {
        if (user == null || goalCycle == null) { return Collections.emptyList(); }
        Search search = new Search(KPI.class);
        search.addFilterEqual("goal.goalCycle", goalCycle);
        search.addFilterEqual("owner", user);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        return super.search(search);
    }

    @Override
    public boolean isDeletable(KPI instance) throws OperationFailedException {
        return true;
    }

    @Override
    public KPI saveInstance(KPI kpi) throws ValidationFailedException, OperationFailedException {
        return saveKpi(kpi);
    }

    private KPI saveKpi(KPI kpi) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(kpi, "KPI cannot be null");
        return super.save(kpi);
    }

}
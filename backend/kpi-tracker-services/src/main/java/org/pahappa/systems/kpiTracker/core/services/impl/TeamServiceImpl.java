package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.dao.TeamDao;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.core.services.impl.base.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.Team;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service("teamService")
@Transactional
public class TeamServiceImpl extends GenericServiceImpl<Team> implements TeamService {

    @Autowired
    private TeamDao teamDao;


    @Override
    public boolean isDeletable(Team instance) throws OperationFailedException {
        // To be implemented: Check if team has users or KPIs.
        return true;
    }

//    @Override
    public Team saveInstance(Team team) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(team, "Team details are required");
        Validate.hasText(team.getName(), "Team name is required");
        Validate.notNull(team.getDepartment(), "Team must be assigned to a department");

        // Ensure team name is unique within the department
        Search search = new Search()
                .addFilterEqual("name", team.getName())
                .addFilterEqual("department", team.getDepartment());

        if (team.isSaved()) {
            search.addFilterNotEqual("id", team.getId());
        }

        if (teamDao.count(search) > 0) {
            throw new ValidationFailedException("A team with this name already exists in this department.");
        }

        return super.merge(team);
    }

    @Override
    public void deleteTeam(Team team) throws OperationFailedException, ValidationFailedException {
        Validate.notNull(team, "Team to delete cannot be null");

        if (!isDeletable(team)) {
            throw new OperationFailedException("Cannot delete a team that has active members assigned to it.");
        }

        // Perform a soft delete
        team.setRecordStatus(RecordStatus.DELETED);
        super.save(team);
    }

    public void saveTeamAndHandleLeadRoles(Team team) throws ValidationFailedException, OperationFailedException {
        RoleService roleService = ApplicationContextProvider.getBean(RoleService.class);
        EmployeeUserService employeeUserService = ApplicationContextProvider.getBean(EmployeeUserService.class);

        final String LEAD_ROLE_NAME = "Team Lead";
        Role teamLeadRole = roleService.getRoleByName(LEAD_ROLE_NAME);
        if (teamLeadRole == null) {
            throw new OperationFailedException("'" + LEAD_ROLE_NAME + "' role was not found in the database. Please create it.");
        }

        EmployeeUser newLead = team.getTeamLead();
        EmployeeUser oldLead = null;

        if (team.isSaved()) {
            Team existingTeam = this.teamDao.find(team.getId());
            if (existingTeam != null) {
                oldLead = existingTeam.getTeamLead();
            }
        }

        if (!Objects.equals(oldLead, newLead)) {
            if (oldLead != null) {
                oldLead.getRoles().remove(teamLeadRole);
                employeeUserService.saveInstance(oldLead);
            }
            if (newLead != null) {
                newLead.getRoles().add(teamLeadRole);
                employeeUserService.saveInstance(newLead);
            }
        }

        this.saveInstance(team);
    }

    @Override
    public List<Team> getTeamsInDepartment(Department department) {
        if (department == null) {
            return new java.util.ArrayList<>();
        }
        return teamDao.search(new Search().addFilterEqual("department", department));
    }
}
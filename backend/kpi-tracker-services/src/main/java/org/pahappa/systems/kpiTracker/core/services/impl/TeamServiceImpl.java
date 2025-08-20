package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.dao.TeamDao;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.core.services.impl.base.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.Team;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
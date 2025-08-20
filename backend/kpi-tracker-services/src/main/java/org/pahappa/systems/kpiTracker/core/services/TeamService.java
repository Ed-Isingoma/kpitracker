package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.core.services.base.GenericService;
import org.pahappa.systems.kpiTracker.models.Team;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;

/**
 * Service for managing Team entities.
 */
public interface TeamService extends GenericService<Team> {
    /**
     * Performs a soft delete on the given team after ensuring it has no active members.
     * @param team The team to delete.
     * @throws OperationFailedException if the team still has members.
     */
    void deleteTeam(Team team) throws OperationFailedException, ValidationFailedException;
}
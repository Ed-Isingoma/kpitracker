package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.TeamDao;
import org.pahappa.systems.kpiTracker.models.Team;
import org.sers.webutils.server.core.dao.impl.BaseDAOImpl;
import org.springframework.stereotype.Repository;

@Repository("teamDao")
public class TeamDaoImpl extends BaseDAOImpl<Team> implements TeamDao {
}
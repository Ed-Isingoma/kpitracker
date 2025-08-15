package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.PeerRatingDao;
import org.pahappa.systems.kpiTracker.models.PeerRating;
import org.sers.webutils.server.core.dao.impl.BaseDAOImpl;
import org.springframework.stereotype.Repository;

@Repository("peerRatingDao")
public class PeerRatingDaoImpl extends BaseDAOImpl<PeerRating> implements PeerRatingDao {
}
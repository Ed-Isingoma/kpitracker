package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.ProfessionalAttributeValueDao;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttributeValue;
import org.sers.webutils.server.core.dao.impl.BaseDAOImpl;
import org.springframework.stereotype.Repository;

@Repository("professionalAttributeValueDao")
public class ProfessionalAttributeValueDaoImpl extends BaseDAOImpl<ProfessionalAttributeValue> implements ProfessionalAttributeValueDao {
}
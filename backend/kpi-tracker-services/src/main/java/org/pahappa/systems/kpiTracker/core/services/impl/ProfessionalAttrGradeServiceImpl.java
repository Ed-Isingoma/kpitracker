package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.ProfessionalAttrGradeService;
import org.pahappa.systems.kpiTracker.core.services.impl.base.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttrGrade;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("professionalAttrGradeService")
@Transactional
public class ProfessionalAttrGradeServiceImpl extends GenericServiceImpl<ProfessionalAttrGrade> implements ProfessionalAttrGradeService {
    public boolean isDeletable(ProfessionalAttrGrade professionalAttrGrade) throws OperationFailedException {
        return true;
    }

    public ProfessionalAttrGrade saveInstance(ProfessionalAttrGrade professionalAttrGrade) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(professionalAttrGrade, "Grade cannot be null");
        return super.save(professionalAttrGrade);
    }
}

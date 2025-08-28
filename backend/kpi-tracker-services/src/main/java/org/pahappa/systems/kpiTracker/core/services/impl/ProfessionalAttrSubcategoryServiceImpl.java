package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.impl.base.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttrSubcategory;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;

public class ProfessionalAttrSubcategoryServiceImpl extends GenericServiceImpl<ProfessionalAttrSubcategory> {
    public boolean isDeletable(ProfessionalAttrSubcategory professionalAttrSubcategory) throws OperationFailedException {
        return true;
    }

    public ProfessionalAttrSubcategory saveInstance(ProfessionalAttrSubcategory professionalAttrSubcategory) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(professionalAttrSubcategory, "Subcategory cannot be null");

        return super.save(professionalAttrSubcategory);
    }
}

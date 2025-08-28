package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.ProfessionalAttrSubcategoryService;
import org.pahappa.systems.kpiTracker.core.services.impl.base.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttrCategory;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttrSubcategory;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("professionalAttrSubcategoryService")
@Transactional
public class ProfessionalAttrSubcategoryServiceImpl extends GenericServiceImpl<ProfessionalAttrSubcategory> implements ProfessionalAttrSubcategoryService {
    public boolean isDeletable(ProfessionalAttrSubcategory professionalAttrSubcategory) throws OperationFailedException {
        return true;
    }

    public ProfessionalAttrSubcategory saveInstance(ProfessionalAttrSubcategory professionalAttrSubcategory) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(professionalAttrSubcategory, "Subcategory cannot be null");
        return super.save(professionalAttrSubcategory);
    }

    public List<ProfessionalAttrSubcategory> getSubcategoriesForAttribute(ProfessionalAttrCategory attribute) {
        Search search = new Search();
        search.addFilterEqual("professionalAttrCategory", attribute); // Assumes field name is 'professionalAttrCategory'
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        return super.search(search);
    }
}

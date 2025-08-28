package org.pahappa.systems.kpiTracker.models;

import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;

@Entity
@Table(name = "professional_attr_subcategory")
public class ProfessionalAttrSubcategory extends BaseEntity {
    private String description;
    private ProfessionalAttrCategory professionalAttrCategory;
    private int maximumValue;
    private int contributionWeight;

    @Column(name = "maximum_value", nullable = false)
    public int getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(int maximumValue) {
        this.maximumValue = maximumValue;
    }

    @Column(name = "description", length = 1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    @JoinColumn(name = "professional_attr_category", nullable = false)
    public ProfessionalAttrCategory getProfessionalAttrCategory() {
        return professionalAttrCategory;
    }

    public void setProfessionalAttrCategory(ProfessionalAttrCategory professionalAttrCategory) {
        this.professionalAttrCategory = professionalAttrCategory;
    }

    @Column(name = "contribution_weight")
    public int getContributionWeight() {
        return contributionWeight;
    }

    public void setContributionWeight(int contributionWeight) {
        this.contributionWeight = contributionWeight;
    }

    @Override
    public String toString() {
        return this.description;
    }
}

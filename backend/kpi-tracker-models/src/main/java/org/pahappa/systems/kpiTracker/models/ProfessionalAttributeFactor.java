package org.pahappa.systems.kpiTracker.models;

import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "prof_attribute_factors")
public class ProfessionalAttributeFactor extends BaseEntity {

    private String title;
    private String description;
    private ProfessionalAttributeValue parentAttributeValue;
    private int contributionWeight;

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "description", length = 1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    @JoinColumn(name = "parent_attribute_value_id", nullable = false)
    public ProfessionalAttributeValue getParentAttributeValue() {
        return parentAttributeValue;
    }

    public void setParentAttributeValue(ProfessionalAttributeValue parentAttributeValue) {
        this.parentAttributeValue = parentAttributeValue;
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
        return this.title;
    }
}
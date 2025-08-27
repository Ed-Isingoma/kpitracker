package org.pahappa.systems.kpiTracker.models;

import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "professional_attr_category")
public class ProfessionalAttrCategory extends BaseEntity {

    private String title;
    private String description;
    private int contributionWeight;
    private GoalCycle goalCycle;

    @ManyToOne
    @JoinColumn(name = "goal_cycle_id", nullable=false)
    public GoalCycle getGoalCycle() {
        return goalCycle;
    }

    public void setGoalCycle(GoalCycle goalCycle) {
        this.goalCycle = goalCycle;
    }

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
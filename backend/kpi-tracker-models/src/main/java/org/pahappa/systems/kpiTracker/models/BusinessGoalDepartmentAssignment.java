package org.pahappa.systems.kpiTracker.models;

import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "business_goal_department_assignments")
public class BusinessGoalDepartmentAssignment extends BaseEntity {

    private BusinessGoal businessGoal;
    private Department department;
    private int contributionWeight;

    @ManyToOne
    @JoinColumn(name = "business_goal_id", nullable = false)
    public BusinessGoal getBusinessGoal() {
        return businessGoal;
    }

    public void setBusinessGoal(BusinessGoal businessGoal) {
        this.businessGoal = businessGoal;
    }

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Column(name = "contribution_weight")
    public int getContributionWeight() {
        return contributionWeight;
    }

    public void setContributionWeight(int contributionWeight) {
        this.contributionWeight = contributionWeight;
    }
}

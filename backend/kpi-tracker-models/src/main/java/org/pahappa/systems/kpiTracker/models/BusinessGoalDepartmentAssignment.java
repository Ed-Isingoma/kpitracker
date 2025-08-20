package org.pahappa.systems.kpiTracker.models;

import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "business_goal_department_assignments")
public class BusinessGoalDepartmentAssignment extends BaseEntity {

    private Goal goal;
    private Department department;
    private int contributionWeight = 0;

    @ManyToOne
    @JoinColumn(name = "goal_id", nullable = false)
    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
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
    }}

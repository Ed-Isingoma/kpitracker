package org.pahappa.systems.kpiTracker.models;

import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.constants.GoalStatus;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "goals")
public class Goal extends BaseEntity {

    private String title;
    private String description;
    private Date startDate;
    private Date targetDate;
    private GoalStatus status = GoalStatus.PENDING;
    private Integer weight;
    private GoalLevel goalLevel;
    private Department department;
    private Team team;
    private Goal parentGoal;
    private Set<Goal> childGoals;
    private GoalCycle goalCycle;
    private Set<BusinessGoalDepartmentAssignment> departmentAssignments = new HashSet<>();
    private Set<Activity> activities = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = true) // Nullable because it only applies to Departmental goals
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = true) // Nullable because it only applies to Team goals
    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "target_date")
    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }


    @Column(name = "weight")
    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_level")
    public GoalLevel getGoalLevel() {
        return goalLevel;
    }

    public void setGoalLevel(GoalLevel goalLevel) {
        this.goalLevel = goalLevel;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_goal_id", nullable = true)
    public Goal getParentGoal() {
        return parentGoal;
    }

    public void setParentGoal(Goal parentGoal) {
        this.parentGoal = parentGoal;
    }

    @OneToMany(mappedBy = "parentGoal", fetch = FetchType.LAZY)
    public Set<Goal> getChildGoals() {
        return childGoals;
    }

    public void setChildGoals(Set<Goal> childGoals) {
        this.childGoals = childGoals;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "goal_cycle_id")
    public GoalCycle getGoalCycle() {
        return goalCycle;
    }

    public void setGoalCycle(GoalCycle goalCycle) {
        this.goalCycle = goalCycle;
    }

    @OneToMany(mappedBy = "goal", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<BusinessGoalDepartmentAssignment> getDepartmentAssignments() {
        return departmentAssignments;
    }

    public void setDepartmentAssignments(Set<BusinessGoalDepartmentAssignment> departmentAssignments) {
        this.departmentAssignments = departmentAssignments;
    }


    @OneToMany(mappedBy = "goal", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Activity> getActivities() {
        return activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }

    @Override
    public String toString() {
        return this.title;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Goal && (super.getId() != null) ? super.getId().equals(((Goal) object).getId())
                : (object == this);
    }

    @Override
    public int hashCode() {
        return super.getId() != null ? this.getClass().hashCode() + super.getId().hashCode() : super.hashCode();
    }
}
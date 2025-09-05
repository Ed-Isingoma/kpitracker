package org.pahappa.systems.kpiTracker.models;

import org.pahappa.systems.kpiTracker.constants.ActivityStatus;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "activities")
public class Activity extends BaseEntity {

    private String title;
    private String description;
    private String requirements; // New field
    private EmployeeUser assignedUser;
    private Date startDate;
    private Date endDate;
    private ActivityStatus status = ActivityStatus.PENDING;
    private int progress;
    private String evidence; // Path to the uploaded file
    private Goal goal; // Will be made nullable
    private Department department; // New field
    private Team team; // New field

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "description", length = 2000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "requirements", length = 2000)
    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    public EmployeeUser getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(EmployeeUser assignedUser) {
        this.assignedUser = assignedUser;
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
    @Column(name = "end_date")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }

    @Column(name = "progress")
    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Column(name = "evidence_path")
    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    @ManyToOne
    @JoinColumn(name = "goal_id", nullable = true) // Made nullable
    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    @ManyToOne
    @JoinColumn(name = "department_id")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = true) // Nullable for users directly in a department
    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
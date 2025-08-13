package org.pahappa.systems.kpiTracker.models;

import org.pahappa.systems.kpiTracker.constants.ActivityStatus;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "activities")
public class Activity extends BaseEntity {

    private String title;
    private String description;
    private User assignedUser;
    private Team assignedTeam;
    private int contributionWeight;
    private Date startDate;
    private Date endDate;
    private ActivityStatus status = ActivityStatus.PENDING;
    private int progress;
    private String evidence; // Path to the uploaded file

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

    @ManyToOne
    @JoinColumn(name = "assigned_user_id", nullable = true)
    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    @ManyToOne
    @JoinColumn(name = "assigned_team_id", nullable = true)
    public Team getAssignedTeam() {
        return assignedTeam;
    }

    public void setAssignedTeam(Team assignedTeam) {
        this.assignedTeam = assignedTeam;
    }

    @Column(name = "contribution_weight")
    public int getContributionWeight() {
        return contributionWeight;
    }

    public void setContributionWeight(int contributionWeight) {
        this.contributionWeight = contributionWeight;
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

    @Override
    public String toString() {
        return this.title;
    }
}
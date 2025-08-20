package org.pahappa.systems.kpiTracker.models;

import org.pahappa.systems.kpiTracker.constants.GoalStatus;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "goal_cycles")
public class GoalCycle extends BaseEntity {

    private String title;
    private Date startDate;
    private Date endDate;
    private GoalStatus status = GoalStatus.PENDING;

    @Column(name = "title", nullable = false, unique = true)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date", nullable = false)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date", nullable = false)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
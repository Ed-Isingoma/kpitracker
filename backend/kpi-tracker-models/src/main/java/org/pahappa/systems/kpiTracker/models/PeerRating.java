package org.pahappa.systems.kpiTracker.models;

import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "peer_ratings")
public class PeerRating extends BaseEntity {

    private User evaluatorUser;
    private User evaluatedUser;
    private ProfessionalAttributeFactor attributeFactor;
    private int score;
    private String comments;
    private Date ratingDate;

    @ManyToOne
    @JoinColumn(name = "evaluator_user_id", nullable = false)
    public User getEvaluatorUser() {
        return evaluatorUser;
    }

    public void setEvaluatorUser(User evaluatorUser) {
        this.evaluatorUser = evaluatorUser;
    }

    @ManyToOne
    @JoinColumn(name = "evaluated_user_id", nullable = false)
    public User getEvaluatedUser() {
        return evaluatedUser;
    }

    public void setEvaluatedUser(User evaluatedUser) {
        this.evaluatedUser = evaluatedUser;
    }

    @ManyToOne
    @JoinColumn(name = "attribute_factor_id", nullable = false)
    public ProfessionalAttributeFactor getAttributeFactor() {
        return attributeFactor;
    }

    public void setAttributeFactor(ProfessionalAttributeFactor attributeFactor) {
        this.attributeFactor = attributeFactor;
    }

    @Column(name = "score")
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Column(name = "comments", length = 2000)
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "rating_date", nullable = false)
    public Date getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(Date ratingDate) {
        this.ratingDate = ratingDate;
    }
}
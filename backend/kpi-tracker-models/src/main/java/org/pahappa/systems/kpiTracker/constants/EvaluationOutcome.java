package org.pahappa.systems.kpiTracker.constants;

public enum EvaluationOutcome {
    REWARD("Reward"),
    SATISFACTORY("Satisfactory"),
    PIP("Performance Improvement Plan");

    private final String name;

    EvaluationOutcome(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
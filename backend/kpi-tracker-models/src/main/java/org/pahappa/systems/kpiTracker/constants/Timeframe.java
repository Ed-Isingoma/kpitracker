package org.pahappa.systems.kpiTracker.constants;


public enum Timeframe {
    ANNUAL("Annual"),
    SEMI_ANNUAL("Semi-Annual"),
    QUARTERLY("Quarterly"),
    MONTHLY("Monthly");

    private final String name;

    Timeframe(String name) {
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
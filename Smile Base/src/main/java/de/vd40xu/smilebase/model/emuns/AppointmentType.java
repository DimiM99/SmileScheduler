package de.vd40xu.smilebase.model.emuns;

public enum AppointmentType {
    QUICKCHECK, EXTENSIVE, SURGERY;

    public long getDuration() {
        return switch (this) {
            case QUICKCHECK -> 30;
            case EXTENSIVE -> 60;
            case SURGERY -> 120;
        };
    }
}

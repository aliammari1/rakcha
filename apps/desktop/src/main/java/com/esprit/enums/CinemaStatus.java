package com.esprit.enums;

public enum CinemaStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REFUSED("Refused");

    private final String status;

    CinemaStatus(String status) {
        this.status = status;
    }

    /**
     * Converts a string value to CinemaStatus enum.
     *
     * @param value the status value (case-insensitive)
     * @return the corresponding CinemaStatus, or PENDING if not found
     */
    public static CinemaStatus fromString(String value) {
        if (value == null) {
            return PENDING;
        }
        for (CinemaStatus cs : CinemaStatus.values()) {
            if (cs.name().equalsIgnoreCase(value) || cs.status.equalsIgnoreCase(value)) {
                return cs;
            }
        }
        return PENDING;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}

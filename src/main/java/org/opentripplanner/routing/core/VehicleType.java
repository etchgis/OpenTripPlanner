package org.opentripplanner.routing.core;

public enum VehicleType {
    UNKNOWN, BICYCLE, SCOOTER, CAR;

    public static VehicleType fromString (String type) {
        type = type.toLowerCase();
        if (type.equals("bike"))
            return BICYCLE;
        if (type.equals("scooter"))
            return SCOOTER;
        if (type.equals("car"))
            return CAR;
        return UNKNOWN;
    }

    @Override
    public String toString() {
        switch (this) {
            case BICYCLE:
                return "bike";
            case SCOOTER:
                return "scooter";
            case CAR:
                return "car";
            default:
                return "unknown";
        }
    }
}

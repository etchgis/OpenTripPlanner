package org.opentripplanner.routing.vehicle_rental;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.locationtech.jts.geom.Geometry;

import java.io.Serializable;
import java.util.Locale;

/**
 * VehicleRentalRegion defines the region in which a vehicle rental operates.
 * Vehicles are only allowed to be dropped off within the operation region and not outside.
 */
public class VehicleRentalRegion implements Serializable, Cloneable {
    private static final long serialVersionUID = -6832065607507589167L;
    /**
     * The vehicle rental network name.
     */
    @JsonSerialize
    public String network;

    @JsonSerialize
    public Geometry geometry;

    public VehicleRentalRegion(String network, Geometry geometry) {
        this.network = network;
        this.geometry = geometry;
    }

    public VehicleRentalRegion() {
    }

    public boolean equals(Object o) {
        if (!(o instanceof VehicleRentalRegion)) {
            return false;
        }
        VehicleRentalRegion other = (VehicleRentalRegion) o;
        return other.network.equals(network);
    }

    public int hashCode() {
        return network.hashCode() + 1;
    }

    public String toString() {
        return String.format(Locale.US, "Vehicle rental region for network %s", network);
    }

    @Override
    public VehicleRentalRegion clone() {
        try {
            return (VehicleRentalRegion) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); //can't happen
        }
    }
}
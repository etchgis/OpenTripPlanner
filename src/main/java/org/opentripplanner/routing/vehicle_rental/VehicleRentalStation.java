package org.opentripplanner.routing.vehicle_rental;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.opentripplanner.routing.core.VehicleType;
import org.opentripplanner.util.ResourceBundleSingleton;

import java.io.Serializable;
import java.util.Locale;
import java.util.Set;

/**
 * This class models a place where a rental vehicle can be returned or picked up.
 * This class is typically used in one of 3 ways:
 *
 * 1. To model a docking station that may have multiple vehicles that can be rented and also parking spaces to return
 *  a rented vehicle.
 * 2. To model a single floating vehicle that can only be picked up.
 * 3. To model a border dropoff where the extent of vehicle rental region ends. At this point, the StreetEdge is split
 *  and a vehicle rental station is created to facilitate the dropoff of vehicles no further than the allowable dropoff
 *  area.
 * FIXME: border dropoff stations, vertices and edges are probably not needed anymore with updated StreetEdge dropoff
 *  checks.
 */
public class VehicleRentalStation extends RentalStation implements Serializable, Cloneable {
    private static final long serialVersionUID = 8311460609708089384L;

    @JsonSerialize
    public int vehiclesAvailable = Integer.MAX_VALUE;

    @JsonSerialize
    public int spacesAvailable = Integer.MAX_VALUE;

    /**
     * Whether or not this rental station models a floating vehicle that is parked outside of a docking station on some
     * StreetEdge.
     */
    @JsonSerialize
    public boolean isFloatingVehicle = false;

    /**
     * List of compatible network names. Null (default) to be compatible with all.
     */
    @JsonSerialize
    public Set<String> networks = null;

    /**
     * The type of vehicle available at this station.
     */
    @JsonSerialize
    public VehicleType type = VehicleType.UNKNOWN;

    /**
     * This is used for localization. Currently "vehicle rental station" isn't part of the name.
     * It can be added on the client. But since it is used as Station: name, and Recommended Pick Up: name.
     * It isn't used.
     *
     * Names can be different in different languages if name tags in OSM have language tags.
     *
     * It is set in {@link org.opentripplanner.api.resource.VehicleRental} from URL parameter.
     *
     * Sets default locale on start
     *
     */
    @JsonIgnore
    public Locale locale = ResourceBundleSingleton.INSTANCE.getLocale(null);

    /**
     * Whether or not this station is a border dropoff for routing purposes
     */
    @JsonIgnore
    public boolean isBorderDropoff = false;

    public boolean equals(Object o) {
        if (!(o instanceof VehicleRentalStation)) {
            return false;
        }
        VehicleRentalStation other = (VehicleRentalStation) o;
        // since ID is set to be a license plate, changes to position constitute a different station
        return other.id.equals(id);
    }

    public int hashCode() {
        return id.hashCode() + 1;
    }

    public String toString () {
        String networkNames = networks == null ? "unknown network" : networks.toString();
        return String.format(Locale.US, "Vehicle rental station %s (%s) at %.6f, %.6f", name, networkNames, y, x);
    }

    @Override
    public VehicleRentalStation clone() {
        try {
            return (VehicleRentalStation) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); //can't happen
        }
    }

    /**
     * Gets translated name of vehicle rental station based on locale
     */
    @JsonSerialize
    public String getName() {
        return name.toString(locale);
    }
}

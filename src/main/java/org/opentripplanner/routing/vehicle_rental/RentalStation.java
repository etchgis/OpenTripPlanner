package org.opentripplanner.routing.vehicle_rental;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.opentripplanner.util.I18NString;

import java.util.Set;

public abstract class RentalStation {
    @JsonSerialize
    public String id;

    @JsonIgnore
    public I18NString name;

    @JsonSerialize
    public double x, y; //longitude, latitude

    @JsonSerialize
    public boolean allowDropoff = true;

    @JsonSerialize
    public boolean allowPickup = true;

    /**
     * List of compatible network names. Null (default) to be compatible with all.
     */
    @JsonSerialize
    public Set<String> networks = null;
}


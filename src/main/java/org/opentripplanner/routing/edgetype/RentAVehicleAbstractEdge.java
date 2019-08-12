package org.opentripplanner.routing.edgetype;

import com.google.common.collect.Sets;
import org.locationtech.jts.geom.LineString;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.vehicle_rental.VehicleRentalStation;

import java.util.Locale;
import java.util.Set;

/**
 * Renting or dropping off a rented vehicle edge.
 * 
 * @author Evan Siroky
 * 
 */
public abstract class RentAVehicleAbstractEdge extends Edge {

    private static final long serialVersionUID = 1L;

    protected VehicleRentalStation station;

    public RentAVehicleAbstractEdge(Vertex v, VehicleRentalStation station) {
        super(v, v);
        this.station = station;
    }

    @Override
    public double getDistance() {
        return 0;
    }

    @Override
    public LineString getGeometry() {
        return null;
    }

    @Override
    public String getName() {
        return getToVertex().getName();
    }

    @Override
    public String getName(Locale locale) {
        return getToVertex().getName(locale);
    }

    public VehicleRentalStation getStation() { return station; }

    @Override
    public boolean hasBogusName() {
        return false;
    }

    /**
     * @param stationNetworks The station where we want to drop the vehicle off.
     * @param rentedNetworks The set of networks of the station we rented the vehicle from.
     * @return true if the vehicle can be dropped off here, false if not.
     */
    protected boolean hasCompatibleNetworks(Set<String> stationNetworks, Set<String> rentedNetworks) {
        // Two stations are compatible if they share at least one network. Special case for "null"
        // networks ("catch-all" network defined).
        if (stationNetworks == null || rentedNetworks == null) return true; // Always a match
        return !Sets.intersection(stationNetworks, rentedNetworks).isEmpty();
    }
}

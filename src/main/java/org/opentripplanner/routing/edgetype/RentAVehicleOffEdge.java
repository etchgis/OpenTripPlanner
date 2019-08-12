package org.opentripplanner.routing.edgetype;

import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.StateEditor;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.vehicle_rental.VehicleRentalStation;

/**
 * Dropping off a rented vehicle edge.
 * 
 * Cost is the time to dropoff a vehicle.
 * 
 * @author Evan Siroky
 * 
 */
public class RentAVehicleOffEdge extends RentAVehicleAbstractEdge {

    private static final long serialVersionUID = 1L;

    public RentAVehicleOffEdge(Vertex v, VehicleRentalStation station) {
        super(v, station);
    }

    @Override
    public State traverse(State s0) {
        RoutingRequest options = s0.getOptions();

        // check if the current state would allow a vehicle dropoff
        if (!s0.isVehicleRentalDropoffAllowed(!station.isBorderDropoff)) {
            // something about the current state makes dropping off a vehicle not possible, return null.
            return null;
        }

        // make sure there is at least one spot to park at the station
        if (options.useVehicleRentalAvailabilityInformation && station.spacesAvailable == 0) {
            // no spots available at the station to park the vehicle, return null.
            return null;
        }

        // dropoff at this station is ok, begin editing a new state
        StateEditor s1e = s0.edit(this);

        if (options.arriveBy) {
            // if in arrive-by mode, the search is progressing backwards and we are entering a rented vehicle state
            s1e.beginVehicleRenting(0, station.networks, !station.isBorderDropoff);
        } else {
            // if in depart-at mode, this is the conclusion of a vehicle rental
            s1e.endVehicleRenting();
        }

        // increment weight and time associated with dropping off the vehicle
        s1e.incrementWeight(options.vehicleRentalDropoffCost);
        s1e.incrementTimeInSeconds(options.vehicleRentalDropoffTime);
        State s1 = s1e.makeState();
        return s1;
    }

    public boolean equals(Object o) {
        if (o instanceof RentAVehicleOffEdge) {
            RentAVehicleOffEdge other = (RentAVehicleOffEdge) o;
            return other.getFromVertex().equals(fromv) && other.getToVertex().equals(tov);
        }
        return false;
    }

    public String toString() {
        return "RentAVehicleOffEdge(" + fromv + " -> " + tov + ")";
    }
}

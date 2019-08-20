package org.opentripplanner.routing.edgetype;

import com.google.common.collect.Sets;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.StateEditor;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.core.VehicleType;
import org.opentripplanner.routing.vehicle_rental.VehicleRentalStation;
import org.opentripplanner.routing.vertextype.VehicleRentalStationVertex;

/**
 * Renting a vehicle edge.
 * 
 * Cost is the time to pickup a vehicle plus "inconvenience of renting".
 * 
 * @author Evan Siroky
 * 
 */
public class RentAVehicleOnEdge extends RentAVehicleAbstractEdge {

    private static final long serialVersionUID = 1L;

    public RentAVehicleOnEdge(VehicleRentalStationVertex v, VehicleRentalStation station) {
        super(v, station);
    }

    @Override
    public State traverse(State s0) {
        RoutingRequest options = s0.getOptions();

        // To rent a vehicle, we need to have vehicle rental allowed in request.
        if (!options.allowVehicleRental) {
            // request settings forbids vehicle renting. Don't rent a vehicle.
            return null;
        }

        // make sure there is at least one vehicle available to rent at this station
        if (options.useVehicleRentalAvailabilityInformation && station.vehiclesAvailable == 0) {
            // no vehicle available at the station, rental not possible from here.
            return null;
        }

        // don't use the same pickup station twice
        if (s0.stateData.getRentedVehicles().contains(station.id)) {
            // this station has already been used in the search. Don't use it again.
            return null;
        }

        // make sure the vehicle being rented is within a network compatible with the request
        if (options.whiteListedProviders != null &&
            !options.whiteListedProviders.isEmpty() &&
            Sets.intersection(station.networks, options.whiteListedProviders).isEmpty()) {
            return null;
        }
        if (options.bannedProviders != null &&
            !options.bannedProviders.isEmpty() &&
            Sets.difference(station.networks, options.bannedProviders).isEmpty()) {
            return null;
        }
        if (options.whiteListedVehicles != null &&
            !options.whiteListedVehicles.isEmpty() &&
            !options.whiteListedVehicles.contains(station.type)) {
            return null;
        }
        if (options.bannedVehicles != null &&
            !options.bannedVehicles.isEmpty() &&
            options.bannedVehicles.contains(station.type)) {
            return null;
        }

        StateEditor s1e = s0.edit(this);
        if (options.arriveBy) {
            // In an "arrive by" search, a vehicle may have already been dropped off and this potential
            // pickup location has been encountered.

            // First make sure the current state has a vehicle rented.
            if (!s0.isVehicleRenting()) {
                // not in a state where a vehicle is being rented, therefore a rental can't begin at this time
                return null;
            }

            if (s0.getVehicleType() != station.type && s0.getVehicleType() != VehicleType.UNKNOWN)
                return null;

            // Check if the vehicle network at this edge is compatible with the allowable vehicle networks
            // where the vehicle was dropped off.  Dropoff points could be either a dropoff station or a
            // StreetEdge for floating vehicle rentals.
            if (!hasCompatibleNetworks(s0.getVehicleRentalNetworks(), station.networks)) {
                // The networks found at the dropoff point are incompatible with those found at
                // this pickup station, so return null.
                return null;
            }

            // make sure the minimum vehicle rental distance has been traveled
            if (s0.vehicleRentalDistance < options.minimumVehicleRentalDistance) {
                // not enough distance has been traveled in order to do the rental, return null
                return null;
            }

            // make sure the vehicle that is about to be rented has floating dropoff capabilities
            if (s0.stateData.rentedVehicleAllowsFloatingDropoffs() && !station.isFloatingVehicle) {
                return null;
            }

            // looks like it's ok to have rented a vehicle from this station. Transition out of the vehicle rental state.
            s1e.setVehicleType(station.type);   // TODO: is this okay to do? what about other states?
            s1e.endVehicleRenting();
            s1e.setBackMode(s0.getNonTransitMode());
        } else {
            // make sure more than 1 vehicle isn't rented at once
            if (s0.isVehicleRenting()) {
                // already renting a vehicle, don't allow 2 rentals
                return null;
            }

            // make sure a vehicle is rented only once during pre/post transit parts of the trip
            if (s0.isEverBoarded()) {
                if (s0.stateData.hasRentedVehiclePostTransit()) {
                    // a vehicle has already been rented after taking transit, don't rent another after taking transit
                    return null;
                }
            } else {
                if (s0.stateData.hasRentedVehiclePreTransit()) {
                    // a vehicle has already been rented after taking transit, don't rent another before taking transit
                    return null;
                }
            }

            // looks like it's ok to have begun renting a vehicle from this station
            s1e.beginVehicleRenting(0, station.networks, station.type, station.isFloatingVehicle);
        }

        // if this point is reached, it is possible to proceed with a vehicle rental pickup from this station

        // increment costs and time associated with picking up a rented vehicle
        s1e.incrementWeight(options.vehicleRentalPickupCost);
        s1e.incrementTimeInSeconds(options.vehicleRentalPickupTime);

        // add this vehicle rental station id to a list of ids of stations that have already been rented from
        s1e.addRentedVehicle(station.id);
        State s1 = s1e.makeState();
        return s1;
    }

    public boolean equals(Object o) {
        if (o instanceof RentAVehicleOnEdge) {
            RentAVehicleOnEdge other = (RentAVehicleOnEdge) o;
            return other.getFromVertex().equals(fromv) && other.getToVertex().equals(tov);
        }
        return false;
    }

    public String toString() {
        return "RentAVehicleOnEdge(" + fromv + " -> " + tov + ")";
    }
}

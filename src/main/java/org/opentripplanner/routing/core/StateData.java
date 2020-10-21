package org.opentripplanner.routing.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.Stop;
import org.opentripplanner.model.Trip;
import org.opentripplanner.routing.edgetype.TripPattern;
import org.opentripplanner.routing.trippattern.TripTimes;

/**
 * StateData contains the components of search state that are unlikely to be changed as often as
 * time or weight. This avoids frequent duplication, which should have a positive impact on both
 * time and space use during searches.
 */
public class StateData implements Cloneable {

    // the time at which the search started
    protected long startTime;

    // which trip index inside a pattern
    protected TripTimes tripTimes;

    protected FeedScopedId tripId;

    protected Trip previousTrip;

    protected double lastTransitWalk = 0;

    protected String zone;

    protected FeedScopedId route;

    protected int numBoardings;

    protected boolean everBoarded;

    protected boolean usingRentedBike;

    protected boolean usingRentedVehicle;

    protected boolean hasRentedVehiclePostTransit = false;
    protected boolean hasRentedVehiclePreTransit = false;

    public boolean rentedVehicleAllowsFloatingDropoffs;

    protected boolean usingHailedCar;

    protected boolean hasHailedCarPostTransit = false;
    protected boolean hasHailedCarPreTransit = false;

    protected boolean carParked;

    protected boolean bikeParked;

    protected Stop previousStop;

    protected long lastAlightedTime;

    protected FeedScopedId[] routeSequence;

    protected HashMap<Object, Object> extensions;

    protected RoutingRequest opt;

    protected TripPattern lastPattern;

    protected boolean isLastBoardAlightDeviated = false;

    protected ServiceDay serviceDay;

    /**
     * The mode of travel to use when not traveling on transit. This should be updated each time transitions happen to
     * other modes such as parking a car at a park and ride or dropping off a bike rental.
     */
    protected TraverseMode nonTransitMode;

    /**
     * This is the wait time at the beginning of the trip (or at the end of the trip for
     * reverse searches). In Analyst anyhow, this is is subtracted from total trip length of each
     * final State in lieu of reverse optimization. It is initially set to zero so that it will be
     * ineffectual on a search that does not ever board a transit vehicle.
     */
    protected long initialWaitTime = 0;

    /**
     * This is the time between the trip that was taken at the previous stop and the next trip
     * that could have been taken. It is used to determine if a path needs reverse-optimization.
     */
    protected int lastNextArrivalDelta;

    /**
     * The mode that was used to traverse the state's backEdge
     */
    protected TraverseMode backMode;

    protected boolean backWalkingBike;

    public Set<String> bikeRentalNetworks;

    // A list of possible vehicle rental networks that the state can be associated with. This data structure is a set
    // because in an arrive-by search, the search progresses backwards from a street edge where potentially multiple
    // vehicle rental providers allow floating drop-offs at the edge.
    public Set<String> vehicleRentalNetworks;

    // The ids of vehicles that have been rented so far
    // TODO: why is this a Set? can't we just store a current rental ID?
    protected Set<String> rentedVehicles = new HashSet<>();

    public VehicleType vehicleType;

    /* This boolean is set to true upon transition from a normal street to a no-through-traffic street. */
    protected boolean enteredNoThroughTrafficArea;

    public StateData(RoutingRequest options) {
        TraverseModeSet modes = options.modes;
        if (modes.getCar())
            nonTransitMode = TraverseMode.CAR;
        else if (modes.getWalk())
            nonTransitMode = TraverseMode.WALK;
        else if (modes.getBicycle())
            nonTransitMode = TraverseMode.BICYCLE;
        else if (modes.getMicromobility())
            nonTransitMode = TraverseMode.MICROMOBILITY;
        else
            nonTransitMode = null;
    }

    protected StateData clone() {
        try {
            StateData clonedStateData = (StateData) super.clone();
            // TODO by Jon: only copy the set to mutate it (create addVehicle and clearVehicles functions)
            // When HashSets (and other collections) are cloned, they contain references to the original HashSet (see
            // https://stackoverflow.com/a/7537385/269834). Therefore, any Collection in a StateData instance must be
            // recreated using a copy constructor if data is added to the Collection throughout a shortest path search.
            // This will then ensure the Collections don't contain data that is specific to a certain path.
            // For example, the rentedCars and rentedVehicles HashSets are populated with IDs of vehicles that were
            // rented at some point during a shortest path search. However, other fields like vehicleRentalNetworks are
            // only set once using data from a StreetEdge and are not added to throughout a shortest path search.
            // See https://github.com/ibi-group/OpenTripPlanner/pull/15 for more discussion.
            clonedStateData.rentedVehicles = new HashSet<>(this.rentedVehicles);
            return clonedStateData;
        } catch (CloneNotSupportedException e1) {
            throw new IllegalStateException("This is not happening");
        }
    }

    public int getNumBooardings(){
        return numBoardings;
    }

    public boolean hasHailedCarPostTransit() { return hasHailedCarPostTransit; }

    public boolean hasHailedCarPreTransit() { return hasHailedCarPreTransit; }

    public Set<String> getRentedVehicles() { return rentedVehicles; }

    public boolean hasRentedVehiclePostTransit() { return hasRentedVehiclePostTransit; }

    public boolean hasRentedVehiclePreTransit() { return hasRentedVehiclePreTransit; }

    public boolean rentedVehicleAllowsFloatingDropoffs() { return rentedVehicleAllowsFloatingDropoffs; }
}

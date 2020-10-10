package org.opentripplanner.api.parameter;

import com.google.common.collect.Sets;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.TraverseMode;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Set;

public class QualifiedMode implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public TraverseMode mode;
    public final Set<Qualifier> qualifiers = Sets.newHashSet();

    public QualifiedMode(String qMode) {
        String[] elements = qMode.split("_");
        String modeName = elements[0].trim();

        // TEMP
        // Change all bike requests to micromobility requests for backward compatibility.
        if (modeName.equals("BICYCLE"))
        {
            modeName = "MICROMOBILITY";
        }

        mode = TraverseMode.valueOf(modeName);
        if (mode == null) {
            throw new InvalidParameterException();
        }
        for (int i = 1; i < elements.length; i++) {
            Qualifier q = Qualifier.valueOf(elements[i].trim());
            if (q == null) {
                throw new InvalidParameterException();
            } else {
                qualifiers.add(q);
            }
        }
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mode);
        for (Qualifier qualifier : qualifiers) {
            sb.append("_");
            sb.append(qualifier);
        }
        return sb.toString();
    }

    public void applyToRoutingRequest (RoutingRequest req, boolean usingTransit) {
        req.modes.setMode(this.mode, true);
        if (this.mode == TraverseMode.BICYCLE) {
            if (this.qualifiers.contains(Qualifier.RENT)) {
                req.modes.setMode(TraverseMode.WALK, true); // turn on WALK for bike rental mode
                req.allowBikeRental = true;
            }
            if (usingTransit) {
                req.bikeParkAndRide = this.qualifiers.contains(Qualifier.PARK);
            }
        }
        if (this.mode == TraverseMode.MICROMOBILITY) {
            if (this.qualifiers.contains(Qualifier.RENT)) {
                req.modes.setMode(TraverseMode.WALK, true); // turn on WALK for micromobility rental mode
                req.allowVehicleRental = true;
            }
        }
        if (this.mode == TraverseMode.CAR) {

            if (this.qualifiers.contains(Qualifier.HAIL)) {
                req.useTransportationNetworkCompany = true;
                // TODO this is probably needed but causes issues in some places where trip must end on foot, so
                // user has to walk around block, like Luxe 12.5 to North Market!
                //req.modes.setWalk(true); // need to walk after exiting car
            }

            if (usingTransit) {
                if (this.qualifiers.contains(Qualifier.PARK)) {
                    req.parkAndRide = true;
                } else if (this.qualifiers.contains(Qualifier.HAIL)) {
                    req.driveTimeReluctance = 1.75;
                    req.driveDistanceReluctance = 0.2;
                } else {
                    req.kissAndRide = true;
                }
                req.modes.setWalk(true); // need to walk after dropping the car off / getting dropped off
            }
        }
    }

    @Override
    public int hashCode() {
        return mode.hashCode() * qualifiers.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof QualifiedMode) {
            QualifiedMode qmOther = (QualifiedMode) other;
            return qmOther.mode.equals(this.mode) && qmOther.qualifiers.equals(this.qualifiers);
        }
        return false;
    }

}

enum Qualifier {
    RENT, HAVE, PARK, HAIL, KEEP
}
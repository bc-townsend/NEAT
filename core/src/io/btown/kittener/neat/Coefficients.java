package io.btown.kittener.neat;

/**
 * This enumeration contains coefficients that are used, so they are located in one single place.
 * @author Chance Simmons and Brandon Townsend
 * @version 21 January 2020
 */
public enum Coefficients {
    // Mutation coefficients.
    CROSSOVER_THRESH(.75),  // Chance that crossover will occur.
    LINK_WEIGHT_MUT(.8),    // Chance that a link weight mutation can occur.
    ADD_LINK_MUT(.15),      // Chance that a new link will be added.
    ADD_NODE_MUT(.05),      // Chance that a new node will be added.

    // Other coefficients.
    DISJOINT_CO(1),
    WEIGHT_CO(.5),
    COMPAT_THRESH(.3),      // Two networks are compatible if below this value.
    STALENESS_THRESH(15),    // A species is stale if above this value.
    CULL_THRESH(.5),        // Used to cull the bottom half of a species.
    BIAS_NODE_LINK_WEIGHT(1); // The value used on the bias nodes outgoing
                                                        // links.

    /** The value of this coefficient. */
    public final double value;

    /**
     * Coefficient constructor. Assigns the supplied value.
     * @param value The value for the coefficient.
     */
    Coefficients(double value) {
        this.value = value;
    }
}

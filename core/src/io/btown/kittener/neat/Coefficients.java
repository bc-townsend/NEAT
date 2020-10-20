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
    TOGGLE_LINK_MUT(.1),    // Chance that a link will be re-enabled.

    // Other coefficients.
    DISJOINT_CO(1),
    WEIGHT_CO(.5),
    COMPAT_THRESH(3),      // Two networks are compatible if below this value.
    COMPAT_MOD(0.3),        // Adjusts the compatibility value by this if we're not hitting our target species number.
    STALENESS_THRESH(15),    // A species is stale if above this value.
    CULL_THRESH(.2),        // The percentage of organisms in a species we want to reproduce.
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

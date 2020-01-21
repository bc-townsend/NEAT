package neat;

import java.util.Map;

/**
 * Class which represents a group of similar organisms, therefore forming a species. This
 * protects new innovations in each network as organisms will compete within their species
 * instead of the total population.
 * @author Chance Simmons and Brandon Townsend
 * @version 20 January 2020
 */
public class Species {

    private Network compatibilityNetwork;

    private Map<Integer, Network> organisms;

    private double sharedFitness;

    public Species(int agentID, Network agentNetwork) {
        this.compatibilityNetwork = new Network(agentNetwork);
    }
}

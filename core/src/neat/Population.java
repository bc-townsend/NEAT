package neat;

import com.mygdx.kittener.game.Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class which handles all overhead NEAT functionality and is connected to the game. Keeps track
 * of the generation, the list of species, and a mapping of agents to their networks.
 * @author Chance Simmons and Brandon Townsend
 * @version 18 January 2020
 */
public class Population {
    /** Keeps track of the generation of organisms we're at. */
    private int generation;

    /** List of every species in the game. */
    private List<Species> species;

    /** Mapping of each game agent to their network. */
    private Map<Agent, Network> organisms;

    /**
     * Constructors our population. Maps every agent to a newly formed network.
     * @param agents The list of agents to connect via mapping.
     * @param input The number of inputs we're expecting.
     * @param output The number of outputs we're expecting.
     */
    public Population(List<Agent> agents, int input, int output) {
        generation  = 0;
        species     = new ArrayList<>();
        organisms   = new HashMap<>();

        for(Agent agent : agents) {
            organisms.put(agent, new Network(input, output));
        }
    }

    /**
     * Returns the current generation we are at.
     * @return The current generation we are at.
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Increments the generation by one. Represents moving to the next generational step.
     */
    public void incrementGeneration() {
        generation++;
    }
}

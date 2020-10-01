package io.btown.kittener.neat;

import com.badlogic.gdx.graphics.Color;

import java.util.*;

/**
 * Class which handles all overhead NEAT functionality and is connected to the game. Keeps track
 * of the generation, the list of species, and a mapping of agents to their networks.
 * @author Chance Simmons and Brandon Townsend
 * @version 21 January 2020
 */
public class OldPopulation {
    /** Keeps track of the generation of organism we're on. */
    private int generation;

    /** List of every species in the game. */
    private final List<OldSpecies> species;

    /** Mapping of each game agent to their network. */
    private final Map<Integer, Network> organisms;

    /** Identification number of the best agent. */
    private int bestAgentID;

    /**
     * Constructors our population. Maps every agent to a newly formed network.
     * @param numAgents The number of agents (aka organisms) we'll be forming.
     * @param input The number of inputs we're expecting.
     * @param output The number of outputs we're expecting.
     */
    public OldPopulation(int numAgents, int input, int output) {
        generation  = 0;
        species     = new ArrayList<>();
        organisms   = new HashMap<>();
        bestAgentID = 0;

        for(int i = 0; i < numAgents; i++) {
            organisms.put(i, new Network(i, input, output));
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

    /**
     * Returns the network mapped to the supplied agent ID number.
     * @param id The agent ID number to search for in our mapping.
     * @return The network mapped to by the supplied agent ID number.
     */
    public Network getNetwork(int id) {
        return organisms.get(id);
    }

    /**
     * Passes along the score of an agent to its network so that the species class can have
     * access to its score.
     * @param id The identification number of the agent to be used as a key to grab the network.
     * @param fitness The score to be passed to the network.
     */
    public void assignFitness(int id, int fitness) {
        getNetwork(id).setFitness(fitness);
    }

    /**
     * Sets the supplied agent with a certain species color.
     * @param id The agent's ID to modify the color of.
     * @return The color to assign the agent to.
     */
    public Color getColor(int id) {
        for(OldSpecies s : species) {
            if(getNetwork(id).isCompatibleTo() {
                return s.getColor();
            }
        }
        return null;
    }

    /**
     * Sets the best agent of this generation.
     */
    private void setBestAgentID() {
        int bestFitness = organisms.get(0).getFitness();
        for(Map.Entry<Integer, OldNetwork> organism : organisms.entrySet()) {
            if(organism.getValue().getFitness() > bestFitness) {
                bestFitness = organism.getValue().getFitness();
                bestAgentID = organism.getKey();
            }
        }
    }

    /**
     * The main NEAT algorithmic method. Calls all needed helper methods to separate our
     * organisms into species, cull them down so that we only get the high-performing ones, and
     * reproduces.
     */
    public void naturalSelection() {
        // Set up for producing babies.
        speciate();
        cullSpecies();
        setBestAgentID();
        removeStaleSpecies();
        removeBadSpecies();

        double avgSum = getAvgFitnessSum();
        List<OldNetwork> babies = new ArrayList<>();

        for(OldSpecies s : species) {
            // Directly clone the best network of the species.
            babies.add(new OldNetwork(12, 12));

            // Find the correct number of babies and reproduce them.
            int numBabies = (int) Math.floor(s.getAverageFitness() / avgSum * organisms.size()) - 1;
            for(int i = 0; i < numBabies; i++) {
                babies.add(s.reproduce());
            }
        }

        // If we don't have enough babies, produce them from random species.
        while(babies.size() < organisms.size()) {
            //FIXME: 1/22/2020 Sometimes when we get to this step we have a species size of 0. I
            // have no idea how that occurs since removing the stale and bad should remove all
            // but one. I think that somehow the best organism is being set wrong or removed on
            // accident from a species.
            babies.add(species.get(new Random().nextInt(species.size())).reproduce());
        }

        // Set up our agent's with their new networks.
        int i = 0;
        for(Map.Entry<Integer, OldNetwork> organism : organisms.entrySet()) {
            organism.setValue(babies.get(i));
            i++;
        }
    }

    /**
     * Separates this populations list of organisms into separate species.
     */
    private void speciate() {
        // First, set up each existing species compatibility network and clear it of all
        // organisms from the last generation.
        for(OldSpecies s : species) {
            s.setCompatibilityNetwork();
            s.getOrganisms().clear();
            s.setAverageFitness();
        }

        // For each organism in the population, see if it is compatible with any existing species.
        for(Map.Entry<Integer, OldNetwork> organism : organisms.entrySet()) {
            int agentID = organism.getKey();
            OldNetwork agentNetwork = organism.getValue();
            boolean speciesFound = false;
            for(int i = 0; !speciesFound && i < species.size(); i++) {
                OldSpecies s = species.get(i);
                if(agentNetwork.isCompatibleTo(s.getCompatibilityNetwork())) {
                    s.addOrganism(agentID, agentNetwork);
                    speciesFound = true;
                }
            }

            // If it is not compatible, create a new species.
            if(!speciesFound) {
                species.add(new OldSpecies(agentID, agentNetwork));
            }
        }
    }

    /**
     * Culls each species as well as share fitness between each organism in the species and set the
     * species average fitness.
     */
    private void cullSpecies() {
        for(OldSpecies s : species) {
            s.cull();
            s.setStaleness();
            s.shareFitness();
            s.setAverageFitness();
        }
    }

    /**
     * Removes all species that are over the staleness threshold, except the one that contains
     * the current best organism.
     */
    private void removeStaleSpecies() {
        for(int i = 0; i < species.size(); i++) {
            if(!species.get(i).getOrganisms().containsKey(bestAgentID)) {
                if(species.get(i).getStaleness() >= Coefficients.STALENESS_THRESH.value) {
                    OldSpecies.takenColors.remove(species.get(i).getColor());
                    species.remove(species.get(i));
                    i--;
                }
            }
        }
    }

    /**
     * Removes any species who would produce zero babies this generation.
     */
    private void removeBadSpecies() {
        double avgSum = getAvgFitnessSum();

        for(int i = 0; i < species.size(); i++) {
            if(!species.get(i).getOrganisms().containsKey(bestAgentID)) {
                if(species.get(i).getAverageFitness() / avgSum * organisms.size() < 1) {
                    OldSpecies.takenColors.remove(species.get(i).getColor());
                    species.remove(i);
                    i--;
                }
            }
        }
    }

    /**
     * Returns the sum of the averaged fitness of each species.
     * @return The sum of the averaged fitness of each species.
     */
    private double getAvgFitnessSum() {
        double avgSum = 0.0;
        for(OldSpecies s : species) {
            avgSum += s.getAverageFitness();
        }
        return avgSum;
    }
}

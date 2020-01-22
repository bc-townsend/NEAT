package neat;

import com.mygdx.kittener.game.Agent;

import java.util.*;

/**
 * Class which handles all overhead NEAT functionality and is connected to the game. Keeps track
 * of the generation, the list of species, and a mapping of agents to their networks.
 * @author Chance Simmons and Brandon Townsend
 * @version 21 January 2020
 */
public class Population {
    /** Keeps track of the generation of organisms we're at. */
    private int generation;

    /** List of every species in the game. */
    private List<Species> species;

    /** Mapping of each game agent to their network. */
    private Map<Integer, Network> organisms;

    /** Identification number of the best agent. */
    private int bestAgentID;

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
        bestAgentID = 0;

        for(Agent agent : agents) {
            organisms.put(agent.getId(), new Network(input, output));
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
        organisms.get(id).setFitness(fitness);
    }

    /**
     * Sets the supplied agent with a certain species color.
     * @param agent The agent to modify the color of.
     */
    public void assignColor(Agent agent) {

        //FIXME: 1/22/2020 This method should be checked. It looks like they could be getting
        // assigned the wrong color, but I'm not sure.

        for(Species s : species) {
            if(organisms.get(agent.getId()).isCompatibleTo(s.getCompatibilityNetwork())) {
                agent.setColor(s.getColor());
                break;
            }
        }
    }

    /**
     * Sets the best agent of this generation.
     *todo currently is the best agent of the generation. Possibly entertain the idea of it
     * being the best over all the generations and retain it through all of them.
     */
    private void setBestAgentID() {
        int bestFitness = organisms.get(0).getFitness();
        for(Map.Entry<Integer, Network> organism : organisms.entrySet()) {
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
        List<Network> babies = new ArrayList<>();

        for(Species s : species) {
            // Directly clone the best network of the species.
            babies.add(new Network(s.getOrganisms().get(s.getBestOrgID())));

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
        for(Map.Entry<Integer, Network> organism : organisms.entrySet()) {
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
        for(Species s : species) {
            s.setCompatibilityNetwork();
            s.getOrganisms().clear();
            s.setAverageFitness();
        }

        // For each organism in the population, see if it is compatible with any existing species.
        for(Map.Entry<Integer, Network> organism : organisms.entrySet()) {
            int agentID = organism.getKey();
            Network agentNetwork = organism.getValue();
            boolean speciesFound = false;
            for(int i = 0; !speciesFound && i < species.size(); i++) {
                Species s = species.get(i);
                if(agentNetwork.isCompatibleTo(s.getCompatibilityNetwork())) {
                    s.addOrganism(agentID, agentNetwork);
                    speciesFound = true;
                }
            }

            // If it is not compatible, create a new species.
            if(!speciesFound) {
                species.add(new Species(agentID, agentNetwork));
            }
        }
    }

    /**
     * Culls each species as well as share fitness between each organism in the species and set the
     * species average fitness.
     */
    private void cullSpecies() {
        for(Species s : species) {
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
                if(species.get(i).getStaleness() >= Coefficients.STALENESS_THRESH.getValue()) {
                    Species.takenColors.remove(species.get(i).getColor());
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
                    Species.takenColors.remove(species.get(i).getColor());
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
        for(Species s : species) {
            avgSum += s.getAverageFitness();
        }
        return avgSum;
    }
}

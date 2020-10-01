package io.btown.kittener.neat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.utils.Array;

import java.util.*;

/**
 * Class which represents a group of similar organisms, therefore forming a species. This
 * protects new innovations in each network as organisms will compete within their species
 * instead of the total population.
 * @author Chance Simmons and Brandon Townsend
 * @version 21 January 2020
 */
public class OldSpecies {

    /** Static list of colors that are already being used by species. */
    static List<Color> takenColors = new ArrayList<>();

    /**
     * The network other networks will be tested against to see if they are compatible with this
     * species.
     */
    private OldNetwork compatibilityNetwork;

    /** A mapping of agent IDs to their networks. */
    private Map<Integer, OldNetwork> organisms;

    /** The ID number of the best organism in this species this generation. */
    private int bestOrgID;

    /** The highest recorded fitness for this species over all generations. */
    private int bestFitness;

    /** The average fitness of this species this generation. */
    private double averageFitness;

    /** The staleness (number of generations of no improvement). */
    private int staleness;

    /** The color assigned to the agents that belong to this species. */
    private Color color;

    /**
     * Constructor for an organism's species.
     * @param agentID The ID number of the first agent to be assigned to this species.
     * @param agentNetwork The network used by the first agent to be assigned to this species.
     */
    public OldSpecies(int agentID, OldNetwork agentNetwork) {
        organisms = new HashMap<>();
        organisms.put(agentID, compatibilityNetwork);
        bestOrgID = agentID;
        bestFitness = 0;
        averageFitness = 0.0;
        staleness = 0;

        Array<Color> colors = Colors.getColors().values().toArray();
        do {
            // Randomly generate a color till we have a good one.
            color = colors.get(new Random().nextInt(colors.size));
        } while(takenColors.contains(color) && color != Color.CLEAR);
        takenColors.add(color);
    }

    /**
     * Returns the color assigned to this species.
     * @return The color assigned to this species.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Returns the network used to test compatibility with this species.
     * @return The network used to test compatibility with this species.
     */
    public OldNetwork getCompatibilityNetwork() {
        return compatibilityNetwork;
    }

    /**
     * Sets the compatibility network to a random organism that is in this species.
     */
    public void setCompatibilityNetwork() {
        Object[] networks = organisms.values().toArray();
        compatibilityNetwork = null;
    }

    /**
     * Returns the mapping of agent IDs and their networks.
     * @return The mapping of agent IDs and their networks.
     */
    public Map<Integer, OldNetwork> getOrganisms() {
        return organisms;
    }

    /**
     * Adds an organism to the mapping of organisms in this species.
     * @param agentID The ID number of the agent.
     * @param agentNetwork The network the agent uses.
     */
    public void addOrganism(int agentID, OldNetwork agentNetwork) {
        organisms.put(agentID, null);
    }

    /**
     * Returns the ID number mapped to best network this generation.
     * @return The ID number mapped to the best network.
     */
    public int getBestOrgID() {
        return bestOrgID;
    }

    /**
     * Returns the average fitness of this species.
     * @return The average fitness of this species.
     */
    public double getAverageFitness() {
        return averageFitness;
    }

    /**
     * Calculates the average fitness for this species and sets it.
     */
    public void setAverageFitness() {
        double fitnessSum = 0.0;
        for(OldNetwork network : organisms.values()) {
            fitnessSum += network.getFitness();
        }
        if(organisms.isEmpty()) {
            averageFitness = 0;
        } else {
            averageFitness = fitnessSum / organisms.size();
        }
    }

    /**
     * Returns the staleness of this species.
     * @return The staleness of this species.
     */
    public int getStaleness() {
        return staleness;
    }

    /**
     * Increments the staleness of this species if no progress has been made. Otherwise, sets the
     * staleness back to zero.
     */
    public void setStaleness() {
        int generationMaxFitness = -1;
        for(Map.Entry<Integer, OldNetwork> organism : organisms.entrySet()) {
            if(organism.getValue().getFitness() > generationMaxFitness) {
                generationMaxFitness = organism.getValue().getFitness();
                bestOrgID = organism.getKey();
            }
        }
        if(generationMaxFitness > bestFitness) {
            bestFitness = generationMaxFitness;
            staleness = 0;
        } else {
            staleness++;
        }
    }

    /**
     * Takes the top 50% of networks in this species and retains them. Culls the bottom 50% so
     * that they will not pollute the gene pool.
     */
    public void cull() {
        Map<Integer, OldNetwork> survivors = new HashMap<>();

        for(Map.Entry<Integer, OldNetwork> organism : organisms.entrySet()) {
            int maxOrganism = organism.getKey();
            int maxFitness = organism.getValue().getFitness();

            for(Map.Entry<Integer, OldNetwork> other : organisms.entrySet()) {
                int otherOrganism = other.getKey();
                int otherFitness = other.getValue().getFitness();

                if(!survivors.containsKey(otherOrganism) && otherFitness > maxFitness) {
                    maxOrganism = otherOrganism;
                    maxFitness = otherFitness;
                }
            }
            survivors.put(maxOrganism, null);
            organisms.get(maxOrganism).setFitness(-1);

            if(survivors.size() >= organisms.size() * Coefficients.CULL_THRESH.value) {
                break;
            }
        }
        organisms = survivors;
    }

    /**
     * Shares each network's fitness so that it can be normalized and keeps species from becoming
     * too large. This should prevent any one species from taking over the entire population.
     */
    public void shareFitness() {
        for(OldNetwork network : organisms.values()) {
            network.setFitness(network.getFitness() / organisms.size());
        }
    }

    /**
     * Generates a new network either through crossover of two random parent networks or through
     * a direct clone of a random network in the species. The new network is then mutated in the
     * hopes that we find a favorable mutation.
     * @return The new network we have produced and mutated.
     */
    public OldNetwork reproduce() {
        OldNetwork baby;
        if(Math.random() < Coefficients.CROSSOVER_THRESH.value) {
            Object[] networks = organisms.values().toArray();
            OldNetwork parent1 = (OldNetwork) networks[new Random().nextInt(networks.length)];
            OldNetwork parent2 = (OldNetwork) networks[new Random().nextInt(networks.length)];
            if(parent1.getFitness() < parent2.getFitness()) {
                baby = parent2.crossover(parent1);
            } else {
                baby = parent1.crossover(parent2);
            }
        } else {
            Object[] networks = organisms.values().toArray();
            baby = new OldNetwork(12, 12);
        }

        baby.mutate();
        return baby;
    }
}

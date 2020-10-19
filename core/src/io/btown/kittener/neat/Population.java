package io.btown.kittener.neat;

import com.badlogic.gdx.graphics.Color;

import java.util.*;
import java.util.stream.Collectors;

public class Population {
    private final int NUM_AGENTS;
    private int generation;
    private final List<Species> speciesList;
    private List<Network> population;
    private double avgPopFitness;

    public Population(int numAgents, int input, int output) {
        NUM_AGENTS = numAgents;
        generation = 0;
        speciesList = new ArrayList<>();
        population = new ArrayList<>();
        avgPopFitness = 0.0;

        for(int i = 0; i < numAgents; i++) {
            population.add(new Network(input, output));
        }
    }

    public void assignFitness(int index, double fitness) {
        population.get(index).setFitness(fitness);
    }

    public void naturalSelection() {
        // All prints below are used for seeing what happens during speciation every generation. They could be retooled
        // to output to a file if wanted, along with adding outputs for each stage of natural selection.
//        System.out.println("================================================================");
//        System.out.println("Generation: " + generation);
        speciate();
//        System.out.println("Total Number of Species: " + speciesList.size());
        speciesFitnessAndStaleness();
//        speciesList.forEach(species -> {
//            System.out.printf("Species %3d -> Orgs: %3d  Fit:%12.2f  Stale:%2d\n",
//                    species.id, species.getOrganisms().size(), species.getAvgFitness(), species.getStaleness());
//        });
        removeStaleSpecies();
        calcAvgPopFitness();
        cullSpecies();

        // Handle the special case if ALL species are stale.
        if(speciesList.isEmpty()) {
            population = population.stream()
                    .sorted(Comparator.comparingDouble(Network::getFitness).reversed())
                    .collect(Collectors.toList());
            int i = population.size() - 1;
            int numToCull = (int) Math.ceil((i+1) * Coefficients.CULL_THRESH.value);
            for(; i >= numToCull; i--) {
                population.remove(i);
            }
        } else {
            population.clear();
        }

        speciesList.forEach(species -> {
            long numBabies = Math.round(species.getAvgFitness() / avgPopFitness);
            species.reproduce(numBabies);
            population.addAll(species.getOrganisms());
        });

        // Below loops handle the cases in which we go over or under the number of organisms needed.
        while(population.size() > NUM_AGENTS) {
            population.remove(new Random().nextInt(population.size() - 1));
        }
        while(population.size() < NUM_AGENTS) {
            Network grabbed = population.get(new Random().nextInt(population.size() - 1));
            Network clone = new Network(grabbed);
            clone.mutate();
            population.add(clone);
        }
    }

    private void speciate() {
        // Clear each species of its previous list of organisms.
        speciesList.forEach(species -> species.getOrganisms().clear());

        for(Network n : population) {
            boolean found = false;
            for(Species s : speciesList) {
                if(n.isCompatibleTo(s.getCompatibilityNetwork())) {
                    s.addOrganism(n);
                    found = true;
                    break;
                }
            }

            if(!found) {
                speciesList.add(new Species(n));
            }
        }
    }

    private void speciesFitnessAndStaleness() {
        speciesList.forEach(species -> {
            species.shareFitness();
            species.setStaleness();
        });
    }

    private void removeStaleSpecies() {
        List<Species> staleSpecies = speciesList.parallelStream()
                                        .filter(species -> species.getStaleness() >= Coefficients.STALENESS_THRESH.value)
                                        .collect(Collectors.toList());

        staleSpecies.forEach(species -> Species.takenColors.remove(species.getColor()));
        speciesList.removeAll(staleSpecies);
    }

    private void calcAvgPopFitness() {
        avgPopFitness = 0.0;
        speciesList.forEach(species -> avgPopFitness += species.getAvgFitness());
        avgPopFitness /= NUM_AGENTS;
    }

    private void cullSpecies() {
        speciesList.forEach(Species::cullOrganisms);
    }

    public Color getColor(int index) {
        Network organism = population.get(index);

        for(Species species : speciesList) {
            if(organism.isCompatibleTo(species.getCompatibilityNetwork())) return species.getColor();
        }
        return Color.WHITE;
    }

    public double[] getOutput(int index, float[] vision) {
        return population.get(index).feedForward(vision);
    }

    public int getGeneration() {
        return generation;
    }

    public void incrementGeneration() {
        generation++;
    }
}

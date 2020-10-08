package io.btown.kittener.neat;

import com.badlogic.gdx.graphics.Color;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Population {
    private final int NUM_AGENTS;
    private int generation;
    private final List<Species> speciesList;
    private final List<Network> population;
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

        System.out.println("================================================================");
        System.out.println("Generation: " + generation);
        long start = System.currentTimeMillis();
        speciate();
        System.out.println("Finish speciation in " + (System.currentTimeMillis() - start) / 1000 + " second(s).");
        System.out.println("Total Number of Species: " + speciesList.size());
        speciesFitnessAndStaleness();
        speciesList.forEach(species -> {
            System.out.printf("Species %3d -> Orgs:%3d  Fit:%14f  Stale:%2d\n",
                    species.id, species.getOrganisms().size(), species.getAvgFitness(), species.getStaleness());
        });
        removeStaleSpecies();
        calcAvgPopFitness();
        cullSpecies();

        population.clear();
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

        // Check each organisms compatibility with an existing species.
        AtomicReference<Species> maxCompatible = new AtomicReference<>();
        AtomicReference<Double> maxCompatibilityValue = new AtomicReference<>();

        // FIXME: 10/7/20 See if there is a different way to perform this nested loop. It just takes a long time.
        population.forEach(network -> {
            maxCompatible.set(null);
            maxCompatibilityValue.set(Coefficients.COMPAT_THRESH.value);

            speciesList.forEach(species -> {
                double value = network.getCompatibilityValue(species.getCompatibilityNetwork());
                if(value < maxCompatibilityValue.get()) {
                    maxCompatibilityValue.set(value);
                    maxCompatible.set(species);
                }
            });

            if(maxCompatible.get() != null) {
                maxCompatible.get().addOrganism(network);
            } else {
                speciesList.add(new Species(network));
            }
        });
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

        // Handle the case in which all species are stale.
        if(staleSpecies.size() == speciesList.size()) {
            Species bestPerforming = speciesList.stream()
                                                .max(Comparator.comparingDouble(Species::getAvgFitness))
                                                .orElse(null);
            assert bestPerforming != null;
            speciesList.forEach(species -> {
                if(species.getAvgFitness() != bestPerforming.getAvgFitness()) {
                    Species.takenColors.remove(species.getColor());
                    speciesList.remove(species);
                }
            });
        } else { // Otherwise, handle the case in which only some are stale.
            staleSpecies.forEach(species -> Species.takenColors.remove(species.getColor()));
            speciesList.removeAll(staleSpecies);
        }
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
        AtomicReference<Species> maxCompatible = new AtomicReference<>();
        AtomicReference<Double> maxCompatibilityValue = new AtomicReference<>();
        Network organism = population.get(index);

        maxCompatible.set(null);
        maxCompatibilityValue.set(Coefficients.COMPAT_THRESH.value);

        speciesList.forEach(species -> {
            double value = organism.getCompatibilityValue(species.getCompatibilityNetwork());
            if(value <= maxCompatibilityValue.get()) {
                maxCompatibilityValue.set(value);
                maxCompatible.set(species);
            }
        });

        if(maxCompatible.get() == null) {
            return Color.WHITE;
        }
        return maxCompatible.get().getColor();
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

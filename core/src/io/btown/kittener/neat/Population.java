package io.btown.kittener.neat;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Population {
    private int generation;
    private List<Species> speciesList;
    private List<Network> population;

    public Population(int numAgents, int input, int output) {
        generation = 0;
        speciesList = new ArrayList<>();
        population = new ArrayList<>();

        for(int i = 0; i < numAgents; i++) {
            population.add(new Network(input, output));
        }
    }

    public void naturalSelection() {
        speciate();
    }

    private void speciate() {
        // Clear each species of its previous list of organisms.
        speciesList.forEach(species -> species.getOrganisms().clear());

        // Check each organisms compatibility with an existing species.
        AtomicReference<Species> maxCompatible = new AtomicReference<>();
        AtomicReference<Double> maxCompatibilityValue = new AtomicReference<>();

        population.forEach(network -> {
            maxCompatible.set(null);
            maxCompatibilityValue.set(Coefficients.COMPAT_THRESH.value);

            speciesList.forEach(species -> {
                double value = network.getCompatibilityValue(species.getCompatibilityNetwork());
                if(value <= maxCompatibilityValue.get()) {
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

    public Color getColor(int index) {
        AtomicReference<Color> color = new AtomicReference<>();
        speciesList.forEach(species -> {
            if(population.get(index).isCompatibleTo(species.getCompatibilityNetwork())) color.set(species.getColor());
        });

        return color.get();
    }

    public int getGeneration() {
        return generation;
    }

    public void incrementGeneration() {
        generation++;
    }
}

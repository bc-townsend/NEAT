package io.btown.kittener.neat;

import com.badlogic.gdx.graphics.Color;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Species {
    static List<Color> takenColors = new ArrayList<>();
    private Color color;
    private List<Network> organisms;
    private final Network compatibilityNetwork;
    private double avgFitness;
    private double bestAvgFitness;
    private int staleness;

    public Species(Network network) {
        organisms = new ArrayList<>();
        organisms.add(network);
        this.compatibilityNetwork = new Network(network);
        Random random = new Random();
        do {
            float red = 0 + random.nextFloat() * 255;
            float blue = 0 + random.nextFloat() * 255;
            float green = 0 + random.nextFloat() * 255;
            color = new Color(red, blue, green, 1);
        } while(colorAlreadyChosen(color));
        avgFitness = 0.0;
        bestAvgFitness = avgFitness;
        staleness = 0;
    }

    private boolean colorAlreadyChosen(Color chosen) {
        AtomicBoolean alreadyTaken = new AtomicBoolean(false);
        takenColors.forEach(taken -> {
            float diff = Math.abs(taken.r - chosen.r + taken.b - chosen.b + taken.g - chosen.g);
            if(diff <= 30) alreadyTaken.set(true);
        });
        return alreadyTaken.get();
    }

    public void reproduce(int numBabies) {
        List<Network> babies = new ArrayList<>();
        Network baby;
        for(int i = 0; i < numBabies; i++) {
            if(Math.random() < Coefficients.CROSSOVER_THRESH.value) {
                Network parent1 = organisms.get(new Random().nextInt(organisms.size()));
                Network parent2 = organisms.get(new Random().nextInt(organisms.size()));
                baby = parent1.crossover(parent2);
            } else {
                baby = new Network(organisms.get(new Random().nextInt(organisms.size())));
            }
            baby.mutate();
            babies.add(baby);
        }
        organisms.clear();
        organisms.addAll(babies);
    }

    public void shareFitness() {
        organisms.forEach(network -> {
            network.setFitness(network.getFitness() / organisms.size());
            avgFitness += network.getFitness();
        });
    }

    public void setStaleness() {
        if(bestAvgFitness < avgFitness) {
            bestAvgFitness = avgFitness;
            staleness = 0;
        } else {
            staleness++;
        }
    }

    public void cullOrganisms() {
        organisms = organisms.stream().sorted(Comparator.comparingDouble(Network::getFitness))
                                        .collect(Collectors.toList());
        int i = organisms.size() - 1;
        for(; i >= Math.ceil((i+1) * Coefficients.CULL_THRESH.value); i--) {
            organisms.remove(i);
        }
    }

    public void addOrganism(Network network) {
        organisms.add(network);
    }

    public Color getColor() {
        return color;
    }

    public double getAvgFitness() {
        return avgFitness;
    }

    public List<Network> getOrganisms() {
        return organisms;
    }

    public int getStaleness() {
        return staleness;
    }

    public Network getCompatibilityNetwork() {
        return compatibilityNetwork;
    }
}

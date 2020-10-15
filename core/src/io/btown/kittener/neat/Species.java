package io.btown.kittener.neat;

import com.badlogic.gdx.graphics.Color;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Species {
    static int idCounter;
    static List<Color> takenColors = new ArrayList<>();
    static {
        takenColors.add(Color.WHITE);
        idCounter = 0;
    }
    public final int id;
    private Color color;
    private List<Network> organisms;
    private final Network compatibilityNetwork;
    private double avgFitness;
    private double bestAvgFitness;
    private int staleness;

    public Species(Network network) {
        id = idCounter++;
        organisms = new ArrayList<>();
        this.addOrganism(network);
        this.compatibilityNetwork = new Network(network);
        Random random = new Random();
        do {
            float red = random.nextFloat();
            float blue = random.nextFloat();
            float green = random.nextFloat();
            color = new Color(red, blue, green, 1);
        } while(colorAlreadyChosen(color));
        takenColors.add(color);
        avgFitness = 0.0;
        bestAvgFitness = avgFitness;
        staleness = 0;
    }

    private boolean colorAlreadyChosen(Color chosen) {
        AtomicBoolean alreadyTaken = new AtomicBoolean(false);
        takenColors.forEach(taken -> {
            float diff = Math.abs(taken.r - chosen.r + taken.b - chosen.b + taken.g - chosen.g);
            if(diff <= .000001) alreadyTaken.set(true);
        });
        return alreadyTaken.get();
    }

    public void reproduce(long numBabies) {
        List<Network> babies = new ArrayList<>();
        Network baby;

        // Perform direct clone of the best performing organism. Can remove this and add 1 to loop if wanted.
        if(!organisms.isEmpty() && numBabies > 0) {
            babies.add(new Network(organisms.get(0)));
        }

        for(int i = 1; i < numBabies; i++) {
            if(Math.random() < Coefficients.CROSSOVER_THRESH.value) {
                Network parent1 = organisms.get(new Random().nextInt(organisms.size()));
                Network parent2 = organisms.get(new Random().nextInt(organisms.size()));
                baby = parent1.crossover(parent2);
            } else {
                baby = new Network(organisms.get(new Random().nextInt(organisms.size())));
            }
            baby.mutate();
            baby.setFitness(0.0);
            babies.add(baby);
        }
        organisms.clear();
        organisms.addAll(babies);
    }

    public void shareFitness() {
        avgFitness = 0;
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

            // If this species is stale and has no organisms, double the increase of staleness.
            if(organisms.size() == 0) staleness++;
        }
    }

    public void cullOrganisms() {
        organisms = organisms.stream().sorted(Comparator.comparingDouble(Network::getFitness).reversed())
                                        .collect(Collectors.toList());
        int i = organisms.size() - 1;
        int numToCull = (int) Math.ceil((i+1) * Coefficients.CULL_THRESH.value);
        for(; i >= numToCull; i--) {
            organisms.remove(i);
        }
    }

    public void addOrganisms(List<Network> networks) {
        organisms.addAll(networks);
//        networks.forEach(network -> network.setPrevSpecies(this));
    }

    public void addOrganism(Network network) {
        organisms.add(network);
//        network.setPrevSpecies(this);
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

    @Override
    public boolean equals(Object o) {
        if(o instanceof Species) {
            Species s = (Species) o;
            return s.id == this.id;
        }
        return false;
    }
}

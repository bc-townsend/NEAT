package io.btown.kittener.neat;

public class TesterGenotype {
    public static void main(String[] args) throws CloneNotSupportedException {
        int inputs = 2;
        int outputs = 3;
        Genotype g = new Genotype(inputs, outputs);
        g.setFitness(10);
        System.out.println("finished building.");
        Genotype c = new Genotype(g);
        System.out.println("finished cloning.");
//        System.out.println(c.getNode(0));
//        System.out.println(c.getNode(8));
//        System.out.println(c.getNode(4));
//        System.out.println(c.getLink(0));
//        System.out.println(c.getLink(25));
//        System.out.println(c.getLink(4));

        System.out.println("compatible > " + g.isCompatibleTo(c));
        float[] inputValues = {1.0f, 10.0f};
        double[] outputValues = g.feedForward(inputValues);
        for(double val : outputValues) {
            System.out.println(val);
        }
        System.out.println(g.isFullyConnected());
    }
}

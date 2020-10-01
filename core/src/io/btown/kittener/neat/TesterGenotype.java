package io.btown.kittener.neat;

public class TesterGenotype {
    public static void main(String[] args) throws CloneNotSupportedException {
        int inputs = 1;
        int outputs = 2;
        Network g = new Network(inputs, outputs);
        g.setFitness(10);
        System.out.println("finished building.");
        Network c = new Network(g);
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

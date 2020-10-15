package io.btown.kittener.neat;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Network {
    private final List<Node> allNodes;
    private final List<Link> allLinks;
    private static Map<Integer, Link> innovation;
    private int numLayers;
    private final Node biasNode;
    private double fitness;
    private final int numInputs;
    private final int numOutputs;
//    private Species prevSpecies;

    public Network(int inputNum, int outputNum) {
        allNodes = new ArrayList<>();
        allLinks = new ArrayList<>();
        innovation = new HashMap<>();
        numLayers = 0;
        biasNode = new Node(-1, numLayers);
        biasNode.setOutputValue(Coefficients.BIAS_NODE_LINK_WEIGHT.value);
        fitness = 0;
        numInputs = inputNum;
        numOutputs = outputNum;
//        prevSpecies = null;

        for(int i = 0; i < inputNum; i++) {
            Node input = new Node(i, numLayers);
            allNodes.add(input);
        }
        // We add the bias node here due to it needing activation after the input nodes.
        allNodes.add(biasNode);
        numLayers++;

        // Our initial output layer is 1 since it is the layer specifically behind our input.
        // If we add a node in the hidden layer, our output layer should grow.
        for(int i = 0; i < outputNum; i++) {
            Node output = new Node(inputNum + i, numLayers);
            allNodes.add(output);
        }

        // Now, fully connect the network; That is, every input node goes to every output node.
        // Also, the bias node will be connected with an initial weight of 1.
        for(Node input : allNodes) {
            if(input.getLayer() == 0) {
                allNodes.forEach(output -> {
                    if(output.getLayer() == numLayers) {
                        if(input.getId() == biasNode.getId()) {
                            addLink(biasNode, output, 1);
                        } else {
                            addLink(input, output);
                        }
                    }
                });
            }
        }
    }

    public Network(Network network) {
        this.numLayers  = network.numLayers;
        this.fitness    = network.fitness;
        this.numInputs  = network.numInputs;
        this.numOutputs = network.numOutputs;
        this.allNodes   = new ArrayList<>();
        this.allLinks   = new ArrayList<>();
//        this.prevSpecies = network.prevSpecies;

        network.allNodes.forEach(node -> this.allNodes.add(new Node(node)));

        network.allLinks.forEach(link -> {
            Node input = this.getNode(link.getInputNodeID());
            Node output = this.getNode(link.getOutputNode().getId());
            this.addLink(input, output, link.getWeight());
        });

        Optional<Node> bias = this.allNodes.stream()
                .filter(node -> node.getId() == -1)
                .findFirst();

        biasNode = bias.orElse(null);
    }

    public double[] feedForward(float[] inputValues) {
        AtomicInteger index = new AtomicInteger(0);

        // Set the output values of our input nodes to the supplied input values.
        allNodes.stream()
                .filter(node -> node.getId() != biasNode.getId() && node.getLayer() == 0)
                .forEach(node -> node.setOutputValue(inputValues[index.getAndIncrement()]));

        // Activate the nodes in order from input -> bias -> hidden -> output.
        for(index.set(0); index.get() <= numLayers; index.getAndIncrement()) {
            allNodes.stream()
                    .filter(node -> node.getLayer() == index.get())
                    .forEach(Node::activate);
        }

        // Write the output values to a double array to pass back as the decisions of this network.
        double[] outputs = new double[numOutputs];
        index.set(0);
        allNodes.stream()
                .filter(node -> node.getLayer() == numLayers)
                .forEach(node -> outputs[index.getAndIncrement()] = node.getOutputValue());

        // Set all input values back to 0 for the next feed forward.
        allNodes.forEach(node -> node.setInputValue(1));
        return outputs;
    }

    public Network crossover(Network parent) {
        Network baby;
        Network primaryParent;
        Network secondParent;
        if(this.fitness > parent.getFitness()) {
            baby = new Network(this);
            primaryParent = this;
            secondParent = parent;
        } else if(this.fitness < parent.getFitness()) {
            baby = new Network(parent);
            primaryParent = parent;
            secondParent = this;
        } else {
            // Since the fitness is identical, doesn't matter which one we clone. However, it is substantially easier
            // working with the one that has more layers when adding new nodes.
            if(this.numLayers >= parent.numLayers) {
                baby = new Network(this);
                primaryParent = this;
                secondParent = parent;
            } else {
                baby = new Network(parent);
                primaryParent = parent;
                secondParent = this;
            }
            // Add any missing nodes from the secondary parent to the baby to prepare for disjoint links.
            secondParent.allNodes.stream().filter(node -> !primaryParent.allNodes.contains(node))
                                            .forEach(node -> baby.allNodes.add(new Node(node)));

            // Now add any missing disjoint links to the baby.
            secondParent.allLinks.stream().filter(link -> !primaryParent.allLinks.contains(link))
                                            .forEach(link -> {
                                                Node input = baby.getNode(link.getInputNodeID());
                                                Node output = baby.getNode(link.getOutputNode().getId());
                                                baby.addLink(input, output, link.getWeight());
                                            });
        }

        // Randomly select the weight and enabled between all matching links. Since we've already cloned from the
        // primary parent, we just need to determine if they should be selected from the secondary one.
        baby.allLinks.stream()
                    .filter(link -> primaryParent.allLinks.contains(link) && secondParent.allLinks.contains(link))
                    .forEach(link -> {
                        if(Math.random() < 0.5) {
                            Link toCopy = secondParent.getLink(link.getInnovationNum());
                            link.setWeight(toCopy.getWeight());
                            link.setEnabled(toCopy.isEnabled());
                        }
                    });
        
        return baby;
    }

    public void mutate() {
        // Mutation for link weight. Each link is either mutated or not each generation.
        allLinks.forEach(link -> {
            if(Math.random() < Coefficients.LINK_WEIGHT_MUT.value) link.mutateWeight();
        });

        // Toggle on the first link that is disabled.
        if(Math.random() < Coefficients.TOGGLE_LINK_MUT.value) toggleLinkMutation();

        // Add a new link to the network if possible.
        if(Math.random() < Coefficients.ADD_LINK_MUT.value) addLinkMutation();

        // Add a new node to the network if possible.
        if(Math.random() < Coefficients.ADD_NODE_MUT.value) addNodeMutation();
    }

    private void toggleLinkMutation() {
        Optional<Link> link = allLinks.stream().filter(l -> !l.isEnabled())
                                                .findFirst();
        link.ifPresent(value -> value.setEnabled(true));
    }

    private void addLinkMutation() {
        if(!isFullyConnected()) {
            Random random = new Random();

            Node input, output;
            do {
                input = allNodes.get(random.nextInt(allNodes.size()));
                output = allNodes.get(random.nextInt(allNodes.size()));
            } while(isBadLink(input, output));

            if(output.getLayer() < input.getLayer()) {
                Node temp = input;
                input = output;
                output = temp;
            }

            addLink(input, output);
        }
    }

    public boolean isFullyConnected() {
        long maxLinks = 0;

        // Count the amount of links.
        AtomicInteger i = new AtomicInteger(0);
        for(; i.get() <= numLayers; i.getAndIncrement()) {
            maxLinks += allNodes.stream().filter(node -> node.getLayer() == i.get()).count()
                            *
                        allNodes.stream().filter(node -> node.getLayer() > i.get()).count();
        }

        return maxLinks == allLinks.size();
    }

    private boolean isBadLink(Node node1, Node node2) {
        return node1.isConnectedTo(node2) || node1.getLayer() == node2.getLayer();
    }

    private void addNodeMutation() {
        Random random = new Random();
        Link link;
        do {
            link = allLinks.get(random.nextInt(allLinks.size()));
        } while(link.getInputNodeID() == biasNode.getId());

        addNode(link);
    }

    private void addNode(Link link) {
        link.setEnabled(false);
        Node oldInput = getNode(link.getInputNodeID());
        assert oldInput != null;
        int layer = (int) Math.ceil((oldInput.getLayer() + link.getOutputNode().getLayer()) / 2.0);

        // If the layer we're placing our new node was the previous output nodes layer, we
        // move all layers that are equal to or greater than the new layer 'down'.
        if(layer == link.getOutputNode().getLayer()) {
            allNodes.forEach(n -> {if(n.getLayer() >= layer) n.incrementLayer();});
        }
        numLayers++;

        // Actually add the node now so it avoids having it's own layer incremented.
        Node toAdd = new Node(allNodes.size(), layer);
        allNodes.add(toAdd);

        // Now add links to either side of the new node. The link going from the old input to the
        // new node gets a weight of 1 while the link going from the new node to the old output
        // receives the weight of the now-disabled link.
        addLink(oldInput, toAdd, 1);
        addLink(toAdd, link.getOutputNode(), link.getWeight());

        // Finally connect our bias node with a weight of 0 to minimize the bias' initial impact.
        addLink(biasNode, toAdd, 0);
    }

    private void addLink(Node input, Node output, double weight) {
        int inputID = input.getId();
        int outputID = output.getId();

        if(!input.isConnectedTo(output)) {
            int innovationNum = getInnovationNumber(inputID, outputID);
            Link toAdd = new Link(innovationNum, inputID, output, weight);
            if(!innovation.containsKey(innovationNum)) {
                innovation.put(innovationNum, toAdd);
            }
            allLinks.add(toAdd);
            input.getOutgoingLinks().add(toAdd);
        }
    }

    private void addLink(Node input, Node output) {
        int inputID = input.getId();
        int outputID = output.getId();

        if(!input.isConnectedTo(output)) {
            int innovationNum = getInnovationNumber(inputID, outputID);
            Link toAdd = new Link(innovationNum, inputID, output);
            if(!innovation.containsKey(innovationNum)) {
                innovation.put(innovationNum, toAdd);
            }
            allLinks.add(toAdd);
            input.getOutgoingLinks().add(toAdd);
        }
    }

    private int getInnovationNumber(int inputID, int outputID) {
        int innovationNum = innovation.size();

        for(Map.Entry<Integer, Link> i : innovation.entrySet()) {
            int num = i.getKey();
            Link link = i.getValue();

            if(link.getInputNodeID() == inputID && link.getOutputNode().getId() == outputID) {
                innovationNum = num;
                break;
            }
        }

        return innovationNum;
    }

    public double getCompatibilityValue(Network network) {
        double compatibility = 0.0;
        int numDisjoint = getNumDisjointLinks(network);
        double avgWeightDiff = getAvgWeightDiff(network);
        double largestNetworkSize = Math.max(allLinks.size(), network.getAllLinks().size());

        if(largestNetworkSize < 20) {
            largestNetworkSize = 1;
        }

        compatibility += (Coefficients.DISJOINT_CO.value * numDisjoint) / largestNetworkSize;
        compatibility += Coefficients.WEIGHT_CO.value * avgWeightDiff;

        return compatibility;
    }

    public boolean isCompatibleTo(Network network) {
        return getCompatibilityValue(network) <= Coefficients.COMPAT_THRESH.value;
    }

    private int getNumDisjointLinks(Network network) {
        int numDisjoint;
        List<Link> otherNetworkLinks = network.getAllLinks();
        numDisjoint = (int) allLinks.stream()
                                    .filter(link -> !otherNetworkLinks.contains(link))
                                    .count();

        numDisjoint += (int) otherNetworkLinks.stream()
                                                .filter(link -> !allLinks.contains(link))
                                                .count();

        return numDisjoint;
    }

    private double getAvgWeightDiff(Network network) {
        AtomicInteger numMatching = new AtomicInteger();
        AtomicReference<Double> weightSum = new AtomicReference<>(0.0);

        allLinks.forEach(link -> {
            if(network.getAllLinks().contains(link)) {
                Link other = network.getLink(link.getInnovationNum());
                weightSum.updateAndGet(v -> v + Math.abs(link.getWeight() - other.getWeight()));
                numMatching.getAndIncrement();
            }
        });

        if(numMatching.get() == 0) {
            return 100;
        } else if(weightSum.get() == 0) {
            return 0;
        }
        return numMatching.get() / weightSum.get();
    }

    public Node getNode(int id) {
        Optional<Node> node = allNodes.parallelStream()
                                .filter(n -> n.getId() == id)
                                .findFirst();
        return node.orElse(null);
    }

    public Link getLink(int innoNum) {
        Optional<Link> link = allLinks.parallelStream()
                                        .filter(l -> l.getInnovationNum() == innoNum)
                                        .findFirst();
        return link.orElse(null);
    }

    private List<Link> getAllLinks() {
        return allLinks;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

//    public Species getPrevSpecies() {
//        return prevSpecies;
//    }
//
//    public void setPrevSpecies(Species prevSpecies) {
//        this.prevSpecies = prevSpecies;
//    }


    @Override
    public String toString() {
        return String.format("\t# Nodes:%4d  # Links:%5d  Fitness: %f\n", allNodes.size(),
                allLinks.size(), fitness);
    }
}

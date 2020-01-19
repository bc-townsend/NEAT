package neat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Network {
    private static Map<Integer, String> innovationList = new HashMap<>();
    private List<Link> links;
    private Node[] inputNodes;
    private Node[] outputNodes;
    private List<Node> hiddenNodes;
    private Node biasNode;

    public Network(int inputNum, int outputNum) {
        links = new ArrayList<>();
        inputNodes = new Node[inputNum];
        outputNodes = new Node[outputNum];
        hiddenNodes = new ArrayList<>();
        biasNode = new Node(-1, NodeLayer.BIAS);
        biasNode.setOutputValue(1);

        for(int i = 0; i < inputNum; i++) {
            inputNodes[i] = new Node(i, NodeLayer.INPUT);
        }

        for(int i = 0; i < outputNum; i++) {
            outputNodes[i] = new Node(inputNodes.length + i, NodeLayer.OUTPUT);
        }

        generateNetwork();
    }

    private void generateNetwork() {
        for(Node input : inputNodes) {
            for(Node output : outputNodes) {
                // Math.random() * 2 - 1 generates a random number between -1 and 1.
                addLink(input, output, Math.random() * 2 - 1);
            }
        }
        for(Node output : outputNodes) {
            // Link the bias node and apply a random link weight. Comment out if using line below.
            addLink(biasNode, output, Math.random() * 2 - 1);
            // NOTE: If you would like to control the link weight of the bias node, please use
            // the created one in the coefficients enumeration and uncomment the line below.
            // addLink(biasNode, output, Coefficients.BIAS_NODE_LINK_WEIGHT.getValue());
        }
    }

    private void addLink(Node input, Node output, double weight) {
        int inputID = input.getId();
        int outputID = output.getId();

        if(!input.isConnectedTo(output)) {
            links.add(new Link(getInnovationNumber(inputID, outputID), inputID, output, weight));
            input.getOutgoingLinks().add(new Link(getInnovationNumber(inputID, outputID), inputID,
                                                    output, weight));
        }
    }

    private int getInnovationNumber(int inputID, int outputID) {
        int innovationNumber = innovationList.size();
        String innovationSearch = inputID + " " + outputID;
        boolean found = false;
        for(Map.Entry<Integer, String> innovation : innovationList.entrySet()) {
            int key = innovation.getKey();
            String value = innovation.getValue();
            if(value.equals(innovationSearch)) {
                innovationNumber = key;
                found = true;
            }
        }
        if(!found) {
            innovationList.put(innovationNumber, innovationSearch);
        }
        return innovationNumber;
    }

    /**
     * Returns whether or not this network is fully connected. Links that are not enabled are
     * still counted in this total.
     * @return True if this network is fully connected, false otherwise.
     */
    public boolean isFullyConnected() {
        // Adding up the total links from input nodes to their outputs.
        int maxLinks = inputNodes.length * hiddenNodes.size();
        maxLinks += inputNodes.length * outputNodes.length;

        // Adding up the total links from hidden nodes to their outputs.
        for(Node node : hiddenNodes) {
            for (Node otherNode : hiddenNodes) {

                // A hidden node cannot be connected to itself.
                if (!node.equals(otherNode)) {
                    maxLinks++;
                }
            }
        }
        maxLinks += hiddenNodes.size() * outputNodes.length;

        // Adding up the total links from bias nodes to their outputs.
        maxLinks += hiddenNodes.size() + outputNodes.length;

        return maxLinks == links.size();
    }
}

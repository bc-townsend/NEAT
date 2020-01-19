package neat;

import java.util.ArrayList;
import java.util.List;

/**
 * The node class contains all data needed by nodes to connect networks.
 * @author Chance Simmons and Brandon Townsend
 * @version 18 January 2020
 */
public class Node {
    /** The identification number for this node. */
    private int id;

    /** The sum of inputs before the node is activated. */
    private double inputValue;

    /** The value generated after activation. */
    private double outputValue;

    /** List of all outgoing links. */
    private List<Link> outgoingLinks;

    /** The layer this node resides in. */
    private NodeLayer layer;

    /**
     * Constructor for a node. Takes an identification number and layer for this node.
     * @param id The supplied identification number.
     * @param layer The supplied layer this node should reside in.
     */
    public Node(int id, NodeLayer layer) {
        this.id = id;
        this.inputValue = 0.0;
        this.outputValue = 0.0;
        this.outgoingLinks = new ArrayList<>();
        this.layer = layer;
    }

    /**
     * Returns this nodes identification number.
     * @return This nodes identification number.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns this nodes value before activation.
     * @return This nodes value before activation.
     */
    public double getInputValue() {
        return inputValue;
    }

    /**
     * Sets this nodes input value to the supplied one.
     * @param inputValue The supplied input value.
     */
    public void setInputValue(double inputValue) {
        this.inputValue = inputValue;
    }

    /**
     * Sets this nodes output value to the supplied one.
     * @param outputValue The supplied output value.
     */
    public void setOutputValue(double outputValue) {
        this.outputValue = outputValue;
    }

    /**
     * Returns this nodes outgoing links.
     * @return This nodes outgoing links.
     */
    public List<Link> getOutgoingLinks() {
        return outgoingLinks;
    }

    /**
     * Returns the layer this node resides on.
     * @return The layer this node resides on.
     */
    public NodeLayer getLayer() {
        return layer;
    }

    /**
     * Activates the node. Calls the activation function if this node is not on the bias or input
     * layer. Then, grabs each of the connected output nodes and send something to that output
     * nodes input.
     */
    public void activate() {
        if(layer != NodeLayer.BIAS && layer != NodeLayer.INPUT) {
            outputValue = activationFunction(inputValue);
        }

        for(Link link : outgoingLinks) {
            if(link.isEnabled()) {
                Node outputNode = link.getOutputNode();
                double oldInputValue = outputNode.getInputValue();
                outputNode.setInputValue(oldInputValue + link.getWeight() * outputValue);
            }
        }
    }

    /**
     * Helper function to call the activation function. Right now, it is a sigmoid function.
     * @param value The value to call the function on.
     * @return The value after the function has finished.
     */
    private double activationFunction(double value) {
        return 1.0 / (1.0 + Math.pow(Math.E, (-1 * value)));
    }

    /**
     * Checks to see whether this node is connected to the supplied node.
     * @param node The supplied node to check connection with.
     * @return True if the nodes are connected, false otherwise.
     */
    public boolean isConnectedTo(Node node) {
        if(layer == NodeLayer.INPUT || layer == NodeLayer.HIDDEN || layer == NodeLayer.BIAS) {
            for(Link link : outgoingLinks) {
                if(link.getOutputNode().equals(node)) {
                    return true;
                }
            }
        }

        if(node.getLayer() == NodeLayer.INPUT || node.getLayer() == NodeLayer.HIDDEN || node.getLayer() == NodeLayer.BIAS) {
            for(Link link : node.getOutgoingLinks()) {
                if(link.getOutputNode().equals(this)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns whether or not this node's id number is the same as the supplied object.
     * @param obj The supplied object.
     * @return True if the supplied objects id number is the same as this nodes.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Node) {
            Node other = (Node) obj;
            return id == other.getId();
        }
        return false;
    }
}

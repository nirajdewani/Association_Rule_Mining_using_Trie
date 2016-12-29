import java.util.HashMap;
import java.util.LinkedHashMap;

class Node {

    public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public HashMap<String, Node> getChildren() {
		return children;
	}

	public void setChildren(LinkedHashMap<String, Node> children) {
		this.children = children;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	String attributeValue;
    String attributeName;
    LinkedHashMap<String, Node> children = new LinkedHashMap<String, Node>();
    Node parent;
    boolean isLeaf = false;
    int frequency;
 
    public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public Node() {}
 
    public Node(String attributeName, String attributeValue, Node parent)
    {
    	this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.parent = parent;
        this.isLeaf = false;
    }
}

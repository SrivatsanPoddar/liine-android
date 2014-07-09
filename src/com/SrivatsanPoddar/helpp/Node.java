package com.SrivatsanPoddar.helpp;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Node implements Serializable{

	private int node_id=3;
	private int parent_node_id;
	private String display_text;
	private String phone_number;
	private ArrayList<Node> children;
	
	public Node(int nodeID, int parentNodeID, String displayText, String phoneNumber) {
		node_id = nodeID;
		parent_node_id = parentNodeID;
		display_text = displayText;
		phone_number = phoneNumber;
		children = new ArrayList<Node>();
	}
	
	public void initChildren()
	{
		children = new ArrayList<Node>();
	}
	
	public Node[] getChildren() {
        return children.toArray(new Node[children.size()]);
    }
	
	public void addChild(Node child)
	{
		assert children != null;
		children.add(child);
	}
	
	public int getNodeId(){
		return node_id;
	}
	
	public int getParentNodeId() {
		return parent_node_id;
	}
	
	@Override
	public String toString() {
		return display_text;
	}

	public String getPhoneNumber() {
		return phone_number;
	}
}
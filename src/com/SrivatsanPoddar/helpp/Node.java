package com.SrivatsanPoddar.helpp;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Node implements Serializable{

	private int node_id=3;
	private int parent_node_id;
	private String display_text;
	private ArrayList<Node> children;
	
	public Node(int nodeID, int parentNodeID, String displayText) {
		node_id = nodeID;
		parent_node_id = parentNodeID;
		display_text = displayText;
	}
	
	public Node[] getChildren() {
        return (Node[]) children.toArray();
    }
	
	public void addChild(Node child)
	{
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
}

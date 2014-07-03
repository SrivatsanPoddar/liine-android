package com.SrivatsanPoddar.helpp;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Node implements Serializable{

	private int node_id=3;
	private int parent_node_id;
	private String display_text;
	
	public Node(int nodeID, int parentNodeID, String displayText) {
		node_id = nodeID;
		parent_node_id = parentNodeID;
		display_text = displayText;
	}
	
	public Node[] getChildren() {
        Node[] childrenNodes={new Node(node_id*2,node_id,"Support"),new Node(node_id*2+1,node_id,"Add Service"),new Node(node_id*2+2,node_id,"Remove Services")};
        return childrenNodes;
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

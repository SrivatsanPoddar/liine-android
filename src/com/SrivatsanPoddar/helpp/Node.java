package com.SrivatsanPoddar.helpp;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Node implements Serializable{

	private int nodeId=3;
	private int parentNodeId;
	private String displayText;
	
	public Node(int node_id, int parent_node_id, String display_text) {
		nodeId = node_id;
		parentNodeId = parent_node_id;
		displayText = display_text;
	}
	
	public Node[] getChildren() {
        Node[] childrenNodes={new Node(nodeId*2,nodeId,"Support"),new Node(nodeId*2+1,nodeId,"Add Service"),new Node(nodeId*2+2,nodeId,"Remove Services")};
        return childrenNodes;
    }
	
	public int getNodeId(){
		return nodeId;
	}
	
	public int getParentNodeId() {
		return parentNodeId;
	}
	
	@Override
	public String toString() {
		return displayText;
	}
}

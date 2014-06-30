package com.SrivatsanPoddar.helpp;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Node implements Serializable{

	private int nodeId=3;
	private int parentNodeId;
	private String displayText;
	private int nodeType;
//	private static final int PARENT = 1;
//	private static final int PHONE_NUMBER = 2;
	private String phoneNumber;
	
	public Node[] getChildren() {
		Node[] childrenNodes={new Node(nodeId*2,nodeId,"Support","6098510053"),new Node(nodeId*2+1,nodeId,"Add Service","6098510053"),new Node(nodeId*2+2,nodeId,"Remove Services","6098510053")};
		return childrenNodes;
	}
	
	public Node(int node_id, int parent_node_id, String display_text) {
		nodeId = node_id;
		parentNodeId = parent_node_id;
		displayText = display_text;
		//nodeType = node_type;		
	}
	
	public Node(int node_id, int parent_node_id, String display_text, String phone_number) {
		nodeId = node_id;
		parentNodeId = parent_node_id;
		displayText = display_text;
		//nodeType = node_type;		
		phoneNumber = phone_number;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public int getNodeId(){
		return nodeId;
	}
	
	public int getParentNodeId() {
		return parentNodeId;
	}
	
	public String toString() {
		return displayText;
	}
	
}

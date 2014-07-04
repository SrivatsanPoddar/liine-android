package com.SrivatsanPoddar.helpp;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.*;

public class SearchActivity extends Activity{
	
	public Node root;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		//Allow us to use internet
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		//Get the nodes from Heroku
		Node[] nodes = new Node[1];
		RestAdapter restAdapter = new RestAdapter.Builder().
				setEndpoint("http://safe-hollows-9286.herokuapp.com").
				build();
		HerokuService herokuService = restAdapter.create(HerokuService.class);
		try
		{
			nodes = herokuService.nodes();
		}
		catch(RetrofitError e)
		{
			nodes[0] = new Node(0, 0, e.getCause().toString());
		}
		
		//Create our tree
		root = new Node(0, 0, "Root");
		for(Node n : nodes)
		{
			if(n.getParentNodeId() == 0)
			{
				root.addChild(n);
			}
			else
			{
				nodes[n.getParentNodeId() - 1].addChild(n);
			}
		}
		
		Bundle extras = this.getIntent().getExtras();
		if (extras != null) {

			Node chosenNode = (Node) extras.getSerializable("chosenNode");
			nodes = chosenNode.getChildren();
		}

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		
	}


	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends ListFragment{

		Node[] fragNodes;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			SearchActivity act = (SearchActivity) getActivity();
			fragNodes = act.root.getChildren();
			ArrayAdapter<Node> adapter = new ArrayAdapter<Node>(getActivity(),android.R.layout.simple_list_item_1,fragNodes);
			setListAdapter(adapter);
		}

		@Override
	    public void onListItemClick(ListView l, View v, int position, long id) {
	        // TODO Auto-generated method stub
	        super.onListItemClick(l, v, position, id);
	        Node chosenNode = fragNodes[position];
			Log.e("Reached", position + "");
			//Node[] childrenOfChosenNode = chosenNode.childrenNodes;
		    Intent intent = new Intent(getActivity(), SearchActivity.class);
		    intent.putExtra("chosenNode",chosenNode);
		    this.startActivity(intent);
	    }
	}
}

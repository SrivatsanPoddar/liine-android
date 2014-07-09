
package com.SrivatsanPoddar.helpp;

import java.io.FileOutputStream;
import java.util.Locale;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.*;

@SuppressWarnings("unused")
public class SearchActivity extends Activity{
	
	public Node[] nodes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		//Allow us to use internet (This is bad practice--we should make an async call -ppod) 
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();  
		StrictMode.setThreadPolicy(policy); 
		
		//Get the nodes from Heroku
		Node[] tempNodes = new Node[1];
		RestAdapter restAdapter = new RestAdapter.Builder().
				setEndpoint("http://safe-hollows-9286.herokuapp.com").
				build();
		
		HerokuService herokuService = restAdapter.create(HerokuService.class);
		try  //We should make this asynchronous -ppod
		{
			tempNodes = herokuService.nodes();
		}
		catch(RetrofitError e)
		{
			tempNodes[0] = new Node(0, 0, e.getCause().toString(), null);
		}
		
		//No idea why we need this but we do
		for(Node n : tempNodes)
		{
			n.initChildren();
		}
		
		//Create our tree
		Node root = new Node(0, 0, "Root", null);
		for(Node n : tempNodes)
		{
			if(n.getParentNodeId() == 0)
			{
				root.addChild(n);
			}
			else
			{
				tempNodes[n.getParentNodeId() - 1].addChild(n);  //This only works if the node ID matches with the ordering of the returned node[], right?
			}
		}
		
		nodes = root.getChildren();
		
		Bundle extras = this.getIntent().getExtras();
		
		//Check if this activity was started clicking of non-root node. If so, find and display children of that node
		if (extras != null) {

			Node chosenNode = (Node) extras.getSerializable("chosenNode");
			nodes = chosenNode.getChildren();
		}


		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.frame_layout, new PlaceholderFragment()).commit();
		}
	}


	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends ListFragment{

		Node[] fragNodes;
		boolean endActionInitiated = false;   //Flag to denote that an 'end action' has been reached i.e. phone call in order to trigger survey
		EditText searchText;
		ArrayAdapter<Node> adapter;
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			SearchActivity act = (SearchActivity) getActivity();
			fragNodes = act.nodes;
			adapter = new ArrayAdapter<Node>(getActivity(),android.R.layout.simple_list_item_1,fragNodes);
			setListAdapter(adapter);
			
			
			//Implement Search Functionality
	        searchText = (EditText) getActivity().findViewById(R.id.search_text);
	        searchText.addTextChangedListener(new TextWatcher() {
	 
	            @Override
	            public void afterTextChanged(Editable arg0) {
	                String text = searchText.getText().toString().toLowerCase(Locale.getDefault());
	                adapter.getFilter().filter(text);
	            }
	 
	            @Override
	            public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {
	            }
	 
	            @Override
	            public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
	            }
	        });
	        
		}

		@Override
		public void onResume() {
			super.onResume();
			
			//If the fragment restarts after an end action was performed, then start the survey activity
			if (endActionInitiated) {
			    Intent intent = new Intent(getActivity(), SurveyActivity.class);
			    int store_id = 1;  //replace this with the store_id of the associated node
			    intent.putExtra("store_id",store_id);
			    this.startActivity(intent);
			}
			
		}
		
		@Override
	    public void onListItemClick(ListView l, View v, int position, long id) {
	        // TODO Auto-generated method stub
	        super.onListItemClick(l, v, position, id);
	        Node chosenNode = fragNodes[position];
			Log.e("Reached", position + "");
			//Node[] childrenOfChosenNode = chosenNode.childrenNodes;
			String chosenPhoneNumber = chosenNode.getPhoneNumber();
			if (chosenPhoneNumber == null) {
			    Intent intent = new Intent(getActivity(), SearchActivity.class);
			    intent.putExtra("chosenNode",chosenNode);
			    this.startActivity(intent);
			}
			else {
			    Intent intent = new Intent(Intent.ACTION_CALL);
			    intent.setData(Uri.parse("tel:" + chosenPhoneNumber));
			    endActionInitiated = true;
			    startActivity(intent);
			}
	    }
	}
}

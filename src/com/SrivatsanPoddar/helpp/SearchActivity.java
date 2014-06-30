package com.SrivatsanPoddar.helpp;


import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.os.Build;

public class SearchActivity extends Activity{
	
	
	public Node[] nodes = {new Node(1,0,"Comcast"), new Node(1,0,"Verizon")};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		Bundle extras = this.getIntent().getExtras();
		if (extras != null) {

			Node chosenNode = (Node) extras.getSerializable("chosenNode");
			nodes = chosenNode.getChildren();
		}

		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}

		
		
//		Node comcastSupport = new Node(3,1,"Support");
//		Node comcastNewService = new Node(4,1,"Add New Services");
//		Node comcastNewServicePhoneNumber = new Node(5,4,"6098510053");
//		Node comcastSupportPhoneNumber = new Node(5,4,"6098510052");
//		Node verizonNewPhoneLine = new Node(4,1,"Add Phone Line");
//		Node verizonNewPhoneLineNumber = new Node(5,4,"6097160816");
		
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
			fragNodes = act.nodes;
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
			String chosenPhoneNumber = chosenNode.getPhoneNumber();
			if (chosenPhoneNumber == null) {
			    Intent intent = new Intent(getActivity(), SearchActivity.class);
			    intent.putExtra("chosenNode",chosenNode);
			    this.startActivity(intent);
			}
			else {
			    Intent intent = new Intent(Intent.ACTION_CALL);
			    intent.setData(Uri.parse("tel:" + chosenPhoneNumber));
			    startActivity(intent);
			}
	    }
		

		
		

	}

}

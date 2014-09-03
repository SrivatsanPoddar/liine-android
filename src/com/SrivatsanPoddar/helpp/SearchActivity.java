package com.SrivatsanPoddar.helpp;

import android.graphics.Color;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.*;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

@SuppressWarnings("unused")
public class SearchActivity extends Activity
{
    public Node[] nodes;
    Bundle state;
    private ActionBar actionBar;
    private ArrayList<Node> path = new ArrayList<Node>();
    private static final String TAG = "SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        state = savedInstanceState;
        Bundle extras = this.getIntent().getExtras();
        
        setContentView(R.layout.activity_search);
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);        
        EditText searchText = (EditText) findViewById(R.id.search_text);
        Style.toOpenSans(this, searchText, "light");
        
        // Check if this activity was started clicking of non-root node. If so,
        // find and display children of that node
        if (extras != null)
        {            
            // Hide search bar since we're in a tree
            searchText.setVisibility(View.GONE);
            
            Node chosenNode = (Node) extras.getSerializable("chosenNode");
            path = (ArrayList<Node>) extras.getSerializable("path");
            nodes = chosenNode.getChildren();
            path.add(chosenNode);  //Add chosen node to path of traveled nodes
            if (state == null)
            {
                getFragmentManager().beginTransaction()
                        .add(R.id.frame_layout, new PlaceholderFragment()).commit();
            }
        }
        // Otherwise we are coming from splash loader
        else
        {
            nodes = Splash.loadedNodes;
            if (state == null)
            {
                getFragmentManager().beginTransaction()
                        .add(R.id.frame_layout, new PlaceholderFragment()).commit();
            }
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        Log.e("Menu Item Id", item.getItemId()+"");
        
        switch (item.getItemId()) {
            case android.R.id.home:
                //Do stuff
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    
    
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends ListFragment
    {

        Node[] fragNodes;
        boolean endActionInitiated = false; // Flag to denote that an 'end action' has been reached i.e.
                                            // phone call in order to trigger survey
        EditText searchText;
        CustomListAdapter<Node> adapter;
        Node chosenNode;
        SearchActivity act = null;
        
        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);
            act = (SearchActivity) getActivity();
            fragNodes = act.nodes;
            
            adapter = new CustomListAdapter<Node>(getActivity(),R.layout.search_list_row,fragNodes);
            setListAdapter(adapter);
            
            // TODO If coming from call, add that company to favorites
            
            // Mark favorites
            /** SharedPreferences prefs = act.getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
            int favorites = prefs.getInt("numFavorites", 0);
            for(int i = 0; i < getListView().getLastVisiblePosition(); i++)
            {
                getListView().getChildAt(i).setBackgroundResource(R.drawable.abc_list_selector_background_transition_holo_light);
            } **/
            
            // Set up long click (favorites) listener
            getListView().setOnItemLongClickListener(new OnItemLongClickListener()
            {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id)
                {
                    // Set up shared preferences
                    SharedPreferences prefs = act.getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
                    int numFavorites = prefs.getInt("numFavorites", 0);
                    
                    Style.makeToast(act, fragNodes[position] + " added to Favorites");
                    Log.e("New Favorite", numFavorites + 1 + " total");
                    Node temp = fragNodes[position];
                    fragNodes[position] = fragNodes[numFavorites];
                    fragNodes[numFavorites] = temp;
                    prefs.edit().putInt("numFavorites", numFavorites + 1).apply();
                    
                    // Set the item color to mark it favorite
                    // view.setBackgroundResource(R.drawable.abc_list_selector_background_transition_holo_light);
                    
                    return true;
                }
            });
            
            // Set up google search listener
            final Button button = (Button) getActivity().findViewById(R.id.search_button);
            button.setVisibility(View.GONE);
            button.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);   
                    intent.putExtra(SearchManager.QUERY, searchText.getText().toString() + " customer support");    
                    startActivity(intent);
                }
            });

            // Implement Search Functionality
            searchText = (EditText) getActivity()
                    .findViewById(R.id.search_text);
            searchText.addTextChangedListener(new TextWatcher()
            {

                @Override
                public void afterTextChanged(Editable arg0)
                {
                    String text = searchText.getText().toString()
                            .toLowerCase(Locale.getDefault());
                    adapter.getFilter().filter(text);
                    
                    //Show or hide button
                    if(!searchText.getText().toString().equals(""))
                    {
                        button.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        button.setVisibility(View.GONE);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1,
                        int arg2, int arg3)
                {
                }

                @Override
                public void onTextChanged(CharSequence arg0, int arg1,
                        int arg2, int arg3)
                {
                }
            });
        }

        @Override
        public void onResume()
        {
            super.onResume();

            // If the fragment restarts after an end action was performed,
            // then start the survey activity
            if (endActionInitiated)
            {
                Intent intent = new Intent(getActivity(), SurveyActivity.class);

                intent.putExtra("company_id", chosenNode.getCompanyId());
                this.startActivity(intent);
            }
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id)
        {
            super.onListItemClick(l, v, position, id);
            chosenNode = (Node) getListView().getItemAtPosition(position);
            Log.e("Reached", position + "");
            
            String chosenPhoneNumber = PhoneNumberUtils.stripSeparators(chosenNode.getPhoneNumber());
            if (chosenPhoneNumber == null)
            {                
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("chosenNode", chosenNode);
                intent.putExtra("path", ((SearchActivity)getActivity()).path);
                this.startActivity(intent);
            }
            else
            {
                // TODO Add this one to favorites
                
                String stringPath = "";
                
                //Create string representing the path of the chosen nodes
                for (Node n : ((SearchActivity) getActivity()).path) {
                    stringPath = stringPath + " --> " + n.toString();
                }
                stringPath = stringPath + " --> " + chosenNode.toString();
                stringPath = stringPath.substring(4);  //Cut-off initial arrow from string display

                //Intent intent = new Intent(Intent.ACTION_CALL);
                //Intent intent = new Intent(getActivity(), PhoneActivity.class);
                Intent intent = new Intent(getActivity(), TwilioActivity.class);
                intent.putExtra("phone_number", chosenPhoneNumber);
                intent.putExtra("company_id", chosenNode.getCompanyId());
                intent.putExtra("string_path", stringPath);
                //intent.setData(Uri.parse("tel:" + chosenPhoneNumber));
                endActionInitiated = true;

                startActivity(intent);
            }
        }
    }
}

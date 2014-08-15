package com.SrivatsanPoddar.helpp;

import android.graphics.Color;

import java.io.FileOutputStream;
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
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.*;

@SuppressWarnings("unused")
public class SearchActivity extends Activity implements Callback<Node[]>
{
    public Node[] nodes;
    private Node[] tempNodes;
    private HerokuService nodeService;
    Bundle state;
    private ActionBar actionBar;
    private long startTime;
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.setTheme(R.style.CustomActionBarTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        state = savedInstanceState;
        Bundle extras = this.getIntent().getExtras();
        
        //Calculate passage of time to ensure splash screen displayed for 2s
        startTime = System.currentTimeMillis();
        
        // Check if this activity was started clicking of non-root node. If so,
        // find and display children of that node
        if (extras != null)
        {
            setContentView(R.layout.activity_search);
            final ActionBar actionBar = getActionBar();
            actionBar.setHomeButtonEnabled(true);        
            EditText searchText = (EditText) findViewById(R.id.search_text);
            Style.toOpenSans(this, searchText, "light");
            
            // Hide search bar since we're in a tree
            searchText.setVisibility(View.GONE);
            
            Node chosenNode = (Node) extras.getSerializable("chosenNode");
            nodes = chosenNode.getChildren();
            if (state == null)
            {
                getFragmentManager().beginTransaction()
                        .add(R.id.frame_layout, new PlaceholderFragment()).commit();
            }
        }
        // Otherwise grab the nodes from Heroku and create the tree
        else
        {
            // Get the nodes from Heroku
            RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://safe-hollows-9286.herokuapp.com")
                .build();
            nodeService = restAdapter.create(HerokuService.class);       
            nodeService.nodes(this);
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
    
    @Override
    public void failure(RetrofitError arg0)
    {
        Log.e("Error retreiving nodes from database:", arg0.toString());
    }

    @Override
    public void success(Node[] arg0, Response arg1)
    {        
        Log.e("Success retrieving nodes from database:", Arrays.toString(arg0));
        tempNodes = arg0;
        
        // We use a hashtable as well to avoid indexing issues
        Hashtable<Integer, Node> nodeHash = new Hashtable<Integer, Node>();
        for (Node n : tempNodes)
        {
            // Initialize child lists
            n.initChildren();
            // Insert into hashtable
            nodeHash.put(n.getNodeId(), n);
        }

        // Create our tree
        Node root = new Node(0, 0, "Root", null,null);
        for (Node n : tempNodes)
        {
            if (n.getParentNodeId() == 0)
            {
                root.addChild(n);
            }
            else
            {
                // Use the hashtable to get the parents
                nodeHash.get(n.getParentNodeId()).addChild(n);
            }
        }

        // Start with the topmost children
        nodes = root.getChildren();
        
        if (state == null)
        {
            getFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, new PlaceholderFragment()).commit();
        }
        
        //See how much longer to show splash screen
        long loadTime = System.currentTimeMillis() - startTime;
        if(loadTime < SPLASH_DISPLAY_LENGTH)
        {
            try
            {
                Thread.sleep(SPLASH_DISPLAY_LENGTH - loadTime);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        setContentView(R.layout.activity_search);
        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);        
        EditText searchText = (EditText) findViewById(R.id.search_text);
        Style.toOpenSans(this, searchText, "light");
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends ListFragment
    {

        Node[] fragNodes;
        boolean endActionInitiated = false; // Flag to denote that an 'end
                                            // action' has been reached i.e.
                                            // phone call in order to trigger
                                            // survey
        EditText searchText;
        CustomListAdapter<Node> adapter;
        Node chosenNode;
        
        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);
            SearchActivity act = (SearchActivity) getActivity();
            fragNodes = act.nodes;
            
            adapter = new CustomListAdapter(getActivity(),android.R.layout.simple_list_item_1,fragNodes);
            //aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //promotionsList.setAdapter(aa);
            setListAdapter(adapter);
            
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
            
            //chosenNode = fragNodes[position];
            Log.e("Reached", position + "");
            // Node[] childrenOfChosenNode = chosenNode.childrenNodes;
            String chosenPhoneNumber = PhoneNumberUtils.stripSeparators(chosenNode.getPhoneNumber());
            if (chosenPhoneNumber == null)
            {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("chosenNode", chosenNode);
                this.startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + chosenPhoneNumber));
                endActionInitiated = true;
                startActivity(intent);
            }
        }
    }
}
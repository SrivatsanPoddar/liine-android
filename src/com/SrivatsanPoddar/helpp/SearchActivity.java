package com.SrivatsanPoddar.helpp;

import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.*;

@SuppressWarnings("unused")
public class SearchActivity extends Activity
{

    public Node[] nodes;
    private Node[] tempNodes;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Bundle extras = this.getIntent().getExtras();

        // Check if this activity was started clicking of non-root node. If so,
        // find and display children of that node
        if (extras != null)
        {
            Node chosenNode = (Node) extras.getSerializable("chosenNode");
            nodes = chosenNode.getChildren();
        }
        // Otherwise grab the nodes from Heroku and create the tree
        else
        {
            //Get the nodes from heroku, and block until we get them
            final CountDownLatch latch = new CountDownLatch(1);
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        // Get the nodes from Heroku
                        RestAdapter restAdapter = new RestAdapter.Builder()
                                .setEndpoint("http://safe-hollows-9286.herokuapp.com")
                                .build();
                        HerokuService herokuService = restAdapter.create(HerokuService.class);
                        tempNodes = herokuService.nodes();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    latch.countDown();
                }
            });
            thread.start();
            try
            {
                latch.await();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

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
        }

        if (savedInstanceState == null)
        {
            getFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, new PlaceholderFragment()).commit();
        }
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
        ArrayAdapter<Node> adapter;
        Node chosenNode;
        
        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);
            SearchActivity act = (SearchActivity) getActivity();
            fragNodes = act.nodes;
            adapter = new ArrayAdapter<Node>(getActivity(),
                    android.R.layout.simple_list_item_1, fragNodes);
            setListAdapter(adapter);

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
            chosenNode = fragNodes[position];
            Log.e("Reached", position + "");
            // Node[] childrenOfChosenNode = chosenNode.childrenNodes;
            String chosenPhoneNumber = chosenNode.getPhoneNumber();
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

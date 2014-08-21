package com.SrivatsanPoddar.helpp;

import com.google.gson.Gson;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TwilioActivity extends Activity implements View.OnClickListener
{
    private TwilioPhone phone;
    private EditText numberField;
    private String company_id;
    private static final WebSocketConnection mConnection = new WebSocketConnection();
    protected static final String TAG = "Twilio Activity";
    private Gson gson = new Gson();
    private String pairsIndex;
    TextView instructionField;
    private Handler mHandler; 
    private LinearLayout mainLayout;
    
    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.activity_twilio);
        
        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true); 
        
        Bundle extras = this.getIntent().getExtras();
        company_id = extras.getString("company_id");
        String stringPath = extras.getString("string_path");
        ChatMessage m = new ChatMessage(stringPath);
        m.setTargetCompany(company_id);
        
        String JSONMessage = this.gson.toJson(m);
        //((SearchActivity) getActivity()).mConnection.sendTextMessage(JSONMessage);
        this.start(JSONMessage);
        
        mainLayout = (LinearLayout) findViewById(R.id.twilio_layout);
        
        phone = new TwilioPhone(getApplicationContext(), company_id);
        //phone.connect(company_id);
//        ImageButton dialButton = (ImageButton)findViewById(R.id.dialButton);
//        dialButton.setOnClickListener(this);
 
        ImageButton hangupButton = (ImageButton)findViewById(R.id.hangupButton);
        hangupButton.setOnClickListener(this);
        
        ImageButton sendDigit = (ImageButton)findViewById(R.id.sendDigit);
        sendDigit.setOnClickListener(this);
        
// 
//        numberField = (EditText)findViewById(R.id.numberField);
//        instructionField = (TextView) findViewById(R.id.live_instructions);
//        
//        Button sendMessage = (Button) findViewById(R.id.send_message);
//        sendMessage.setOnClickListener(this);
        
    }
 
    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.dialButton)
            phone.connect(company_id);
        else if (view.getId() == R.id.hangupButton)
            phone.disconnect();
        else if (view.getId() == R.id.sendDigit) {
            Log.e("Send digit pushed","woo");
            phone.sendDigit();
        }
//        else if (view.getId() == R.id.send_message) {
//            String messageToSend = numberField.getText().toString();
//            ChatMessage m = new ChatMessage(messageToSend,pairsIndex);
//            String JSONMessage = gson.toJson(m);
//            mConnection.sendTextMessage(JSONMessage);
//        }
    }
    
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(pingServer);
        mConnection.disconnect();
    }
    
    Runnable pingServer = new Runnable() {
        
        public void run () {
            ChatMessage m = new ChatMessage("Ping from android");
            String JSONMessage = gson.toJson(m);
            mConnection.sendTextMessage(JSONMessage);
            mHandler.postDelayed(pingServer, 30000);
        }
    };
    
    /*
     * Initiates a web-socket connection to send the node path to company
     */
    private void start(final String initialMessage) {

        final String wsuri = "ws://safe-hollows-9286.herokuapp.com/live";

        try {
           mConnection.connect(wsuri, new WebSocketHandler() {

              @Override
              public void onOpen() {
                 Log.d(TAG, "Status: Connected to " + wsuri);
//                 ChatMessage m = new ChatMessage();
//                 m.setString("Hello Bob!");
//                 
//                 String JSONMessage = gson.toJson(m);
                 mConnection.sendTextMessage(initialMessage);
                 Log.e("Sending initial message to target_id of:",  initialMessage);
                 //CLOSE WEB SOCKET
                 //mConnection.disconnect();
                 mHandler = new Handler();
                 pingServer.run();

              }

              @Override
              public void onTextMessage(String payload) {
                 Log.d(TAG, "Got echo: " + payload);
                 ChatMessage m = gson.fromJson(payload, ChatMessage.class);
                 
                 //If just paired, then set pairsIndex
                 if(m.pair != null) {
                     pairsIndex = m.pairsIndex;
                     Log.e("Pairing Request Received with pairsIndex:", pairsIndex + "");
                 }
                 
//                 if(m.message != null) {
//                     instructionField.setText(m.message);
//                 }
                 
                 
                 //If String information was requested from caller, then add UI to give info and pre-fill with Shared Preferences if it exists
                 if (m.request_format != null && m.request_format.equals("edit_text")) {

                     TwilioActivity.this.addEditText(m);  //Add instructions, text field and button UI
   
                 }
                 
                 
              }

              @Override
              public void onClose(int code, String reason) {
                 mHandler.removeCallbacks(pingServer);
                 Log.d(TAG, "Connection lost.");
              }
           });
        } catch (WebSocketException e) {

           Log.d(TAG, e.toString());
        }
     }
    
    public void addEditText(ChatMessage m) {
        
        
        //Retrieve shared preferences
        SharedPreferences prefs = TwilioActivity.this.getApplicationContext().getSharedPreferences("com.SrivatsanPoddar.helpp", Context.MODE_PRIVATE);
        final String preferenceKey = "com.SrivatsanPoddar.helpp." + m.request_type;
        String preference = prefs.getString(preferenceKey, "");
        Log.e("Retrieved preference: " + preference + " for preference key: ", preferenceKey);
        
        
        //Log.e("Adding view","WOO");
        final LinearLayout toAdd = new LinearLayout(TwilioActivity.this);
        toAdd.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        
        LinearLayout.LayoutParams instructionsParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        instructionsParams.setMargins(20, 20, 20, 5);
        TextView instructions = new TextView(TwilioActivity.this);
        instructions.setGravity(Gravity.CENTER_HORIZONTAL);
        
        instructions.setText(m.message);
        toAdd.addView(instructions,instructionsParams);
        
        LinearLayout horizontalLayout = new LinearLayout(TwilioActivity.this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams horizontalLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
       
        final EditText toEdit = new EditText(TwilioActivity.this);
        toEdit.setGravity(Gravity.BOTTOM);
        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
        editParams.weight = 1;
        toEdit.setHint("type...");
        if (!preference.equals("")) {
            toEdit.setText(preference);
        }
        
        horizontalLayout.addView(toEdit, editParams);
        
        Button send = new Button(TwilioActivity.this);
        LinearLayout.LayoutParams sendParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        send.setGravity(Gravity.BOTTOM);
        send.setText("Send");
        horizontalLayout.addView(send, sendParams);
        send.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                if (!toEdit.getText().toString().equals("")) {
                    SharedPreferences prefs = TwilioActivity.this.getSharedPreferences("com.SrivatsanPoddar.helpp", Context.MODE_PRIVATE);
                    Editor editor = prefs.edit();
                    editor.putString(preferenceKey, toEdit.getText().toString());
                    editor.commit();
                    Log.e("Adding preference: " + toEdit.getText().toString() + " for preference key: ", preferenceKey);
                    ChatMessage toSend = new ChatMessage(toEdit.getText().toString(), pairsIndex);

                    String JSONMessage = gson.toJson(toSend);
                    mConnection.sendTextMessage(JSONMessage);
                    
                    //Remove the view!
                    mainLayout.removeView(toAdd);
                    
                }

            }
        });
        
        toAdd.addView(horizontalLayout, horizontalLayoutParams);
        mainLayout.addView(toAdd);
    }
}

package com.SrivatsanPoddar.helpp;

import java.util.ArrayList;

import com.google.gson.Gson;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity
{

    private static final String TAG = "ChatActivity";

    private final WebSocketConnection mConnection = new WebSocketConnection();
    private Gson gson = new Gson();
    private ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();
    private ArrayAdapter<ChatMessage> aa;
    private ListView messageList;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {

       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_chat);

       messageList = (ListView) findViewById(R.id.messages);
       aa = new ArrayAdapter<ChatMessage>(this,android.R.layout.simple_list_item_1, messages.toArray(new ChatMessage[messages.size()]));
       messageList.setAdapter(aa);
              
       final EditText textField = (EditText) findViewById(R.id.message_field);
       Button sendButton = (Button) findViewById(R.id.send_message_button);
       
       sendButton.setOnClickListener(new Button.OnClickListener() {

        @Override
        public void onClick(View v)
        {
            
            String messageToSend = textField.getText().toString();
            if (messageToSend.length() > 0) {
                ChatMessage m = new ChatMessage(messageToSend);
                String JSONMessage = gson.toJson(m);
                mConnection.sendTextMessage(JSONMessage);
                textField.setText("");
            }

        }
           
       });
       
       start();
   }
    
    private void start() {

       final String wsuri = "ws://safe-hollows-9286.herokuapp.com/live";

       try {
          mConnection.connect(wsuri, new WebSocketHandler() {

             @Override
             public void onOpen() {
                Log.d(TAG, "Status: Connected to " + wsuri);
//                ChatMessage m = new ChatMessage();
//                m.setString("Hello Bob!");
//                
//                String JSONMessage = gson.toJson(m);
//                mConnection.sendTextMessage(JSONMessage);
                
             }

             @Override
             public void onTextMessage(String payload) {
                Log.d(TAG, "Got echo: " + payload);
                ChatMessage m = gson.fromJson(payload, ChatMessage.class);
                messages.add(m);
                aa = new ArrayAdapter<ChatMessage>(ChatActivity.this,android.R.layout.simple_list_item_1,messages.toArray(new ChatMessage[messages.size()]));
                messageList.setAdapter(aa);
             }

             @Override
             public void onClose(int code, String reason) {
                Log.d(TAG, "Connection lost.");
             }
          });
       } catch (WebSocketException e) {

          Log.d(TAG, e.toString());
       }
    }


 
}

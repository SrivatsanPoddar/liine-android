package com.SrivatsanPoddar.helpp;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.provider.Settings.Secure;

public class SurveyActivity extends Activity implements ListView.OnItemClickListener, Callback<ArrayList<SurveyQuestion>>{
	
	ArrayList<SurveyQuestion> questions;
	int currentQuestionsIndex = 0;
	ListView optionsList;
	TextView questionDisplayText;
	HerokuService ui;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey);
		
        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        
		TextView surveyIntro = (TextView) findViewById(R.id.survey_intro);
		Style.toOpenSans(this, surveyIntro, "light");

		Bundle extras = this.getIntent().getExtras();
		String company_id = extras.getString("company_id");
		
		// Initialize the question list
		questions = new ArrayList<SurveyQuestion>();
		
		//Make retrofit GET call to '/:store_id/questions' to get survey questions associated with a given store
        RestAdapter restAdapter = new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint("http://safe-hollows-9286.herokuapp.com")
            .build();
        ui = restAdapter.create(HerokuService.class);
        // First get company questions, then Liine questions
        ui.getQuestions(company_id,this);
        ui.getQuestions("0", this);
        
		optionsList = (ListView)findViewById(R.id.question_options_list);
		optionsList.setOnItemClickListener(this);
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            //Do stuff
            Style.makeToast(this, "Thanks for your time!");
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
	  @Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
		     
		  	//Make a retrofit POST call to '/responses/questionID' here, sending questions[currentQuestionIndex].getOptions()[position] as the response!
	        SurveyQuestion curQuestion = questions.get(currentQuestionsIndex);
	        curQuestion.setResponse(curQuestion.getOptions()[position]);
	        curQuestion.setDeviceID(Secure.getString(this.getApplicationContext().getContentResolver(),Secure.ANDROID_ID));
		    ui.addResponse(curQuestion, new postResponse());
		    
		  	if (currentQuestionsIndex < questions.size() - 1) {
		  	    
		  		
		  		currentQuestionsIndex++;
				questionDisplayText.setText(questions.get(currentQuestionsIndex).getDisplayText());
				
				CustomListAdapter<String> adapter = new CustomListAdapter<String>(this,R.layout.search_list_row,questions.get(currentQuestionsIndex).getOptions());
				optionsList.setAdapter(adapter);
				
		  	}
		  	else {
		  	    Style.makeToast(this, "Thanks for your time!");
	  			Intent intent = new Intent(this, SearchActivity.class);
			    startActivity(intent);
		  	}
		}

	private class postResponse implements Callback<String> {

        @Override
        public void failure(RetrofitError err)
        {
            // TODO Auto-generated method stub
            Log.e("Error Posting Question Response",err.toString());
        }

        @Override
        public void success(String result, Response arg1)
        {
            Log.e("Response successfully posted with result:", result);
            // TODO Auto-generated method stub
            
        }
	    
	}
	
    @Override
    public void failure(RetrofitError err)
    {
        Log.e("Error Retrieving Questions", err.toString());
    }

    @Override
    public void success(ArrayList<SurveyQuestion> returnedList, Response res)
    {     
        for(SurveyQuestion sq : returnedList)
        {
            questions.add(sq);
        }
        
        if (questions.size() == 0) {
            Style.makeToast(this, "Thanks for the call!");
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
        else {
            questionDisplayText = (TextView)findViewById(R.id.question_display_text);
            questionDisplayText.setText(questions.get(currentQuestionsIndex).getDisplayText());
            Style.toOpenSans(this, questionDisplayText, "light");
            
            Log.e("Setting adapter", "setting list adapter upon success");
            
            CustomListAdapter<String> adapter = new CustomListAdapter<String>(this,
                    R.layout.search_list_row, questions.get(currentQuestionsIndex).getOptions());
            optionsList.setAdapter(adapter);
        }
     
    }
}


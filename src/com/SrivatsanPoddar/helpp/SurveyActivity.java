package com.SrivatsanPoddar.helpp;



import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
		
		TextView surveyIntro = (TextView) findViewById(R.id.survey_intro);
		Style.toOpenSans(this, surveyIntro, "light");
		
		
		
		Bundle extras = this.getIntent().getExtras();
		String company_id = extras.getString("company_id");
		
		//Make retrofit GET call to '/:store_id/questions' to get survey questions associated with a given store
        RestAdapter restAdapter = new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint("http://safe-hollows-9286.herokuapp.com")
            .build();
        ui = restAdapter.create(HerokuService.class);       
        ui.getQuestions(company_id,this);
        
		optionsList = (ListView)findViewById(R.id.question_options_list);
		optionsList.setOnItemClickListener(this);
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
				
				//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,questions.get(currentQuestionsIndex).getOptions());
				CustomListAdapter<String> adapter = new CustomListAdapter(this,android.R.layout.simple_list_item_1,questions.get(currentQuestionsIndex).getOptions());
		        
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
        // TODO Auto-generated method stub
        Log.e("Error Retrieving Questions", err.toString());
    }

    @Override
    public void success(ArrayList<SurveyQuestion> returnedList, Response res)
    {
//        String[] q1Options = {"True","False"};
//        SurveyQuestion q1 = new SurveyQuestion("true_false","I was satisfied with my service today.",q1Options,"1");
//        String[] q2Options = {"Very Prompt", "Prompt", "Not Prompt at All", "Not Applicable"};
//        SurveyQuestion q2 = new SurveyQuestion("multiple_choice","How prompt was your service today?",q2Options,"2");
//        
//        questions = new SurveyQuestion[2];
//        questions[0] = q1;
//        questions[1] = q2;
//      
        questions = returnedList;  
        questionDisplayText = (TextView)findViewById(R.id.question_display_text);
        questionDisplayText.setText(questions.get(currentQuestionsIndex).getDisplayText());
        Style.toOpenSans(this, questionDisplayText, "light");
        
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,questions.get(currentQuestionsIndex).getOptions());
        CustomListAdapter<String> adapter = new CustomListAdapter(this,android.R.layout.simple_list_item_1,questions.get(currentQuestionsIndex).getOptions());
        optionsList.setAdapter(adapter);        
    }
}


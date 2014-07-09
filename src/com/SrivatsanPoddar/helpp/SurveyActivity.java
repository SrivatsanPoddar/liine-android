package com.SrivatsanPoddar.helpp;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SurveyActivity extends Activity implements ListView.OnItemClickListener{
	
	SurveyQuestion[] questions;
	int currentQuestionsIndex = 0;
	ListView optionsList;
	TextView questionDisplayText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey);
		
		Bundle extras = this.getIntent().getExtras();
		int store_id = extras.getInt("store_id");
		
		//Make retrofit GET call to '/:store_id/questions' to get survey questions associated with a given store
		
		//Dummy data for now
		String[] q1Options = {"True","False"};
		SurveyQuestion q1 = new SurveyQuestion("true_false","I was satisfied with my service today.",q1Options,"1");
		String[] q2Options = {"Very Prompt", "Prompt", "Not Prompt at All", "Not Applicable"};
		SurveyQuestion q2 = new SurveyQuestion("multiple_choice","How prompt was your service today?",q2Options,"2");
		
		questions = new SurveyQuestion[2];
		questions[0] = q1;
		questions[1] = q2;
		
		questionDisplayText = (TextView)findViewById(R.id.question_display_text);
		questionDisplayText.setText(questions[currentQuestionsIndex].getDisplayText());
		
		optionsList = (ListView)findViewById(R.id.question_options_list);
		optionsList.setOnItemClickListener(this);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,questions[currentQuestionsIndex].getOptions());
		optionsList.setAdapter(adapter);
		
		
	}
	
	  @Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
		  
		  	//Make a retrofit POST call to '/responses/questionID' here, sending questions[currentQuestionIndex].getOptions()[position] as the response!
		   
		  	if (currentQuestionsIndex < questions.length - 1) {
		  		
		  		currentQuestionsIndex++;
		  		
				questionDisplayText = (TextView)findViewById(R.id.question_display_text);
				questionDisplayText.setText(questions[currentQuestionsIndex].getDisplayText());
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,questions[currentQuestionsIndex].getOptions());
				optionsList.setAdapter(adapter);
				
		  	}
		  	else {
	  			Intent intent = new Intent(this, SearchActivity.class);
			    startActivity(intent);
		  	}

		    
			
		}
}


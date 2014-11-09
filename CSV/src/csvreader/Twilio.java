package csvreader;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Sms;
import com.twilio.sdk.resource.list.SmsList;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Twilio {

	//default age
	 private int age = 5;
	 private boolean mealTime = false;
	 private boolean bedTime = false;
	 private static final String ACCOUNT_SID = "AC781d1a15b57b265850465913c830bfa5"; 
	 private static final String AUTH_TOKEN = "6060950865885af435f07f6d8e451e86"; 

	 public Twilio (){
		 
	 }
	 
	 public Twilio(int age){
		 this.age = age; 
	 } 
	 
	 
	  public void sendMessage(int val) throws TwilioRestException{
		  
		  String toSend = createMessage(val);
		  TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
			 
		  if(!toSend.equals("")){
		  
		    // Build a filter for the SmsList
		    List<NameValuePair> params = new ArrayList<NameValuePair>();
		    params.add(new BasicNameValuePair("Body", "Abhi says Your Blood Glucose Level: " + val + "," + toSend));
		    params.add(new BasicNameValuePair("From", "+16467628959")); 
		    params.add(new BasicNameValuePair("To", "+19177506286"));
		     
		     
		    SmsFactory smsFactory = client.getAccount().getSmsFactory();
		    Sms sms = smsFactory.create(params);
		    System.out.println(sms.getSid());
		    
		  }    
	  }
	  
	  private String createMessage(int val){
		  
		  if(val < generateRange(val, true))
			  return " BCG is too low";
		  else if (val < generateRange(val, false))
			  return " BCG is too high";
		  
		  return "";
	  }
	  
	  private int generateRange(int value, boolean low){
		  //what should the values be when it's not bedtime or sleeptime
		  //algorithm gathered off of BASIC1.ppt
		  //LOW VALUES
		  if(low){
			  if(mealTime){
				  if(age < 5)
					  return 80;
				  else
					  return 70;
			  }
			  else if(bedTime){
				  if(age < 5)
					  return 150;
				  else if(age >= 5 && age <=11 )
					  return 120;
				  else
					  return 100;
			  }
			  
			  else{
				  if(age < 5)
					  return 80;
				  else if(age >= 5 && age <=11 )
					  return 100;
				  else
					  return 120;  
			  }  
		  }
		  
		  //HIGH Values in Range
		  else {
				
			if(mealTime){
			  if(age < 5)
				  return 200;
			  else if(age >= 5 && age <=11 )
				  return 180;
			  else
				  return 150;
			}
			  // no need for bedtime high as there is only one given value
			  // question so what will the range be then??
			  
			  else{
				  
				  if(age < 5)
					  return 180;
				  else if(age >= 5 && age <=11 )
					  return 135;
				  else
					  return 100;
				  
			  }
			}
	  }
	  
}

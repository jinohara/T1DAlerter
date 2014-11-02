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

	 private int low;
	 private int high;
	 private static final String ACCOUNT_SID = "AC781d1a15b57b265850465913c830bfa5"; 
	 private static final String AUTH_TOKEN = "6060950865885af435f07f6d8e451e86"; 

	 
	 public Twilio(int low, int high){
		 this.high = high;
		 this.low = low;
		 
	 } 
	 
	 
	  public void sendMessage(int val) throws TwilioRestException{
		  
		  String toSend = message(val);
		  TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
			 
		  if(!toSend.equals("")){
		  
		    // Build a filter for the SmsList
		    List<NameValuePair> params = new ArrayList<NameValuePair>();
		    params.add(new BasicNameValuePair("Body", "Abhi says Your Blood Glucose Level: " + val +  toSend));
		    params.add(new BasicNameValuePair("From", "+16467628959")); 
		    params.add(new BasicNameValuePair("To", "+19177506286"));
		     
		     
		    SmsFactory smsFactory = client.getAccount().getSmsFactory();
		    Sms sms = smsFactory.create(params);
		    System.out.println(sms.getSid());
		    
		  }
		    
	  }
	  
	  private String message(int val){
		  
		  if( val < low)
			  return " is too high";
		  
		  else if(val > high)
			  return " is too low";
		  
		  else
			  return "";
	  }
	  
	
}

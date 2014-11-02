package csvreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.twilio.sdk.TwilioRestException;


public class Reader {

	 public static void main(String[] args) throws FileNotFoundException, NumberFormatException, TwilioRestException 
	    {
		 
		 	//Create a Twilio Object
		 	Twilio trial = new Twilio(60, 180);
		 
	        //Get scanner instance
	        Scanner scanner = new Scanner(new File("//Users//abhimanyumuchhal//Desktop//Mich-Freshman//Research//data.csv"));
	         
	        //Set the delimiter used in file
	        scanner.useDelimiter(",");
	         
	        //Get all tokens and store them in some data structure
	        //I am just printing them
	        while (scanner.hasNext()) 
	        {
	        	scanner.nextLine();
	        	
	        	for(int i = 1; i < 6; i++)
	        		scanner.next();
	     
	        	String s = scanner.next();
	        	if(!s.equals("")){
	        		System.out.println(s);
	        		trial.sendMessage(Integer.parseInt(s));
	        		
	        		
	        	}
	            scanner.nextLine();
	            
	        }
	         
	        //Do not forget to close the scanner  
	        scanner.close();
	    }

}

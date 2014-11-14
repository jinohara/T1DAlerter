package csvreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.twilio.sdk.TwilioRestException;


public class Reader {
	//TODO:How to read starting from a certain part of a file
	 public static void main(String[] args) throws FileNotFoundException, NumberFormatException, TwilioRestException 
	    {
		 
		 
		 	//Create a Twilio Object
		 	Twilio trial = new Twilio();
		 
	        //Get scanner instance
	        Scanner scanner = new Scanner(new File("//Users//abhimanyumuchhal//Desktop//Mich-Freshman//Research//data.csv"));
	         
	        //Set the delimiter used in file
	        scanner.useDelimiter(",");
	         
	        //Get all tokens and store them in some data structure
	        //I am just printing them
	        while (scanner.hasNext()) 
	        {
	        	
	        	//gets to column AI
	        	for(int i = 1; i < 34; i++)
	        		scanner.next();
	        	
	        	String type = scanner.next();    	
	        	String reading = scanner.next();
	        	try{
	        	type = type.substring(0,3);
	        
	        	//checks if it is a BCG value reading
	        	if(type.equals("Glu") && reading.substring(1,5).equals("AMOU")){
	        		//separates all the necessary BCG numbers from the value
	        		reading = reading.substring(8, reading.length());
	        		System.out.println(reading);
	        		double v = Double.parseDouble(reading);
	        		trial.sendMessage((int)v);
	        		
	        	}
	        	}
	        	//catches any exceptions: values that don't match the format
	        	catch(StringIndexOutOfBoundsException e){}
	        	catch(NumberFormatException e){}
	            scanner.nextLine();
	            
	        }
	         
	        //Do not forget to close the scanner  
	        scanner.close();
	    }

}

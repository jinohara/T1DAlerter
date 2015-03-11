package csvreader;
import java.util.ArrayList;
public class Algorithm {

	 private static final int  VALUE_CONSTANT = 4;
	 private int age = 5;
	 private boolean mealTime = false;
	 private boolean bedTime = false;
	 private ArrayList<Integer> bcgValues;
	 
	 Algorithm(){
		 
		 bcgValues = new ArrayList<Integer>();
		 
	 }

	 /**
	  * looks at array of past 5 values
	  * if the slope of all 5 of them is positive returns +1, negative -1 and if
	  * there is a mixture it returns 0 
	  */
	 private int slopeSign(int val){
		 
		 updateArray(val);
		 //initial
		 int sign = -999;
		 
		 // no need to alert if there is only a small data set to work with
		 if (bcgValues.size() < VALUE_CONSTANT)
			 return 0;
		 
		 for(int i =1; i < VALUE_CONSTANT; i++){
			 double slope = ((bcgValues.get(i) - bcgValues.get(i-1)) / 5);
			 	if(sign == -999)
			 		sign = unary(slope);
			 	else if(sign != unary(slope))
			 		return 0;	
		 }
		
		 return sign;
		 
	 }
	 /**
	  * creates the appropriate message to send to Twilio
	  * @param val
	  * @return
	  */
	  public String createMessage(int val){
		  
		  //temp
		  int sign =  slopeSign(val);
		  
		  if(val < generateRange(val, true) && sign == -1)
			  return " BCG is too low";
		  else if (val < generateRange(val, false) && sign ==1)
			  return " BCG is too high";
		  
		  return "";
	  }
	  
	  private void updateArray(int val){
		  
		  if(bcgValues.size() < VALUE_CONSTANT)
			  bcgValues.add(val);
		  else{
			  bcgValues.remove(0);
			  bcgValues.add(val);
		  }  
	  }
	  
	 /**
	  * @param val
	  * @return sign of value
	  */
	 private int unary(double val){
		
		 if (val == 0.0)
			 return 0;
		 else if(val > 0)
			 return 1;
		 else 
			 return -1;
		 
	 }
	 /**
	  * creates range of acceptable BCG values for user
	  * @param value
	  * @param low
	  * @return
	  */
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

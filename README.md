# UROP2014-2015

My Result Receiver has now been updated to have two options.
1- Normal. Only most updated value will be shown from the endpoint.
2 - Train/Retrain. Entire endpoint will be in the array list.


CreateAndTest.java demonstrates how to use the methods for creating, training, testing, and classifying with SVMs that are defined in SVMMethods.java.  DexComReading.java defines the class I am using to represent readings.

To compile and run CreateAndTest.java, make sure you have all the .jar files, DexComReading.java, SVMMethods.java, CreateAndTest.java, and the file sgvNums in your current directory and run the following 2 commands.

javac -cp ".:ajt-2.9.jar:commons-math-1.2.jar:DexComReading.java:Jama-1.0.2.jar:javaml-0.1.7.jar:libsvm.jar:weka.jar:SVMMethods.java" CreateAndTest.java

java -cp ".:ajt-2.9.jar:commons-math-1.2.jar:DexComReading.java:Jama-1.0.2.jar:javaml-0.1.7.jar:libsvm.jar:weka.jar:SVMMethods.java" CreateAndTest 


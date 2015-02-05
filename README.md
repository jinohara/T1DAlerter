# UROP2014-2015

CreateAndTest.java demonstrates how to use the methods for creating, training, testing, and classifying with SVMs that are defined in SVMMethods.java.  DexComReading.java defines the class I am using to represent readings.

To compile and run CreateAndTest.java, make sure you have all the .jar files, DexComReading.java, SVMMethods.java, and CreateAndTest.java in your current directory and run the following 2 commands.

javac -cp ".:ajt-2.9.jar:commons-math-1.2.jar:DexComReading.java:Jama-1.0.2.jar:javaml-0.1.7.jar:libsvm.jar:weka.jar:SVMMethods.java" CreateAndTest.java

java -cp ".:ajt-2.9.jar:commons-math-1.2.jar:DexComReading.java:Jama-1.0.2.jar:javaml-0.1.7.jar:libsvm.jar:weka.jar:SVMMethods.java" CreateAndTest 


# T1Alerter - UI Update

New and improved!

Fixed a bunch of crashes

Fixed orientation screwing up the graph

Fixed wrong data display for first value

Added simple material design

Added shared preferences instead of wading through menus

Instead of GraphView, added MPAndroidChart because it's nicer.


Known issues:

The graph loads correctly the first time, but screws up when it updates, as it fills the oldest values with the newest ones. This is probably a bug in QueryService or elsewhere.

Refresh button doesn't work.


TODO:

Implement refreshing data through the refresh button.

Implement more robust error checking.

Fix the graph-update issue.

---

Old Readme:

My Result Receiver has now been updated to have two options. 1- Normal. Only most updated value will be shown from the endpoint. 2 - Train/Retrain. Entire endpoint will be in the array list.

CreateAndTest.java demonstrates how to use the methods for creating, training, testing, and classifying with SVMs that are defined in SVMMethods.java. DexComReading.java defines the class I am using to represent readings.

To compile and run CreateAndTest.java, make sure you have all the .jar files, DexComReading.java, SVMMethods.java, CreateAndTest.java, and the file sgvNums in your current directory and run the following 2 commands.

javac -cp ".:ajt-2.9.jar:commons-math-1.2.jar:DexComReading.java:Jama-1.0.2.jar:javaml-0.1.7.jar:libsvm.jar:weka.jar:SVMMethods.java" CreateAndTest.java

java -cp ".:ajt-2.9.jar:commons-math-1.2.jar:DexComReading.java:Jama-1.0.2.jar:javaml-0.1.7.jar:libsvm.jar:weka.jar:SVMMethods.java" CreateAndTest
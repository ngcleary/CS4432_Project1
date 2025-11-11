Nora Cleary | 175441581

Section I 
To Compile: cd to Project1\src and run javac Main.java
To Run: java Main

Section II
All provided test case commands are successful.

Section III
I used a lastEvicted variable in the BufferPool class to evict a frame in a circular fashion. The lastEvicted variable is 
in an int initilized to -1 to indicate there have been no files previously evicted. The lastEvicted variable is updated
in the evict() function to hold the slot number (the frame number in the buffer array) that was just evicted. 
This value is used in the findEvict() method to produce the circular search. The search starts at the frame following the 
last evicted slot number and goes to the begining of the array when the end is reached. 
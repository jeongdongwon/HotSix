import processing.serial.*; 
 
char interest = ' ';
Serial myPort;        // The serial port 
int xPos = 1;         // horizontal position of the graph 
float height_old1 = 0; 
float height_old2 = 0; 
float height_old3 = 0; 
float height_old4 = 0; 
float height_old5 = 0; 
float height_old6 = 0; 

float height_new = 0; 
float inByte = 0; 

void setup () { 
  // set the window size: 
  size(1000, 800); 
  println(Serial.list()); 
  // Open whatever port is the one you're using. 
  myPort = new Serial(this, Serial.list()[0], 9600); 
  // don't generate a serialEvent() unless you get a newline character: 
  myPort.bufferUntil('\n'); 
  background(0xff); 
} 

void draw (){
}
 
void serialEvent (Serial myPort) { 
  // get the ASCII string: 
  String inString1 = myPort.readStringUntil(interest); 
  String inString2 = myPort.readStringUntil('\n'); 
  stroke(0, 0, 0);
  line(1, height*2/3, width-1, height*2/3); 
  line(1, height/3, width-1, height/3); 
 
  inByte = map(-20000, -50000, 50000,  height/3, height*2/3);
  print(height/3); print(" ");print(height*2/3); print(" "); println(inByte);
  stroke(0xff, 0, 0);
  line(1, inByte, width-1, inByte); 
//  print(inString1);  print(" "); 
//  print(inString2);  print(" ");  
//  print(inString3);  print(" ");  
//  print(inString4);  print(" "); 
//  print(inString5);  print(" "); 
//  print(inString6);  print(" "); 

  inString1 = trim(inString1); 
  stroke(0xff, 0, 0); //Set stroke to red ( R, G, B) 
  inByte = float(inString1);  
  inByte = map(inByte,  -50000, 50000, height*2/3, height); 
  height_new = height - inByte;  
  line(xPos - 1, height_old1, xPos, height_new); 
  height_old1 = height_new; 
  
  inString2 = trim(inString2); 
  stroke(0, 0xff, 0); //Set stroke to red ( R, G, B) 
  inByte = float(inString2);  
  inByte = map(inByte, -50000, 50000,  height/3, height*2/3); 
  height_new = height - inByte;  
  line(xPos - 1, height_old2, xPos, height_new); 
  height_old2 = height_new; 
  
  
  if (xPos >= width) { 
    xPos = 0; 
    background(0xff); 
  }  
  else { 
    xPos+=2; 
  }      
} 

float d;
float distance;
float emptyValue;
int setHeight = 0;
int led = 12;
int start = 0;
int limit = 2;

// constants won't change. They're used here to set pin numbers:
const int buttonPin = 2;     // the number of the pushbutton pin
const int ledPin =  13;      // the number of the LED pin
float bottleHeight;
// variables will change:
int buttonState = 0;         // variable for reading the pushbutton status
int val = 0;     // variable for reading the pin status

String recieved;
char character;
float height = 15;
float lowest = 10; 
float tankheight = 100;
int Astvar = 0;
int num = 0;
int va;
String substr;
long duration;
float percentage;
float setpercentage;
float setheight;
float setlowest;

void setup() {
 Serial.begin(9600);
 pinMode(7,INPUT);//echo pin of ultraSonic
 pinMode(8,OUTPUT);//trig pin of ultraSonic
 pinMode(10,OUTPUT);//relayin
 pinMode(6,OUTPUT);//relayout
 pinMode(9,OUTPUT);//green light
 pinMode(led,OUTPUT);//red led
 // initialize the pushbutton pin as an input:
  pinMode(buttonPin, INPUT);
  initialization();

}




void initialization() //distance calculaion...
{
  while(1)
{
  
  val = digitalRead(buttonPin);  // read input value
  if (val == HIGH) 
  // check if the input is HIGH (button released)
  {
     digitalWrite(ledPin, HIGH);  // turn LED ON
    digitalWrite(8,HIGH);
    delayMicroseconds(8);
    digitalWrite(8,LOW);
    delayMicroseconds(2);
    d=pulseIn(7,HIGH);
    //d=d/69;
    d = (d/2) / 29.1;     
    //d=d*340/20000;
    
    if(d > tankheight){
        d = 0;
    }
    
    buttonState = digitalRead(buttonPin);
  
    //Serial.println("jhhh");
    bottleHeight = d;

    //Serial.println(bottleHeight);
    delay(1000);
    break;
  } 
  else 
  {
   digitalWrite(ledPin, LOW);  // turn LED OFF
  }
 // Serial.println("");
  
}
} 
void vol() //distance calculaion...
{
 digitalWrite(8,HIGH);
 delayMicroseconds(8);
 digitalWrite(8,LOW);
 delayMicroseconds(2);
 d=pulseIn(7,HIGH);
 //d=d/69;
 //d = duration*0.024/2;
  //d = d/58;
  d = d/29/2;
}
 
void loop() {
  
//Serial.println("hello");

digitalWrite(led,HIGH); 
while(Serial.available() > 0){
    character = Serial.read();
    recieved = recieved + character;
    //Serial.print(recieved);
    num = num + 1;
    if (character == '*'){
      Astvar = num - 1;
    }
    if (character == '|'){
      va = num -1;
    }
    delay(50);
  } 

  if(recieved.length() > 0){
    //Serial.println("");
    //Serial.println(recieved);
    
    if (recieved.equals("Start")){
      recieved = "";
      num = 0;
    }
    else if(va = recieved.length()-1){
      //Serial.println(recieved);
      substr = recieved;
      substr = substr.substring(0, Astvar);
      //Serial.println(substr);
        if (substr.equals("Height")){
          recieved  = recieved.substring(Astvar+1, recieved.length()-1);
          setheight = recieved.toFloat();
          //height = tankheight - setheight;
          float temp = tankheight - setheight;
          if (temp < (lowest+limit)){
            tone(9,3000);//buzzer on.....
              delay(100);
              noTone(9);
              tone(9,3000);//buzzer on.....
              delay(100);
              noTone(9);
              recieved = "";
          num = 0;
          substr = "";
          }
          else{
            height = tankheight - setheight;
             recieved = "";
          num = 0;
          substr = "";
          }
          recieved = "";
          num = 0;
          substr = "";
        }
         if (substr.equals("Lowest")){
          recieved  = recieved.substring(Astvar+1, recieved.length()-1);
          setlowest = recieved.toFloat();
          
          float temp = tankheight - setlowest;
          if (temp > (height-limit)){
            tone(9,3000);//light on.....
              delay(100);
              noTone(9);
              tone(9,3000);//light on.....
              delay(100);
              noTone(9);
              recieved = "";
          num = 0;
          substr = "";
          }
          else{
            lowest = tankheight - setlowest;
             recieved = "";
          num = 0;
          substr = "";
          }
          //Serial.println(lowest);
          recieved = "";
          num = 0;
          substr = "";
        }
        
        if (substr.equals("Tank Height")){
          recieved  = recieved.substring(Astvar+1, recieved.length()-1);
          tankheight = recieved.toFloat();
          //Serial.println(tankheight);
          recieved = "";
          num = 0;
          substr = "";
        }
        if (substr.equals("Percentage")){
          recieved  = recieved.substring(Astvar+1, recieved.length()-1);
          setpercentage = recieved.toFloat();
          float lo = ((setpercentage*tankheight)/100);
          //Serial.println(lo);
          lowest = tankheight - lo;  
         
          height = lowest + limit; 
          recieved = "";
          num = 0;
          substr = "";      
        }
      Astvar = 0;
      recieved = "";
      va = 0;
     
    }
    
  }
  
 start = 1; 
  
 vol();
 if(d > height)
  {
   digitalWrite(10,HIGH);// Pump On...
   delay(125);
     tone(9,3000);//buzzer on.....
     delay(125);
     noTone(9);
    
  }
 else if(d < height)
  {
   digitalWrite(10,LOW);// pump off...
   delay(250);
   
  }

if(d < lowest)
  {
   
   digitalWrite(6,HIGH);// Pump On...
   delay(250);   
  }
 else if(d > lowest)
  {
   digitalWrite(6,LOW);// pump off...
   delay(250);
   
  }
  printall();
}

void printall(){
  percentage = (((tankheight-d)*100)/tankheight);
  setheight = tankheight - height;
  setlowest = tankheight - lowest;
  distance = tankheight - d;
  Serial.print(setheight);
  Serial.print(":");
  Serial.print(setlowest);
  Serial.print(":");
  Serial.print(distance);
  Serial.print(":");
  Serial.print(tankheight);
  Serial.print(":");
  Serial.print(percentage);
  Serial.print(":");
  Serial.println("");
  delay(400);
 
}

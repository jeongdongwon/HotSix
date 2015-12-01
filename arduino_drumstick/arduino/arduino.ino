#include<Wire.h>
#include <SoftwareSerial.h>

int blue_tx = 5;
int blue_rx = 4;

SoftwareSerial blu_serial(blue_tx, blue_rx);

const int MPU=0x68;  //MPU 6050 의 I2C 기본 주소
const int MPU2=0x69;//2번째 


int16_t AcX,AcY,AcZ,Tmp,GyX,GyY,GyZ;
int16_t AcX2,AcY2,AcZ2,Tmp2,GyX2,GyY2,GyZ2;

int16_t tempX = 0;
int16_t tempY = 0;
int16_t tempZ = 0;
int16_t X,Y,Z;

unsigned long int prev_time1;
unsigned long int prev_time2;

int time_gap = 100;

void setup(){
  blu_serial.begin(9600);
  
  Wire.begin();      //Wire 라이브러리 초기화
  Wire.beginTransmission(MPU); //MPU로 데이터 전송 시작
  Wire.write(0x6B);  // PWR_MGMT_1 register
  Wire.write(0);     //MPU-6050 시작 모드로
  Wire.endTransmission(true); 

  Wire.beginTransmission(MPU2); //MPU로 데이터 전송 시작
  Wire.write(0x6B);  // PWR_MGMT_1 register
  Wire.write(0);     //MPU-6050 시작 모드로
  Wire.endTransmission(true); 

  prev_time1 = millis();
  prev_time2 = millis();
  
  Serial.begin(9600);
}

void loop(){
  Wire.beginTransmission(MPU);    //데이터 전송시작
  Wire.write(0x3B);               // register 0x3B (ACCEL_XOUT_H), 큐에 데이터 기록
  Wire.endTransmission(false);    //연결유지
  Wire.requestFrom(MPU,14,true);  //MPU에 데이터 요청
  //데이터 한 바이트 씩 읽어서 반환
  AcX=Wire.read()<<8|Wire.read();  // 0x3B (ACCEL_XOUT_H) & 0x3C (ACCEL_XOUT_L)    
  AcY=Wire.read()<<8|Wire.read();  // 0x3D (ACCEL_YOUT_H) & 0x3E (ACCEL_YOUT_L)
  AcZ=Wire.read()<<8|Wire.read();  // 0x3F (ACCEL_ZOUT_H) & 0x40 (ACCEL_ZOUT_L)
  
Wire.beginTransmission(MPU2);    //데이터 전송시작
  Wire.write(0x3B);               // register 0x3B (ACCEL_XOUT_H), 큐에 데이터 기록
  Wire.endTransmission(false);    //연결유지
  Wire.requestFrom(MPU2,14,true);  //MPU에 데이터 요청
  //데이터 한 바이트 씩 읽어서 반환
  AcX2=Wire.read()<<8|Wire.read();  // 0x3B (ACCEL_XOUT_H) & 0x3C (ACCEL_XOUT_L)    
  AcY2=Wire.read()<<8|Wire.read();  // 0x3D (ACCEL_YOUT_H) & 0x3E (ACCEL_YOUT_L)
  AcZ2=Wire.read()<<8|Wire.read();  // 0x3F (ACCEL_ZOUT_H) & 0x40 (ACCEL_ZOUT_L)  
  //시리얼 모니터에 출력

//Serial.print(AcX);
//Serial.print(" ");

  if(AcY > 9000){
    if(millis() - prev_time1 > time_gap){
    blu_serial.print("1");
    }
    prev_time1 = millis();
    
  }

  if(AcY2 > 9000 ){
    if(millis() - prev_time2 > time_gap){
      blu_serial.print("2");
    }
 
    prev_time2 = millis();
  }
  
  delay(50);
/*
Serial.print(AcY);

Serial.print("           ");
Serial.println(AcY2);
// delay(100);

if(AcY > 12000)
blu_serial.println("1");

if(AcY2 > 12000)
blu_serial.println("2");
*/

  //delay(50);

}


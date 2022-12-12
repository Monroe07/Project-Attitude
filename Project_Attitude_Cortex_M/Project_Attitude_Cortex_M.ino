#include <Wire.h>
#include "Adafruit_BNO055.h"
#include "utility/imumaths.h"

//#include <Adafruit_HMC5883_U.h>

#include <Adafruit_NeoPixel.h>

// PROTOTYPES
void sendRollPitch();
//void sendHead();
void respondToRequestFromGauge();

#define LEDPIN 16
#define NUMPIXELS 1

// FOR SECOND BUS ACTING AS SLAVE
const byte PICO_I2C_ADDRESS = 0x55;
const byte PICO_I2C_SDA = 2;
const byte PICO_I2C_SCL = 3;

byte cmd;

// FOR FIRST BUS ACTING AS MASTER FOR BNO055
Adafruit_BNO055 bno = Adafruit_BNO055(55, 0x28);  // For Waveshare Pi Pico Version
//Adafruit_HMC5883_Unified mag = Adafruit_HMC5883_Unified(12345);

Adafruit_NeoPixel pixels(NUMPIXELS, LEDPIN, NEO_GRB + NEO_KHZ800);

union floatToBytes {
  char buff[4];
  float val;
} r, p, h;

char rollBuff[4];
char pitchBuff[4];



///////////////////////////////////////
//- CORE 0 -//
void setup() {
  pixels.begin(); // INITIALIZE NeoPixel strip object (REQUIRED)
  pixels.clear(); // Set all pixel colors to 'off'
  pixels.setPixelColor(0, pixels.Color(0, 0, 0));
  /* Initialise the sensor */
  Serial.begin(115200);
  //delay(2000);
  Serial.print("Waiting...");


  while (!bno.begin()) {
    pixels.setPixelColor(0, pixels.Color(255, 0, 0));
    pixels.show();
    delay(500);
    pixels.setPixelColor(0, pixels.Color(0, 0, 0));
    pixels.show();
    delay(500);
    Serial.print(".");
  }
  Serial.println();

  // Set Amber showing bno is initialized
  pixels.setPixelColor(0, pixels.Color(255, 191, 0));
  pixels.show();
  Serial.println("BNO055 Initialized!");
  //delay(1000);

  /* Use external crystal for better accuracy */
  bno.setExtCrystalUse(true);
/*
  while (!mag.begin()) {
    pixels.setPixelColor(0, pixels.Color(255, 191, 0));
    pixels.show();
    delay(500);
    pixels.setPixelColor(0, pixels.Color(0, 0, 0));
    pixels.show();
    delay(500);
    Serial.print(".");

  }
  */
  
  pixels.setPixelColor(0, pixels.Color(0, 255, 0));
  pixels.show();
  //delay(1000);


  Wire1.setSDA(PICO_I2C_SDA);
  Wire1.setSCL(PICO_I2C_SCL);
  Wire1.begin(PICO_I2C_ADDRESS);
  Wire1.onRequest(respondToRequestFromGauge);
  Wire1.onReceive(respondToCommandFromGauge);

}

void loop() {}

// REGUEST FOR DATA: Pi Pico --> GAUGE
void respondToRequestFromGauge() {
  // get data from sensor, package it in a nice format, and write it to Wire1
  sensors_event_t event;
  bno.getEvent(&event);

  switch (cmd) {
    // Send Roll/Pitch
    case 7:
      {
        sendRollPitch();
        break;
      } // END OF CASE 7
    default:
    {
      sendRollPitch();
    }
  } // END OF SWITCH
} // END OF respondToRequestFromGauge

// DATA: GAUGE --> Pi Pico
void respondToCommandFromGauge(int len) {
  cmd = Wire1.read();
  Serial.println(cmd);
}


void sendRollPitch() {
  sensors_event_t event;
  bno.getEvent(&event);
  //Serial.println((float)event.orientation.x);
  // Set Unison variables
  p.val = -(float)event.orientation.y;
  r.val = -(float)event.orientation.z;
  // Write corresponding char arrays to I2C bus 1
  Serial.print(r.buff); Serial.println(p.buff);
  Wire1.write(r.buff, sizeof(r.buff));
  Wire1.write(p.buff, sizeof(p.buff));
}

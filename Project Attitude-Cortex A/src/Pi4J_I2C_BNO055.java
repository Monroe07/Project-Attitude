// Copyright 2022 Kyle Monroe
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http ://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Pi4J_I2C_BNO055{
    //ADI adi;
    Boolean retry = true;
    public Pi4J_I2C_BNO055(){
        ADI adi = new ADI();
        adi.noConnection = true;
        adi.repaint();


        Context pi4j = Pi4J.newAutoContext();
        //I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
        I2CProvider i2CProvider = pi4j.provider("pigpio-i2c");

        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("I2C_Test").bus(11).device(0x55).build();
        byte valByte[] = new byte[8];
        byte val1[] = new byte[4];
        byte val2[] = new byte[4];
        // this is the retry loop for lost connection
        do {
            // Attempt to create an I2C Instance  using Config
            try (I2C I2C_Inst = i2CProvider.create(i2cConfig)){
                // While there is an instance if I2C Open
                adi.noConnection = false;
                adi.repaint();
                while(I2C_Inst.isOpen()){
                    try {
                        I2C_Inst.write(0x07);
                        I2C_Inst.read(valByte, 8);
                        retry = false;

                        System.arraycopy(valByte, 0, val1, 0, 4);
                        System.arraycopy(valByte, 4, val2, 0, 4);

                        float f1 = ByteBuffer.wrap(val1).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        float f2 = ByteBuffer.wrap(val2).order(ByteOrder.LITTLE_ENDIAN).getFloat();

                        double pitch = Math.toRadians((double)f2);
                        double roll = Math.toRadians((double)f1);
                        adi.setPitchBankValues(pitch, roll);
                        adi.repaint();


                        Thread.sleep(10);
                    }catch (IOException | InterruptedException e){
                        retry = true;
                        adi.noConnection = true;
                        adi.repaint();
                    }
                }
            } catch(Exception e){
                adi.noConnection = true;
                adi.repaint();
                retry = true;
            }

        }while(retry);
        // No longer running program
        pi4j.shutdown();
    }
    public static void main(String[] args){
        Pi4J_I2C_BNO055 gauge = new Pi4J_I2C_BNO055();
    }
}


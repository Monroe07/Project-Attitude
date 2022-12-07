// Copyright 2015 Kyle Monroe
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

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.*;

public class ADI extends JFrame {
    private static final long serialVersionUID = 1L;
    private int cx, cy;
    double pitch = 0.0;
    double bank = 0.0;
    double locOffV = 0.0;

    int sizex = 720;
    int sizey = 720;

    boolean noConnection;

    public ADI() {
        super("Basic ADI");

        //default closing action
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(800, 600);
        setUndecorated(true);
        //add label and button
        setContentPane(new ADIPanel());

        setLocation(0, 0);

        //arrange components inside window
        setVisible(true);

        cx = this.getWidth()/2;
        cy = this.getHeight()/2;
    }

    public void setPitchBankValues(double _pitch, double _bank) {
        pitch = _pitch;
        bank = _bank;
    }
    public void setGSAngle(double value) {
        locOffV = value;
    }


    public class ADIPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        ArtHorizon artHorizon;

        public ADIPanel() {
            super();

            artHorizon = new ArtHorizon(sizex, sizey);
            artHorizon.setDisplayGS(false);
        }


        public void paintComponent(Graphics g) {
            cx = this.getWidth()/2;
            cy = this.getHeight()/2;


            //clear background
            g.setColor(Color.black);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());

            //draw artificial horizon
            artHorizon.reposition(cx, cy);
            artHorizon.setValues(pitch, bank);
            artHorizon.setGSAngle(locOffV);
            artHorizon.setNoConnection(noConnection);
            artHorizon.draw(g);

        }



    }
}

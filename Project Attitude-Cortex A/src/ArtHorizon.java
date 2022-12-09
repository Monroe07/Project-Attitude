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

import java.awt.*;
import java.awt.geom.*;

public class ArtHorizon {
    int sizex, sizey;
    int cx, cy;
    boolean displGS;
    double gsOffAngle = 0.0;

    Color skycolor;
    Color earthcolor;
    Color gsColor;

    Color staticLine;
    Font numfont;

    FontMetrics fontMetrics;
    Rectangle2D fontRectangle;

    double pitch = 0.0;
    double bank = 0.0;

    boolean noConnection;

    public ArtHorizon(int sx, int sy) {
        sizex = sx;
        sizey = sy;

        skycolor = new Color(0, 132, 202);
        earthcolor = new Color(107, 86, 51);
        gsColor = new Color(255, 255, 255, 255);
        staticLine = new Color(255, 255, 255, 255);
        numfont = new Font("Helvertica", Font.BOLD, 20);
    }



    public void setNoConnection(boolean flag){
        noConnection = flag;
    }

    public void draw(Graphics g) {
        int ipitch = (int)(Math.toDegrees(pitch) * sizey/50);


        //rotate
        Graphics2D g2d = (Graphics2D)g;
        g2d.rotate(bank, cx, cy);
        g2d.setStroke(new BasicStroke(1));


        //sky color
        g.setColor(skycolor);
        g.fillRect(cx - sizex, cy - sizey*3, sizex*2, sizey*3 + ipitch);

        //earth color
        g.setColor(earthcolor);
        g.fillRect(cx - sizex, cy + ipitch, sizex*2, sizey*3);


        //set line color and font
        g.setColor(Color.white);
        g.setFont(numfont);

        //horz mainline
        //g.setColor(Color.black);
        g.drawLine(cx - sizex, cy + ipitch, cx + sizex, cy + ipitch);
        g.setColor(Color.white);
        //sub lines
        for(int i = -8; i <= 8; i++) {
            g.drawLine(cx - sizex/4, cy - i*sizey/5 + ipitch, cx + sizex/4, cy - i*sizey/5 + ipitch);

            int drawint = i * 10;
            String drawText = Integer.toString(drawint);
            calcTextRectangle(g, drawText);
            drawCenteredString(drawText, - sizex/3, - i*sizey/5-2 + ipitch, g);
            drawCenteredString(drawText, + sizex/3, - i*sizey/5-2 + ipitch, g);
        }
        for(double i = -8.5; i <= 8.5; i++) {
            g.drawLine(cx - sizex/6, cy - (int)(i*sizey/5) + ipitch, cx + sizex/6, cy - (int)(i*sizey/5) + ipitch);
        }

        //min and max circle
        g.drawOval(cx - 5, cy - 5 - 9*sizey/5 + ipitch, 10, 10);
        g.drawOval(cx - 5, cy - 5 + 9*sizey/5 + ipitch, 10, 10);

        //rotation triangle
        Polygon p = new Polygon();
        p.addPoint(cx-8, cy - (int)(sizey*0.38));
        p.addPoint(cx+8, cy - (int)(sizey*0.38));
        p.addPoint(cx, cy-9 - (int)(sizey*0.38));
        g.fillPolygon(p);


        //rotate back
        g2d.rotate(-bank, cx, cy);


        //draw rotation angle lines
        g.drawLine(cx, cy - (int)(sizey*0.45), cx, cy - (int)(sizey*0.40));
        g2d.rotate(Math.toRadians(10), cx, cy);
        g.drawLine(cx, cy - (int)(sizey*0.45), cx, cy - (int)(sizey*0.43));
        g2d.rotate(Math.toRadians(10), cx, cy);
        g.drawLine(cx, cy - (int)(sizey*0.45), cx, cy - (int)(sizey*0.43));
        g2d.rotate(Math.toRadians(10), cx, cy);
        g.drawLine(cx, cy - (int)(sizey*0.45), cx, cy - (int)(sizey*0.40));
        g2d.rotate(Math.toRadians(15), cx, cy);
        g.drawLine(cx, cy - (int)(sizey*0.45), cx, cy - (int)(sizey*0.40));
        g2d.rotate(Math.toRadians(-45), cx, cy);
        g2d.rotate(Math.toRadians(-10), cx, cy);
        g.drawLine(cx, cy - (int)(sizey*0.45), cx, cy - (int)(sizey*0.43));
        g2d.rotate(Math.toRadians(-10), cx, cy);
        g.drawLine(cx, cy - (int)(sizey*0.45), cx, cy - (int)(sizey*0.43));
        g2d.rotate(Math.toRadians(-10), cx, cy);
        g.drawLine(cx, cy - (int)(sizey*0.45), cx, cy - (int)(sizey*0.40));
        g2d.rotate(Math.toRadians(-15), cx, cy);
        g.drawLine(cx, cy - (int)(sizey*0.45), cx, cy - (int)(sizey*0.40));
        g2d.rotate(Math.toRadians(45), cx, cy);
        int thickness = 6;
        //draw aircraft static line
        g.setColor(staticLine);
        g.fillRect(cx - (int)(sizex*0.3), cy-(thickness/2), (int)(sizex*0.23), thickness);

        g.fillRect(cx + (int)(sizex*0.07), cy-(thickness/2), (int)(sizex*0.23), thickness);
        g.fillRect(cx -2, cy -2, 4, 4);


        //draw Glideslope indication
        if (displGS) {
            Polygon pgs = new Polygon();
            pgs.addPoint(cx - (int)(sizex * 0.45), cy - 5);
            pgs.addPoint(cx - 4 - (int)(sizex * 0.45), cy);
            pgs.addPoint(cx - (int)(sizex * 0.45), cy + 5);
            pgs.addPoint(cx + 4 - (int)(sizex * 0.45), cy);

            //draw diamonds
            g.drawPolygon(pgs);

            g2d.translate(0, (int)sizey * 0.12);
            g.fillPolygon(pgs);
            g2d.translate(0, (int)sizey * 0.12);
            g.fillPolygon(pgs);
            g2d.translate(0, -(int)sizey * 2 * 0.12);

            g2d.translate(0, -(int)sizey * 0.12);
            g.fillPolygon(pgs);
            g2d.translate(0, -(int)sizey * 0.12);
            g.fillPolygon(pgs);
            g2d.translate(0, (int)sizey * 2 * 0.12);

            int offpix = (int)( 2 * gsOffAngle * sizey * 0.12);
            g.setColor(gsColor);
            g.fillRect(cx - (int)(sizex * 0.45) - 10, cy - 2 + offpix, 20, 4);
        }



        if(noConnection){
            // Draw X over gauge
            g2d.setStroke(new BasicStroke(5));
            g.setColor(Color.red);
            // Bottom Left -> Top Right
            g.drawLine(cx - 300, cy - 300, cx + 300, cy + 300);
            // Top Left -> Bottom Right
            g.drawLine( cx - 300, cy + 300, cx + 300, cy -300);
        }
    }


    void calcTextRectangle(Graphics g, String testtext) {
        Graphics2D g2d = (Graphics2D) g;
        fontMetrics = g2d.getFontMetrics();
        fontRectangle = fontMetrics.getStringBounds(testtext, g2d);
    }

    //draws a centered string
    void drawCenteredString(String text, int _x, int _y, Graphics g) {
        int x = (cx - (int) fontRectangle.getWidth()/2) + _x;
        int y = (cy - (int) fontRectangle.getHeight()/2) + _y + fontMetrics.getAscent();
        g.drawString(text, x, y);
    }


    public double clamp(double value, double minmax) {
        if (value > minmax)
            value = minmax;
        if (value < -minmax)
            value = -minmax;

        return value;
    }


    public void reposition(int _cx, int _cy) {
        cx = _cx;
        cy = _cy;
    }

    public void setDisplayGS(boolean value) {
        displGS = value;
    }
    public void setGSAngle(double value) {
        gsOffAngle = value;
    }


    public void setValues(double _pitch, double _bank) {
        pitch = _pitch;
        bank = _bank;
    }
}

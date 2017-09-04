/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radar.saca;

/**
 *
 * @author Lenovo
 */
public class AvionData {

    public int x;
    public int y;
    public double cap;
    public int vitesse;
    public int altitude;
    
    public AvionData(int x, int y, double cap, int vitesse, int altitude) {
        this.x = x;
        this.y = y;
        this.cap = cap;
        this.vitesse = vitesse;
        this.altitude = altitude;
    }
}

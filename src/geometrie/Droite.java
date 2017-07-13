/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geometrie;

/**
 *
 * @author Lenovo
 */
public class Droite {

    private int pont;
    private int b;
    
    public Droite(int pont, int b) {
        this.pont = pont;
        this.b = b;
    }
    public Droite(Point A, Point B) {
        this.pont = (B.getY() - A.getY()) / (B.getX() - A.getX());
        this.b = A.getY() - this.pont*A.getX();
    }
    
    public int getPont() {
        return pont;
    }

    public void setPont(int pont) {
        this.pont = pont;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
    public boolean siIntersect(Droite d) {
        return !(this.pont == d.pont);
    }
    public Point intersection(Droite d) {
        if(siIntersect(d)) {
            int x , y;
            x = (d.getB() - this.getB()) / (this.getPont() - d.getPont());
            y = this.getPont() * x + this.getB();
            return new Point(x , y);
        } 
        return null;
    }
}

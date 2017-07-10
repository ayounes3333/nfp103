/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radar.saca;

/**
 *
 * @author Ali Younes
 */
public class Avion {

    private String nom;
    private int x;
    private int y;
    private double angle;
    private int vitesse;
    private int altitude;
    private boolean isCrashed = false;
    private boolean isOnACrashCourse = false;
    
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public int getVitesse() {
        return vitesse;
    }

    public void setVitesse(int vitesse) {
        this.vitesse = vitesse;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public boolean isIsCrashed() {
        return isCrashed;
    }

    public void setIsCrashed(boolean isCrashed) {
        this.isCrashed = isCrashed;
    }

    public boolean isIsOnACrashCourse() {
        return isOnACrashCourse;
    }

    public void setIsOnACrashCourse(boolean isOnACrashCourse) {
        this.isOnACrashCourse = isOnACrashCourse;
    }
}

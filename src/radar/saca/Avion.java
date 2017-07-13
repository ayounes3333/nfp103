/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radar.saca;

import com.sun.org.apache.xerces.internal.util.DraconianErrorHandler;
import geometrie.Droite;
import geometrie.Point;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author Ali Younes
 */
public class Avion {

    public static final int VIT_MIN = 200;  //en noeud
    public static final int VIT_MAX = 1000; // en noeud
    public static final int ALT_MIN = 0; //en metres
    public static final int ALT_MAX = 20000; //en mettres
    public static final int LONG_ZONE = 50000; //en Kilo mettres
    public static final int LARG_ZONE = 50000; //en Kilo mettres
    public static final int TEMP_MIN_COLLISION = 10; //en minutes
    public static final int MIN_SAFE_ALT_DIFF = 25; //en mettres
    
    private String nom;
    private String avion;
    private int x;
    private int y;
    private double cap;
    private int vitesse;
    private int altitude;
    private boolean isCrashed = false;
    private boolean isOnACrashCourse = false;

    public String getAvion() {
        return avion;
    }

    public void setAvion(String avion) {
        this.avion = avion;
    }

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

    public double getCap() {
        return cap;
    }

    public void setCap(double angle) {
        this.cap = angle;
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

    void initialiser_avion() {
        // initialisation aleatoire du compteur aléatoire
        Date date = new Date();
        Random rand = new Random(date.getTime());

        // intialisation des paramétres de l'avion
        this.x = 1000 + rand.nextInt() % 1000;
        this.y = 1000 + rand.nextInt() % 1000;
        this.altitude = 900 + rand.nextInt() % 100;

        this.cap = rand.nextInt() % 360;
        this.vitesse = 600 + rand.nextInt() % 200;

        // initialisation du numero de l'avion : chaine de 5 caract�res 
        // formée de 2 lettres puis 3 chiffres
        char[] nomAvion = new char[6];
        nomAvion[0] = (char) ((rand.nextInt() % 26) + (int) 'A');
        nomAvion[1] = (char) ((rand.nextInt() % 26) + (int) 'A');
        nomAvion[2] = (char) ((rand.nextInt() % 26) + (int) 'A');
        nomAvion[3] = (char) ((rand.nextInt() % 10) + (int) '0');
        nomAvion[4] = (char) ((rand.nextInt() % 10) + (int) '0');
        nomAvion[5] = (char) ((rand.nextInt() % 10) + (int) '0');
        this.nom = new String(nomAvion);
    }

    // modifie la valeur de l'avion avec la valeur pass�e en param�tre
    void changer_vitesse(int vitesse) {
        if (vitesse < 0) {
            this.vitesse = 0;
        } else if (vitesse > VIT_MAX) {
            this.vitesse = VIT_MAX;
        } else {
            this.vitesse = vitesse;
        }
    }

    // modifie le cap de l'avion avec la valeur passée en paramètre
    void changer_cap(int cap) {
        if ((cap >= 0) && (cap < 360)) {
            this.cap = cap;
        }
    }

    // modifie l'altitude de l'avion avec la valeur passée en paramètre
    void changer_altitude(int alt) {
        if (alt < 0) {
            this.altitude = 0;
        } else if (alt > ALT_MAX) {
            this.altitude = ALT_MAX;
        } else {
            this.altitude = alt;
        }
    }

    // recalcule la localisation de l'avion en fonction de sa vitesse et de son cap
    public Point calcul_position_apres_temp_minimal() {
        double cosinus, sinus;
        double dep_x, dep_y;

        if (this.vitesse < VIT_MIN) {
            //printf("Vitesse trop faible : crash de l'avion\n");
            //fermer_communication();
            //exit(2);
            return null;
        }
        if (this.altitude == 0) {
            //printf("L'avion s'est ecrase au sol\n");
            //fermer_communication();
            //exit(3);
            return null;
        }
        //cos et sin ont un paramétre en radian, this.cap en degré nos habitudes francophone
        /*  Angle en radian = pi * (angle en degré) / 180 
        */

        cosinus = Math.cos(this.cap * 2 * Math.PI / 360);
        sinus = Math.sin(this.cap * 2 * Math.PI / 360);

        //Pythagore
        dep_x = cosinus / (this.vitesse * TEMP_MIN_COLLISION * 60 / 1.852);
        dep_y = sinus / (this.vitesse * TEMP_MIN_COLLISION * 60 / 1.852);

        // on se deplace d'au moins une case quels que soient le cap et la vitesse
        // sauf si cap est un des angles droit
        if ((dep_x > 0) && (dep_x < 1)) {
            dep_x = 1;
        }
        if ((dep_x < 0) && (dep_x > -1)) {
            dep_x = -1;
        }

        if ((dep_y > 0) && (dep_y < 1)) {
            dep_y = 1;
        }
        if ((dep_y < 0) && (dep_y > -1)) {
            dep_y = -1;
        }
        
        return new Point(this.x + (int) dep_x , this.y + (int) dep_y);
    }
    public boolean siCollision(Avion avion) {
        if(this.altitude - avion.getAltitude() <= MIN_SAFE_ALT_DIFF) {
            Point A = new Point(this.getX() , this.getY());
            Point B = new Point(avion.getX() , avion.getY());
            Point An = this.calcul_position_apres_temp_minimal();
            Point Bn = avion.calcul_position_apres_temp_minimal();
            Droite d1 = new Droite(A , An);
            Droite d2 = new Droite(B ,Bn);
            if(d1.siIntersect(d2)) {
                Point intersection = d1.intersection(d2);
                if(intersection.getX() > A.getX() && intersection.getX() < An.getX()) 
                    return true;
            }
        }
        return false;
    }
    public int tempsAvansCollision(Avion avion) {
        if(this.siCollision(avion)) {
            Point A = new Point(this.getX() , this.getY());
            Point B = new Point(avion.getX() , avion.getY());
            Point An = this.calcul_position_apres_temp_minimal();
            Point Bn = avion.calcul_position_apres_temp_minimal();
            Droite d1 = new Droite(A , An);
            Droite d2 = new Droite(B ,Bn);
            if(d1.siIntersect(d2)) {
                Point intersection = d1.intersection(d2);
                if(intersection.getX() > A.getX() && intersection.getX() < An.getX()) {
                    return (int) ((A.distance(intersection) / 1.852) / this.vitesse) * 60;
                }
            }
        } return -1;
    }
}

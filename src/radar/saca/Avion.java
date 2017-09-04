/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radar.saca;

import networking.Config;
import geometrie.Droite;
import geometrie.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import networking.Message;
import networking.SenderTypes;
import networking.MessageTypes;

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

    //Data Attrs
    private String nom;
    private String avionModelle;
    private int x;
    private int y;
    private double cap;
    private int vitesse;
    private int altitude;
    private boolean isCrashed = false;
    private boolean isOnACrashCourse = false;

    //static instance 
    private static Avion avion;

    //Network Attrs
    private static Socket avionSocket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    
    //Threads
    private static Thread threadRecoitControl;

    public static void main(String[] args) {
        threadRecoitControl = new Thread() {
            @Override
            public void run() {
                Message message;
                while(true) {
                    try {
                        message = (Message) in.readObject();
                        if(message.senderType == SenderTypes.SENDER_SACA) {
                            if(message.messageType == MessageTypes.MESSAGE_CONTROL) {
                                ControlData data = (ControlData) message.payload;
                                switch(data.controlType){
                                    case ContrlTypes.CONTROL_ALTITUDE :
                                        changer_altitude(data.controlData);
                                        break;
                                    case ContrlTypes.CONTROL_CAP: 
                                        changer_cap(data.controlData);
                                        break;
                                    case ContrlTypes.CONTROL_VITESSE :
                                        changer_vitesse(data.controlData);
                                        break;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        System.err.println("radar.saca.Avion.main(): Connexion Terminee!\n" + ex.getMessage());
                        System.exit(-1);
                    } catch (ClassNotFoundException ex) {
                        System.err.println("radar.saca.Avion.main(): Erreur de casting!\n" + ex.getMessage());
                        System.exit(-1);
                    }
                }
            }            
        };
        initialiser_avion();
        initialiser_connexion();
        threadRecoitControl.start();
        while(true) {
            try {
                out.writeObject(new Message(avion.nom , SenderTypes.SENDER_AVION , "Position: " , MessageTypes.MESSAGE_POSITION , new AvionData(avion.x, avion.y, avion.cap, avion.altitude, avion.vitesse)));
                Thread.sleep(1000);
            } catch (IOException ex) {
                System.out.println("Avion.main(): POSIION: ConectionTerminee!" + ex.getMessage());
                System.exit(-1);
            } catch (InterruptedException ex) {
                Logger.getLogger(Avion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }

    public static void initialiser_avion() {
        // initialisation aleatoire du compteur aléatoire
        Date date = new Date();
        Random rand = new Random(date.getTime());

        // intialisation des paramétres de l'avion
        avion.x = 1000 + rand.nextInt() % 1000;
        avion.y = 1000 + rand.nextInt() % 1000;
        avion.altitude = 900 + rand.nextInt() % 100;

        avion.cap = rand.nextInt() % 360;
        avion.vitesse = 600 + rand.nextInt() % 200;

        // initialisation du numero de l'avion : chaine de 5 caract�res 
        // formée de 2 lettres puis 3 chiffres
        char[] nomAvion = new char[6];
        nomAvion[0] = (char) ((rand.nextInt() % 26) + (int) 'A');
        nomAvion[1] = (char) ((rand.nextInt() % 26) + (int) 'A');
        nomAvion[2] = (char) ((rand.nextInt() % 26) + (int) 'A');
        nomAvion[3] = (char) ((rand.nextInt() % 10) + (int) '0');
        nomAvion[4] = (char) ((rand.nextInt() % 10) + (int) '0');
        nomAvion[5] = (char) ((rand.nextInt() % 10) + (int) '0');
        avion.nom = new String(nomAvion);
    }

    public static void initialiser_connexion() {
        try {
            avionSocket = new Socket(Config.SACA_HOST, Config.AVION_PORT);
            out = new ObjectOutputStream(avionSocket.getOutputStream());
            in = new ObjectInputStream(avionSocket.getInputStream());
            out.writeObject(new Message(avion.nom , SenderTypes.SENDER_AVION , "Estableshing Connection" , MessageTypes.MESSAGE_CONNECT , avion));
        } catch (IOException ex) {
            System.err.println("radar.saca.Avion.initialiser_connection(): Erreur de connexion!\n" + ex.getMessage());
            System.exit(-1);
        }
    }

    private static void fermer_connexion() {
        try {
            out.writeObject(new Message(avion.nom, SenderTypes.SENDER_AVION, avion.nom + ": Connexion terminee!", MessageTypes.MESSAGE_LOG));
            avionSocket.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(-1);
        }
    }

    // modifie la valeur de l'avion avec la valeur pass�e en param�tre
    public static void changer_vitesse(int vitesse) {
        if (vitesse < 0) {
            avion.vitesse = 0;
        } else if (vitesse > VIT_MAX) {
            avion.vitesse = VIT_MAX;
        } else {
            avion.vitesse = vitesse;
        }
    }

    // modifie le cap de l'avion avec la valeur passée en paramètre
    public static void changer_cap(int cap) {
        if ((cap >= 0) && (cap < 360)) {
            avion.cap = cap;
        }
    }

    // modifie l'altitude de l'avion avec la valeur passée en paramètre
    public static void changer_altitude(int alt) {
        if (alt < 0) {
            avion.altitude = 0;
        } else if (alt > ALT_MAX) {
            avion.altitude = ALT_MAX;
        } else {
            avion.altitude = alt;
        }
    }

    // recalcule la localisation de l'avion en fonction de sa vitesse et de son cap
    public static Point calcul_position_apres_temp_minimal() {
        double cosinus, sinus;
        double dep_x, dep_y;

        if (avion.vitesse < VIT_MIN) {
            try {
                out.writeObject(new Message(avion.nom, SenderTypes.SENDER_AVION, "Vitesse tres faible", MessageTypes.MESSAGE_CRASH));
            } catch (IOException ex) {
                System.err.println("calcul_position_apres_temp_minimal(): Connexion Terminee!" + ex.getMessage());
            } finally {
                fermer_connexion();
                System.exit(-1);
            }
        }
        if (avion.altitude == 0) {
            try {
                out.writeObject(new Message(avion.nom, SenderTypes.SENDER_AVION, "L'avion s'est ecrase au sol", MessageTypes.MESSAGE_CRASH));
            } catch (IOException ex) {
                System.err.println("calcul_position_apres_temp_minimal(): Connexion Terminee!" + ex.getMessage());
            } finally {
                fermer_connexion();
                System.exit(-1);
            }
        }
        //cos et sin ont un paramétre en radian, this.cap en degré nos habitudes francophone
        /*  Angle en radian = pi * (angle en degré) / 180 
         */

        cosinus = Math.cos(avion.cap * 2 * Math.PI / 360);
        sinus = Math.sin(avion.cap * 2 * Math.PI / 360);

        //Pythagore
        dep_x = cosinus / (avion.vitesse * TEMP_MIN_COLLISION * 60 / 1.852);
        dep_y = sinus / (avion.vitesse * TEMP_MIN_COLLISION * 60 / 1.852);

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

        return new Point(avion.x + (int) dep_x, avion.y + (int) dep_y);
    }

    public static Point calcul_position_apres_temp_minimal(Avion avion) {
        double cosinus, sinus;
        double dep_x, dep_y;

       if (avion.vitesse < VIT_MIN) {
            try {
                out.writeObject(new Message(avion.nom, SenderTypes.SENDER_AVION, "Vitesse tres faible", MessageTypes.MESSAGE_CRASH));
            } catch (IOException ex) {
                System.err.println("calcul_position_apres_temp_minimal(): Connexion Terminee!" + ex.getMessage());
            } finally {
                fermer_connexion();
                System.exit(-1);
            }
        }
        if (avion.altitude == 0) {
            try {
                out.writeObject(new Message(avion.nom, SenderTypes.SENDER_AVION, "L'avion s'est ecrase au sol", MessageTypes.MESSAGE_CRASH));
            } catch (IOException ex) {
                System.err.println("calcul_position_apres_temp_minimal(): Connexion Terminee!" + ex.getMessage());
            } finally {
                fermer_connexion();
                System.exit(-1);
            }
        }
        //cos et sin ont un paramétre en radian, this.cap en degré nos habitudes francophone
        /*  Angle en radian = pi * (angle en degré) / 180 
         */

        cosinus = Math.cos(avion.cap * 2 * Math.PI / 360);
        sinus = Math.sin(avion.cap * 2 * Math.PI / 360);

        //Pythagore
        dep_x = cosinus / (avion.vitesse * TEMP_MIN_COLLISION * 60 / 1.852);
        dep_y = sinus / (avion.vitesse * TEMP_MIN_COLLISION * 60 / 1.852);

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

        return new Point(avion.x + (int) dep_x, avion.y + (int) dep_y);
    }

    public static boolean siCollision(Avion autreAvion) {
        if (avion.altitude - autreAvion.altitude <= MIN_SAFE_ALT_DIFF) {
            Point A = new Point(avion.x, avion.y);
            Point B = new Point(avion.x, avion.y);
            Point An = calcul_position_apres_temp_minimal();
            Point Bn = calcul_position_apres_temp_minimal();
            Droite d1 = new Droite(A, An);
            Droite d2 = new Droite(B, Bn);
            if (d1.siIntersect(d2)) {
                Point intersection = d1.intersection(d2);
                if (intersection.getX() > A.getX() && intersection.getX() < An.getX()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int tempsAvansCollision(Avion autreAvion) {
        if (siCollision(autreAvion)) {
            Point A = new Point(avion.x, avion.y);
            Point B = new Point(autreAvion.x, autreAvion.y);
            Point An = calcul_position_apres_temp_minimal();
            Point Bn = calcul_position_apres_temp_minimal(autreAvion);
            Droite d1 = new Droite(A, An);
            Droite d2 = new Droite(B, Bn);
            if (d1.siIntersect(d2)) {
                Point intersection = d1.intersection(d2);
                if (intersection.getX() > A.getX() && intersection.getX() < An.getX()) {
                    return (int) ((A.distance(intersection) / 1.852) / avion.vitesse) * 60;
                }
            }
        }
        return -1;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radar.saca;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import networking.Config;
import networking.EndPoint;
import networking.Message;
import threads.*;

/**
 *
 * @author Lenovo
 */
public class RadarSACA {

    private static Thread radarConnectionThread;
    private static Thread controllerConnectionThread;
    private static Thread avionConnectionThread;

    private static LinkedList<EndPoint> radarEndPoints;
    private static LinkedList<EndPoint> controllerEndPoints;
    private static LinkedList<EndPoint> avionEndPoints;
    private static LinkedList<ThreadRadar> radarThreads;
    private static LinkedList<ThreadControleur> controllerThreads;
    private static LinkedList<ThreadAvion> avionThreads;

    private static ReentrantLock radarEndPointsLock;
    private static ReentrantLock controllerEndPointsLock;
    private static ReentrantLock avionEndPointsLock;

    private static ServerSocket radarConnectionSocket;
    private static ServerSocket controllerConnectionSocket;
    private static ServerSocket avionConnectionSocket;

    public static void main(String[] args) {
        initialiser_connection();
        radarEndPointsLock = new ReentrantLock();
        controllerEndPointsLock = new ReentrantLock();
        avionEndPointsLock = new ReentrantLock();

        radarEndPoints = new LinkedList<>();
        controllerEndPoints = new LinkedList<>();
        avionEndPoints = new LinkedList<>();
        radarThreads = new LinkedList<>();
        controllerThreads = new LinkedList<>();
        avionThreads = new LinkedList<>();

        radarConnectionThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    Socket clientSocket;
                    try {
                        clientSocket = radarConnectionSocket.accept();
                        EndPoint radarEndPoint = new EndPoint("", new ObjectInputStream(clientSocket.getInputStream()), new ObjectOutputStream(clientSocket.getOutputStream()), clientSocket);
                        radarEndPoint.name = ((Message) radarEndPoint.in.readObject()).senderName;
                        ThreadRadar thread = new ThreadRadar(radarEndPoint);
                        radarEndPointsLock.lock();
                        try {
                            radarEndPoints.add(radarEndPoint);
                        } finally {
                            radarEndPointsLock.unlock();
                        }
                        radarThreads.add(thread);
                        thread.start();

                    } catch (IOException ex) {
                        System.err.println("radar.saca.RadarSACA.radarConnectionThread: Erreur de connexion!\n" + ex.getMessage());
                    } catch (ClassNotFoundException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            }
        };
        controllerConnectionThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    Socket clientSocket;
                    try {
                        clientSocket = controllerConnectionSocket.accept();
                        EndPoint controllerEndPoint = new EndPoint("", new ObjectInputStream(clientSocket.getInputStream()), new ObjectOutputStream(clientSocket.getOutputStream()), clientSocket);
                        controllerEndPoint.name = ((Message) controllerEndPoint.in.readObject()).senderName;
                        ThreadControleur thread = new ThreadControleur(controllerEndPoint);
                        controllerEndPointsLock.lock();
                        try {
                            controllerEndPoints.add(controllerEndPoint);
                        } finally {
                            controllerEndPointsLock.unlock();
                        }
                        controllerThreads.add(thread);
                        thread.start();

                    } catch (IOException ex) {
                        System.err.println("radar.saca.RadarSACA.controllerConnectionThread: Erreur de connexion!\n" + ex.getMessage());
                    } catch (ClassNotFoundException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            }
        };
        avionConnectionThread = new Thread() {
            @Override
            public void run() {
                Socket clientSocket;
                while (true) {
                    try {
                        clientSocket = avionConnectionSocket.accept();
                        EndPoint avionEndPoint = new EndPoint("", new ObjectInputStream(clientSocket.getInputStream()), new ObjectOutputStream(clientSocket.getOutputStream()), clientSocket);
                        avionEndPoint.name = ((Message) avionEndPoint.in.readObject()).senderName;
                        ThreadAvion thread = new ThreadAvion(avionEndPoint);
                        avionEndPointsLock.lock();
                        try {
                            avionEndPoints.add(avionEndPoint);
                        } finally {
                            avionEndPointsLock.unlock();
                        }
                        avionThreads.add(thread);
                        thread.start();

                    } catch (IOException ex) {
                        System.err.println("radar.saca.RadarSACA.avionConnectionThread: Erreur de connexion!\n" + ex.getMessage());
                    } catch (ClassNotFoundException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            }
        };
        
        radarConnectionThread.start();
        controllerConnectionThread.start();
        avionConnectionThread.start();
    }

    private static void initialiser_connection() {
        try {
            radarConnectionSocket = new ServerSocket(Config.RADAR_PORT);
            controllerConnectionSocket = new ServerSocket(Config.CONTROLLER_PORT);
            avionConnectionSocket = new ServerSocket(Config.AVION_PORT);
        } catch (IOException ex) {
            System.err.println("radar.saca.RadarSACA.initialiser_connection(): Erreur de connexion!\n" + ex.getMessage());
        }
    }

}

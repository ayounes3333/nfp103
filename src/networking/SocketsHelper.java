/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Random;

/**
 *
 * @author Lenovo
 */
public class SocketsHelper {

    private static final int MAX_PORT_NUMBER = 7999;
    private static final int MIN_PORT_NUMBER = 1024;

    public synchronized static boolean isPortAvailable(int port) {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }
    public synchronized static int getAnAvailablePort() {
        Random random = new Random(System.currentTimeMillis());
        int port = 0;
        do { 
            port = 1024 + (random.nextInt() % 6975);
        } while(!isPortAvailable(port));
        return port;
    }
}

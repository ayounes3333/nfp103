/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Lenovo
 */
public class EndPoint {

    public String name;
    public ObjectInputStream in;
    public ObjectOutputStream out;
    public Socket socket;
    
    public EndPoint(String name, ObjectInputStream in, ObjectOutputStream out, Socket socket) {
        this.name = name;
        this.in = in;
        this.out = out;
        this.socket = socket;
    }
}

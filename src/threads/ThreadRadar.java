/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import networking.EndPoint;

/**
 *
 * @author Lenovo
 */
public class ThreadRadar extends Thread {

    private final EndPoint radarEndPoint;

    @Override
    public void run() {
        
    }
    
    public ThreadRadar(EndPoint radarEndPoint) {
        this.radarEndPoint = radarEndPoint;
    }
    
}

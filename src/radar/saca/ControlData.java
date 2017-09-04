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
public class ControlData {
    public int controlType;
    public int controlData;
    
    public ControlData(int type , int data) {
        this.controlData = data;
        this.controlType = type;
    }
}

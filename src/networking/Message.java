/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

/**
 *
 * @author Lenovo
 */
public class Message {

    public String senderName;
    public int senderType;
    public String messageContents;
    public int messageType;
    public Object payload;
    
    public Message(String senderName, int senderType, String messageContents, int messageType) {
        this.senderName = senderName;
        this.senderType = senderType;
        this.messageContents = messageContents;
        this.messageType = messageType;
        this.payload = null;
    }
    public Message(String senderName, int senderType, String messageContents, int messageType , Object payload) {
        this.senderName = senderName;
        this.senderType = senderType;
        this.messageContents = messageContents;
        this.messageType = messageType;
        this.payload = payload;
    }
}

/*
 * POP3Auth.java
 *
 * Created on 2004년 10월 6일 (수), 오후 9:33
 */

package control;

import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.event.*;
import javax.mail.internet.*;
import com.sun.mail.pop3.*;
import javax.activation.*;

/**
 *
 * @author  jongmin
 */
public class POP3Auth {
    
    /** Creates a new instance of POP3Auth */
    public POP3Auth(String host, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;
    }
    
    private String host;
    
    private String user;
    
    private String password;
    
    /**
     * Goal: POP3 인증을 통한 사용자 인증
     * Return value:
     *    - true: (user, password) auth successful
     *    - false: auth failed.
     */
    public boolean DoAuthentication() {
        // Create some properties.
        Properties props = System.getProperties();
        props.put("mail.pop3.apop.enable", true);
        props.put("mail.pop3.host", this.host);
        props.put("mail.pop3.user", this.user);
        
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);
        
        POP3Store store = null;
        boolean authSuccessful;
        try {
            store = (POP3Store)session.getStore("pop3");
            store.connect(this.host, this.user, this.password);
            authSuccessful = store.isConnected();
            store.close();
        }
        catch (Exception ex) {
            System.out.println("POP3Auth.DoAuthentication() : " + ex);
            return false;
        }
        
        return authSuccessful;
    }
    
}

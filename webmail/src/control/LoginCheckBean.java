/*
 * LoginCheckBean.java
 *
 * Created on 2004³â 10¿ù 2ÀÏ
 */

package control;

/**
 *
 * @author  Administrator
 */
public class LoginCheckBean {
    
    /**
     * Holds value of property userid.
     */
    private String userid;
    
    /**
     * Holds value of property passwd.
     */
    private String passwd;
    
    /** Creates a new instance of loginTestBean */
    public LoginCheckBean() {
    }
    
    /**
     * Setter for property userid.
     * @param userid New value of property userid.
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }
    
    /**
     * Setter for property passwd.
     * @param passwd New value of property passwd.
     */
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
    
    private boolean auth_success = false;
    
    /**
     * Holds value of property host.
     */
    private String host;
    
    

    public boolean linuxAuth() {
        POP3Auth auth = new POP3Auth(this.host, this.userid, this.passwd);
        return auth.DoAuthentication();
    }
    
    /**
     * Getter for property host.
     * @return Value of property host.
     */
    public String getHost() {
        return this.host;
    }
    
    /**
     * Setter for property host.
     * @param host New value of property host.
     */
    public void setHost(String host) {
        this.host = host;
    }
 
}

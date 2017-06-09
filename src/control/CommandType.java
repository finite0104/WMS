/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

/**
 *
 * @author haei
 */
public class CommandType {
    
    public enum InOutState { 
     LOGIN(91), LOGOUT(92);
     

     private int value;

     InOutState(int value) {
         this.value = value;
     }

     public int getValue() { 
         return value;
     }
    }
    
    public enum AddDelMenu{
     ADD_USER_MENU(3), DELETE_USER_MENU(4);
     
     private int value;
     AddDelMenu(int value) {
         this.value = value;
     }

     public int getValue() { 
         return value;
     }
    }
    
    public enum SendDelDown{
     SEND_MAIL_COMMAND(5), DELETE_MAIL_COMMAND(6), DOWNLOAD_COMMAND(7);
     
     private int value;
     SendDelDown(int value) {
         this.value = value;
     }

     public int getValue() { 
         return value;
     }
    }
    
    public enum AddDelCommand{
     ADD_USER_COMMAND(8), DELETE_USER_COMMAND(9);
     
     private int value;
     AddDelCommand(int value) {
         this.value = value;
     }

     public int getValue() { 
         return value;
     }      
    }
}


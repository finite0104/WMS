/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author haei
 */
public class UserAdminAgent {
    private String server;
    private int port;
    Socket socket = null;
    InputStream is = null;
    OutputStream os = null;
    boolean isConnected = false;
    private final String ROOT_ID = "root";
    private final String ROOT_PASSWORD = "root";
    private final String ADMIN_ID = "admin";
    private final String EOL = "\r\n";
    
    public UserAdminAgent(String server, int port)throws Exception{
        this.server = server;
        this.port = port;
        isConnected = connect();
    }
    
    public boolean addUser(String userID, String password) throws Exception{
        boolean status = false;
        byte[] messageBuffer = new byte[1024];
        System.out.println("addUser() called");
        if(!isConnected){
            System.out.println("status is :" + status);///command to log
            return status;
        }
        
        
        try{
            String addUserCommand = "adduser" +" " +userID + " " + password + EOL;
            os.write(addUserCommand.getBytes());
            
            java.util.Arrays.fill(messageBuffer, (byte)0);
            
            is.read(messageBuffer);
            String recvMessage = new String(messageBuffer);
            System.out.println(recvMessage);
            
            if(recvMessage.contains("added")){
                status = true;
            }else{
                status = false;
            }
            
            quit();
            System.out.flush();
            socket.close();
        }catch(Exception ex){
            System.out.println(ex.toString());
            status = false;
        }finally{
            return status;
        }
    }
    
    public List<String> getUserList() throws Exception{
        List<String> userList = new LinkedList<String>();
        byte[] messageBuffer = new byte[1024];
        
        if(!isConnected){
            System.out.println("ok?" + userList);
            return userList;
        }
        
        try{
            //send command
            String command = "listusers" + EOL;
            os.write(command.getBytes());
            
            //get results of listusers 
            java.util.Arrays.fill(messageBuffer, (byte)0);
            is.read(messageBuffer);
            
            String recvMessage = new String(messageBuffer);
            System.out.println("##"+recvMessage+"##");
            userList = parseUserList(recvMessage);
            
            quit();
            
        }catch(IOException ex){
            System.out.println(ex);
        }finally{
            return userList;
        }
    }

    private List<String> parseUserList(String message) throws NumberFormatException{
        List<String> userList = new LinkedList<String>();
        
        String[] lines = message.split(EOL);
        String[] firstLine = lines[0].split(" ");
        String[] first = firstLine[2].split("\n");
        
        int numberOfUsers = Integer.parseInt(first[0]);
        
        for(int i = 1; i<=numberOfUsers; i++){
            String[] userLine = lines[0].split("user: ");
            if(!userLine[i].equals(ADMIN_ID)){
                userList.add(userLine[i]);
            }
        }
        return userList;
    }
    
    private boolean connect() throws Exception{
        byte[] messageBuffer = new byte[1024];
        boolean returnVal = false;
        String sendMessage;
        
        System.out.println("UserAdminAgetn.connect() called..");
        
        socket = new Socket(server, port);
        is = socket.getInputStream();
        os = socket.getOutputStream();
        
        is.read(messageBuffer);
        String recvMessage = new String(messageBuffer);
        System.out.println(recvMessage);
        
        sendMessage = ROOT_ID + EOL;
        os.write(sendMessage.getBytes());
        
        java.util.Arrays.fill(messageBuffer, (byte)0);
        is.read(messageBuffer);
        recvMessage = new String(messageBuffer);
        System.out.println(recvMessage);
        
        sendMessage = ROOT_PASSWORD + EOL;
        os.write(sendMessage.getBytes());
        
        java.util.Arrays.fill(messageBuffer, (byte)0);
        is.read(messageBuffer);
        recvMessage = new String(messageBuffer);
        System.out.println(recvMessage);
        
        if(recvMessage.contains("Welcome")){
            returnVal = true;
        }else{
            returnVal = false;
        }
        return returnVal;
    }
    
    private boolean quit()  {
        byte[] messageBuffer = new byte[1024];
        boolean status = false;
        
        try{
            String quitCommand = "quit" + EOL;
            os.write(quitCommand.getBytes());
            java.util.Arrays.fill(messageBuffer, (byte)0);
            is.read(messageBuffer);
            String recvMessage = new String(messageBuffer);
            System.out.println(recvMessage);
            if(recvMessage.contains("closed")){
                status = true;
            }else{
                status = false;
            }
        }catch(IOException ex){
            System.out.println("UserAdminAgent.quit()" + ex);
        }finally{
            return status;
        }
    }
    
    public boolean deleteUsers(String[] userList) throws IOException{
        byte[] messageBuffer = new byte[1024];
        String command;
        String recvMessage;
        boolean status = false;
        if(!isConnected){
            return status;
        }
        
        try{
            for(String userID : userList){
                command = "deluser " + userID + EOL;
                os.write(command.getBytes());
                System.out.println(command);
                
                java.util.Arrays.fill(messageBuffer, (byte)0);
                is.read(messageBuffer);
                
                recvMessage = new String(messageBuffer);
                System.out.println(recvMessage);
                if(recvMessage.contains("deleted")){
                    status = true;
                }
            }
            quit();
        }catch(Exception ex){
            System.out.println("&&&"+ ex);
        }finally{
            return status;
        }
    }

}

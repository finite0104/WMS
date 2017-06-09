package control;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import control.CommandType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author haei
 * @since 06/01
 */
public class UserAdminHandler extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        RequestDispatcher view = request.getRequestDispatcher("admin_menu.jsp");
        
        try {
            request.setCharacterEncoding("UTF-8");
            
            int select = Integer.parseInt((String)request.getParameter("command"));/////////error
            
            if(select == CommandType.AddDelCommand.ADD_USER_COMMAND.getValue()){
                addUser(request, response, out);
            }
            else if(select == CommandType.AddDelCommand.DELETE_USER_COMMAND.getValue()){
                deleteUsers(request, response, out);
            }else{
                out.println("No Menu. Get Out of here!");
            }
           
        }catch(IOException ex){
            out.println(ex.toString());
        } catch (NumberFormatException ex) {
            out.println(ex.toString());
        }finally{
            out.close();
        }
    }    
        
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void addUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String server = "127.0.0.1";
        int port = 4555;
        try{
            UserAdminAgent agent = new UserAdminAgent(server, port);
            
            String userid = request.getParameter("id");
            String password = request.getParameter("password");
            
            out.println("userid ="+userid + "<br>");
            out.println("password ="+password + "<br>");
            out.flush();
            
            if(agent.addUser(userid, password)){
                out.println("Go to main.jsp");////////////need to change error
                response.sendRedirect("admin_menu.jsp");
                //getUserAddSuccessPopUp();
            }
            else{
                getUserAddFailurePopUp();
            }
            out.flush();
        }catch(Exception ex){
            out.println("Fail to access in System.");
        }
    }
    
    

    private String getUserAddSuccessPopUp() {
        
        String alertMessage = "Success in adding user!!!";
        StringBuilder successPopup = new StringBuilder();
        successPopup.append("<html>");
        successPopup.append("<head>");
        successPopup.append("<title> Result of adding users </title>");
        successPopup.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"css/index.css\" />");
        successPopup.append("</head>");
        successPopup.append("<body onload=\"goMainMenu()\">");
        successPopup.append("<script type=\"text/javascript\">");
        successPopup.append("function goMainMenu(){");
        successPopup.append("alert(\"");
        successPopup.append(alertMessage);
        successPopup.append("\");");
        successPopup.append("window.location = \"admin_menu.jsp\";");
        successPopup.append("} </script>");
        successPopup.append("</body></html>");
        return successPopup.toString();
        
        /*
        PrintWriter out = null;
        out.println("successed in add");
        
        File f = new File("../WebPages/results/UserAddSuccessPopUp.jsp");
        
        try {
          ////////////////////////////////////////////////////////////////
          BufferedReader in = new BufferedReader(new FileReader(f));
          String s;

          while ((s = in.readLine()) != null) {
            System.out.println(s);
          }
          in.close();
          ////////////////////////////////////////////////////////////////
        } catch (IOException e) {
            System.err.println(e); // 에러가 있다면 메시지 출력
            System.exit(1);
        }*/
    }

    private String getUserAddFailurePopUp() {
        String alertMessage = "Failed in adding user..";
        StringBuilder successPopup = new StringBuilder();
        successPopup.append("<html>");
        successPopup.append("<head>");
        successPopup.append("<title> Result of adding users </title>");
        successPopup.append("</head>");
        successPopup.append("<body onload=\"goMainMenu()\">");
        successPopup.append("<script type=\"text/javascript\">");
        successPopup.append("function goMainMenu(){");
        successPopup.append("alert(\"");
        successPopup.append(alertMessage);
        successPopup.append("\");");
        successPopup.append("window.location = \"admin_menu.jsp\";");
        successPopup.append("} </script>");
        successPopup.append("</body></html>");
        return successPopup.toString();
        /*
        PrintWriter out = null;
        out.println("successed in add");
        
        File f = new File("../WebPages/results/UserAddSuccessPopUp.jsp");
        
        try {
          ////////////////////////////////////////////////////////////////
          BufferedReader in = new BufferedReader(new FileReader(f));
          String s;

          while ((s = in.readLine()) != null) {
            System.out.println(s);
          }
          in.close();
          ////////////////////////////////////////////////////////////////
        } catch (IOException e) {
            System.err.println(e); // 에러가 있다면 메시지 출력
            System.exit(1);
        }*/
    }

    private void deleteUsers(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String server = "127.0.0.1";
        int port = 4555;
        try{
            UserAdminAgent agent = new UserAdminAgent(server, port);
            
            String[] deleteUserList = request.getParameterValues("selectedUsers");
            agent.deleteUsers(deleteUserList);
            response.sendRedirect("../admin_menu.jsp");
            
        }catch(Exception ex){
            System.out.println("UserAdminHandler.deleteUser Exception ="+ ex);
        }
    }

}

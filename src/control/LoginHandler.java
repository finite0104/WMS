/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import control.CommandType.InOutState;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author haei
 */
public class LoginHandler extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private final String ADMINISTRATOR = "admin";
    private final String homeDirectory = "/webmail/index.jsp";
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        int command = Integer.parseInt((String)request.getParameter("command"));
        LoginCheckBean login_check = new LoginCheckBean();
        
        try {
            // switch is not allowed enum. thus, "enum" need to use "if"
            if(command == InOutState.LOGIN.getValue())
            {
                    /**
                     * if you want to login, you should through LoginCheckBean.java
                     */
                    login_check.setHost((String) request.getSession().getAttribute("host"));
                    login_check.setPasswd(request.getParameter("userid"));
                    login_check.setUserid(request.getParameter("passwd"));
                    
                    String userid = login_check.getUserid();
                    String password = login_check.getPasswd();
                                       
                    if(login_check.linuxAuth()){
                        boolean admin = isAdmin(userid);
                        if(admin){
                            session.setAttribute("userid",userid);
                            response.sendRedirect("admin_menu.jsp");//*********ADD
                        }else{
                            session.setAttribute("userid", userid);
                            session.setAttribute("password", password);
                            response.sendRedirect("menu_form.jsp");
                        }
                    }else{
                        response.sendRedirect("login_error.jsp");
                    }
            }//if
            else if(command == InOutState.LOGOUT.getValue())
            {
                out = response.getWriter();
                session.invalidate();
                response.sendRedirect(homeDirectory);
            }
        }catch(Exception ex){
            System.out.println("LoginCheck = LOGIN error :" + ex);
        }finally{
            out.close();
        }
    }

    protected boolean isAdmin(String userid){
        boolean status = false;
        if(userid.equals(this.ADMINISTRATOR)){
            status = true;
        }
        return status;
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

}

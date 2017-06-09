/*
 * DownloadServlet.java
 *
 */

package control;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;


/**
 *
 * @author  jongmin
 * @version
 */
public class DownloadServlet extends HttpServlet {
    
    /** Initializes the servlet.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
    }
    
    /** Destroys the servlet.
     */
    public void destroy() {
        
    }
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("application/octet-stream");
        
        ServletOutputStream sos = response.getOutputStream();
        
        request.setCharacterEncoding("euc-kr");
        String fileName = new String(request.getParameter("filename").getBytes("8859_1"), "euc-kr");

        fileName = fileName.replaceAll(" ", "+");
        String userid = request.getParameter("userid");
        //String fileName = URLDecoder.decode(request.getParameter("filename"), "utf-8");
        


        //String downloadDir = "/var/spool/webmail/download/";

        // �������� ȯ�� ����
        String downloadDir = "C:/temp/download/";
        // LJM 090430 : �����ؾ� �� �κ� - end   ------------------

        response.setHeader("Content-DIsposition", "attachment; filename=" + fileName + ";");
        
        File f = new File(downloadDir + userid + "/" + fileName);
        byte[] b = new byte[(int)f.length()];
        FileInputStream fis = new FileInputStream(f);
        fis.read(b);

        // �ٿ�ε�
        sos.write(b);
        sos.flush();
        
        // �ٿ�ε��� ���� ����
        //f.delete();
        
        sos.close();
    }
    
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    
}

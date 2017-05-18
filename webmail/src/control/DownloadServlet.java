/*
 * DownloadServlet.java
 *
 * Created on 2004�� 12�� 2�� (��), ���� 8:31
 */

package control;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import javax.mail.internet.*;  // LJM 041202 - MimeUtility class

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
        
        /* TODO output your page here */
        request.setCharacterEncoding("euc-kr");
        // LJM 041203 - �Ʒ��� ���� �ؼ� �ѱ����ϸ� ����� �νĵǴ� �� Ȯ������.
        String fileName = new String(request.getParameter("filename").getBytes("8859_1"), "euc-kr");
        // fileName�� �ִ� ' '�� '+'�� �Ķ���ͷ� ���۵Ǵ� �������� ���� ���̹Ƿ�
        // �ٽ� ��ȯ������.
        fileName = fileName.replaceAll(" ", "+");
        String userid = request.getParameter("userid");
        //String fileName = URLDecoder.decode(request.getParameter("filename"), "utf-8");
        
        // download�� ���� �б�

        // LJM 090430 : �����ؾ� �� �κ� - start ------------------
        // ������ ���� ����
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

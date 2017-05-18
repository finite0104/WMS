/*
 * DownloadServlet.java
 *
 * Created on 2004년 12월 2일 (목), 오후 8:31
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
        // LJM 041203 - 아래와 같이 해서 한글파일명 제대로 인식되는 것 확인했음.
        String fileName = new String(request.getParameter("filename").getBytes("8859_1"), "euc-kr");
        // fileName에 있는 ' '는 '+'가 파라미터로 전송되는 과정에서 변한 것이므로
        // 다시 변환시켜줌.
        fileName = fileName.replaceAll(" ", "+");
        String userid = request.getParameter("userid");
        //String fileName = URLDecoder.decode(request.getParameter("filename"), "utf-8");
        
        // download할 파일 읽기

        // LJM 090430 : 수정해야 할 부분 - start ------------------
        // 리눅스 서버 사용시
        //String downloadDir = "/var/spool/webmail/download/";

        // 윈도우즈 환경 사용시
        String downloadDir = "C:/temp/download/";
        // LJM 090430 : 수정해야 할 부분 - end   ------------------

        response.setHeader("Content-DIsposition", "attachment; filename=" + fileName + ";");
        
        File f = new File(downloadDir + userid + "/" + fileName);
        byte[] b = new byte[(int)f.length()];
        FileInputStream fis = new FileInputStream(f);
        fis.read(b);

        // 다운로드
        sos.write(b);
        sos.flush();
        
        // 다운로드후 파일 삭제
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

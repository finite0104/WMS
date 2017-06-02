/*
 * smtpServlet.java
 *
 * Created on 2004년 11월 30일 (화), 오후 6:45
 */

package control;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

import java.util.*;
import org.apache.commons.fileupload.*;

/**
 *
 * @author  jongmin
 * @version
 */
public class smtpServlet extends HttpServlet {


    // LJM 090430 : 수정해야 할 부분 - start ------------------
    // 윈도우즈 환경 실행시
    // uploadTempDir과 uploadTargetDir를 동일 디렉토리로 하도록 권장함.
    //final private String uploadTempDir = "C:/temp/upload/";
    //final private String uploadTargetDir = "C:/temp/upload/";

    // 리눅스 환경 실행시
    //final private String uploadTempDir = "/var/spool/webmail/";
    //final private String uploadTargetDir = "/var/spool/webmail/";
    // LJM 090430 : 수정해야 할 부분 - end   ------------------
    private final String OSName = System.getProperty("os.name").toLowerCase();
    String uploadTempDir;
    String uploadTargetDir;
    
    private void setDirectory() {
        if(OSName.contains("win")) {
            //window 운영체제
            uploadTempDir = "C:/temp/upload/";
            uploadTargetDir = "C:/temp/upload/";
        }else if(OSName.contains("nix") 
                || OSName.contains("nux")|| OSName.contains("aix")) {
            //unix 운영체제들
            uploadTempDir = "/var/spool/webmail";
            uploadTargetDir = "/var/spool/webmail";
        }
    }
    
    /** Initializes the servlet.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        setDirectory();
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
        response.setContentType("text/html; charset=euc-kr");
        PrintWriter out = response.getWriter();
        /* TODO output your page here */
        JspFactory _jspxFactory = null;
        PageContext pageContext = null;
        HttpSession session = null;
        
        _jspxFactory = JspFactory.getDefaultFactory();
        pageContext = _jspxFactory.getPageContext(this, request, response,
        null, true, 8192, true);
        session = pageContext.getSession();
        
        DiskFileUpload fu = new DiskFileUpload();
        fu.setSizeMax(50*1024*1024); // 최대 40 Mbytes upload 가능
        fu.setSizeThreshold(512);  // 1024 bytes
        fu.setRepositoryPath(uploadTempDir);
        
        String file1 = null;
        String file2 = null;

        MailHandleBean mhb = new MailHandleBean();
        mhb.setHost((String)session.getAttribute("host"));  System.out.println("*** SMTP host = " + mhb.getHost());
        mhb.setUserid((String)session.getAttribute("userid"));
        mhb.setPasswd((String)session.getAttribute("passwd"));
        
        try {
            List fileItems = fu.parseRequest(request);
            Iterator i = fileItems.iterator();
            while (i.hasNext()) {
                FileItem fi = (FileItem)i.next();
                out.println("<br> Content type: " + fi.getContentType());
                if ( fi.getContentType() == null ) {
                    // maybe field variable...
                    //String str = new String(fi.getString().getBytes("8859_1"), "euc-kr");
                    String str = fi.getString();
                    
                    out.println("field name = " + fi.getFieldName());
                    //out.println(": " + fi.getString() + "<br>");
                    out.println(": " + str + "<br>");
                    
                    String fieldName = fi.getFieldName();
                    
                    if (fieldName.equals("to"))
                        mhb.setTo(str);
                    else if (fieldName.equals("cc"))
                        mhb.setCc(str);
                    else if (fieldName.equals("subj"))
                        mhb.setSubj(str);
                    else if (fieldName.equals("body"))
                        mhb.setMsgBody(str);
                    
                }
                else {  // attach files...
                    String fileName = fi.getName();
                    out.println("File name = " + fileName);
                    File fn = new File(uploadTargetDir + fileName);
                    // upload 완료. 추후 메일 전송후 해당 파일을 삭제하도록 해야 함.
                    if (fileName != null) fi.write(fn);
                    
                    if (fileName != null && fi.getFieldName().equals("file1")) {
                        file1 = uploadTargetDir + fileName;
                        mhb.setFile1(file1);
                        out.println("<br> file1 = " + file1);
                    }
                    else if (fileName != null && fi.getFieldName().equals("file2")) {
                        file2 = uploadTargetDir + fileName;
                        mhb.setFile2(file2);
                        out.println("<br> file2 = " + file2);
                    }
                }
            }
        }  // try {}
        catch (Exception ex) {
            out.println("smtpServlet exception: " + ex);
        }
        
        if (mhb.sendMessage()) {
            out.println("메일이 성공적으로 전송되었습니다.");
        }
        else
            out.println("메일 전송이 실패하였습니다.");
        
        out.close();
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
    
    private boolean upload(HttpServletRequest request, PrintWriter out) {
        DiskFileUpload fu = new DiskFileUpload();
        fu.setSizeMax(20*1024*1024); // 10 Mbytes
        fu.setSizeThreshold(1024);  // 1024 bytes
        fu.setRepositoryPath(uploadTempDir);
        
        try {
            List fileItems = fu.parseRequest(request);
            Iterator i = fileItems.iterator();
            while (i.hasNext()) {
                FileItem fi = (FileItem)i.next();
                String fileName = fi.getName();
                out.println("File name = " + fileName);
                File fn = new File(uploadTargetDir + fileName);
                fi.write(fn);
            }
        }
        catch (Exception ex) {
            return false;
        }
        return true;  // upload successful!!!
    }
    
//    private String getFileNameOnly(String fileName) {
//        String result = null;
//        int index;
//        if ( (index = fileName.lastIndexOf('/')) != -1 ) {
//            result = fileName.substring(index+1);
//        }
//        else if ( (index = fileName.lastIndexOf('\\')) != -1 ) {
//            result = fileName.substring(index+1);
//        }
//        return result;
//    }
}

package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class add_005fuser_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html;charset=x-windows-949");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE html>\n");
      out.write("<html>\n");
      out.write("    <head>\n");
      out.write("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=x-windows-949\">\n");
      out.write("        <title>회원가입</title>\n");
      out.write("    </head>\n");
      out.write("    <body>\n");
      out.write("        ");
      org.apache.jasper.runtime.JspRuntimeLibrary.include(request, response, "header.jsp", out, false);
      out.write("\n");
      out.write("        \n");
      out.write("        <div id=\"sidebar\">\n");
      out.write("            ");
      org.apache.jasper.runtime.JspRuntimeLibrary.include(request, response, "sidebar_admin_previous_menu.jsp", out, false);
      out.write("\n");
      out.write("        </div>\n");
      out.write("        \n");
      out.write("        <div id=\"main\">\n");
      out.write("            추가로 등록할 사용자 ID와 암호를 입력해 주시기 바랍니다. <br> <br>\n");
      out.write("            \n");
      out.write("            <form name=\"AddUser\"\n");
      out.write("                  method=\"POST\">\n");
      out.write("                  <table border=\"0\" align=\"left\">\n");
      out.write("                      <tr>\n");
      out.write("                          <td>사용자 ID</td>\n");
      out.write("                          <td> <input type=\"text\" name=\"id\" value=\"\"\n");
      out.write("                                      size=\"20\" /> </td>\n");
      out.write("                      </tr>\n");
      out.write("                      <tr>\n");
      out.write("                          <td>암호</td>\n");
      out.write("                          <td> <input type=\"password\" name=\"password\"\n");
      out.write("                                      value=\"\" /> </td>\n");
      out.write("                      </tr>\n");
      out.write("                      <tr>\n");
      out.write("                          <td colspan=\"2\">\n");
      out.write("                              <input type=\"submit\" value=\"추가\"\n");
      out.write("                                     name=\"register\" />\n");
      out.write("                              <input type=\"reset\" value=\"초기화\"\n");
      out.write("                                     name=\"reset\" />\n");
      out.write("                          </td>\n");
      out.write("                      </tr>\n");
      out.write("                </table>\n");
      out.write("        </form>\n");
      out.write("        </div>\n");
      out.write("                  \n");
      out.write(" \n");
      out.write("    </body>\n");
      out.write("</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}

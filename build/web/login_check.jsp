<%@page contentType="text/html; charset=euc-kr"%>
<%@page pageEncoding="UTF-8"%>

<%-- <<control --%>

<jsp:useBean id="login_check" scope="page" class="control.LoginCheckBean"/>
<jsp:setProperty name="login_check" property="userid"/>
<jsp:setProperty name="login_check" property="passwd"/>
<%
login_check.setHost( (String)session.getAttribute("host") );
%>

<html>
    <head><title>Login Check Page</title></head>
    <body>

    <% if (login_check.linuxAuth()) { 
        session.setAttribute("userid", request.getParameter("userid"));
        session.setAttribute("passwd", request.getParameter("passwd"));
    %>
       <jsp:forward page="menu_form.jsp"/>
    <% }
       else { %>
       <jsp:forward page="login_error.jsp"/>
    <%   }
     %>
    
    </body>
</html>

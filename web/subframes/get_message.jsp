<%@page contentType="text/html; charset=euc-kr"%>
<%@page pageEncoding="UTF-8"%>

<jsp:useBean id="mget" scope="page" class="control.MailHandleBean"/>
<%--<jsp:setProperty name="mget" property="<%= session.getAttribute("userid") %>"/>
<jsp:setProperty name="mget" property="<%= session.getAttribute("passwd") %>"/>
--%>
<% 
mget.setHost((String)session.getAttribute("host"));
mget.setUserid((String)session.getAttribute("userid"));
mget.setPasswd((String)session.getAttribute("passwd"));
%>

<html>
    <head><title>Get MessageBody Page</title></head>
    <body bgcolor="#CCFF99">
    
    <%-- Menu for reply, forward, delete, etc. --%>
    <%-- <jsp:include page="mail_handling_menu.jsp/> --%>

    <font face="굴림" size="3">
    <span style="background-color: #CCFF99">
    <% out.print("<a href=\"reply.jsp?msgid=" + request.getParameter("msgid") + "\"> 답장 </a>,"); %> 
    재전송, 
    <% out.print("<a href=\"delete_message.jsp?msgid=" + request.getParameter("msgid") + "\"> 삭제 </a></html>"); %>  
    </span>
    </font>
    <hr>

    
     <%= mget.getMessageBody(Integer.parseInt(request.getParameter("msgid"))) %>  
     


    </body>
</html>

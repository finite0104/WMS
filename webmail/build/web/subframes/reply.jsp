<%@page contentType="text/html; charset=euc-kr"%>
<%@page pageEncoding="UTF-8"%>

<jsp:useBean id="reply" scope="page" class="control.MailHandleBean"/>

<% 
reply.setHost((String)session.getAttribute("host"));
reply.setUserid((String)session.getAttribute("userid"));
reply.setPasswd((String)session.getAttribute("passwd"));
%>

<html>
    <head><title>Reply Page</title></head>
    <body bgcolor="#CCFF99">

        <%= reply.showReplyForm(Integer.parseInt((String)request.getParameter("msgid"))) %>

    </body>
</html>

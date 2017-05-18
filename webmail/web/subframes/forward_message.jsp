<%-- 
    Document   : forward_message
    Created on : 2017. 5. 18, 오후 5:12:08
    Author     : 918-16
--%>

<%@page contentType="text/html; charset=euc-kr"%>
<%@page pageEncoding="UTF-8"%>

<jsp:useBean id="forward" scope="page" class="control.MailHandleBean"/>

<% 
forward.setHost((String)session.getAttribute("host"));
forward.setUserid((String)session.getAttribute("userid"));
forward.setPasswd((String)session.getAttribute("passwd"));
%>

<!DOCTYPE html>
<html>
    <head>
        <title>Forwarding Mail Page</title>
    </head>
    <body bgcolor="#CCFF99">
        <%= forward.showForwardingMailForm(Integer.parseInt(request.getParameter("msgid"))) %>
    </body>
</html>

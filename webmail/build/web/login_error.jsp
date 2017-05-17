<%@page contentType="text/html; charset=euc-kr"%>
<%@page pageEncoding="UTF-8"%>

<%
// invalidate this session
session.invalidate();
%>

<html>
    <head><title>Login Error Page</title></head>
    <body>
    
    <body bgcolor="#CCFF99">
    
        사용자id( <%= request.getParameter("userid") %>)나 암호가 틀렸습니다. 확인해 보시고 바랍니다. 
        <a href=index.jsp> 웹메일 시스템 홈페이지로 </a>

    </body>
</html>

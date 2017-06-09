<%@page contentType="text/html; charset=euc-kr"%>
<%@page pageEncoding="UTF-8"%>

<%-- LJM 041008 2:00am 한글 인코딩 문제 해결을 위해서 반드시 들어가야 함. --%>
<% request.setCharacterEncoding("utf-8"); %>  

<jsp:useBean id="mput" scope="page" class="control.MailHandleBean"/>

<% 
mput.setHost((String)session.getAttribute("host"));
mput.setUserid((String)session.getAttribute("userid"));
mput.setPasswd((String)session.getAttribute("passwd"));

mput.setTo((String)request.getParameter("to"));
mput.setCc((String)request.getParameter("cc"));
mput.setSubj((String)request.getParameter("subj"));
mput.setMsgBody((String)request.getParameter("body"));
%>

<html>
    <head><title>메일 전송 페이지</title></head>
    <body bgcolor="#CCFF99">

    <% 
     if (mput.sendMessage()) {
         out.println("메일이 성공적으로 전송되었습니다.");
     }
     else 
         out.println("메일 전송이 실패하였습니다.");
     %>
    </body>
</html>

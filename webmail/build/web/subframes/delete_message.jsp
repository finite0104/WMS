<%@page contentType="text/html; charset=euc-kr"%>
<%@page pageEncoding="UTF-8"%>

<jsp:useBean id="mget" scope="page" class="control.MailHandleBean"/>
<% 
mget.setHost((String)session.getAttribute("host"));
mget.setUserid((String)session.getAttribute("userid"));
mget.setPasswd((String)session.getAttribute("passwd"));
mget.setPageno(Integer.parseInt((String)session.getAttribute("pageno")));
String _pageno = (String)session.getAttribute("pageno");
%>



<html>
    <head><title>Delete Message Page</title></head>
    <body bgcolor="#CCFF99">

    <%= mget.deleteMessage(Integer.parseInt(request.getParameter("msgid")), true) %>

    <%  String temp = "<br> " +
                      "<jsp:forward page=\"list_mail.jsp?pageno=" + _pageno + "\"/>";
        out.println(temp);
    %>
    <jsp:forward page="list_mail.jsp?pageno=-1" />
    
    </body>
</html>

<%@page contentType="text/html; charset=euc-kr"%>
<%@page pageEncoding="UTF-8"%>

<jsp:useBean id="mget" scope="page" class="control.MailHandleBean"/>
<%--<jsp:setProperty name="mget" property="<%= session.getAttribute("userid") %>"/>
<jsp:setProperty name="mget" property="<%= session.getAttribute("passwd") %>"/>
--%>
<%-- LJM 041026: add--%>
<%
response.setDateHeader("Expires", 0);
response.setHeader("Pragma", "no-cache");
if(request.getProtocol().equals("HTTP/1.1")) {
    response.setHeader("Cache-Control", "no-cache");
}
%>

<% 
mget.setHost((String)session.getAttribute("host"));
mget.setUserid((String)session.getAttribute("userid"));
mget.setPasswd((String)session.getAttribute("passwd"));
String _pageno = (String)request.getParameter("pageno");
if (_pageno.equals("-1") == false) {
    mget.setPageno(Integer.parseInt(_pageno));
    session.setAttribute("pageno", _pageno);
}
else {
    mget.setPageno(Integer.parseInt((String)session.getAttribute("pageno")));
}
%>


<html>
    <head><title>Message List Page</title></head>
    <body bgcolor="#CCFF99">
    
    <%= mget.listMessages() %>

    </body>
</html>

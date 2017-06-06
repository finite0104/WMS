<%@page contentType="text/html; charset=euc-kr"%>
<%@page pageEncoding="UTF-8"%>

<%--
Author: Jong Min Lee
Date:   2004. 10. 1.
Description:
   본 프로젝트의 목적은 SMTP/POP3를 사용하는 메일서버에 웹메일 인터페이스를 제공하여
   웹메일 시스템으로 사용할 수 있게 해주는데 있다.
--%>

<%
            if (session.isNew()) {
                session.setAttribute("host", "localhost");   // should be modified if you change the POP3 server
                session.setAttribute("debug", "false");
                session.setAttribute("pageno", "1");
                session.setMaxInactiveInterval(session.getMaxInactiveInterval() * 2);
            }
%>

<html>
    <head>
        <title>Webmail Homepage</title>
        <link type="text/css" rel="stylesheet" href="css/index.css" />
    </head>

    <body>
        <div id="login_title">
        웹메일 시스템 <br />
        (<%= session.getAttribute("host")%>)
        </div>

        <div id="login_form">
        <form method="POST" action="login_check.jsp">
            사용자: <input type="text" name="userid" size="20"> <br />
            암&nbsp;&nbsp;&nbsp;호: <input type="password" name="passwd" size="20"> <br /> <br />
            <input type="submit" value="로그인" name="B1">&nbsp;&nbsp;&nbsp;
            <input type="reset" value="다시 입력" name="B2">
        </form>
        </div>

    </body>
</html>

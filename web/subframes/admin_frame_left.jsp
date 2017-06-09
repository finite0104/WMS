<%-- 
    Document   : admin_frame_left
    Created on : Jun 1, 2017, 10:26:45 PM
    Author     : haei
--%>

<%@page import="control.CommandType.InOutState"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>WebMailSystem Menu</title>
    </head>
    <body>
        <!--make all green background-->
        <br><br>
        <span style="color: indigo">
            <b>User: <%=session.getAttribute("userid")%><b/>
        </span>
        <br>
        
        <p><a href="subframes/add_user.jsp" target=_top> User add</a></p>
        <p><a href="subframes/delete_user.jsp" target=_top> User del (change!!) </a></p>
        
        <p><a href="Login.do?command=<%=InOutState.LOGOUT.getValue()%>"> logout </a></p>
        
    </body>
</html>

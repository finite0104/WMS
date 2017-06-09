<%-- 
    Document   : admin_menu
    Created on : Jun 1, 2017, 6:48:28 PM
    Author     : haei
--%>

<%@page import="control.UserAdminAgent"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Admin Control Page</title>
        <link type="text/css" rel="stylesheet" href="css/index.css" />
    </head>
    <body>
 
        <jsp:include page="subframes/frame_top.jsp"/>
        
        <div>
            <jsp:include page="subframes/admin_frame_left.jsp"/>
        </div>
        
        <div id="main">
            <h2> User list</h2>
            <%
                UserAdminAgent agent = new UserAdminAgent("127.0.0.1", 4555);
            %>
            <ul>
            <%
                for(String userID : agent.getUserList()){
                    out.println( "<li>" + userID+ "</li>" );
                }
            %>
            </ul>
        </div>
            <jsp:include page="subframes/footer.jsp"/>
    </body>
</html>

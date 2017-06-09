<%-- 
    Document   : delete_user
    Created on : Jun 7, 2017, 1:46:23 AM
    Author     : haei
--%>

<%@page import="control.UserAdminAgent"%>
<%@page import="control.CommandType"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<script type="text/javascripts">
            function getConfirmResult(){
                var result = confirm("Really do you want to delete user?");
                return result;
            }
</script>
        
        
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>User Delete</title>
    </head>
    <body>
        <jsp:include page="frame_top.jsp"/>
        
        <div id="main">
            <h2> choose the members what you want to delete.</h2>
            <%
                UserAdminAgent agent = new UserAdminAgent("127.0.0.1", 4555);
            %>
            <form name="DeleteUser" action="UserAdmin.do?command=<%=CommandType.AddDelCommand.DELETE_USER_COMMAND.getValue()%>" method="POST"/>
            <%
                for(String userID : agent.getUserList()){
                    
                    out.println( "<input type=checkbox name=\"selectedUsers\" value=\""+userID+"\"/>");
                    out.println(userID + "<br>");
                }
            %>
       
        
        <input type="submit" value="delete" name="delete_command" onClick="return getConfirmResult()"/>
        <input type="reset" value="all reset"/>
    </form>
    </div>
    <jsp:include page="footer.jsp"/>
              
    </body>
</html>

<%-- 
    Document   : add_user
    Created on : Jun 1, 2017, 10:54:22 PM
    Author     : haei
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="control.CommandType"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>User Add</title>
    </head>
    <body>
        
        <%-- title --%>
        <iframe width=1000 height=150 frameborder=0 src="frame_top.jsp">
        </iframe>
        
       
        <div id="main"><!--ok-->
            <form name="AddUser" action="UserAdmin.do?command=<%=CommandType.AddDelCommand.ADD_USER_COMMAND.getValue()%>"
                  method="POST">
                <table border="0" align="left">
                    <tr>
                        <td>User ID</td>
                        <td><input type="text" name="id" value="" size="20"/></td>
                    </tr>
                    
                    <tr>
                        <td>Password</td>
                        <td><input type="password" name="password" value=""/></td>
                    </tr>

                                        
                    <tr>
                        <td colspan="2">
                            <input type="submit" value="add" name="register"/>
                            <input type="reset" value="resets" name="reset"/>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
       

        <%-- 메일 내용 --%>
        <iframe width=800 height=1000  hspace=150 vspace='-1000' frameborder=0 src="footer.jsp">
        </iframe>
    </body>
</html>

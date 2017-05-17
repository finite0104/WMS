<%@page contentType="text/html; charset=euc-kr"%>
<%@page pageEncoding="UTF-8"%>
<html>
    <head><title>Mail Handling Menu Page</title></head>
    <body>

        <%-- <jsp:useBean id="beanInstanceName" scope="session" class="beanPackage.BeanClassName" /> --%>
        <%-- <jsp:getProperty name="beanInstanceName"  property="propertyName" /> --%>
        답장, 재전송, <a href="delete_message.jsp"> 삭제 </a> msgid=<%=request.getParameter("msgid")> <br>
        
        <hr>

    </body>
</html>

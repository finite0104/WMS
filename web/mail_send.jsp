<%@page contentType="text/html; charset=euc-kr"%>
<%@page pageEncoding="UTF-8"%>
<html>
    <head><title>Mail Send Page</title></head>
    <body bgcolor="#CCFF99">

        <%-- title --%>
        <iframe width=1000 height=150 frameborder=0 src="subframes/frame_top.jsp">
        </iframe>
        
        <%-- menu 표시 --%>
        <iframe  width=150 height=1000 frameborder=0 target=_top src="subframes/frame_left.jsp">
        </iframe>
        
        <%-- 메일 내용 --%>
        <iframe width=800 height=1000  hspace=150 vspace='-1000' frameborder=0 src="subframes/mail_send_form2.jsp">
        </iframe>
    </body>
</html>

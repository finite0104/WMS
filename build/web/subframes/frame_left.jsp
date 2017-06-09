<%@page contentType="text/html; charset=euc-kr"%>
<%@page pageEncoding="UTF-8"%>
<html>
    <head><title>Frame Left Page</title></head>
    <body bgcolor="#CCFF99">

        
        사용자: <%= session.getAttribute("userid") %> <br>
        
        <p><a href="../handle.jsp?menu=1" target=_top> 메일 읽기 </a></p>
        <p><a href="../handle.jsp?menu=2" target=_top> 메일 쓰기 </a></p>
        <p><a href="../handle.jsp?menu=3" target=_top> 주소록 </a></p>
        <p><a href="../handle.jsp?menu=99" target=_top> Logout </a></p>

    </body>
</html>

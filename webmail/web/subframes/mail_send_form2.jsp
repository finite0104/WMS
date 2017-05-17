<%@page contentType="text/html; charset=euc-kr"%>
<%@page pageEncoding="UTF-8"%>

<html>
    <head><title>Mail Send Form Page</title></head>
    <body bgcolor="#CCFF99">

        <form method="POST" action="../smtpServlet" enctype="multipart/form-data">
        <p>받는 사람 <input type="text" name="to" size="60" 
           value=<%=request.getParameter("recv")==null?"":request.getParameter("recv")%>> 
        </p>
        <p>CC:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
           <input type="text" name="cc" size="60"></p>
        <p>메일 제목 <input type="text" name="subj" size="70"></p>
        <p>내 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 용</p>
        <p><textarea rows="15" name="body" cols="80"></textarea></p>
        <p> </p>
        <p> 파일 첨부1: <input type="file" name="file1"> <br> </p>
        <p> 파일 첨부2: <input type="file" name="file2"> <br> </p>
        <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
        <input type="submit" value="메일 보내기" name="B1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
        <input type="reset" value="다시 입력" name="B2"></p>
        </form>

    </body>
</html>

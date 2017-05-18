<%-- 
    Document   : add_user
    Created on : 2017. 5. 18, 오후 6:33:24
    Author     : 희원
--%>

<%@page contentType="text/html" pageEncoding="x-windows-949"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=x-windows-949">
        <title>회원가입</title>
    </head>
    <body>
        <jsp:include page="frame_top.jsp" />
             
        <div id="main">
            추가로 등록할 사용자 ID와 암호를 입력해 주시기 바랍니다. <br> <br>
            
            <form name="AddUser"
                  method="POST">
                  <table border="0" align="left">
                      <tr>
                          <td>사용자 ID</td>
                          <td> <input type="text" name="id" value=""
                                      size="20" /> </td>
                      </tr>
                      <tr>
                          <td>암호</td>
                          <td> <input type="password" name="password"
                                      value="" /> </td>
                      </tr>
                      <tr>
                          <td colspan="2">
                              <input type="submit" value="추가"
                                     name="register" />
                              <input type="reset" value="초기화"
                                     name="reset" />
                          </td>
                      </tr>
                </table>
        </form>
        </div>
                  
 
    </body>
</html>

<%-- 
    Document   : add_user
    Created on : 2017. 5. 18, ���� 6:33:24
    Author     : ���
--%>

<%@page contentType="text/html" pageEncoding="x-windows-949"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=x-windows-949">
        <title>ȸ������</title>
    </head>
    <body>
        <jsp:include page="frame_top.jsp" />
             
        <div id="main">
            �߰��� ����� ����� ID�� ��ȣ�� �Է��� �ֽñ� �ٶ��ϴ�. <br> <br>
            
            <form name="AddUser"
                  method="POST">
                  <table border="0" align="left">
                      <tr>
                          <td>����� ID</td>
                          <td> <input type="text" name="id" value=""
                                      size="20" /> </td>
                      </tr>
                      <tr>
                          <td>��ȣ</td>
                          <td> <input type="password" name="password"
                                      value="" /> </td>
                      </tr>
                      <tr>
                          <td colspan="2">
                              <input type="submit" value="�߰�"
                                     name="register" />
                              <input type="reset" value="�ʱ�ȭ"
                                     name="reset" />
                          </td>
                      </tr>
                </table>
        </form>
        </div>
                  
 
    </body>
</html>

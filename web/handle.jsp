<%@page contentType="text/html; charset=euc-kr"%>
<%@page pageEncoding="UTF-8"%>

<%-- << control >> --%>


<html>
    <head><title>메일 처리 페이지</title></head>
    <body>

        메일을 읽기 위한 절차를 수행한 후 이 페이지에 그 결과를 가져올 수 있도록 해야 합니다.. <br>
        당신이 선택한 메뉴는
        <% if (request.getParameter("menu").equals("1")) {   // 메일 읽기%>
                <jsp:forward page="mail_read.jsp"/>
        <%  } 
            else if (request.getParameter("menu").equals("2")) {  // 메일 쓰기
               out.println("메일 쓰기 선택"); %>
               <jsp:forward page="mail_send.jsp"/>
         <%  }
            else if (request.getParameter("menu").equals("3")) {  // 메일 쓰기 %>
                <jsp:forward page="addr_book.jsp"/>
         <%   }
            else if (request.getParameter("menu").equals("99")) {  // Logout
                session.invalidate();
                response.sendRedirect("index.jsp");
            }
            else {
               out.println("선택이 잘못 되었습니다.");
            }
        %>
        

    </body>
</html>


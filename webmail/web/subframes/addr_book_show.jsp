<%@page contentType="text/html; charset=euc-kr" import="java.sql.*, java.net.URLDecoder" %>
<%@page pageEncoding="UTF-8"%>

<%      // 주소록 추가
        String db_name = "webmail.db";
        request.setCharacterEncoding("euc-kr");

        Connection conn = null;
        Statement stmt = null;

        String jdbc_driver = "com.mysql.jdbc.Driver";
        String jdbc_url = "jdbc:mysql://localhost/?characterEncoding=utf8";   // 외부에서 테스트할 때 사용
//String jdbc_url = "jdbc:mysql://localhost/?characterEncoding=euckr";  // seps2에서 local하게 동작할 때는 이렇게 표시해야 함.


        try {
// JDBC driver loading
            Class.forName(jdbc_driver);

            String mysql_userid = "webmail";      // 제대로 실행 안 될 것임. 정확한 사용자 ID 필요
            String mysql_password = "2007";  // 제대로 실행 안 될 것임. 정확한 사용자 암호 필요
            conn = DriverManager.getConnection(jdbc_url, mysql_userid, mysql_password);

            stmt = conn.createStatement();
            String email = request.getParameter("email");
//if (email != null && !request.getParameter("email").equals("")) {
            String addType = request.getParameter("add");
            if (addType != null) {
                String userid = (String) session.getAttribute("userid");
                //String name = java.net.URLDecoder.decode(request.getParameter("name"), "euc-kr");
                String name = null;
                if (addType.equals("1")) {
                    name = new String(request.getParameter("name").getBytes("8859_1"), "euc-kr");
                    name = name.replaceAll("%20", " ");
                } else {
                    name = request.getParameter("name");
                }
                String office_phone = request.getParameter("office_phone");
                String mobile_phone = request.getParameter("mobile_phone");
                String home_phone = request.getParameter("home_phone");
                if (office_phone == null) {
                    office_phone = "NULL";
                }
                if (mobile_phone == null) {
                    mobile_phone = "NULL";
                }
                if (home_phone == null) {
                    home_phone = "NULL";
                }
                String sql = "insert into " + db_name + " values(NULL, '" + userid + "', '" +
                        name + "', '" + email + "', '" + office_phone + "', '" +
                        mobile_phone + "', '" + home_phone + "')";
                stmt.execute(sql);
            }
        } catch (Exception ex) {
            out.println("addAddrBook exception: " + ex);
        }
%>

<%  // 주소록 삭제 코드
        String deleteid = (String) request.getParameter("del");
        if (deleteid != null) {
            String sql = "delete from " + db_name + " where id='" + deleteid + "'";
            stmt.execute(sql);
        }
%>

<html>
    <head><title>Address Book</title></head>
    <body bgcolor="#CCFF99">

        <p><b>주소록</b></p>
        <hr>
        <p># 주소록 등록</p>
        <form method="POST" action="addr_book_show.jsp?add=2">  <%-- recursive call --%>
            <p>이름:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="text" name="name" size="20">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            이메일: <input type="text" name="email" size="38"></p>
            <p>사무실 전화: <input type="text" name="office_phone" size="20">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            핸드폰: <input type="text" name="mobile_phone" size="20"></p>
            <p>집 전화:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type="text" name="home_phone" size="20"></p>
            <p align="center"><input type="submit" value="추가" name="B1"> &nbsp;&nbsp; <input type="reset" value="원래대로" name="B2"></p>
        </form>
        <hr>
        <p># 주소록 목록</p>

        <font size='2'>
            <%
        try {
            String sql = "select id, userid, name, email, office_phone, mobile_phone, home_phone from " + db_name + " order by name";

            ResultSet rs = stmt.executeQuery(sql);
            int i = 1;

            while (rs.next()) {
                if (rs.getString("userid").equals((String) session.getAttribute("userid"))) {
                    String result = i + ": " + rs.getString("name") + ", " +
                            // email 선택시 "메일 쓰기" 기능으로 전환
                            "<a href=mail_send_form2.jsp?recv=" + rs.getString("email") + "> " +
                            rs.getString("email") + " </a> ";
                    String temp = null;

                    temp = rs.getString("office_phone");
                    if (temp != null && !temp.equals("")) {
                        result += (", " + temp);
                    }

                    temp = rs.getString("mobile_phone");
                    if (temp != null && !temp.equals("")) {
                        result += (", " + temp);
                    }

                    temp = rs.getString("home_phone");
                    if (temp != null && !temp.equals("")) {
                        result += (", " + temp);
                    }

                    result += ( // 수정 기능
                            "&nbsp;&nbsp; <a href=addr_book_show.jsp?mod=" + rs.getString("id") + "> 수정 </a> " +
                            // 삭제 추가
                            "&nbsp; <a href=addr_book_show.jsp?del=" + rs.getString("id") + "> 삭제 </a> " +
                            "<br>");

                    out.println(result);
                }
                i++;
            }  // while()
        } catch (Exception ex) {
            System.out.println("주소록 목록 예외상황: " + ex);
        }

            %>
        </font>


    </body>
</html>

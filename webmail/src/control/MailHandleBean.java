/*
 * GetMailBean.java
 *
 * Created on 2004년 10월 5일 7:42
 */
package control;

import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import com.sun.mail.pop3.*;
import com.sun.mail.smtp.*;
//import com.sun.mail.util.SharedByteArrayInputStream;
import com.sun.mail.util.SharedByteArrayInputStream;
import javax.activation.*;
//import javax.mail.util.SharedByteArrayInputStream;

/**
 *
 * @author  jongmin
 */
public class MailHandleBean {

    // LJM 041202 : 다운로드할 파일의 임시 저장 디렉토리
    // 파일 다운로드후 파일은 DownloadServlet에서 삭제됨.
    // 동시에 여러 사용자가 동일한 파일명을 가지는 파일 다운로드시 문제 발생되므로
    // 각 사용자별로 이 디렉토리 밑에 새로 디렉토리를 생성해서 관리할 필요있음.
    // LJM 050727 - 파일 다운로드 제대로 되도록 수정중.
    // LJM 090430 : 수정해야 할 부분 - start ------------------
    // 리눅스 서버 사용시
    //final String downloadTempDir = "/var/spool/webmail/download/";
    // 윈도우즈 환경 사용시
    //final String downloadTempDir = "C:/temp/download/";
    /**
     * Download File Directory.
     */
    String downloadTempDir;
    // LJM 090430 : 수정해야 할 부분 - end   ------------------
    /**
     * OS Name property.
     */
    private final String OSName = System.getProperty("os.name").toLowerCase();
    /**
     * Holds value of property userid.
     */
    private String userid;
    /**
     * Holds value of property passwd.
     */
    private String passwd;
    /**
     * Holds value of property host.
     */
    private String host;
    /**
     * Holds value of property cc.
     */
    private String cc;
    /**
     * Holds value of property subj.
     */
    private String subj;
    /**
     * Holds value of property msgBody.
     */
    private String msgBody;
    /**
     * Holds value of property to.
     */
    private String to;
    /**
     * Holds value of property file1.
     */
    private String file1;
    /**
     * Holds value of property file2.
     */
    private String file2;
    /**
     * Holds value of property pageno.
     */
    private int pageno;

    String content;
    ArrayList fileList;
    
    /** Creates a new instance of GetMailBean */
    public MailHandleBean() {
        cc = null;
        to = null;
        msgBody = null;
        file1 = null;
        file2 = null;
        content = null;
        fileList = null;
        setDirectory();
    }

    /**
     * Setting Mail Directory.
     */
    private void setDirectory() {
        if(OSName.contains("win")) {
            //window 운영체제
            downloadTempDir = "C:/temp/download/";
        }else if(OSName.contains("nix") 
                || OSName.contains("nux")|| OSName.contains("aix")) {
            //unix 운영체제들
            downloadTempDir = "/var/spool/webmail/download/";
        }
    }
    
    /**
     * Setter for property userid.
     * @param userid New value of property userid.
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * Setter for property passwd.
     * @param passwd New value of property passwd.
     */
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String test() {
        String s = "hello";
        return s;
    }

    /**
     * Getter for property host.
     * @return Value of property host.
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Setter for property host.
     * @param host New value of property host.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /*
     * 한 페이지당 10개의 메시지를 보여준다.
     * 페이지 구분: page
     * 10페이지 단위 구분:
     */
    public String listMessages() {
        // temporary user directory에 남아있는 파일 모두 삭제
        String tempUserDir = this.downloadTempDir + this.userid;
        File dir = new File(tempUserDir);
        if (dir.exists()) {
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                fileList[i].delete();
            }
        }


        // 메시지 제목 보여주기
        String table_start = "<table border=\"1\" width=\"100%\" height=\"30\">";
        String table_end = "</table>";
        String table_title = "<tr> "
                + " <td width=\"5%\" height=\"16\" align=\"center\"> No. </td> "
                + " <td width=\"20%\" height=\"16\" align=\"center\"> 보낸 사람 </td>"
                + " <td width=\"40%\" height=\"16\" align=\"center\"> 제목 </td>     "
                + " <td width=\"15%\" height=\"16\" align=\"center\"> 보낸 날짜 </td>   "
                + " <td width=\"5%\" height=\"16\" align=\"center\"> 삭제 </td>   "
                + " </tr>";

        //String result = table_start + table_title;
        String result = table_start + table_title;

        // Property 설정
        Properties props = System.getProperties();

        // Session 설정
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);

        // Store 설정
        POP3Store store = null;
        String sender = null;
        String subject = null;
        String date = null;

        // LJM 041207 - 페이지 단위 메일 목록 보여주기에 사용됨.
        int totalPageCount = 0;

        try {
            store = (POP3Store) session.getStore("pop3");
            //System.out.println("host = " + host + ", user = " + userid + ", passwd = " + passwd);
            store.connect(this.host, this.userid, this.passwd);
            if (store.isConnected() == false) {
                result += "Not connected for some reason...";
                result += table_end;
                store.close();
                return result;
            }

            // Folder 설정
            Folder folder = store.getDefaultFolder();
            folder = folder.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            // 수신한 메시지 갯수 파악
            int totalMessages = folder.getMessageCount();

            /* LJM 041207 - page당 10개의 메시지 제목 출력하도록 수정
             *
             *      message no.  (messagesPerPage = 10인 경우)
             *          22    21   20     ...     13    12   11   10      ...      3     2     1     0
             *           |<--  pageno = 1       -->|     |<--  pageno = 2       -->|     |  pageno=3 |
             *           ^ start                   ^ end ^ start                   ^ end ^ start     ^ end
             *
             */
            final int messagesPerPage = 10;
            // totalPageCount는 메일 제목 목록 표시후 페이지 단위로 메일 제목 목록을
            // 볼 수 있도록 page indicator를 하단에 표시할 때 사용됨.
            totalPageCount = (totalMessages / messagesPerPage);
            if (totalMessages % messagesPerPage != 0) {
                totalPageCount++;
            }

            int start = totalMessages - (messagesPerPage * (pageno - 1));
            int end = start - messagesPerPage + 1;
            if (end <= 0) {
                end = 1;
            }

            // 메시지 헤더 가져오기
            Message[] msgs = folder.getMessages(end, start);  // 보여줄 메시지만 가져오도록 수정되어야 함.
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add("X-mailer");
            folder.fetch(msgs, fp);



            //for (int i=msgs.length-1; i>=0; i--) {
            for (int i = start; i >= end; i--) {
                // msgs[i]에서 sender, subject, date 정보 추출
                // sender
                /*
                Address [] a = msgs[i].getFrom();
                sender = a[0].toString();
                 **/
                int index = i - end;
                sender = getMyFrom((POP3Message) msgs[index]);

                // subject
                //subject = msgs[i].getSubject();  // 수정 필요
                //subject = getMySubject((POP3Message) msgs[index]);
                subject = msgs[index].getSubject();


                // date
                date = msgs[index].getSentDate().toLocaleString();


                // 추출한 정보를 출력 포맷 사용하여 스트링으로 만들기
                String temp = "<tr> "
                        + " <td width=\"5%\" height=\"16\" align=\"center\">" + i + " </td> "
                        + " <td width=\"20%\" height=\"16\" align=\"center\">" + sender + "</td>"
                        + " <td width=\"40%\" height=\"16\" align=\"center\"> "
                        + " <a href=get_message.jsp?msgid=" + i + "> " + subject + "</a> </td>"
                        + " <td width=\"15%\" height=\"16\" align=\"center\">" + date + "</td>"
                        + " <td width=\"5%\" height=\"16\" align=\"center\">"
                        + "<a href=delete_message.jsp?msgid=" + i + "> 삭제 </a>" + "</td>"
                        + " </tr>";
                result += temp;
            }
        } catch (Exception ex) {
            System.out.println("listMessages error: " + ex);
        }

        result += table_end;

        //System.out.println("****************************************");
        //System.out.println(result);
        //System.out.println("****************************************");


        // LJM 041207 - 페이지 목록 넣기
        // line break
        result += "<br>";
        result += "<center>";

        for (int i = 1; i <= totalPageCount; i++) {
            if (i != this.pageno) {
                result += "<a href=list_mail.jsp?pageno=" + i + "> " + i + "</a> &nbsp; ";
            } else {
                result += i + "&nbsp; ";
            }
            // 매 20개마다 줄 바꾸기 삽입
            if (i % 20 == 0) {
                result += " <br> ";
            }
        }
        result += "</center>";

        // close the store to issue "QUIT" command.
        try {
            store.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result;
    }

    public String getMessageBody(int num) {
        String body = " ";

        // Property 설정
        Properties props = System.getProperties();
        props.put("mail.pop3.apop.enable", true);  // LJM 050111 - add

        // Session 설정
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);

        // Store 설정
        POP3Store store = null;
        String sender;
        String subject;
        String date;

        try {
            store = (POP3Store) session.getStore("pop3");
            //System.out.println("host = " + host + ", user = " + userid + ", passwd = " + passwd);
            store.connect(this.host, this.userid, this.passwd);
            if (store.isConnected() == false) {
                store.close();
                return body;
            }

            // Folder 설정
            Folder folder = store.getDefaultFolder();
            folder = folder.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            // 메시지 본문 가져오기
            /*  LJM 041010 : 아래 코드로 수정
            Message [] msgs = folder.getMessages();
            int id = num - 1;
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add("X-mailer");
            fp.add("Content-Type");
            folder.fetch(msgs, fp);
            // Get body
            Object obj = msgs[id].getContent();
             **/

            POP3Message msg = (POP3Message) folder.getMessage(num);

            // LJM 041231 - start
            // 메시지 본문에 앞서 "보낸 사람" & "제목"을 출력하는 코드를  (String)body에 추가
            String From = getMyFrom2(msg);
            String To = this.getMailReceiver(msg);
            String Cc = this.getCc(msg);
            String Subj = getMySubject(msg);
            // 050325 보낸 날짜에 대한 Locale 수정
            String _Date = null;

            _Date = msg.getSentDate().toLocaleString();


            body += ("보낸 사람: " + From + " <br>");
            body += ("받은 사람: " + To + " <br>");
            body += ("Cc &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; : " + Cc + " <br>");
            body += ("보낸 날짜: " + _Date + " <br>");
            body += ("제 &nbsp;&nbsp;&nbsp;  목: " + Subj + " <br> <hr>");
            // LJM 041231 - end

            // Get body
            Object obj = msg.getContent();
            String contentType = msg.getContentType();

            //if (obj instanceof String) {
            if (contentType.toLowerCase().startsWith("text")) {
                // LJM 041009 : 메일을 HTML 폼으로 만듬
                //String contentType = msgs[id].getContentType();

                //System.out.println("메시지의 ContentType = " + contentType);
                if (contentType.toLowerCase().startsWith("text/plain")) {
                    //System.out.println("******** Content-Type: text/plain *************");
                    String temp = (String) obj;
                    // LJM 051101 -수정
                    temp = temp.replaceAll("\r\n", "<br>");
                    body += temp;
                    // 위 두줄로 수정했음. 051101
                    //body += "<pre>" + temp + "</pre>";

                } else if (contentType.toLowerCase().startsWith("text/html")) {  // maybe "text/html"
                    //System.out.println("******** Content-Type: text/html *************");
                    body += (String) obj;
                } else {
                    //System.out.println("******** Content-Type: unknown type *************");
                    String temp = (String) obj;
                    temp = temp.replaceAll("\r\n", "<br>");
                    body += "<p>" + temp + "</p>";
                }
            } else if (contentType.toLowerCase().startsWith("multipart/alternative")) {
                MimeMultipart mp = (MimeMultipart) obj;
                MimeBodyPart part = null;
                //System.out.println("*** mp.getCount() = " + mp.getCount());
                for (int i = 0; i < mp.getCount(); i++) {
                    part = (MimeBodyPart) mp.getBodyPart(i);
                    Object subObj = part.getContent();
                    String partContentType = part.getContentType();
                    //System.out.println("partContentType: " + partContentType);
                    if (partContentType.toLowerCase().lastIndexOf("text/html") >= 0) {
                        body += (String) subObj;
                    }
                }  // for()
            } else if (contentType.toLowerCase().startsWith("multipart/mixed")) {  // multipart/mixed, multipart/related, ...
                MimeMultipart mp = (MimeMultipart) obj;
                MimeBodyPart part = null;
                //System.out.println("*** mixed: mp.getCount() = " + mp.getCount());
                for (int i = 0; i < mp.getCount(); i++) {
                    part = (MimeBodyPart) mp.getBodyPart(i);
                    //Object subObj = part.getContent();
                    String partContentType = part.getContentType();
                    String partContentDisposition = part.getDisposition(); // LJM 041202
                    // LJM 041203 : for easy checking
                    if (partContentDisposition == null) {
                        partContentDisposition = "null";
                    }
                    //System.out.println("******* partContentType: " + partContentType);
                    if (partContentType.toLowerCase().startsWith("multipart/alternative")) {
                        //System.out.println("******* multipart/alternative found....") ;
                        MimeMultipart submp = (MimeMultipart) part.getContent();  // LJM 041202
                        //System.out.println("*** submp.getCount() = " + submp.getCount());
                        for (int j = 0; j < submp.getCount(); j++) {
                            MimeBodyPart subpart = (MimeBodyPart) submp.getBodyPart(j);
                            if (subpart.getContentType().toLowerCase().startsWith("text/html")) {
                                body += (String) subpart.getContent();
                            }
                        }  // for(j)
                    } else if (partContentType.toLowerCase().startsWith("multipart/related") && partContentType.lastIndexOf("multipart/alternative") > 0) {
                        MimeMultipart submp = (MimeMultipart) part.getContent();  // LJM 041202
                        //System.out.println("*** submp.getCount() = " + submp.getCount());
                        for (int j = 0; j < submp.getCount(); j++) {
                            MimeBodyPart subpart = (MimeBodyPart) submp.getBodyPart(j);
                            //System.out.println("*** subpartContentType: " + subpart.getContentType());
                            String subpartContentType = subpart.getContentType();
                            if (subpartContentType.toLowerCase().startsWith("text/html")) {
                                body += (String) subpart.getContent();
                            } else if (subpartContentType.toLowerCase().startsWith("multipart/alternative")) {
                                MimeMultipart subsubmp = (MimeMultipart) subpart.getContent();
                                //System.out.println("*** subsubmp.getCount() = " + subsubmp.getCount());
                                for (int k = 0; k < subsubmp.getCount(); k++) {
                                    MimeBodyPart subsubpart = (MimeBodyPart) subsubmp.getBodyPart(k);
                                    if (subsubpart.getContentType().toLowerCase().startsWith("text/html")) {
                                        body += (String) subsubpart.getContent();
                                    }
                                }  // for(k)
                            }
                        }  // for(j)
                    } else if (partContentType.toLowerCase().startsWith("text/html")) {
                        body += (String) part.getContent();  // LJM 041202
                    } else if (partContentType.toLowerCase().startsWith("text/plain")
                            && !partContentDisposition.equalsIgnoreCase(Part.ATTACHMENT)) {
                        //body += ("<pre>" + (String)part.getContent() + "</pre>");  // LJM 041202
                        // LJM 050110 - 위의 문장으로 대체  // LJM 051101
                        body += (String) part.getContent();
                        body = body.replaceAll("\n", "<br>");

                    } else if (partContentType.toLowerCase().startsWith("message/rfc822")) {
                        byte[] b = new byte[50 * 1024];  // 20KB
                        part.getInputStream().read(b);
                        String t = new String(b);
                        body += ("<hr> <pre>" + t + "</pre>");
                    } else { // LJM 041202 - start : 파일 첨부 처리
                        // 1. 첨부 파일이면 다운로드 디렉토리에 저장
                        // 1.1 첨부 파일이 아니면 무시
                        if (!partContentDisposition.equalsIgnoreCase(Part.ATTACHMENT)
                                && !partContentDisposition.equalsIgnoreCase(Part.INLINE)) {
                            continue;
                        }

                        // 1.2 첨부 파일을 저장할 temporary user directory 유무 조사 및 필요시 생성
                        String tempUserDir = this.downloadTempDir + this.userid;
                        File dir = new File(tempUserDir);
                        if (!dir.exists()) {  // tempUserDir 생성
                            dir.mkdir();
                        }

                        // 1.3 part의 내용을 파일로 저장
                        String filename = MimeUtility.decodeText(part.getFileName());
                        // 파일명에 " "가 있을 경우 서블릿에 파라미터로 전달시 문제 발생함.
                        // " "를 모두 "_"로 대체함.
                        filename = filename.replaceAll(" ", "_");
                        DataHandler dh = part.getDataHandler();
                        FileOutputStream fos = new FileOutputStream(tempUserDir + "/" + filename);
                        dh.writeTo(fos);
                        fos.flush();
                        fos.close();

                        // 2. 파일에 대한 하이퍼링크 생성


                        // LJM 050727 - 수정중

                        body += ("<hr> 첨부파일: <a href=../DownloadServlet?userid=" + this.userid
                                + "&filename=" + filename
                                + " target=_top> " + filename + "</a> <br>");

                    }  // // LJM 041202 - end : 파일 첨부 처리
                }  // for(i)
            } else if (contentType.toLowerCase().startsWith("multipart/related")) {  // multipart/mixed, multipart/related, ...
                MimeMultipart mp = (MimeMultipart) obj;
                MimeBodyPart part = null;
                //System.out.println("*** related: mp.getCount() = " + mp.getCount());
                for (int i = 0; i < mp.getCount(); i++) {
                    part = (MimeBodyPart) mp.getBodyPart(i);
                    Object subObj = part.getContent();
                    String partContentType = part.getContentType();
                    //System.out.println("******* partContentType: " + partContentType);
                    if (partContentType.toLowerCase().startsWith("multipart/alternative")) {
                        // System.out.println("******* multipart/alternative found....") ;
                        MimeMultipart submp = (MimeMultipart) subObj;
                        //System.out.println("*** submp.getCount() = " + submp.getCount());
                        for (int j = 0; j < submp.getCount(); j++) {
                            MimeBodyPart subpart = (MimeBodyPart) submp.getBodyPart(j);
                            if (subpart.getContentType().toLowerCase().startsWith("text/html")) {
                                body += (String) subpart.getContent();
                            }
                        }  // for(j)
                    } else if (partContentType.toLowerCase().startsWith("text/html")) {
                        body += (String) subObj;
                    }
                }  // for(i)
            } else if (contentType.toLowerCase().startsWith("multipart/report")) {  // multipart/mixed, multipart/related, ...
                MimeMultipart mp = (MimeMultipart) obj;
                MimeBodyPart part = null;
                //System.out.println("*** related: mp.getCount() = " + mp.getCount());
                for (int i = 0; i < mp.getCount(); i++) {
                    part = (MimeBodyPart) mp.getBodyPart(i);
                    Object subObj = part.getContent();
                    String partContentType = part.getContentType();
                    //System.out.println("******* partContentType: " + partContentType);
                    if (partContentType.toLowerCase().startsWith("multipart/alternative")) {
                        // System.out.println("******* multipart/alternative found....") ;
                        MimeMultipart submp = (MimeMultipart) subObj;
                        //System.out.println("*** submp.getCount() = " + submp.getCount());
                        for (int j = 0; j < submp.getCount(); j++) {
                            MimeBodyPart subpart = (MimeBodyPart) submp.getBodyPart(j);
                            if (subpart.getContentType().toLowerCase().startsWith("text/html")) {
                                body += (String) subpart.getContent();
                            }
                        }  // for(j)
                    } else if (partContentType.toLowerCase().startsWith("text/html")) {
                        body += (String) subObj;
                    } else if (partContentType.toLowerCase().startsWith("message/delivery-status")) {
                        byte[] b = new byte[1000];
                        ((SharedByteArrayInputStream) subObj).read(b);
                        String t = new String(b);
                        body += ("<hr> <pre>" + t + "</pre>");
                    } else if (partContentType.toLowerCase().startsWith("message/rfc822")) {
                        byte[] b = new byte[2000];
                        part.getInputStream().read(b);
                        String t = new String(b);
                        body += ("<hr> <pre>" + t + "</pre>");
                    } else {
                        body += ("<hr> <pre>" + (String) subObj + "</pre>");
                    }
                }  // for(i)
            }

        } catch (Exception ex) {
            System.out.println("getMessageBody error: " + ex);
            body += ("getMessageBody error: " + ex);
        }
        try {
            store.close();
        } catch (Exception ex) {
            System.out.println("store.close() error: " + ex);
        }
        return body;
    }

    public boolean sendMessage() {
        // property
        Properties props = System.getProperties(); //new Properties();  // LJM 070411: 수정함.
        props.put("mail.smtp.host", this.host);
        //props.put("mail.smtp.auth", true);
        System.out.println("SMTP host : " + props.get("mail.smtp.host"));

        // session
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(false);  // LJM 070411: 메일 보내기가 잘 안되면 false -> true로 해서 보면 필요한 정보가 나옴.

        try {
            SMTPMessage msg = new SMTPMessage(session);
            msg.setFrom(new InternetAddress(this.userid + "@" + this.host));

            // setRecipient() can be called repeatedly if ';' or ',' exists
            // Thus, the following statement should be modified...
            //msg.setRecipient(Message.RecipientType.TO, new InternetAddress(this.to));
            if (this.to.indexOf(';') != -1) {
                this.to = this.to.replaceAll(";", ",");
            }
            msg.setRecipients(Message.RecipientType.TO, this.to);

            //msg.setSubject(s);
            msg.setSubject(MimeUtility.encodeText(this.subj, "euc-kr", "B"));
            if (this.cc.length() > 1) {// i.e. CC address has more than one char's
                //msg.setRecipient(Message.RecipientType.CC, new InternetAddress(this.cc));
                if (this.cc.indexOf(';') != -1) {
                    this.cc = this.cc.replaceAll(";", ",");
                }
                msg.setRecipients(Message.RecipientType.CC, this.cc);
            }
            //msg.setHeader("Content-Type", "text/plain; charset=utf-8");
            msg.setHeader("User-Agent", "LJM-WM/0.1");
            //msg.setHeader("Content-Transfer-Encoding", "8bit");
            //msg.setAllow8bitMIME(true);

            // body
            MimeBodyPart mbp = new MimeBodyPart();
            // Content-Type, Content-Transfer-Encoding 설정 의미 없음.
            // 자동으로 설정되는 것 같음. - LJM 041202
            //mbp.setHeader("Content-Type", "text/plain; charset=euc-kr");
            //mbp.setHeader("Content-Transfer-Encoding", "8bit");
            mbp.setText(this.msgBody);

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp);

            // attachment #1
            if (this.file1 != null) {
                MimeBodyPart a1 = new MimeBodyPart();
                DataSource src = new FileDataSource(this.file1);
                a1.setDataHandler(new DataHandler(src));
                int index = this.file1.lastIndexOf('/');
                String fileName = this.file1.substring(index + 1);
                // "B": base64, "Q": quoted-printable
                a1.setFileName(MimeUtility.encodeText(fileName, "euc-kr", "B"));
                mp.addBodyPart(a1);

                // LJM 041201
                // 여기서 파일 삭제하니 메일 전송 실패 에러 발생함.
                // 그래서 메일 전송후에 파일 삭제하기로 함.
                // now delete the temporary file
                //File f = new File(this.file1);
                //f.delete();
            }

            // attachment #2
            if (this.file2 != null) {
                MimeBodyPart a2 = new MimeBodyPart();
                DataSource src = new FileDataSource(this.file2);
                a2.setDataHandler(new DataHandler(src));
                int index = this.file2.lastIndexOf('/');
                String fileName = this.file2.substring(index + 1);
                // "B": base64, "Q": quoted-printable
                a2.setFileName(MimeUtility.encodeText(fileName, "euc-kr", "B"));
                mp.addBodyPart(a2);

                // now delete the temporary file
                //File f = new File(this.file2);
                //f.delete();
            }

            msg.setContent(mp);


            /* 참고할 것...
            String body = new String(this.msgBody.getBytes("8859_1"), "UTF-8");
            msg.setText(body);
             **/

            //msg.setText(this.msgBody);

            // send the message using SMTP
            Transport.send(msg);

            // now delete files if exists
            if (this.file1 != null) {
                File f = new File(this.file1);
                f.delete();
            }
            if (this.file2 != null) {
                File f = new File(this.file2);
                f.delete();
            }
        } catch (Exception ex) {
            System.out.println("sendMessage() error: " + ex);
            return false;
        }
        return true;
    }

    /**
     * Getter for property cc.
     * @return Value of property cc.
     */
    public String getCc() {
        return cc;
    }

    /**
     * Setter for property cc.
     * @param cc New value of property cc.
     */
    public void setCc(String cc) {
        this.cc = cc;
    }

    /**
     * Getter for property subj.
     * @return Value of property subj.
     */
    public String getSubj() {

        return this.subj;
    }

    /**
     * Setter for property subj.
     * @param subj New value of property subj.
     */
    public void setSubj(String subj) {

        this.subj = subj;
    }

    /**
     * Getter for property msgBody.
     * @return Value of property msgBody.
     */
    public String getMsgBody() {

        return this.msgBody;
    }

    /**
     * Setter for property msgBody.
     * @param msgBody New value of property msgBody.
     */
    public void setMsgBody(String msgBody) {

        this.msgBody = msgBody;
    }

    /**
     * Getter for property to.
     * @return Value of property to.
     */
    public String getTo() {

        return this.to;
    }

    /**
     * Setter for property to.
     * @param to New value of property to.
     */
    public void setTo(String to) {

        this.to = to;
    }

    /**
     * 목적: 메시지 영구 삭제/삭제 취소
     * 사용자가 msg_num과 what(= true or false)을 넘겨주면
     * 그에 따른 동작을 수행한다.
     */
    public String deleteMessage(int msg_num, boolean what) {
        boolean exception_met = false;

        // Property 설정
        Properties props = System.getProperties();

        // Session 설정
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);

        // Store 설정
        POP3Store store = null;
        String sender;
        String subject;
        String date;

        try {
            store = (POP3Store) session.getStore("pop3");
            //System.out.println("host = " + host + ", user = " + userid + ", passwd = " + passwd);
            store.connect(this.host, this.userid, this.passwd);
            if (store.isConnected() == false) {
                store.close();
                return new String("메일 삭제가 실패하였습니다");
            }

            // Folder 설정
            Folder folder = store.getDefaultFolder();
            folder = folder.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            // Message에 DELETED flag 설정
            Message msg = folder.getMessage(msg_num);
            msg.setFlag(Flags.Flag.DELETED, what);  // set the DELETED flag

            // 폴더에서 메시지 삭제
            //Message [] expungedMessage = folder.expunge();  <-- 현재 지원 안 되고 있음. 폴더를 close()할 때 expunge해야 함.
            folder.close(true);
        } catch (Exception ex) {
            System.out.println("deleteMessage() error: " + ex);
            exception_met = true;
        }

        try {
            store.close();
        } catch (Exception ex) {
            System.out.println("store.close() error: " + ex);
        }
        String result = null;
        if (exception_met) {
            result = "메일 삭제가 실패하였습니다.";
        } else {
            result = "메일 삭제가 성공하였습니다";
        }

        return result;  // 성공적으로 삭제/삭제 취소 완료
    }

    public String getMySubject(POP3Message msg) {
        String subj = null;
        char charSubj[] = new char[100];
        byte byteSubj[] = new byte[100];

        //System.out.println("************** getMySubject: S *************");
        try {
            subj = msg.getSubject();  // default subject
            //System.out.println("original Subject = " + subj);
            InputStream is = msg.top(0);
            InputStreamReader isr = new InputStreamReader(is);

            char ch;
            int cnt = 0;
            int i = 0;
            //System.out.println("char read");
            while ((ch = (char) isr.read()) != -1 && cnt++ < 5000) {
                // System.out.print(ch);
                if (ch == 'S') {
                    if ((ch = (char) isr.read()) == 'u') {
                        if ((ch = (char) isr.read()) == 'b') {
                            if ((ch = (char) isr.read()) == 'j') {
                                if ((ch = (char) isr.read()) == 'e') {
                                    if ((ch = (char) isr.read()) == 'c') {
                                        if ((ch = (char) isr.read()) == 't') {
                                            if ((ch = (char) isr.read()) == ':') {
                                                while ((ch = (char) isr.read()) == ' ');
                                                charSubj[i++] = ch;
                                                while ((ch = (char) isr.read()) != '\r') {
                                                    charSubj[i++] = ch;
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            charSubj[i] = '\0';

            if (i > 0 && !testEncodedSubject(charSubj, i)) {
                subj = new String(charSubj);
                //System.out.println("char modified Subject = " + subj);
            }

            //System.out.println("************** getMySubject: E *************");

        } catch (Exception ex) {
            System.out.println("getMySubject error: " + ex);
        }
        return subj;
    }

    private boolean testEncodedSubject(char[] subj, int length) {
        boolean result = false;
        // search the first "=?"
        for (int i = 0; i < length; i++) {
            if (subj[i] == '=' && subj[i + 1] == '?') {
                for (int j = i + 2; j < length; j++) {
                    if (subj[j] == '?' && subj[j + 1] == '=') {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public String getMyFrom(POP3Message msg) {
        String from = null;
        char charFrom[] = new char[100];

        // LJM 041231 - start
        String name = null;
        String email = null;
        // LJM 041231 - end

        //System.out.println("************** getMyFrom: S *************");
        try {
            Address[] a = msg.getFrom();
            from = a[0].toString();  // default subject
            //System.out.println("original From = " + from);
            InputStream is = msg.top(0);
            InputStreamReader isr = new InputStreamReader(is);

            char ch;
            int cnt = 0;
            int i = 0;
            //System.out.println("char read");
            // 5000: TOP 명령에 의해 반환되는 메일 헤더 길이가 수KB에 달하므로
            // 이를 읽을 수 있도록 충분히 큰  임의의 값을 사용했음.
            while ((ch = (char) isr.read()) != -1 && cnt++ < 5000) {
                // System.out.print(ch);
                if (ch == '\n') {
                    if ((ch = (char) isr.read()) == 'F') {
                        if ((ch = (char) isr.read()) == 'r') {
                            if ((ch = (char) isr.read()) == 'o') {
                                if ((ch = (char) isr.read()) == 'm') {
                                    if ((ch = (char) isr.read()) == ':') {
                                        while ((ch = (char) isr.read()) == ' ');
                                        charFrom[i++] = ch;
                                        while ((ch = (char) isr.read()) != '\r') {
                                            charFrom[i++] = ch;
                                        }
                                        //break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            charFrom[i] = '\0';

            if (i > 0) {
                from = new String(charFrom);
                //System.out.println("char modified From = " + from);
            }

            // LJM 041231 - start
            /*
            from = from.replaceAll("\"", " ");
            from = from.replaceFirst("<", "&lt;");
            from = from.replaceFirst(">", "&gt; ");
             */
            if (from.indexOf('<') != -1 && from.indexOf('>') != -1) { // "name" <email@host> 형태로 되어 있는 경우 처리
                int start;// = from.indexOf('"');
                int end;// = from.indexOf('"', start+1);
                if (from.indexOf('"') != -1) {
                    start = from.indexOf('"') + 1;
                    end = from.indexOf('"', start + 1);
                } else {  // name <email@host> 형태로 되어 있는 경우 처리
                    start = 0;
                    end = from.indexOf('<');
                }

                name = from.substring(start, end);
                if (name.startsWith("=?")) // inline encoding 여부 판단
                {
                    name = MimeUtility.decodeText(name);
                }
                if ((start = name.indexOf('"')) != -1) {  // "이름" 형태로 되어 있을 때 '"' 제거
                    end = name.indexOf('"', start + 1);
                    name = name.substring(start + 1, end);
                }

                start = from.indexOf('<') + 1;
                end = from.indexOf('>');
                email = from.substring(start, end);
            } else {
                name = from;
                email = from;
            }
            // LJM 041231 - end

            // System.out.println("************** getMyFrom: E *************");

        } catch (Exception ex) {
            System.out.println("getMySubject error: " + ex);
        }
        //return from;
        return "<a href=mail_send_form2.jsp?recv=" + email + "> " + name + " </a>";
    }

    public String getMyFrom2(POP3Message msg) {
        String from = null;
        char charFrom[] = new char[100];

        // LJM 041231 - start
        String name = null;
        String email = null;
        // LJM 041231 - end

        //System.out.println("************** getMyFrom: S *************");
        try {
            Address[] a = msg.getFrom();
            from = a[0].toString();  // default subject
            //System.out.println("original From = " + from);
            InputStream is = msg.top(0);
            InputStreamReader isr = new InputStreamReader(is);

            char ch;
            int cnt = 0;
            int i = 0;
            //System.out.println("char read");
            while ((ch = (char) isr.read()) != -1 && cnt++ < 5000) {
                // System.out.print(ch);
                if (ch == '\n') {
                    if ((ch = (char) isr.read()) == 'F') {
                        if ((ch = (char) isr.read()) == 'r') {
                            if ((ch = (char) isr.read()) == 'o') {
                                if ((ch = (char) isr.read()) == 'm') {
                                    if ((ch = (char) isr.read()) == ':') {
                                        while ((ch = (char) isr.read()) == ' ');
                                        charFrom[i++] = ch;
                                        while ((ch = (char) isr.read()) != '\r') {
                                            charFrom[i++] = ch;
                                        }
                                        //break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            charFrom[i] = '\0';

            if (i > 0) {
                from = new String(charFrom);
                //System.out.println("char modified From = " + from);
            }

            // LJM 041231 - start
            /*
            from = from.replaceAll("\"", " ");
            from = from.replaceFirst("<", "&lt;");
            from = from.replaceFirst(">", "&gt; ");
             */
            if (from.indexOf('<') != -1 && from.indexOf('>') != -1) { // "name" <email@host> 형태로 되어 있는 경우 처리
                int start;// = from.indexOf('"');
                int end;// = from.indexOf('"', start+1);
                if (from.indexOf('"') != -1) {
                    start = from.indexOf('"') + 1;
                    end = from.indexOf('"', start + 1);
                } else {  // name <email@host> 형태로 되어 있는 경우 처리
                    start = 0;
                    end = from.indexOf('<') - 1;
                }

                name = from.substring(start, end);
                if (name.startsWith("=?")) {
                    name = MimeUtility.decodeText(name);
                }
                if ((start = name.indexOf('"')) != -1) {
                    end = name.indexOf('"', start + 1);
                    name = name.substring(start + 1, end);
                }

                start = from.indexOf('<') + 1;
                end = from.indexOf('>');
                email = from.substring(start, end);
            } else {
                name = from;
                email = from;
            }
            // LJM 041231 - end

            // System.out.println("************** getMyFrom: E *************");

        } catch (Exception ex) {
            System.out.println("getMySubject error: " + ex);
        }

        // name을 URLEncoding함.
        String param_name = null;
        try {
            //param_name = java.net.URLEncoder.encode(name, "euc-kr");
            param_name = name.replaceAll(" ", "%20");
        } catch (Exception ex) {
            System.out.println("getMyFrom2(): URLEncoder.encode error: " + ex);
        }

        return "<a href=mail_send_form2.jsp?recv=" + email + "> " + name + " </a> "
                + " &nbsp;&nbsp; <a href=addr_book_show.jsp?add=1&name=" + param_name + "&email=" + email + "> 주소록 등록 </a>";
    }
    
    public String showForwardingMailForm(int msgid) {
        String result = null;
        String subject = null;
        String forwardContent = "\r\n\r\n*************** Original Message ***************\r\n";
        fileList = new ArrayList();
        
        Properties props = System.getProperties();
        
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);
        
        POP3Store pop3 = null;
        
        try{
            pop3 = (POP3Store) session.getStore("pop3");
            
            pop3.connect(host, userid, passwd);
            if(!pop3.isConnected()) {
                pop3.close();
                return "POP3Store Connection Error";
            }
            
            Folder folder = pop3.getDefaultFolder();
            folder = folder.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            
            Message msg = folder.getMessage(msgid);
            
            subject = "Foward Message : " + msg.getSubject();
            
            getMessageContent(msg);
            
            forwardContent += msg.getContent();
            
            //메세지의 파일이름 가져와서 다시 입력시키기            
        }catch(Exception e){
            
        }
        
        result = "<form method=\"POST\" action=\"../smtpServlet\" enctype=\'multipart/form-data\'> "
                + "<p>받는 사람 <input type=\"text\" name=\"to\" size=\"80\" value=\"" + "\"></p>"
                + "<p>CC:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                + "<input type=\"text\" name=\"cc\" size=\"80\"></p>"
                + "<p>메일 제목 <input type=\"text\" name=\"subj\" size=\"80\" value=\" " + subject + "\"></p>"
                + "<p>내 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 용</p>"
                + //"<p><textarea rows=\"10\" name=\"body\" cols=\"80\" value=\"" + replyBody + "\"></textarea></p>" +
                "<p><textarea rows=\"15\" name=\"body\" cols=\"80\">" + forwardContent + content + "</textarea></p>"
                + "<p> </p>";
        if(fileList.isEmpty()) {
            result += "<p> 파일 첨부1: <input type=\"file\" name=\"file1\" size=80> <br> </p>"
                    + "<p> 파일 첨부2: <input type=\"file\" name=\"file2\" size=80> <br> </p>";
        }else if(fileList.size() == 1) {
            result += "<p> 파일 첨부1: <input type=\"text\" name=\"file1\" value=\"" + downloadTempDir + this.userid + "/" + fileList.get(0).toString() +"\" readonly> <br> </p>"
                    + "<p> 파일 첨부2: <input type=\"file\" name=\"file2\" size=80> <br> </p>";
        }else if(fileList.size() == 2) {
            result += "<p> 파일 첨부1: <input type=\"text\" name=\"file1\" value=\"" + downloadTempDir + this.userid + "/" + fileList.get(0).toString() +"\" readonly> <br> </p>"
                    + "<p> 파일 첨부2: <input type=\"text\" name=\"file2\" value=\"" + downloadTempDir + this.userid + "/" + fileList.get(1).toString() +"\" > <br> </p>";
        }
            result += "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "
                    + "<input type=\"submit\" value=\"메일 보내기\" name=\"B1\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "
                    + "<input type=\"reset\" value=\"다시 입력\" name=\"B2\"></p>"
                    + "</form>";
        
        fileList = null;
        
        return result;
    }
    
    private void getMessageContent(Part msg) {
        try {
            String disposition = msg.getDisposition();
            
            if(disposition != null && (disposition.equalsIgnoreCase(Part.ATTACHMENT) 
                    || disposition.equalsIgnoreCase(Part.INLINE))) {
                //첨부파일인 경우 처리
                String fileName = MimeUtility.decodeText(msg.getFileName());
                if (fileName != null) {
                    //첨부파일이 있는 경우
                    fileList.add(fileName);
                }
            }else {
                //메일 본문 처리
                if(msg.isMimeType("text/*")) {
                    content = (String)msg.getContent();
                }else if(msg.isMimeType("multipart/alternative")) {
                    Multipart mp = (Multipart)msg.getContent();
                    for(int i=0;i<mp.getCount();i++) {
                        Part part = mp.getBodyPart(i);
                        if(part.isMimeType("text/plain")) {
                            getMessageContent(msg);
                        }
                    }
                }else if(msg.isMimeType("multipart/*")) {
                    Multipart mp = (Multipart)msg.getContent();
                    for(int i=0;i<mp.getCount();i++) {
                        getMessageContent(mp.getBodyPart(i));
                    }
                }
            }
        }catch (Exception e) {
            System.out.println("getMessageContent error: " + e);
        }
    }
    
    public String showReplyForm(int msgid) {
        String result = null;
        String replyTo = null;
        String replySubj = "Re: ";
        String replyBody = "\r\n\r\n\r\n*************** Original Message ***************r\n";

        // POP3 이용하여 메시지 헤더 정보중  replyTo, replySubj, replyBody 정보 가져오기
        // Property 설정
        Properties props = System.getProperties();

        // Session 설정
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);

        // Store 설정
        POP3Store store = null;

        try {
            store = (POP3Store) session.getStore("pop3");
            //System.out.println("host = " + host + ", user = " + userid + ", passwd = " + passwd);
            store.connect(this.host, this.userid, this.passwd);
            if (store.isConnected() == false) {
                store.close();
                return "Cannot get the message you want to reply for some errors";
            }

            // Folder 설정
            Folder folder = store.getDefaultFolder();
            folder = folder.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            // 메시지 본문 가져오기
            Message msg = folder.getMessage(msgid);

            // replyTo
            Address[] a = msg.getReplyTo();
            String t = a[0].toString();
            int start = t.lastIndexOf("<");
            int end = t.lastIndexOf(">");
            char[] charFrom = new char[100];
            if (start >= 0 && end >= start) {
                int i;
                for (i = 0; i < end - start - 1; i++) {
                    charFrom[i] = t.charAt(start + i + 1);
                }
                charFrom[i] = '\0';
                replyTo = new String(charFrom);
            } else {
                replyTo = t;
            }

            // replySubj
            replySubj += msg.getSubject();

            // replyBody
            replyBody += (String)msg.getContent();

        } catch (Exception ex) {
            System.out.println("showReplyForm error: " + ex);
        }

        try {
            store.close();
        } catch (Exception ex) {
            System.out.println("showReplyForm error: " + ex);
        }

        // final result
        result = "<form method=\"POST\" action=\"../smtpServlet\" enctype=\'multipart/form-data\'> "
                + "<p>받는 사람 <input type=\"text\" name=\"to\" size=\"80\" value=\"" + replyTo + "\"></p>"
                + "<p>CC:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                + "<input type=\"text\" name=\"cc\" size=\"80\"></p>"
                + "<p>메일 제목 <input type=\"text\" name=\"subj\" size=\"80\" value=\" " + replySubj + "\"></p>"
                + "<p>내 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 용</p>"
                + //"<p><textarea rows=\"10\" name=\"body\" cols=\"80\" value=\"" + replyBody + "\"></textarea></p>" +
                "<p><textarea rows=\"15\" name=\"body\" cols=\"80\">" + replyBody + "</textarea></p>"
                + "<p> </p>"
                + "<p> 파일 첨부1: <input type=\"file\" name=\"file1\" size=80> <br> </p>"
                + "<p> 파일 첨부2: <input type=\"file\" name=\"file2\" size=80> <br> </p>"
                + "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "
                + "<input type=\"submit\" value=\"메일 보내기\" name=\"B1\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "
                + "<input type=\"reset\" value=\"다시 입력\" name=\"B2\"></p>"
                + "</form>";

        return result;

    }
    
    /**
     * Getter for property file1.
     * @return Value of property file1.
     */
    public String getFile1() {
        return file1;
    }

    /**
     * Setter for property file1.
     * @param file1 New value of property file1.
     */
    public void setFile1(String file1) {
        this.file1 = file1;
    }

    /**
     * Getter for property file2.
     * @return Value of property file2.
     */
    public String getFile2() {
        return file2;
    }

    /**
     * Setter for property file2.
     * @param file2 New value of property file2.
     */
    public void setFile2(String file2) {
        this.file2 = file2;
    }

    /**
     * Getter for property pageno.
     * @return Value of property pageno.
     */
    public int getPageno() {
        return this.pageno;
    }

    /**
     * Setter for property pageno.
     * @param pageno New value of property pageno.
     */
    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

    /*
     * Date: 050119
     * Description: 메일 수신자(To:) 보여주기에 필요
     *  - Cc: 도 이와 같은 방식으로 처리가능함.
     */
    public String getMailReceiver(POP3Message msg) {
        String mailReceiver = null;
        String to = null;
        StringBuffer sb = new StringBuffer();
        char charTo[] = new char[1000];
        Vector addressVector = new Vector();

        // LJM 041231 - start
        String name = null;
        String email = null;
        // LJM 041231 - end

        try {
            InputStream is = msg.top(0);
            InputStreamReader isr = new InputStreamReader(is);

            char ch;
            int cnt = 0;
            int i = 0;

            // get all characters following "To: "
            while ((ch = (char) isr.read()) != -1 && cnt++ < 5000) {
                // System.out.print(ch);
                if (ch == '\n') {
                    if ((ch = (char) isr.read()) == 'T') {
                        if ((ch = (char) isr.read()) == 'o') {
                            if ((ch = (char) isr.read()) == ':') {
                                boolean endOfTo = false;
                                do {
                                    while ((ch = (char) isr.read()) == ' ');
                                    charTo[i++] = ch;
                                    while ((ch = (char) isr.read()) != '\r') {
                                        charTo[i++] = ch;
                                    }
                                    if ((char) isr.read() == '\n' && (char) isr.read() != ' ') {
                                        endOfTo = true;
                                    }
                                } while (!endOfTo);
                                //break;
                            }
                        }
                    }
                }
            }  // while()
            charTo[i] = '\0';


            if (i > 0) {
                to = new String(charTo);
                //System.out.println("char modified To = " + to);
                to = to.replaceAll("\"", "");
            }

            // Now make hyperlinks for the pair <name, email>
            boolean endOfTo = false;

            int start = 0;// = from.indexOf('"');
            int end = 0;// = from.indexOf('"', start+1);

            do {
                if (to.indexOf('<') != -1 && to.indexOf('>') != -1) { // "name" <email@host> 형태로 되어 있는 경우 처리

                    if (to.indexOf('"') != -1) {
                        start = to.indexOf('"') + 1;
                        end = to.indexOf('"', start + 1);
                    } else {  // name <email@host> 형태로 되어 있는 경우 처리
                        start = 0;
                        end = to.indexOf('<') - 1;
                    }

                    name = to.substring(start, end);
                    if (name.startsWith("=?")) {
                        name = MimeUtility.decodeText(name);
                    }

                    if ((start = name.indexOf('"')) != -1) {
                        end = name.indexOf('"', start + 1);
                        name = name.substring(start + 1, end);
                    }

                    start = to.indexOf('<') + 1;
                    end = to.indexOf('>');
                    email = to.substring(start, end);
                } else {
                    name = to;
                    if (name.startsWith("=?")) {
                        name = MimeUtility.decodeText(name);
                    }
                    email = to;
                }

                if (!endOfTo) {
                    int idx = to.indexOf(',');
                    if (idx >= 0) {
                        to = to.substring(idx + 1);
                    } else {
                        endOfTo = true;
                    }

                    String t = " <a href=mail_send_form2.jsp?recv=" + email + " >"
                            + name + "</a> " + (endOfTo ? "" : ", ");
                    sb.append(t);
                }

            } while (!endOfTo);
            // LJM 041231 - end
        } catch (Exception ex) {
            System.out.println("getMySubject error: " + ex);
        }
        mailReceiver = sb.toString();

        return mailReceiver;
    }

    public String getCc(POP3Message msg) {
        String mailReceiver = null;
        String to = null;
        StringBuffer sb = new StringBuffer();
        char charTo[] = new char[1000];
        Vector addressVector = new Vector();

        // LJM 041231 - start
        String name = null;
        String email = null;
        // LJM 041231 - end

        try {
            InputStream is = msg.top(0);
            InputStreamReader isr = new InputStreamReader(is);

            char ch;
            int cnt = 0;
            int i = 0;

            // get all characters following "To: "
            while ((ch = (char) isr.read()) != -1 && cnt++ < 5000) {
                // System.out.print(ch);
                if (ch == '\n') {
                    if ((ch = (char) isr.read()) == 'C') {
                        if ((ch = (char) isr.read()) == 'c') {
                            if ((ch = (char) isr.read()) == ':') {
                                boolean endOfTo = false;
                                do {
                                    while ((ch = (char) isr.read()) == ' ');
                                    charTo[i++] = ch;
                                    while ((ch = (char) isr.read()) != '\r') {
                                        charTo[i++] = ch;
                                    }
                                    if ((char) isr.read() == '\n' && (char) isr.read() != ' ') {
                                        endOfTo = true;
                                    }
                                } while (!endOfTo);
                                //break;
                            }
                        }
                    }
                }
            }  // while()
            charTo[i] = '\0';


            if (i > 0) {
                to = new String(charTo);
                //System.out.println("char modified To = " + to);
                to = to.replaceAll("\"", "");
            }

            // Now make hyperlinks for the pair <name, email>
            boolean endOfTo = false;

            int start = 0;// = from.indexOf('"');
            int end = 0;// = from.indexOf('"', start+1);

            do {
                if (to.indexOf('<') != -1 && to.indexOf('>') != -1) { // "name" <email@host> 형태로 되어 있는 경우 처리

                    if (to.indexOf('"') != -1) {
                        start = to.indexOf('"') + 1;
                        end = to.indexOf('"', start + 1);
                    } else {  // name <email@host> 형태로 되어 있는 경우 처리
                        start = 0;
                        end = to.indexOf('<') - 1;
                    }

                    name = to.substring(start, end);
                    if (name.startsWith("=?")) {
                        name = MimeUtility.decodeText(name);
                    }
                    if ((start = name.indexOf('"')) != -1) {
                        end = name.indexOf('"', start + 1);
                        name = name.substring(start + 1, end);
                    }

                    start = to.indexOf('<') + 1;
                    end = to.indexOf('>');
                    email = to.substring(start, end);
                } else {
                    name = to;
                    email = to;
                }

                if (!endOfTo) {
                    int idx = to.indexOf(',');
                    if (idx >= 0) {
                        to = to.substring(idx + 1);
                    } else {
                        endOfTo = true;
                    }

                    String t = " <a href=mail_send_form2.jsp?recv=" + email + " >"
                            + name + "</a> " + (endOfTo ? "" : ", ");
                    sb.append(t);
                }

            } while (!endOfTo);
            // LJM 041231 - end
        } catch (Exception ex) {
            System.out.println("getMySubject error: " + ex);
        }
        mailReceiver = sb.toString();

        return mailReceiver;
    }
}  // class MailHandleBean


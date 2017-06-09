/*
 * GetMailBean.java
 *
 * Created on 2004. 10. 5 7:42
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
import static java.lang.Boolean.TRUE;
import javax.activation.*;
//import javax.mail.util.SharedByteArrayInputStream;


///////////////////////
import java.util.logging.*;

/**
 *
 * @author  jongmin
 */
public class MailHandleBean {

    final String downloadTempDir = "/home/haei/NetBeansProjects/web/sendfile/";
    // LJM 090430 : �����ؾ� �� �κ� - end   ------------------
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

    /** Creates a new instance of GetMailBean */
    public MailHandleBean() {
        cc = null;
        to = null;
        msgBody = null;
        file1 = null;
        file2 = null;
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
     * �� �������� 10���� �޽����� �����ش�.
     * ������ ����: page
     * 10������ ���� ����:
     */
    public String listMessages() {
        // temporary user directory�� �����ִ� ���� ��� ����
        String tempUserDir = this.downloadTempDir + this.userid;
        File dir = new File(tempUserDir);
        if (dir.exists()) {
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                fileList[i].delete();
            }
        }
        

        // �޽��� ���� �����ֱ�
        String table_start = "<table border=\"1\" width=\"100%\" height=\"30\">";
        String table_end = "</table>";
        String table_title = "<tr> "
                + " <td width=\"5%\" height=\"16\" align=\"center\"> No. </td> "
                + " <td width=\"20%\" height=\"16\" align=\"center\"> From </td>"
                + " <td width=\"40%\" height=\"16\" align=\"center\"> Title </td>     "
                + " <td width=\"15%\" height=\"16\" align=\"center\"> Datetime </td>   "
                + " <td width=\"5%\" height=\"16\" align=\"center\"> CC </td>   "
                + " </tr>";

        //String result = table_start + table_title;
        String result = table_start + table_title;

        // Property ����
        Properties props = System.getProperties();

        // Session ����
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);

        // Store ����
        POP3Store store = null;
        String sender = null;
        String subject = null;
        String date = null;

        // LJM 041207 - ������ ���� ���� ��� �����ֱ⿡ ����.
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

            // Folder ����
            Folder folder = store.getDefaultFolder();
            folder = folder.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            // ������ �޽��� ���� �ľ�
            int totalMessages = folder.getMessageCount();

            /* LJM 041207 - page�� 10���� �޽��� ���� ����ϵ��� ����
             *
             *      message no.  (messagesPerPage = 10�� ���)
             *          22    21   20     ...     13    12   11   10      ...      3     2     1     0
             *           |<--  pageno = 1       -->|     |<--  pageno = 2       -->|     |  pageno=3 |
             *           ^ start                   ^ end ^ start                   ^ end ^ start     ^ end
             *
             */
            final int messagesPerPage = 10;
            // totalPageCount�� ���� ���� ��� ǥ���� ������ ������ ���� ���� �����
            // �� �� �ֵ��� page indicator�� �ϴܿ� ǥ���� �� ����.
            totalPageCount = (totalMessages / messagesPerPage);
            if (totalMessages % messagesPerPage != 0) {
                totalPageCount++;
            }

            int start = totalMessages - (messagesPerPage * (pageno - 1));
            int end = start - messagesPerPage + 1;
            if (end <= 0) {
                end = 1;
            }

            // �޽��� ��� ��������
            Message[] msgs = folder.getMessages(end, start);  // ������ �޽����� ���������� �����Ǿ�� ��.
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add("X-mailer");
            folder.fetch(msgs, fp);

            for (int i = start; i >= end; i--) {
                /*
                Address [] a = msgs[i].getFrom();
                sender = a[0].toString();
                 **/
                int index = i - end;//get num in one page
                //sender = msgs[index].getFrom()[0].toString();//new line
                sender = getMyFrom((POP3Message) msgs[index]);

                // subject
                subject = msgs[index].getSubject();
                //***********************************************
                // show title of get mails, "Reading Mail" lists
                //subject = getMySubject((POP3Message) msgs[index]);


                // date
                date = msgs[index].getSentDate().toLocaleString();


                // ������ ������ ��� ���� ����Ͽ� ��Ʈ������ �����
                String temp = "<tr> "
                        + " <td width=\"5%\" height=\"16\" align=\"center\">" + i + " </td> "
                        + " <td width=\"20%\" height=\"16\" align=\"center\">" + sender + "</td>"
                        + " <td width=\"40%\" height=\"16\" align=\"center\"> "
                        + " <a href=get_message.jsp?msgid=" + i + "> " + subject + "</a> </td>"
                        + " <td width=\"15%\" height=\"16\" align=\"center\">" + date + "</td>"
                        + " <td width=\"5%\" height=\"16\" align=\"center\">"
                        + "<a href=delete_message.jsp?msgid=" + i + "> DEL </a>" + "</td>"
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


        // LJM 041207 - ������ ���
        // line break
        result += "<br>";
        result += "<center>";

        for (int i = 1; i <= totalPageCount; i++) {
            if (i != this.pageno) {
                result += "<a href=list_mail.jsp?pageno=" + i + "> " + i + "</a> &nbsp; ";
            } else {
                result += i + "&nbsp; ";
            }
            // �� 20������ �� �ٲٱ� ����
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

        // Property ����
        Properties props = System.getProperties();
        props.put("mail.pop3.apop.enable", true);  // LJM 050111 - add

        // Session ����
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);

        // Store ����
        POP3Store store = null;
//        String sender; //unused variables
//        String subject;//unused variables
//        String date; //unused variables

        try {
            store = (POP3Store) session.getStore("pop3");
            //System.out.println("host = " + host + ", user = " + userid + ", passwd = " + passwd);
            store.connect(this.host, this.userid, this.passwd);
            if (store.isConnected() == false) {
                store.close();
                return body;
            }

            // Folder ����
            Folder folder = store.getDefaultFolder();
            folder = folder.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            // �޽��� ���� ��������
            /*  LJM 041010 : �Ʒ� �ڵ�� ����
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
            // �޽��� ������ �ռ� "���� ���" & "����"�� ����ϴ� �ڵ带  (String)body�� �߰�
            String From = this.getMyFrom2(msg);//msg.getFrom()[0].toString();//new line
            String To = this.getMailReceiver(msg);
            String Cc = this.getCc(msg);
            String Subj = msg.getSubject();//new line
            // 050325 ���� ��¥�� ���� Locale ����
            String _Date = null;

            _Date = msg.getSentDate().toLocaleString();


            body += ("FROM: " + From + " <br>");
            body += ("TO: " + To + " <br>");
            body += ("CC &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; : " + Cc + " <br>");
            body += ("DATE: " + _Date + " <br>");
            body += ("TITLE &nbsp;&nbsp;&nbsp; : " + Subj + " <br> <hr>");
            // LJM 041231 - end

            // Get body
            Object obj = msg.getContent();
            String contentType = msg.getContentType();

            //if (obj instanceof String) {
            if (contentType.toLowerCase().startsWith("text")) {
                // LJM 041009 : ������ HTML ������ ����
                //String contentType = msgs[id].getContentType();

                //System.out.println("�޽����� ContentType = " + contentType);
                if (contentType.toLowerCase().startsWith("text/plain")) {
                    //System.out.println("******** Content-Type: text/plain *************");
                    String temp = (String) obj;
                    // LJM 051101 -����
                    temp = temp.replaceAll("\r\n", "<br>");
                    body += temp;
                    // �� ���ٷ� ��������. 051101
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
                        // LJM 050110 - ���� �������� ��ü  // LJM 051101
                        body += (String) part.getContent();
                        body = body.replaceAll("\n", "<br>");

                    } else if (partContentType.toLowerCase().startsWith("message/rfc822")) {
                        byte[] b = new byte[50 * 1024];  // 20KB
                        part.getInputStream().read(b);
                        String t = new String(b);
                        body += ("<hr> <pre>" + t + "</pre>");
                    } else { // LJM 041202 - start : ���� ÷�� ó��
                        // 1. ÷�� �����̸� �ٿ�ε� ���丮�� ����
                        // 1.1 ÷�� ������ �ƴϸ� ����
                        if (!partContentDisposition.equalsIgnoreCase(Part.ATTACHMENT)
                                && !partContentDisposition.equalsIgnoreCase(Part.INLINE)) {
                            continue;
                        }

                        // 1.2 ÷�� ������ ������ temporary user directory ���� ���� �� �ʿ�� ����
                        String tempUserDir = this.downloadTempDir + this.userid;
                        File dir = new File(tempUserDir);
                        if (!dir.exists()) {  // tempUserDir ����
                            dir.mkdir();
                        }

                        // 1.3 part�� ������ ���Ϸ� ����
                        String filename = MimeUtility.decodeText(part.getFileName());
                        // ���ϸ� " "�� ���� ��� ������ �Ķ���ͷ� ���޽� ���� �߻���.
                        // " "�� ��� "_"�� ��ü��.
                        filename = filename.replaceAll(" ", "_");
                        DataHandler dh = part.getDataHandler();
                        FileOutputStream fos = new FileOutputStream(tempUserDir + "/" + filename);
                        dh.writeTo(fos);
                        fos.flush();
                        fos.close();

                        // 2. ���Ͽ� ���� �����۸�ũ ����


                        // LJM 050727 - ������

                        body += ("<hr> ATTACHMENT: <a href=../DownloadServlet?userid=" + this.userid
                                + "&filename=" + filename
                                + " target=_top> " + filename + "</a> <br>");

                    }  // // LJM 041202 - end : ���� ÷�� ó��
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
        Properties props = System.getProperties(); //new Properties();  // LJM 070411: ������.
        props.put("mail.smtp.host", this.host);
        //props.put("mail.smtp.auth", true);
        System.out.println("SMTP host : " + props.get("mail.smtp.host"));

        // session
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(false);  // LJM 070411: ���� �����Ⱑ �� �ȵǸ� false -> true�� �ؼ� ���� �ʿ��� ������ ����.

        try {
            SMTPMessage msg = new SMTPMessage(session);
            //read host id & email =>
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
            // Content-Type, Content-Transfer-Encoding ���� �ǹ� ����.
            // �ڵ����� �����Ǵ� �� ����. - LJM 041202
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
                // ���⼭ ���� �����ϴ� ���� ���� ���� ���� �߻���.
                // �׷��� ���� �����Ŀ� ���� �����ϱ�� ��.
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


            /* ������ ��...
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

    
//"답장" clicked,
    public String showReplyForm(int msgid) {
        String result = null;
        String replyTo = null;
        String replySubj = "Re: ";
        String replyBody = "\r\n\r\n\r\n--------------- Original Message ---------------\r\n";

        // POP3 �̿��Ͽ� �޽��� ��� ������  replyTo, replySubj, replyBody ���� ��������
        // Property ����
        Properties props = System.getProperties();
//        console.

        // Session ����
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);

        // Store ����
        POP3Store store = null;

        try {
            store = (POP3Store) session.getStore("pop3");
            //System.out.println("host = " + host + ", user = " + userid + ", passwd = " + passwd);
            store.connect(this.host, this.userid, this.passwd);
            if (store.isConnected() == false) {
                store.close();
                return "Cannot get the message you want to reply for some errors";
            }

            // Folder ����
            Folder folder = store.getDefaultFolder();
            folder = folder.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            // �޽��� ���� ��������
            Message msg = folder.getMessage(msgid);

            // replyTo
            Address[] a = msg.getReplyTo();
            System.out.println("a is : "+ a);
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
            replyTo = msg.getReplyTo()[0].toString();
            // replySubj
            //replySubj += this.getMySubject((POP3Message) msg);
            replySubj += msg.getSubject();
            
            // replyBody
            replyBody += msg.getContent();

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
                + "<p>TO <input type=\"text\" name=\"to\" size=\"80\" value=\"" + replyTo + "\"></p>"
                + "<p>CC:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                + "<input type=\"text\" name=\"cc\" size=\"80\"></p>"
                + "<p>SUBJECT <input type=\"text\" name=\"subj\" size=\"80\" value=\" " + replySubj + "\"></p>"
                + "<p>SEND &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; CONTENT</p>"
                + //"<p><textarea rows=\"10\" name=\"body\" cols=\"80\" value=\"" + replyBody + "\"></textarea></p>" +
                "<p><textarea rows=\"15\" name=\"body\" cols=\"80\">" + replyBody + "</textarea></p>"
                + "<p> </p>"
                + "<p> FILE 1: <input type=\"file\" name=\"file1\" size=80> <br> </p>"
                + "<p> FILE 2: <input type=\"file\" name=\"file2\" size=80> <br> </p>"
                + "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "
                + "<input type=\"submit\" value=\"SENDING\" name=\"B1\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "
                + "<input type=\"reset\" value=\"RESET\" name=\"B2\"></p>"
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
     * Description: ���� ������(To:) �����ֱ⿡ �ʿ�
     *  - Cc: �� �̿� ���� ������� ó��������.
     */
    public String getMailReceiver(POP3Message msg) {
        String mailReceiver = null;
        String to = null;
        StringBuffer sb = new StringBuffer();
        char charTo[] = new char[1000];
        //Vector addressVector = new Vector();//unused variables

        // LJM 041231 - start
        String name = null;
        String email = null;
        // LJM 041231 - end
           
        try {
            InputStream is = msg.top(0);
            InputStreamReader isr = new InputStreamReader(is);
            
            //ArrayList<Character> places = new ArrayList<Character>(Arrays.asList('\n','T', 'o', ':', ' ', '\r'));

            char ch;
            int cnt = 0;
            int i = 0;
            boolean endOfTo = true;
            
            // get all characters following "To: "
            while ((ch = (char) isr.read()) != -1 && cnt++ < 5000) {
                // System.out.print(ch);
                
                //////////////////////////////////////////////////////////
                /*int right_cnt = 0;
                for( int next = 0; places.size() >= next; next++ ){
                    if((ch = (char) isr.read()) == places.get(next))
                    {
                        right_cnt++;
                    }
                }*/
                //////////////////////////////////////////////////////////
                // NEXT : isr.read(), EXIT : endOfTo , KEEP : 
                
                if (ch == '\n') {
                    if ((ch = (char) isr.read()) == 'T') {
                        if ((ch = (char) isr.read()) == 'o') {
                            if ((ch = (char) isr.read()) == ':') {
                                
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
                    
                    //1. state pattern
                    //2.{'S','C','a','n'} -> compare with isr.read()
                }//if
            }  // while()
            charTo[i] = '\0';


            if (i > 0) {
                to = new String(charTo);
                //System.out.println("char modified To = " + to);
                to = to.replaceAll("\"", "");
            }

            // Now make hyperlinks for the pair <name, email>
            endOfTo = false;

            int start = 0;// = from.indexOf('"');
            int end = 0;// = from.indexOf('"', start+1);

            do {
                if (to.indexOf('<') != -1 && to.indexOf('>') != -1) { // "name" <email@host> ���·� �Ǿ� �ִ� ��� ó��

                    if (to.indexOf('"') != -1) {
                        start = to.indexOf('"') + 1;
                        end = to.indexOf('"', start + 1);
                    } else {  // name <email@host> ���·� �Ǿ� �ִ� ��� ó��
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
//        Vector addressVector = new Vector(); //unused variables

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
                    if (((ch = (char) isr.read()) == 'C') && (((char) isr.read()) == 'c') && (((char) isr.read()) == ':')) {
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
                    }//if (isr.read()) x3
                }//if (ch)
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
                if (to.indexOf('<') != -1 && to.indexOf('>') != -1) { // "name" <email@host> ���·� �Ǿ� �ִ� ��� ó��

                    if (to.indexOf('"') != -1) {
                        start = to.indexOf('"') + 1;
                        end = to.indexOf('"', start + 1);
                    } else {  // name <email@host> ���·� �Ǿ� �ִ� ��� ó��
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
    
    /**
     * ����: �޽��� ���� ����/���� ���
     * ����ڰ� msg_num�� what(= true or false)�� �Ѱ��ָ�
     * �׿� ���� ������ �����Ѵ�.
     */
    public String deleteMessage(int msg_num, boolean what) {
        boolean exception_met = false;

        // Property ����
        Properties props = System.getProperties();

        // Session ����
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);

        // Store ����
        POP3Store store = null;
//        String sender; //unused variables
//        String subject; //unused variables
//        String date; //unused variables

        try {
            store = (POP3Store) session.getStore("pop3");
            //System.out.println("host = " + host + ", user = " + userid + ", passwd = " + passwd);
            store.connect(this.host, this.userid, this.passwd);
            if (store.isConnected() == false) {
                store.close();
                return new String("this is not connecting..");
            }

            // Folder ����
            Folder folder = store.getDefaultFolder();
            folder = folder.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            // Message�� DELETED flag ����
            Message msg = folder.getMessage(msg_num);
            msg.setFlag(Flags.Flag.DELETED, what);  // set the DELETED flag

            // �������� �޽��� ����
            //Message [] expungedMessage = folder.expunge();  <-- ���� ���� �� �ǰ� ����. ������ close()�� �� expunge�ؾ� ��.
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
            result = "���� ������ �����Ͽ����ϴ�.";
        } else {
            result = "���� ������ �����Ͽ����ϴ�";
        }

        return result;  // ���������� ����/���� ��� �Ϸ�
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
        
        //########check the original version!! it gives same results name == email 
            name = from;
            email = from;
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
            //########check the original version!! it gives same results name == email 
                name = from;
                email = from;
            }
            // LJM 041231 - end

            // System.out.println("************** getMyFrom: E *************");

         catch (Exception ex) {
            System.out.println("getMySubject error: " + ex);
        }

        // name�� URLEncoding��.
        String param_name = null;
        try {
            //param_name = java.net.URLEncoder.encode(name, "euc-kr");
            param_name = name.replaceAll(" ", "%20");
        } catch (Exception ex) {
            System.out.println("getMyFrom2(): URLEncoder.encode error: " + ex);
        }
        /*email : ok, but name : same with email --> "mailtester@127.0.0.1 mailtester@127.0.0.1 NULL NULL NULL"*/
        return "<a href=mail_send_form2.jsp?recv=" + email + "> " + name + " </a> "
                + " &nbsp;&nbsp; <a href=addr_book_show.jsp?add=1&name=" + param_name + "&email=" + email + "> ADD ADDRESS</a>";
    }
}  // class MailHandleBean
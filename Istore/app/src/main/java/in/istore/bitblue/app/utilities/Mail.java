package in.istore.bitblue.app.utilities;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mail extends Authenticator {
    private String _user, _pass, _from, _port, _sport, _host, _subject, _body;
    private String[] _to;
    private boolean _auth, _debuggable;
    private Multipart _multipart;

    public Mail() {
        _host = "smtp.gmail.com";
        _port = "465";
        _sport = "465";
        _user = "";
        _pass = "";
        _from = "";
        _subject = "";
        _body = "";
        _debuggable = false;
        _auth = true;
        _multipart = new MimeMultipart();
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    public Mail(String user, String pass) {
        this();
        _user = user;
        _pass = pass;
    }

    public boolean send() throws Exception {
        Properties props = _setProperties();
        if (!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("") && !_subject.equals("") && !_body.equals("")) {
            Session session = Session.getInstance(props, this);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(_from));
            InternetAddress[] addressTo = new InternetAddress[_to.length];
            for (int i = 0; i < _to.length; i++) {
                addressTo[i] = new InternetAddress(_to[i]);
            }
            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);
            msg.setSubject(_subject);
            msg.setSentDate(new Date());
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(_body);
            _multipart.addBodyPart(messageBodyPart);
            msg.setContent(_multipart);
            Transport.send(msg);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(_user, _pass);
    }

    private Properties _setProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", _host);
        if (_debuggable) {
            props.put("mail.debug", "true");
        }
        if (_auth) {
            props.put("mail.smtp.auth", "true");
        }
        props.put("mail.smtp.port", _port);
        props.put("mail.smtp.socketFactory.port", _sport);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        return props;
    }

    public String getBody() {
        return _body;
    }

    public void setBody(String _body) {
        this._body = _body;
    }

    public void setTo(String[] _to) {
        this._to = _to;
    }

    public String getFrom() {
        return _from;
    }

    public void setFrom(String _from) {
        this._from = _from;
    }

    public String getSubject() {
        return _subject;
    }

    public void setSubject(String _subject) {
        this._subject = _subject;
    }
}

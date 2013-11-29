package dderrien.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;

public class MailConnector {

    private static Logger log = Logger.getLogger(MailConnector.class.getName());

    /// Made available for test purposes
    public static void setMockLogger(Logger mockLogger) {
        log = mockLogger;
    }

    protected static Logger getLogger() {
        return log;
    }

    //
    // Mail properties (transparently handled by App Engine)
    //
    // Common mail properties
    //   mail.transport.protocol: smtp/pop3/imap
    //   mail.host: ApplicationSettings.get().getEmailDomain()
    //   mail.user: ???
    //   mail.password: ???
    //

    /**
     * Use the Google App Engine API to get the Mail message carried by the HTTP request
     *
     * @param request Request parameters submitted by the Google App Engine in response to the reception of an mail message sent to a valid engine entry point
     * @return Extracted mail message information
     *
     * @throws IOException If the HTTP request stream parsing fails
     */
    public static MimeMessage getMailMessage(HttpServletRequest request) throws IOException, MessagingException {
        // Extract the incoming message
        Session session = Session.getDefaultInstance(new Properties(), null);
        MimeMessage mailMessage = new MimeMessage(session, request.getInputStream());
        return mailMessage;
    }

    public static InternetAddress notifier;
    static {
        try {
            notifier = prepareInternetAddress(
                    "UTF8",
                    "notifier@custom-portal.appspotmail.com",
                    "notifier-noreply@custom-portal.appspotmail.com"
            );
        }
        catch (AddressException e) { } // Not expected as the default are valid addresses
    }

    /**
     * Helper setting up an InternetAddress instance with the given parameters
     *
     * @param name Display name of the e-mail address
     * @param email E-mail address
     * @return address Fetched InternetAddress instance
     *
     * @throws AddressException If the given email address is invalid
     */
    public static InternetAddress prepareInternetAddress(String charsetEncoding, String name, String email) throws AddressException {
        InternetAddress address = new InternetAddress(email);
        if (name != null && 0 < name.length()) {
            try {
                address.setPersonal(name, charsetEncoding);
            }
            catch (UnsupportedEncodingException ex) {
                // Too bad! The recipient will only see the e-mail address
                getLogger().warning("Invalid email user name: " + name + " -- message: " + ex.getMessage());

                // Note for the testers:
                //   Don't know how to generate a UnsupportedEncodingException by just
                //   injecting a corrupted UTF-8 sequence and/or a wrong character set

            }
        }
        return address;
    }

    /**
     * Use the Google App Engine API to send an mail message to the identified e-mail address
     *
     * @param receiverId E-mail address of the recipient
     * @param recipientName recipient display name
     * @param subject Subject of the message that triggered this response
     * @param message Message to send
     * @param locale recipient's locale
     *
     * @throws MessagingException If one of the message attribute is incorrect
     * @throws UnsupportedEncodingException if the e-mail address is invalid
     */
    public static void sendMailMessage(String receiverId, String recipientName, String subject, String message, Locale locale) throws MessagingException, UnsupportedEncodingException {
        if (0 < foolMessagePost) {
            foolMessagePost --;
            throw new MessagingException("Done in purpose!");
        }

        InternetAddress recipient = new InternetAddress(receiverId, recipientName, "UTF8");

        Session session = Session.getDefaultInstance(new Properties(), null);

        MimeMessage mailMessage = new MimeMessage(session);
        mailMessage.setFrom(notifier);
        mailMessage.setRecipient(Message.RecipientType.TO, recipient);
        mailMessage.setSubject(subject, "UTF8");
        setContentAsPlainTextAndHtml(mailMessage, message, locale.getLanguage());
        Transport.send(mailMessage);
    }

    /**
     * Utility inserting the given text into the message as a multi-part segment with a plain text and a plain HTML version
     *
     * @param message Message container
     * @param content Text to send
     *
     * @throws MessagingException If the submitted text is not accepted
     */
    public static void setContentAsPlainTextAndHtml(MimeMessage message, String content, String language) throws MessagingException {
        MimeBodyPart textPart = new MimeBodyPart();
        MimeBodyPart htmlPart = new MimeBodyPart();

        // TODO: send a default message in case "content" is null or empty

        if (content == null) {
        	content = "";
        }
        textPart.setContent(content, "text/plain; charset=UTF-8");
        textPart.setContentLanguage(new String[] { language });
        htmlPart.setContent(content, "text/html; charset=UTF-8");
        htmlPart.setContentLanguage(new String[] { language });

        MimeMultipart multipart = new MimeMultipart("alternative");
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(htmlPart);

        message.setContent(multipart);
    }

    /**
     * Study the given MIME message part to extract the first text content
     *
     * @param message MimeMessage or MimeBodyPart to study
     * @return Extracted piece of text (can be text/plain or text/HTML, on one or many lines)
     *
     * @throws MessagingException When an error occurs while parsing a piece of the message
     * @throws IOException When an error occurs while reading the message stream
     */
    public static String getText(MimeMessage message) throws MessagingException, IOException {
        if (message.isMimeType("text/plain")) {
            return (String) message.getContent();
            // return convertToString((InputStream) message.getContent());
        }
        if (message.isMimeType("text/html")) {
            return (String) message.getContent();
            // return convertToString((InputStream) message.getContent());
        }
        if (message.isMimeType("multipart/*")) {
            Multipart multipart = new MimeMultipart(message.getDataHandler().getDataSource());
            return getText(multipart);
        }
        return "";
    }

    /**
     * Study the given MIME part collection to extract the first text content
     *
     * @param multipart Collection to study
     * @return Extracted text or empty string
     *
     * @throws MessagingException When an error occurs while parsing a piece of the message
     * @throws IOException When an error occurs while reading the message stream
     */
    protected static String getText(Multipart multipart) throws MessagingException, IOException {
        int count = multipart.getCount();
        for(int i = 0; i < count; i++) {
            Part part = multipart.getBodyPart(i);
            String partText = getText(part);
            if (!"".equals(partText)) {
                return partText;
            }
        }
        throw new MessagingException("No text found in this message");
    }

    /**
     * Study the given MIME part to extract the first text content.
     * Note that the algorithm excludes "text/*" attachment.
     * Note also that the algorithm does not parse recursively the "multipart/*" elements.
     *
     * @param part Piece of content to study
     * @return Extracted text or empty string
     *
     * @throws MessagingException When an error occurs while parsing a piece of the message
     * @throws IOException When an error occurs while reading the message stream
     */
    protected static String getText(Part part) throws MessagingException, IOException {
        String filename = part.getFileName();
        if (filename == null && part.isMimeType("text/plain")) {
            return (String) part.getContent();
            // return StringUtils.toUTF8((String) part.getContent());
            // return convertToString((InputStream) part.getContent());
        }
        if (filename == null && part.isMimeType("text/html")) {
            return (String) part.getContent();
            // return StringUtils.toUTF8((String) part.getContent());
            // return convertToString((InputStream) part.getContent());
        }
        // We don't want to go deeper because this part is probably an attachment or a reply!
        // if (part.isMimeType("multipart/*")) {
            // return getText(new MimeMultipart(part.getDataHandler().getDataSource()));
        // }
        return "";
    }

    /**
     * Utility method dumping the content of an InputStream into a String buffer
     *
     * @param in InputStream to process
     * @return String containing all characters extracted from the InputStream
     *
     * @throws IOException If the InputStream process fails
     */
    /*
    //
    // Not used anymore since 1.2.8 because getContent() return String instance instead of InputStream
    //
    protected static String convertToString(InputStream in) throws IOException {
        int character = in.read();
        StringBuilder out = new StringBuilder();
        while (character != -1) {
            out.append((char) character);
            character = in.read();
        }
        return LocaleValidator.toUTF8(out.toString());
    }
    */

    public static int foolMessagePost = 0;

    /**
     * Made available for unit tests
     */
    public static void foolNextMessagePost() {
        foolMessagePost ++;
    }

    /**
     * Send the specified message to the recipients of the "administrators" list
     *
     * @param subject Message subject
     * @param body Message content
     *
     * @throws MessagingException If the message sending fails
     */
    public static void reportErrorToAdmins(String subject, String body) throws MessagingException {
        reportErrorToAdmins(null, subject, body);
    }

    /**
     * Send the specified message to the recipients of the "administrators" list
     *
     * @param from Message initiator
     * @param subject Message subject
     * @param body Message content
     *
     * @throws MessagingException If the message sending fails
     */
    public static void reportErrorToAdmins(String from, String subject, String body) throws MessagingException {
        if (0 < foolMessagePost) {
            foolMessagePost --;
            throw new MessagingException("Done in purpose!");
        }

        Session session = Session.getDefaultInstance(new Properties(), null);

        MimeMessage messageToForward = new MimeMessage(session);
        messageToForward.setFrom(notifier);
        messageToForward.setRecipient(Message.RecipientType.TO, new InternetAddress("admins"));
        messageToForward.setSubject(from == null ? subject : "Fwd: (" + from + ") " + subject);
        setContentAsPlainTextAndHtml(messageToForward, body, "en");

        // getLogger().warning("Reporting to 'admins' (medium: mail) -- subject: [" + messageToForward.getSubject() + "] -- message: [" + body + "]");

        Transport.send(messageToForward);
    }

    /**
     * Helper extracting the content of a badly quoted-printable message
     *
     * @param message Message the system cannot decode
     * @return Decoded message
     *
     * @throws MessagingException If the system cannot the message InputStream
     * @throws IOException If the message InputStream cannot be read one character at a time
     */
    public static String alternateGetText(MimeMessage message) throws MessagingException, IOException {
        InputStream in = message.getRawInputStream();
        if (in == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());
        byte[] buffer = new byte[2];
        int status;
        while ((status = in.read(buffer, 0, 1)) != -1) {
            if (buffer[0] == '=') {
                // Get the next controlled character
                status = in.read(buffer, 0, 1);
                if (status == -1) {
                    // Ignore trailing '='
                }
                else if (buffer[0] == '\r') {
                    // Get next '\n' and ignore sequence '=\r\n'
                    in.read(buffer, 0, 1);
                }
                else if (buffer[0] == '\n') {
                    // Ignore sequence '=\n'
                }
                else {
                    // Convert encoded character
                    in.read(buffer, 1, 1);
                    out.write((byte) ((Character.digit(buffer[0], 16) << 4) + Character.digit(buffer[1], 16)));
                }
            }
            else {
                out.write(buffer, 0, 1);
            }
        }

        String contentType = message.getContentType(), charset = null;
        int charsetPartIdx = contentType.indexOf("charset");
        if (charsetPartIdx != -1) {
            charset = contentType.substring(contentType.indexOf('=', charsetPartIdx + 1) + 1).trim();
        }
        else {
            charset = "UTF-8";
        }

        return new String(out.toByteArray(), charset);
    }
}
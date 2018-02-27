package pumpkinbox.server;

import com.sendgrid.*;

import java.io.IOException;

/**
 * Created by ramiawar on 4/4/17.
 */
public class EmailTest {

    public static void main(String[] args) throws IOException {
        Email from = new Email("PumpkinBox@pumpkinbox.com");
        String subject = "Sending with PumpkinBox is Fun";
        Email to = new Email("rami.awar.ra@gmail.com");
        Content content = new Content("text/plain", "and easy to do anywhere, even with Java!");
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid("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");

        Request request = new Request();

        try {
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = mail.build();
            Response response = sg.api(request);
            System.out.println(response.statusCode);
            System.out.println(response.body);
            System.out.println(response.headers);
        } catch (IOException ex) {
            throw ex;
        }
    }

}

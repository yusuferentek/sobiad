import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;

    public class MailGun {
        public String sendMail_asosSocial(String to, String subject, String text, String campaign) {
            //String replyto = "info@ispescongress.com";

            Client client = Client.create();
            client.addFilter(new HTTPBasicAuthFilter("api",
                    "key-0b0e55e4aaacddb970c4067de747892b"));
            WebResource webResource = client.resource("https://api.mailgun.net/v3/akademikiletisim.com/messages");
            MultivaluedMapImpl formData = new MultivaluedMapImpl();
            formData.add("from", "Asos Eğitim <sosyalmedya@asosegitim.com>");
            formData.add("to", to);
            formData.add("subject", subject);
            formData.add("html", text);
            formData.add("h:MIME-Version", "1.0");
            formData.add("h:Content-Type", "text/html");
            formData.add("h:charset", "UTF-8");
            formData.add("o:campaign", campaign);
            formData.add("o:tag", campaign);
            formData.add("o:dkim", "yes");
            //if (replyto != null) {
                //formData.add("h:Reply-To", replyto);
            //}
            return webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData).toString();


        }



    }





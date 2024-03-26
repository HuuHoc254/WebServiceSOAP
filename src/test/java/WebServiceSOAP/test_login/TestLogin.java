package WebServiceSOAP.test_login;

import WebServiceSOAP.config.TestWebConfiguration;
import org.example.Main;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

@Import(TestWebConfiguration.class)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
public class TestLogin {
    @Autowired
    private WebTestClient webClient;
    @Test
    public void loginSuccess() {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:your=\"http://yournamespace.com\">\n" +
                "<soapenv:Header/>\n" +
                "<soapenv:Body>\n" +
                "<your:loginRequest>\n" +
                "<your:accountName>huuhoc</your:accountName>\n" +
                "<your:password>Chaungoanbacho1</your:password>\n" +
                "</your:loginRequest>\n" +
                "</soapenv:Body>\n" +
                "</soapenv:Envelope>";

        this.webClient.post().uri("/ws/example.wsdl").contentType(MediaType.TEXT_XML).accept(MediaType.TEXT_XML).bodyValue(request)
                .exchange().expectStatus().isOk();
    }
    @Test
    public void loginFail() {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:your=\"http://yournamespace.com\">\n" +
                "<soapenv:Header/>\n" +
                "<soapenv:Body>\n" +
                "<your:loginRequest>\n" +
                "<your:accountName>huuhoc</your:accountName>\n" +
                "<your:password>Chaungoanbacho</your:password>\n" +
                "</your:loginRequest>\n" +
                "</soapenv:Body>\n" +
                "</soapenv:Envelope>";

        this.webClient.post().uri("/ws/example.wsdl").contentType(MediaType.TEXT_XML).accept(MediaType.TEXT_XML).bodyValue(request)
                .exchange().expectStatus().is5xxServerError();
    }

}

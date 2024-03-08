package WebServiceSOAP.account_test;

import WebServiceSOAP.config.TestWebConfiguration;
import org.example.Main;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@Import(TestWebConfiguration.class)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
public class SearchAccountTest {
    @Autowired
    private WebTestClient webClient;
    @Test
    public void searchAccountSuccess() {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:your=\"http://yournamespace.com\">\n" +
                "<soapenv:Header/>\n" +
                "<soapenv:Body>\n" +
                "<your:searchAccountRequest>\n" +
                "<your:accountName></your:accountName>\n" +
                "<your:fullName></your:fullName>\n" +
                "<your:phoneNumber></your:phoneNumber>\n" +
                "</your:searchAccountRequest>\n" +
                "</soapenv:Body>\n" +
                "</soapenv:Envelope>";

        this.webClient
                .post()
                .uri("/ws/example.wsdl")
                .contentType(MediaType.TEXT_XML)
                .accept(MediaType.TEXT_XML)
                .header("token","$2a$10$mqIWQViDBl1hMOUS8ZnDV.mbTxvQv7mASFT.GHcaCiWlbZP7.iAra")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk();
    }

}
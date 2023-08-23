package us.pax.basil.controller;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import us.pax.basil.service.aws.ses.EmailService;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "AWS related APIs")
@RestController
@RequestMapping("/aws")
@RequiredArgsConstructor
public class AWSController {

    private final EmailService emailService;

    @GetMapping("/email/test")
    public Mono<String> sendEmail() throws IOException {
        Integer moOID = 111;
        Double invoice = 100d;
        Integer clientGroup = 459;
        String email = "fangchen.ye@pax.us";
        String subject = String.format("RMA #%d Confirmation", moOID);
        String content = constructEmail(moOID, invoice, clientGroup);
//        String email = "success@simulator.amazonses.com";
        return emailService.sendEmail(email, subject, content)
                .map(response ->
                        response.isSuccess() ? "Success, message ID: " + response.getResponse().messageId()
                                : response.getException() != null ? response.getException().getMessage()
                                : "Service Disabled");
    }

    private String constructEmail(Integer moOID, Double invoice, Integer clientGroup) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("moOID", moOID);
        map.put("invoice", invoice);

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = null;
        if(clientGroup.equals(458)){
            //small client
            mustache = mf.compile("html/email/smallMktRmaEmail.mustache");
        }else {
            mustache = mf.compile("html/email/midLargeRmaEmail.mustache");
        }
        StringWriter writer = new StringWriter();
        mustache.execute(writer, map).flush();
        String emailBody = writer.toString();
        return emailBody;
    }
}

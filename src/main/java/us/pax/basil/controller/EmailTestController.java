package us.pax.basil.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import us.pax.basil.dto.output.ApiResultDTO;
import us.pax.basil.service.EmailService;

@RestController
public class EmailTestController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/testEmail")
    public ApiResultDTO testEmail(){
        try{
            emailService.sendRmaConfirmationEmail(123, "xiaoxuan.liao@pax.us");
            return new ApiResultDTO("0", "Email Delivery success");
        }
        catch (Exception e){
            return new ApiResultDTO("0", "Email Delivery fail");
        }
    }
}

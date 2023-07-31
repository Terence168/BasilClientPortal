package us.pax.basil.service;

public interface EmailService {
    void sendRmaConfirmationEmail(int rma, String email) throws Exception;
}

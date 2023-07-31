package us.pax.basil.service;

public interface EmailService {
    public void sendRmaConfirmationEmail(int rma, String email) throws Exception;
}

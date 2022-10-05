package us.pax.basil.constant;

public class PasswordConstant {
	public final static String PASSWORD_CHANGE_EMAIL_SUBJECT="Basil Client Portal Password Change";
	public final static String PASSWORD_RESET_EMAIL_SUBJECT="Basil Client Portal Password Reset";
	public final static String CHANGE_PASSWORD_LINK_TITLE= "Set Password";
	public final static String RESET_PASSWORD_LINK_TITLE= "Reset Password";
	public final static String RESET_PASSWORD_HTML_FILE= "Reset Password";
	
    public final static String WELCOME_USER_SUBJECT="Welcome To Basil Client Portal";
    public final static String WELCOME_USER_LINK_TITLE="Activate Account";

	public final static String FORGOT_PASSWORD_HTML_FILE= "html/forgot.html";
    public final static String WELCOME_USER_HTML_FILE="html/welcome.html";

	public final static String FRONTEND_CHANGE_PASSWORD_URL="/basil/<frontend change password URL>/";
	public final static String FRONTEND_RESET_PASSWORD_URL= "/basil/<frontend reset password URL>/";
    public final static String FRONTEND_WELCOME_USER_URL="/basil/<frontend welcome user URL>/";

    public static final Integer EXPIRATION=3600000;  // 1 hour to respond
    public static final String NEW_USER_PASSWORD="Pax4Future!@";

}

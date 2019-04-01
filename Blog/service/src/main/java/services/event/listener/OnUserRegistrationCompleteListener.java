package services.event.listener;

import freemarker.template.TemplateException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pojos.User;
import services.event.OnUserRegistrationCompleteEvent;
import services.exceptions.MailSendException;
import services.impl.EmailVerificationTokenService;
import services.impl.MailService;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
public class OnUserRegistrationCompleteListener implements ApplicationListener<OnUserRegistrationCompleteEvent> {

	@Autowired
	private EmailVerificationTokenService emailVerificationTokenService;

	@Autowired
	private MailService mailService;

	private static final Logger logger = Logger.getLogger(OnUserRegistrationCompleteListener.class);

	@Override
	@Async
	public void onApplicationEvent(OnUserRegistrationCompleteEvent onUserRegistrationCompleteEvent) {
		sendEmailVerification(onUserRegistrationCompleteEvent);
	}

	private void sendEmailVerification(OnUserRegistrationCompleteEvent event) {
		User user = event.getUser();
		String token = emailVerificationTokenService.generateNewToken();
		emailVerificationTokenService.createVerificationToken(user, token);

		String recipientAddress = user.getEmail();
		String emailConfirmationUrl = event.getRedirectUrl().queryParam("token", token).toUriString();

		try {
			mailService.sendEmailVerification(emailConfirmationUrl, recipientAddress);
		} catch (IOException | TemplateException | MessagingException e) {
			logger.error(e);
			throw new MailSendException(recipientAddress, "Email Verification");
		}
	}
}

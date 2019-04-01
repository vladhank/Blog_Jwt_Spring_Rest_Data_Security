package services.event.listener;

import freemarker.template.TemplateException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pojos.User;
import pojos.tokens.EmailVerificationToken;
import services.event.OnRegenerateEmailVerificationEvent;
import services.exceptions.MailSendException;
import services.impl.MailService;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
public class OnRegenerateEmailVerificationListener implements ApplicationListener<OnRegenerateEmailVerificationEvent> {

	@Autowired
	private MailService mailService;

	private static final Logger logger = Logger.getLogger(OnRegenerateEmailVerificationListener.class);

	@Override
	@Async
	public void onApplicationEvent(OnRegenerateEmailVerificationEvent onRegenerateEmailVerificationEvent) {
		resendEmailVerification(onRegenerateEmailVerificationEvent);
	}

	private void resendEmailVerification(OnRegenerateEmailVerificationEvent event) {
		User user = event.getUser();
		EmailVerificationToken emailVerificationToken = event.getToken();
		String recipientAddress = user.getEmail();

		String emailConfirmationUrl =
				event.getRedirectUrl().queryParam("token", emailVerificationToken.getToken()).toUriString();
		try {
			mailService.sendEmailVerification(emailConfirmationUrl, recipientAddress);
		} catch (IOException | TemplateException | MessagingException e) {
			logger.error(e);
			throw new MailSendException(recipientAddress, "Email Verification");
		}
	}

}

package services.event.listener;

import freemarker.template.TemplateException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pojos.PasswordResetToken;
import pojos.User;
import services.event.OnGenerateResetLinkEvent;
import services.exceptions.MailSendException;
import services.impl.MailService;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
public class OnGenerateResetLinkEventListener implements ApplicationListener<OnGenerateResetLinkEvent> {
	@Autowired
	private MailService mailService;

	private static final Logger logger = Logger.getLogger(OnGenerateResetLinkEventListener.class);

	@Override
	@Async
	public void onApplicationEvent(OnGenerateResetLinkEvent onGenerateResetLinkMailEvent) {
		sendResetLink(onGenerateResetLinkMailEvent);
	}

	private void sendResetLink(OnGenerateResetLinkEvent event) {
		PasswordResetToken passwordResetToken = event.getPasswordResetToken();
		User user = passwordResetToken.getUser();
		String recipientAddress = user.getEmail();
		String emailConfirmationUrl = event.getRedirectUrl().queryParam("token", passwordResetToken.getToken())
				.toUriString();
		try {
			mailService.sendResetLink(emailConfirmationUrl, recipientAddress);
		} catch (IOException | TemplateException | MessagingException e) {
			logger.error(e);
			throw new MailSendException(recipientAddress, "Email Verification");
		}
	}

}

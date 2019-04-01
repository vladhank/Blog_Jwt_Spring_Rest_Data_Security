package services.event.listener;

import pojos.User;
import freemarker.template.TemplateException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import services.event.OnUserAccountChangeEvent;
import services.exceptions.MailSendException;
import services.impl.MailService;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
public class OnUserAccountChangeListener implements ApplicationListener<OnUserAccountChangeEvent> {

	@Autowired
	private MailService mailService;

	private static final Logger logger = Logger.getLogger(OnUserAccountChangeListener.class);


	@Override
	@Async
	public void onApplicationEvent(OnUserAccountChangeEvent onUserAccountChangeEvent) {
		sendAccountChangeEmail(onUserAccountChangeEvent);
	}


	private void sendAccountChangeEmail(OnUserAccountChangeEvent event) {
		User user = event.getUser();
		String action = event.getAction();
		String actionStatus = event.getActionStatus();
		String recipientAddress = user.getEmail();

		try {
			mailService.sendAccountChangeEmail(action, actionStatus, recipientAddress);
		} catch (IOException | TemplateException | MessagingException e) {
			logger.error(e);
			throw new MailSendException(recipientAddress, "Account Change Mail");
		}
	}
}

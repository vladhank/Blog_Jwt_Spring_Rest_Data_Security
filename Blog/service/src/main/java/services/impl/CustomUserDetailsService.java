package services.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pojos.CustomUserDetails;
import pojos.User;
import repositories.UserRepository;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	private static final Logger logger = Logger.getLogger(CustomUserDetailsService.class);

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> dbUser = userRepository.findByEmail(email);
		logger.info("Got user: " + dbUser + " for " + email);
		return dbUser.map(CustomUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("Couldn't find a matching email in the " +
						"database for " + email));
	}

	public UserDetails loadUserById(Long id) {
		Optional<User> dbUser = userRepository.findById(id);
		logger.info("Got user: " + dbUser + " for " + id);
		return dbUser.map(CustomUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("Couldn't find a matching id in the " +
						"database for " + id));
	}
}

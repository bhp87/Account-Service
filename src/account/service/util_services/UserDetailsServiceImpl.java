package account.service.util_services;

import account.model.User;
import account.repo.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountLockedException;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    private final UserRepository accountRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optUser = accountRepository.findByEmailIgnoreCase(email);

        if (optUser.isPresent()) {
            if (!optUser.get().isAccountNonLocked()) {
                throw new LockedException("User account is locked");
            }
            return new MyUserDetails(optUser.get());
        } else {
            throw new UsernameNotFoundException(String.format("Email[%s] not found", email));
        }

    }


}
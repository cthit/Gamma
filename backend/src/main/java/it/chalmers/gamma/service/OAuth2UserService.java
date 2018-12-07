package it.chalmers.gamma.service;

import it.chalmers.gamma.db.repository.OAuth2UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("oauth2UserService")
public class OAuth2UserService implements UserDetailsService{

    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;

    @Override
    public UserDetails loadUserByUsername(String nick) throws UsernameNotFoundException {
        return oAuth2UserRepository.findOneByNick(nick);
    }
}

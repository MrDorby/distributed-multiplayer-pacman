package it.unibo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.unibo.mongodb.PlayerInfoMongoDB;
import it.unibo.mongodb.PlayerInfoRepository;

/* Performs the requests for the database.*/
@Service
public class QueriesDetailsService {

    @Autowired
    private PlayerInfoRepository playerInfoRepository;

    public PlayerInfoMongoDB loadUserByUsername(String username) throws UsernameNotFoundException {
        return playerInfoRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

}

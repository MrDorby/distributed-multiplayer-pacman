package it.unibo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.MappingException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.unibo.mongodb.PlayerInfoMongoDB;
import it.unibo.mongodb.PlayerInfoRepository;

/* Performs the requests for the database.*/
@Service
public class QueriesDetailsService {

    @Autowired
    private PlayerInfoRepository playerInfoRepository;

    /**
     * Finds the infos about a specific player by means of its username.
     * @param username the identifier of the player.
     * @return the player's info.
     * @throws UsernameNotFoundException
     */
    public PlayerInfoMongoDB loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return playerInfoRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));   
        } catch (MappingException e) {
            return null;
        }
    }

}

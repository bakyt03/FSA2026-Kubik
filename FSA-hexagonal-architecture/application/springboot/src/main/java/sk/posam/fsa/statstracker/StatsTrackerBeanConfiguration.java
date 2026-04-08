package sk.posam.fsa.statstracker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.posam.fsa.statstracker.domain.PlayerRepository;
import sk.posam.fsa.statstracker.domain.PlayerStatsRepository;
import sk.posam.fsa.statstracker.domain.TeamBalancer;
import sk.posam.fsa.statstracker.domain.service.PlayerFacade;
import sk.posam.fsa.statstracker.domain.service.PlayerService;
import sk.posam.fsa.statstracker.domain.service.TeamSuggestionFacade;
import sk.posam.fsa.statstracker.domain.service.TeamSuggestionService;

/**
 * Skladá doménové beany pre Stats Tracker modul.
 * Inštancie repository portov (PlayerRepository, PlayerStatsRepository)
 * sú automaticky injektované Springom z outbound-repository-jpa modulu.
 */
@Configuration
public class StatsTrackerBeanConfiguration {

    @Bean
    public PlayerFacade playerFacade(PlayerRepository playerRepository) {
        return new PlayerService(playerRepository);
    }

    @Bean
    public TeamSuggestionFacade teamSuggestionFacade(PlayerRepository playerRepository,
            PlayerStatsRepository playerStatsRepository) {
        return new TeamSuggestionService(playerRepository, playerStatsRepository, new TeamBalancer());
    }
}

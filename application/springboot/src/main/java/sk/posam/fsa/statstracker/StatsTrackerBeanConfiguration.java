package sk.posam.fsa.statstracker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.posam.fsa.statstracker.domain.MatchRepository;
import sk.posam.fsa.statstracker.domain.PlayerRepository;
import sk.posam.fsa.statstracker.domain.PlayerStatsRepository;
import sk.posam.fsa.statstracker.domain.TeamBalancer;
import sk.posam.fsa.statstracker.domain.UserManagementPort;
import sk.posam.fsa.statstracker.domain.service.MatchFacade;
import sk.posam.fsa.statstracker.domain.service.MatchService;
import sk.posam.fsa.statstracker.domain.service.PlayerFacade;
import sk.posam.fsa.statstracker.domain.service.PlayerService;
import sk.posam.fsa.statstracker.domain.service.TeamSuggestionFacade;
import sk.posam.fsa.statstracker.domain.service.TeamSuggestionService;
import sk.posam.fsa.statstracker.domain.service.UserManagementFacade;
import sk.posam.fsa.statstracker.domain.service.UserManagementService;

/**
 * Skladá doménové beany pre Stats Tracker modul.
 * Inštancie repository portov (PlayerRepository, PlayerStatsRepository)
 * sú automaticky injektované Springom z outbound-repository-jpa modulu.
 */
@Configuration
public class StatsTrackerBeanConfiguration {

    @Bean
    public PlayerFacade playerFacade(PlayerRepository playerRepository,
            PlayerStatsRepository playerStatsRepository,
            MatchRepository matchRepository) {
        return new PlayerService(playerRepository, playerStatsRepository, matchRepository);
    }

    @Bean
    public TeamSuggestionFacade teamSuggestionFacade(PlayerRepository playerRepository,
            PlayerStatsRepository playerStatsRepository) {
        return new TeamSuggestionService(playerRepository, playerStatsRepository, new TeamBalancer());
    }

    @Bean
    public MatchFacade matchFacade(MatchRepository matchRepository,
            PlayerStatsRepository playerStatsRepository,
            PlayerRepository playerRepository) {
        return new MatchService(matchRepository, playerStatsRepository, playerRepository);
    }

    @Bean
    public UserManagementFacade userManagementFacade(UserManagementPort userManagementPort,
            PlayerRepository playerRepository) {
        return new UserManagementService(userManagementPort, playerRepository);
    }
}

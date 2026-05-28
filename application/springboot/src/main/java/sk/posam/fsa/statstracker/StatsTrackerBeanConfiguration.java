package sk.posam.fsa.statstracker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.posam.fsa.statstracker.domain.match.MatchRepository;
import sk.posam.fsa.statstracker.domain.player.PlayerRepository;
import sk.posam.fsa.statstracker.domain.player.PlayerStatsRepository;
import sk.posam.fsa.statstracker.domain.team.TeamBalancer;
import sk.posam.fsa.statstracker.domain.user.UserManagementPort;
import sk.posam.fsa.statstracker.domain.service.match.MatchFacade;
import sk.posam.fsa.statstracker.domain.service.match.MatchService;
import sk.posam.fsa.statstracker.domain.service.player.PlayerFacade;
import sk.posam.fsa.statstracker.domain.service.player.PlayerService;
import sk.posam.fsa.statstracker.domain.service.teamSuggestion.TeamSuggestionFacade;
import sk.posam.fsa.statstracker.domain.service.teamSuggestion.TeamSuggestionService;
import sk.posam.fsa.statstracker.domain.service.userManagement.UserManagementFacade;
import sk.posam.fsa.statstracker.domain.service.userManagement.UserManagementService;

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

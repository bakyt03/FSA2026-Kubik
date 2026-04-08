package sk.posam.fsa.statstracker.jpa;

import org.springframework.stereotype.Repository;
import sk.posam.fsa.statstracker.domain.PlayerMatchStats;
import sk.posam.fsa.statstracker.domain.PlayerStatsRepository;
import sk.posam.fsa.statstracker.domain.PlayerStatsSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class JpaPlayerStatsRepositoryAdapter implements PlayerStatsRepository {

    private final PlayerMatchStatsSpringDataRepository statsRepository;

    public JpaPlayerStatsRepositoryAdapter(PlayerMatchStatsSpringDataRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public List<PlayerStatsSnapshot> getForPlayers(List<Long> playerIds) {
        List<Object[]> rows = statsRepository.findAggregatedStatsForPlayers(playerIds);

        Map<Long, PlayerStatsSnapshot> resultMap = new HashMap<>();
        for (Object[] row : rows) {
            PlayerStatsSnapshot snap = new PlayerStatsSnapshot();
            snap.setPlayerId((Long) row[0]);
            snap.setAvgHltvRating((Double) row[1]);
            snap.setAvgAdr((Double) row[2]);
            snap.setAvgKdRatio((Double) row[3]);
            snap.setMatchesPlayed(((Long) row[4]).intValue());
            resultMap.put(snap.getPlayerId(), snap);
        }

        for (Long id : playerIds) {
            if (!resultMap.containsKey(id)) {
                PlayerStatsSnapshot empty = new PlayerStatsSnapshot();
                empty.setPlayerId(id);
                empty.setMatchesPlayed(0);
                resultMap.put(id, empty);
            }
        }

        return new ArrayList<>(resultMap.values());
    }

    @Override
    public List<PlayerStatsSnapshot> getForPlayers(List<Long> playerIds, int recentMatchesLimit) {
        List<PlayerMatchStats> allStats = statsRepository.findAllByPlayerIdInOrderByIdDesc(playerIds);

        Map<Long, List<PlayerMatchStats>> byPlayer = allStats.stream()
                .collect(Collectors.groupingBy(PlayerMatchStats::getPlayerId));

        List<PlayerStatsSnapshot> result = new ArrayList<>();
        for (Long playerId : playerIds) {
            List<PlayerMatchStats> recent = byPlayer.getOrDefault(playerId, List.of())
                    .stream().limit(recentMatchesLimit).collect(Collectors.toList());

            PlayerStatsSnapshot snap = new PlayerStatsSnapshot();
            snap.setPlayerId(playerId);
            snap.setMatchesPlayed(recent.size());

            if (!recent.isEmpty()) {
                snap.setAvgHltvRating(
                        recent.stream().mapToDouble(PlayerMatchStats::getHltvRating).average().orElse(0.0));
                snap.setAvgAdr(recent.stream().mapToDouble(PlayerMatchStats::getAdr).average().orElse(0.0));
                snap.setAvgKdRatio(recent.stream()
                        .mapToDouble(s -> s.getDeaths() == 0 ? s.getKills() : (double) s.getKills() / s.getDeaths())
                        .average().orElse(0.0));
            }

            result.add(snap);
        }

        return result;
    }


}

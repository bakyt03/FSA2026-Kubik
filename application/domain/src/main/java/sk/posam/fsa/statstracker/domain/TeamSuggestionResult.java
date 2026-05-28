package sk.posam.fsa.statstracker.domain;

import java.util.List;
import java.util.Map;

/**
 * Výsledok generovania tímov – nesie zoznam návrhov aj prípadné varovania.
 */
public class TeamSuggestionResult {

    private List<TeamSuggestion> suggestions;
    private Map<Long, Double> playerAdrMap;
    /**
     * Prezývky hráčov, pre ktorých neboli nájdené štatistiky (použité neutrálne
     * hodnoty).
     */
    private List<String> warnings;

    public List<TeamSuggestion> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<TeamSuggestion> suggestions) {
        this.suggestions = suggestions;
    }

    public Map<Long, Double> getPlayerAdrMap() {
        return playerAdrMap;
    }

    public void setPlayerAdrMap(Map<Long, Double> playerAdrMap) {
        this.playerAdrMap = playerAdrMap;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}

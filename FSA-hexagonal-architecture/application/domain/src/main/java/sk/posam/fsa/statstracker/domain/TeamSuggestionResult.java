package sk.posam.fsa.statstracker.domain;

import java.util.List;

/**
 * Výsledok generovania tímov – nesie zoznam návrhov aj prípadné varovania.
 */
public class TeamSuggestionResult {

    private List<TeamSuggestion> suggestions;
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

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}

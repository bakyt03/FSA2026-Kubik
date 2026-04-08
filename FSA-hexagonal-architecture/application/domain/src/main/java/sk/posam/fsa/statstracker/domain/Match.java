package sk.posam.fsa.statstracker.domain;

import java.util.Date;

public class Match {
    private Long id;
    private Map map;
    private Date playedAt;
    private Team winningTeam;
    private Team[] teams;
}

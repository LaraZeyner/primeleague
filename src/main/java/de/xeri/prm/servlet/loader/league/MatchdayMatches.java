package de.xeri.prm.servlet.loader.league;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.xeri.prm.models.league.League;
import de.xeri.prm.models.league.Matchday;
import de.xeri.prm.models.league.TurnamentMatch;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Lara on 31.05.2022 for web
 */
@Data
public class MatchdayMatches implements Comparable<MatchdayMatches>, Serializable {
  private static final transient long serialVersionUID = -6850961030542570589L;

  private final Matchday matchday;
  private final List<TurnamentMatch> matches;

  public MatchdayMatches(Matchday matchday, League league) {
    this.matchday = matchday;
    this.matches = league.getMatchdays().get(matchday)
        .stream().sorted(Comparator.comparing(TurnamentMatch::getStart)).collect(Collectors.toList());
  }

  @Override
  public int compareTo(@NotNull MatchdayMatches o) {
    return matchday.getStart().compareTo(o.getMatchday().getStart());
  }
}

package de.xeri.league.servlet;

import de.xeri.league.models.enums.Matchstate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
  private Matchstate state;
  private String date;
}
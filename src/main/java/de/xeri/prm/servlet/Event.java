package de.xeri.prm.servlet;

import de.xeri.prm.models.enums.Matchstate;
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
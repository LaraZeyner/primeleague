package de.xeri.prm.servlet.loader.scouting.performance;

import java.util.List;

import lombok.Data;

/**
 * Created by Lara on 01.07.2022 for web
 */
@Data
public class Tag {
  private final String name;
  private final List<String> description;
}

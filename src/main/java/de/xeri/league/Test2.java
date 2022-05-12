package de.xeri.league;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import de.xeri.league.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 09.05.2022 for web
 */
public class Test2 {
  public static void main(String[] args) {
    EntityManagerFactory entityManagerFactory = Persistence
        .createEntityManagerFactory("Persistence");
    final EntityManager entityManager = entityManagerFactory
        .createEntityManager();
    final List<Playerperformance> collect = new ArrayList<>(Playerperformance.get());
      /*final List<Team> collect = Data.getInstance().getCurrentGroup().getTeams().stream()
          .filter(Objects::nonNull)
          .sorted((Comparator.comparingLong(Team::getScore)).reversed())
          .collect(Collectors.toList());*/
    System.out.println("hi");
  }
}

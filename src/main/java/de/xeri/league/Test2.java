package de.xeri.league;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import de.xeri.league.manager.Data;
import de.xeri.league.models.league.Team;

/**
 * Created by Lara on 09.05.2022 for web
 */
public class Test2 {
  public static void main(String[] args) {
    EntityManagerFactory entityManagerFactory = Persistence
        .createEntityManagerFactory("Persistence");
    final EntityManager entityManager = entityManagerFactory
        .createEntityManager();
    Query query = entityManager.createQuery("FROM Team");

    final List<Team> collect = ((List<Team>) query.getResultList()).stream().filter(team -> team.getLeagues().contains(Data.getInstance().getCurrentGroup()))
        .sorted((Comparator.comparingLong(Team::getScore).reversed())).collect(Collectors.toList());
      /*final List<Team> collect = Data.getInstance().getCurrentGroup().getTeams().stream()
          .filter(Objects::nonNull)
          .sorted((Comparator.comparingLong(Team::getScore)).reversed())
          .collect(Collectors.toList());*/
    System.out.println("hi");
  }
}

package de.xeri.league.models.match.stat.neu;

import java.util.List;

/**
 * Created by Lara on 22.04.2022 for web
 */
public abstract class StatImplementation {

  abstract String avg();

  abstract String count();

  abstract String max();

  abstract String min();

  abstract String sum();

  abstract List<Double> list();

  abstract double perMinute();



}

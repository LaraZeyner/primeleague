package de.xeri.league;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lara on 06.04.2022 for web
 */
public class Test {

  public static void main(String[] args) {
    Map<Integer, List<Integer>> map = new HashMap<>();
    map.put(1, new ArrayList<>(Arrays.asList(1, 2)));
    final List<Integer> integers = map.get(1);
    integers.add(2);
    System.out.println("A");
  }

}

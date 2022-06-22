package de.xeri.prm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lara on 21.06.2022 for web
 */
public class Test3 {

  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    final List<List<Integer>> generate = generate(3, 7);
    System.out.println(System.currentTimeMillis() - start);
  }

  public static List<List<Integer>> generate(int n, int r) {
    List<List<Integer>> combinations = new ArrayList<>();
    double amount = Math.pow(r, n);
    for (int i = 0; i < amount; i++) {
      List<Integer> ints = IntStream.range(0, r).mapToObj(j -> 0).collect(Collectors.toList());
      int index = ints.size();
      int iterator = i;
      while (iterator > 0) {
        index--;
        ints.set(index, iterator % n);
        iterator /= n;
      }
      combinations.add(ints);
    }
    return combinations;
  }

}

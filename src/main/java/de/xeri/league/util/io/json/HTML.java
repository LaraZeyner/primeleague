package de.xeri.league.util.io.json;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public class HTML {
  private final String html;

  public HTML(URL url) throws IOException {
    final Scanner scanner = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A");
    html = (scanner.hasNext() ? scanner.next() : "");
    scanner.close();
  }

  public HTML(String html) {
    this.html = html;
  }

  public HTML read(String tagString) {
    final String[] tags = tagString.split("\\.");
    String htmlToRead = html;
    for (String tag : tags) htmlToRead = new HTML(htmlToRead).readTag(tag).toString();
    return new HTML(htmlToRead);
  }

  public HTML readTag(String tag) {
    return new HTML(html.substring(html.indexOf("<" + tag + ">") + tag.length() + 2, html.indexOf("</" + tag + ">")));
  }

  public List<HTML> find(String tag, String clazz, boolean closing) {
    if (closing) {
      final List<HTML> list = new ArrayList<>();
      for (String str : html.split("<" + tag)) {
        if (!str.isEmpty() && (clazz == null ||
            str.split(">")[0].contains("class=\"" + clazz + "\"") || str.split(">")[0].contains("id=\"" + clazz + "\""))) {
          final String s = str.substring(str.indexOf('>') + 1).split("</" + tag + ">")[0];
          list.add(new HTML(s));
        }
      }
      return list;
    }
    return Arrays.stream(html.split("<" + tag + ">"))
        .map(HTML::new).collect(Collectors.toList());
  }


  @Override
  public String toString() {
    return html;
  }

}

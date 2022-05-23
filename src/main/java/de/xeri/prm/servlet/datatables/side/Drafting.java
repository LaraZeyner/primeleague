package de.xeri.prm.servlet.datatables.side;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import lombok.Data;

/**
 * Created by Lara on 22.05.2022 for web
 */
@ManagedBean
@RequestScoped
@Data
public class Drafting implements Serializable {
  private List<PickRow> picks;
  private List<Timing> timings;

  @PostConstruct
  public void init() {
    this.picks = Arrays.asList(
        new PickRow("Cho'Gath", "Vayne", "Shen"),
        new PickRow("Jarvan IV", "Jhin", "Viego"),
        new PickRow("Ahri", "Tahm Kench", "Viktor"),
        new PickRow("Jinx", "Xerath", "Senna"),
        new PickRow("Sett", "Veigar", "Galio")
    );

    this.timings = Arrays.asList(
        new Timing("Matchup", "3 33%", "8 75%", "19 34%", "20 65%"),
        new Timing("LaneLead", "-1024", "-493", "-119", "1380"),
        new Timing("1. Ward", "2:44", "1:12", "3:54", "2:59"),
        new Timing("1. Objec.", "10:31", "6:34", "7:44", "7:01"),
        new Timing("1. Kill", "8:41", "7:43", "8:01", "7:56"),
        new Timing("1. Recall", "6:54", "3:59", "5:43", "4:21"),
        new Timing("1. Item", "13:02", "15:22", "12:55", "14:03"),
        new Timing("Lategame", "Split", "Engage", "Carry", "Carry")
    );
  }
}

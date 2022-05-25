package de.xeri.prm.servlet.datatables.scouting;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import de.xeri.prm.models.league.Team;
import de.xeri.prm.servlet.datatables.scouting.draft.Draft;
import lombok.Getter;
//TODO (Abgie) 18.05.2022: Wenn Spieler ausgewählt wird - Spiele suchen

/**
 * Created by Lara on 18.05.2022 for web
 */
@ManagedBean
@ApplicationScoped
@Getter
public class LoadPlayers implements Serializable {
  private static final long serialVersionUID = 4532805787883011744L;
  private TeamView ourTeam;
  private TeamView enemyTeam;
  private Draft draft;
  private List<Timing> timings;


  @PostConstruct
  public void init() {
    try {
      Team we = Team.find("Technical Really Unique Esports");
      this.ourTeam = new TeamView(we);

      Team enemy = Team.find("Mieser Billiger Spielmodus");
      this.enemyTeam = new TeamView(enemy);

      this.draft = new Draft(ourTeam, enemyTeam);

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

      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Geladen", "");
      FacesContext.getCurrentInstance().addMessage(null, message);
      System.out.println("GELADEN!!!!!");


      /*this.picks = Arrays.asList(
          new PickRow("Cho'Gath", "Vayne", "Shen"),
          new PickRow("Jarvan IV", "Jhin", "Viego"),
          new PickRow("Ahri", "Tahm Kench", "Viktor"),
          new PickRow("Jinx", "Xerath", "Senna"),
          new PickRow("Sett", "Veigar", "Galio")
      );*/

    } catch (Exception exception) {
      exception.printStackTrace();
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      exception.printStackTrace(pw);
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exception loading Table + ", sw.toString());
      FacesContext.getCurrentInstance().addMessage(null, message);
      System.out.println("NICHT GELADEN!!!!!");
    }
  }
}

package de.xeri.prm.servlet.datatables.scheduling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import de.xeri.prm.models.enums.ScheduleType;
import de.xeri.prm.models.league.Player;
import de.xeri.prm.models.league.Schedule;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.util.Const;
import lombok.Data;
import org.primefaces.model.DualListModel;

/**
 * Created by Lara on 02.06.2022 for web
 */
@ManagedBean
@RequestScoped
@Data
public class LoadSchedule implements Serializable {
  private List<String> schedules;
  private String schedule;

  private List<String> valueableTeams;
  private String valueableTeam;

  private List<String> types;
  private int scheduleId;
  private Schedule selected;

  private Date startTime;
  private Date endTime;
  private String title;

  private DualListModel<String> participants;


  private String type;

  @PostConstruct
  public void init() {
    this.types = Arrays.stream(ScheduleType.values()).map(ScheduleType::getDisplayname).collect(Collectors.toList());
    this.type = isSelected() ? selected.getType().toString() : "Typ eintragen";

    this.startTime = new Date();
    this.endTime = new Date();

    final Team team = Team.findTid(Const.TEAMID);
    final List<String> collect = new ArrayList<>(team.getPlayers()).stream().map(Player::getName).collect(Collectors.toList());
    this.participants = new DualListModel<>(collect, new ArrayList<>());

    this.schedules = new ArrayList<>(Schedule.get()).stream().sorted(Comparator.comparing(Schedule::getStartTime)).map(Schedule::toString).collect(Collectors.toList());

    this.valueableTeams = Team.getValueableTeams().stream().map(Team::getTeamName).collect(Collectors.toList());
  }

  public void load() {
    this.selected = Schedule.find(scheduleId);
    this.type = isSelected() ? selected.getType().toString() : "Typ eintragen";
  }

  public boolean isSelected() {
    return selected != null;
  }
}

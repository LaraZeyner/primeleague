package de.xeri.prm.servlet.loader.ourteam.scrim;

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

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.enums.MemberStatus;
import de.xeri.prm.models.enums.ScheduleType;
import de.xeri.prm.models.league.Schedule;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.models.league.TeamMember;
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

  private List<Team> valueableTeams;
  private List<String> valueableTeamsList;
  private String valueableTeam;

  private List<String> types;
  private int scheduleId;
  private Schedule selected;

  private Date startTime;
  private Date endTime;

  private DualListModel<String> participants;


  private String type;

  @PostConstruct
  public void init() {
    this.types = Arrays.stream(ScheduleType.values()).map(ScheduleType::getDisplayname).collect(Collectors.toList());
    this.type = isSelected() ? selected.getType().toString() : "Typ eintragen";

    this.startTime = new Date();
    this.endTime = new Date();

    final List<String> collect = TeamMember.get().stream()
        .filter(teamMember -> !teamMember.getMemberStatus().equals(MemberStatus.INACTIVE))
        .map(TeamMember::getMemberName).collect(Collectors.toList());
    this.participants = new DualListModel<>(collect, new ArrayList<>());

    this.schedules = new ArrayList<>(Schedule.get()).stream().sorted(Comparator.comparing(Schedule::getStartTime)).map(Schedule::toString).collect(Collectors.toList());

    this.valueableTeams = Team.getValueableTeams();
    this.valueableTeamsList = valueableTeams.stream().map(t -> t.getTurneyId() + " " + t.getTeamName() + " (" + t.getTeamAbbr() + ")").collect(Collectors.toList());
  }

  public void load() {
    this.selected = Schedule.find(scheduleId);
    this.type = isSelected() ? selected.getType().toString() : "Typ eintragen";
    this.startTime = selected.getStartTime();
    this.endTime = selected.getEndTime();

    final List<String> collect = TeamMember.get().stream()
        .filter(teamMember -> !teamMember.getMemberStatus().equals(MemberStatus.INACTIVE))
        .map(TeamMember::getMemberName).collect(Collectors.toList());
    final String participants = selected.getParticipants();
    if (participants.contains(",")) {
      final List<String> added = new ArrayList<>();
      for (String s : participants.split(",")) {
        final int i = Integer.parseInt(s);
        final TeamMember teamMember = TeamMember.find(i);
        final String memberName = teamMember.getMemberName();
        added.add(memberName);
        collect.remove(memberName);
      }
      this.participants = new DualListModel<>(collect, added);
    } else {
      this.participants = new DualListModel<>(collect, new ArrayList<>());
    }
  }

  public void add() {
    //TODO (Abgie) 07.06.2022:
  }

  public void remove() {
    //TODO (Abgie) 07.06.2022:
  }

  public void save() {
    final ScheduleType scheduleType = ScheduleType.fromName(type);
    final int tId = Integer.parseInt(valueableTeam.split(" ")[0]);
    final Team enemy = valueableTeams.stream().filter(t -> t.getTurneyId() == tId).findFirst().orElse(Team.findTid(tId));
    String title = (type.contains("Clash") ? "CLASH" : "SCRIMMAGE - " + scheduleType.getDisplayname() + " gegen " + enemy.getTeamAbbr());
    String small = type.contains("Clash") ? "Clash" : (scheduleType.getShortAttribute() + " vs " + enemy.getTeamAbbr());
    final Schedule schedule = Schedule.get(new Schedule(scheduleType, startTime, title, small));
    enemy.addSchedule(schedule);
    PrimeData.getInstance().save(schedule);
    PrimeData.getInstance().commit();
  }

  public boolean isSelected() {
    return selected != null;
  }
}

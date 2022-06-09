package de.xeri.prm.models.league;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.enums.MemberStatus;
import de.xeri.prm.util.HibernateUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "TeamMember")
@Table(name = "team_member", indexes = {
    @Index(name = "idx_teammember_name", columnList = "member_name", unique = true),
    @Index(name = "player", columnList = "player")
})
@NamedQuery(name = "TeamMember.findAll", query = "FROM TeamMember m")
@NamedQuery(name = "TeamMember.findById", query = "FROM TeamMember p WHERE id = :pk")
@NamedQuery(name = "TeamMember.findBy", query = "FROM TeamMember p WHERE memberName = :name")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class TeamMember implements Serializable {

  public static Set<TeamMember> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(TeamMember.class));
  }

  public static TeamMember get(TeamMember neu) {
    final TeamMember teamMember = find(neu.getMemberName());
    if (teamMember != null) {
      teamMember.setMemberStatus(neu.getMemberStatus());
      return teamMember;
    }
    PrimeData.getInstance().save(neu);
    return neu;
  }

  public static boolean has(int id) {
    return HibernateUtil.has(TeamMember.class, id);
  }

  public static boolean has(String title, Date start) {
    return HibernateUtil.has(TeamMember.class, new String[]{"title", "start"}, new Object[]{title, start});
  }

  public static TeamMember find(String name) {
    return HibernateUtil.find(TeamMember.class, new String[]{"name"}, new Object[]{name});
  }

  public static TeamMember find(int id) {
    return HibernateUtil.find(TeamMember.class, id);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id", nullable = false)
  private Byte id;

  @Column(name = "member_name", nullable = false, length = 25)
  private String memberName;

  @Enumerated(EnumType.STRING)
  @Column(name = "member_status", nullable = false, length = 8)
  private MemberStatus memberStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player")
  @ToString.Exclude
  private Player player;

  public TeamMember(String memberName, MemberStatus memberStatus) {
    this.memberName = memberName;
    this.memberStatus = memberStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final TeamMember that = (TeamMember) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
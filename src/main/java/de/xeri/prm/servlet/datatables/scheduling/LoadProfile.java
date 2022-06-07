package de.xeri.prm.servlet.datatables.scheduling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import lombok.Data;

/**
 * Created by Lara on 02.06.2022 for web
 */
@ManagedBean
@RequestScoped
@Data
public class LoadProfile implements Serializable {
  private static final long serialVersionUID = -5844301390587599409L;
  private List<Poolentry> mains;
  private List<Poolentry> niche;
  private List<Poolentry> others;
  private List<Poolinfo> infos;

  @PostConstruct
  public void init() {
    this.mains = new ArrayList<>();
    mains.add(new Poolentry("", "", "", "", ""));
    mains.add(new Poolentry("", "", "", "", ""));
    mains.add(new Poolentry("", "", "", "", ""));
    mains.add(new Poolentry("", "", "", "", ""));
    mains.add(new Poolentry("", "", "", "", ""));
    this.niche = new ArrayList<>();
    niche.add(new Poolentry("", "", "", "", ""));
    niche.add(new Poolentry("", "", "", "", ""));
    niche.add(new Poolentry("", "", "", "", ""));
    niche.add(new Poolentry("", "", "", "", ""));
    niche.add(new Poolentry("", "", "", "", ""));
    this.others = new ArrayList<>();
    others.add(new Poolentry("", "", "", "", ""));
    others.add(new Poolentry("", "", "", "", ""));
    others.add(new Poolentry("", "", "", "", ""));
    others.add(new Poolentry("", "", "", "", ""));
    others.add(new Poolentry("", "", "", "", ""));
    this.infos = new ArrayList<>();
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
    infos.add(new Poolinfo("", ""));
  }

  public void load() {

  }
}

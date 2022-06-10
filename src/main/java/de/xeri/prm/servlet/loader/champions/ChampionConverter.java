package de.xeri.prm.servlet.loader.champions;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import de.xeri.prm.models.dynamic.Champion;

/**
 * Created by Lara on 31.05.2022 for web
 */
@FacesConverter(forClass = de.xeri.prm.models.dynamic.Champion.class, value = "championConverter")
public class ChampionConverter implements Converter {
  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    ValueExpression vex =
        context.getApplication().getExpressionFactory()
            .createValueExpression(context.getELContext(),
                "#{loadChampions}", LoadChampions.class);

    LoadChampions champions = (LoadChampions) vex.getValue(context.getELContext());
    return null; //champions.getChampion(Integer.parseInt(value));
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object champion) {
    return String.valueOf(((Champion) champion).getId());
  }
}

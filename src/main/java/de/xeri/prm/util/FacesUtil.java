package de.xeri.prm.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Created by Lara on 02.06.2022 for web
 */
public final class FacesUtil {
  public static void sendError(String msg, String detail) {
    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, detail);
    FacesContext.getCurrentInstance().addMessage(null, message);
  }

  public static void sendWarning(String msg, String detail) {
    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, detail);
    FacesContext.getCurrentInstance().addMessage(null, message);
  }

  public static void sendMessage(String msg, String detail) {
    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, detail);
    FacesContext.getCurrentInstance().addMessage(null, message);
  }

  public static void sendFatal(String msg, String detail) {
    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, msg, detail);
    FacesContext.getCurrentInstance().addMessage(null, message);
  }

  public static void sendException(String msg, Exception exception) {
    exception.printStackTrace();
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    exception.printStackTrace(pw);
    if (exception instanceof RuntimeException) {
      sendFatal(msg, sw.toString());
    } else {
      sendError(msg, sw.toString());
    }
  }
}

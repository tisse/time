package jm.converter;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lukyanov on 10.11.16.
 */
@FacesConverter("CalendarConverter")
public class CalendarConverter implements Converter {

    public static final String PATTERN = "dd.MM.yyyy";

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        String pattern = getPattern(uiComponent);
        try {
            Date date = DateUtils.parseDate(s, pattern);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getPattern(UIComponent uiComponent) {
        String pattern = PATTERN;

        if (uiComponent instanceof org.primefaces.component.calendar.Calendar){
            System.out.println("uiComponent is org.primefaces.component.calendar.Calendar");
            pattern = ((org.primefaces.component.calendar.Calendar) uiComponent).getPattern();
        } else {
            System.out.println("uiComponent is not org.primefaces.component.calendar.Calendar - use default pattern");
        }
        System.out.println(pattern);

        return pattern;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        if (o != null) {
            if (o instanceof Calendar) {
                String pattern = getPattern(uiComponent);
                Calendar calendar = (Calendar) o;
                return DateFormatUtils.format(calendar, pattern);
            }
        }
        return null;
    }

}

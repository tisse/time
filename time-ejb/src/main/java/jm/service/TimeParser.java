package jm.service;

import jm.dao.EventDao;
import jm.model.Event;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by vk on 21.03.17.
 */

@Stateless
public class TimeParser {

    @Inject
    private Logger logger;

    @EJB
    private EventHashCalcer eventHashCalcer;

    @EJB
    private EventDao eventDao;

    public List<Event> parse(InputStream inputStream) {
        List<Event> events = new ArrayList<>();

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            int saved = 0;

            for (Row row : sheet) {
                if (row.getRowNum() > 0 && saved < 500) {
                    Event event = new Event();
                    event.setEvent(row.getCell(3).getStringCellValue());
                    event.setPerson(row.getCell(2).getStringCellValue());
                    Date date = row.getCell(0).getDateCellValue();

                    String format = DateFormatUtils.format(date, "dd.MM.yyyy");
//                    logger.info(format);

                    String cellValue = new DataFormatter().formatCellValue(row.getCell(1));
//                    logger.info(cellValue);

                    Date parseDate = null;
                    try {
                        parseDate = DateUtils.parseDate(format.concat(" ").concat(cellValue), "dd.MM.yyyy HH:mm:ss");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(parseDate);
                        event.setDate(calendar);
                    } catch (ParseException e) {
                        logger.warning(e.getLocalizedMessage());
                    }

//                    logger.info(event.toString());

                    String hash = eventHashCalcer.calc(event);

                    Boolean existsByHash = eventDao.existsByHash(hash);

                    logger.info(hash + (existsByHash ? " exists" : " does not exists"));

                    if (!existsByHash) {
                        event.setHash(hash);
                        eventDao.save(event);

                        logger.info(">" + event.toString());
                        eventDao.flush();
                        logger.info(">>" + event.toString());

                        events.add(event);

                        saved++;

                    } else {
                        logger.info("!" + event.toString());
                    }

                }
            }

        } catch (IOException e) {
            logger.warning(e.getLocalizedMessage());
        }

        return events;
    }

}

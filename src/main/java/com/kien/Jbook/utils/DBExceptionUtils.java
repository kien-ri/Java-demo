package com.kien.Jbook.utils;

import org.springframework.dao.DataIntegrityViolationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBExceptionUtils {

    public static boolean isForeignKeyViolation(DataIntegrityViolationException e) {
        Throwable rootCause = e.getRootCause();
        return rootCause instanceof SQLIntegrityConstraintViolationException
                && ((SQLIntegrityConstraintViolationException) rootCause).getErrorCode() == 1452;
    }

    public static String extractForeignKeyColumn(String errorMessage) {
        Pattern pattern = Pattern.compile("FOREIGN KEY \\(`(\\w+)`\\)");
        Matcher matcher = pattern.matcher(errorMessage);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
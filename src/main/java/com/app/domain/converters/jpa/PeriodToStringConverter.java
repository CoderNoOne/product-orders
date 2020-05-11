package com.app.domain.converters.jpa;

import com.app.domain.other.Period;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Converter
public class PeriodToStringConverter implements AttributeConverter<Period, String> {

    @Override
    public String convertToDatabaseColumn(Period period) {
        return period == null ? null : convertToString(period);
    }

    @Override
    public Period convertToEntityAttribute(String dbData) {
        return dbData == null ? null : parse(dbData);
    }

    private String convertToString(Period period){

        var years = period.getYears() != null ? "years: " + period.getYears() : "";
        var months = period.getMonths() != null ? "months: " + period.getMonths() : "";
        var days = period.getDays() != null ? "days: " + period.getDays() : "";

        return Stream.of(years, months, days)
                .filter(Strings::isNotBlank)
                .collect(Collectors.joining(", "));
    }

    private Period parse(String content){

        var builder = Period.builder();

        for (String element : content.split("[,]")) {
            if(element.contains("years")){
                builder.years(Integer.valueOf(element.split("[:]")[1].substring(1)));
            }
            if(element.contains("months")){
                builder.months(Integer.valueOf(element.split("[:]")[1].substring(1)));
            }
            if(element.contains("days")){
                builder.days(Integer.valueOf(element.split("[:]")[1].substring(1)));
            }

        }

        return builder.build();
    }
}

package org.example.steammatchmakingservice.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.Arrays;
import java.util.List;

public class StringArrayConverter {
    @ReadingConverter
    public static class StringArrayToListConverter implements Converter<String[], List<String>> {
        @Override
        public List<String> convert(String[] source) {
            return source == null ? List.of() : Arrays.asList(source);
        }
    }

    @WritingConverter
    public static class ListToStringArrayConverter implements Converter<List<String>, String[]> {
        @Override
        public String[] convert(List<String> source) {
            return source.toArray(new String[0]);
        }
    }
}

package org.example.steammatchmakingservice.converter;

import org.example.steammatchmakingservice.entity.SteamBaseEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public class SessionStatusConverter {
    @WritingConverter
    public static class SessionStatusToIntegerConverter implements Converter<SteamBaseEntity.SessionStatus, Integer> {
        @Override
        public Integer convert(SteamBaseEntity.SessionStatus source) {
            return source.ordinal();
        }
    }

    @ReadingConverter
    public static class IntegerToStatusStatusConverter implements Converter<Integer, SteamBaseEntity.SessionStatus> {
        @Override
        public SteamBaseEntity.SessionStatus convert(Integer source) {
            return SteamBaseEntity.SessionStatus.values()[source];
        }
    }

}

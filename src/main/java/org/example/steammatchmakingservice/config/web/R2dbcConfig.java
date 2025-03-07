package org.example.steammatchmakingservice.config.web;

import org.example.steammatchmakingservice.converter.SessionStatusConverter;
import org.example.steammatchmakingservice.converter.StringArrayConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.mapping.R2dbcMappingContext;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class R2dbcConfig {
    @Bean
    public R2dbcMappingContext r2dbcMappingContext() {
        return new R2dbcMappingContext();
    }

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new SessionStatusConverter.SessionStatusToIntegerConverter());
        converters.add(new SessionStatusConverter.IntegerToStatusStatusConverter());
        converters.add(new StringArrayConverter.ListToStringArrayConverter());
        converters.add(new StringArrayConverter.StringArrayToListConverter());
        return new R2dbcCustomConversions(CustomConversions.StoreConversions.NONE, converters);
    }
}

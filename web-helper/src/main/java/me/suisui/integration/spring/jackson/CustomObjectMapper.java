package me.suisui.integration.spring.jackson;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class CustomObjectMapper extends ObjectMapper {
    public CustomObjectMapper() {
    	registerModule(new Hibernate4Module());
    	registerModule(new JodaModule());

        SimpleModule timestampModule = new SimpleModule("TimestampModule", Version.unknownVersion());
        timestampModule.addDeserializer(Timestamp.class, new TimestampDeserializer());
        registerModule(timestampModule);
        
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        configure(SerializationFeature.INDENT_OUTPUT, true);
        
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);  
    }
}

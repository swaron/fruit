package me.suisui.integration.spring.jackson;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

public class TimestampDeserializer extends StdScalarDeserializer<Timestamp> {
    public TimestampDeserializer() {
        super(Timestamp.class);
    }

    /**
     * fix null pointer exception when value is null.
     */
    @Override
    public java.sql.Timestamp deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
            JsonProcessingException {
        Date date = _parseDate(jp, ctxt);
        if(date == null){
            return null;
        }else{
            return new Timestamp(date.getTime());
        }
    }
}

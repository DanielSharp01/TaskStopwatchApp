package com.danielsharp01.taskstopwatch.api;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

import java.io.IOException;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null)
            out.nullValue();
        else
            out.value(value.toInstant(ZoneId.systemDefault().getRules().getOffset(value)).toEpochMilli());
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in != null) {
            if (in.peek() != JsonToken.NULL) {
                return Instant.ofEpochMilli(in.nextLong()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            else {
                in.nextNull();
                return null;
            }
        }
        else return null;
    }
}
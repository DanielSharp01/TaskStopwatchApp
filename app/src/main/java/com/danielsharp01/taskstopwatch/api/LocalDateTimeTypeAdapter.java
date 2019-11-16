package com.danielsharp01.taskstopwatch.api;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
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
            out.value(value.toInstant((ZoneOffset) ZoneId.systemDefault()).toEpochMilli());
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in != null)
            return Instant.ofEpochMilli(in.nextLong()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        else
            return null;
    }
}
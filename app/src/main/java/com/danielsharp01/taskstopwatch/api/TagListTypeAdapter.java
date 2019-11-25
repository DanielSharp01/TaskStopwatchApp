package com.danielsharp01.taskstopwatch.api;

import com.danielsharp01.taskstopwatch.DI;
import com.danielsharp01.taskstopwatch.model.Tag;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TagListTypeAdapter extends TypeAdapter<List<Tag>> {
    @Override
    public void write(JsonWriter out, List<Tag> value) throws IOException {
        if (value == null)
            out.nullValue();
        else {
            out.beginArray();
            for (Tag tag: value) {
                out.beginObject();
                out.name("name");
                out.value(tag.getName());
                out.name("color");
                out.value(tag.getColor());
                out.endObject();
            }
            out.endArray();
        }
    }

    @Override
    public List<Tag> read(JsonReader in) throws IOException {
        if (in != null) {
            in.beginArray();
            List<Tag> tag = new ArrayList<>();
            while (in.peek() != JsonToken.END_ARRAY) {
                tag.add(DI.getStorage().getTagByName(in.nextString()));
            }
            in.endArray();
            return tag;
        }
        else return null;
    }
}

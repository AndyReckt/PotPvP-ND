package net.frozenorb.potpvp.kittype;

import org.bukkit.craftbukkit.libs.com.google.gson.TypeAdapter;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonReader;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;

import java.io.IOException;

public final class KitTypeJsonAdapter extends TypeAdapter<KitType> {

    public void write(final JsonWriter writer, final KitType type) throws IOException {
        writer.value(type.getId());
    }

    public KitType read(final JsonReader reader) throws IOException {
        return KitType.byId(reader.nextString());
    }
}

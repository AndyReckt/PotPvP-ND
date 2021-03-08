package net.frozenorb.potpvp.kit;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kit.listener.KitEditorListener;
import net.frozenorb.potpvp.kit.listener.KitItemListener;
import net.frozenorb.potpvp.kit.listener.KitLoadListener;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.util.MongoUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class KitHandler {

    public static final String MONGO_COLLECTION_NAME="playerKits";
    public static final int KITS_PER_TYPE=4;

    private final Map<UUID, List<Kit>> kitData=new ConcurrentHashMap<>();

    public KitHandler() {
        Bukkit.getPluginManager().registerEvents(new KitEditorListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new KitItemListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new KitLoadListener(), PotPvPSI.getInstance());
    }

    public List<Kit> getKits(Player player, KitType kitType) {
        List<Kit> kits=new ArrayList<>();

        for ( Kit kit : kitData.getOrDefault(player.getUniqueId(), ImmutableList.of()) ) {
            if (kit.getType() == kitType) {
                kits.add(kit);
            }
        }

        return kits;
    }

    public Optional<Kit> getKit(Player player, KitType kitType, int slot) {
        return kitData.getOrDefault(player.getUniqueId(), ImmutableList.of())
                .stream()
                .filter(k -> k.getType() == kitType && k.getSlot() == slot)
                .findFirst();
    }

    public Kit saveDefaultKit(Player player, KitType kitType, int slot) {
        Kit created=Kit.ofDefaultKit(kitType, "Kit " + slot, slot);

        kitData.computeIfAbsent(player.getUniqueId(), i -> new ArrayList<>()).add(created);
        saveKitsAsync(player);

        return created;
    }

    public void removeKit(Player player, KitType kitType, int slot) {
        boolean removed=kitData.computeIfAbsent(player.getUniqueId(), i -> new ArrayList<>())
                .removeIf(k -> k.getType() == kitType && k.getSlot() == slot);

        if (removed) {
            saveKitsAsync(player);
        }
    }

    public void saveKitsAsync(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            MongoCollection<Document> collection=MongoUtils.getCollection(MONGO_COLLECTION_NAME);
            List kitJson=(List) JSON.parse(PotPvPSI.getGson().toJson(kitData.getOrDefault(player.getUniqueId(), ImmutableList.of())));

            Document query=new Document("_id", player.getUniqueId().toString());
            Document kitUpdate=new Document("$set", new Document("kits", kitJson));

            collection.updateOne(query, kitUpdate, MongoUtils.UPSERT_OPTIONS);
        });
    }

    public void wipeKitsForPlayer(UUID target) {
        kitData.remove(target);

        MongoCollection<Document> collection=MongoUtils.getCollection(MONGO_COLLECTION_NAME);
        collection.deleteOne(new Document("_id", target.toString()));
    }

    public int wipeKitsWithType(KitType kitType) {
        // remove kits for online players
        for ( List<Kit> playerKits : kitData.values() ) {
            playerKits.removeIf(k -> k.getType() == kitType);
        }

        MongoCollection<Document> collection=MongoUtils.getCollection(MONGO_COLLECTION_NAME);
        Document typeQuery=new Document("type", kitType.getId());

        collection.updateMany(
                new Document("kits", new Document("$elemMatch", typeQuery)),
                new Document("$pull", new Document("kits", typeQuery))
        );

        return -1;
    }

    public void loadKits(UUID playerUuid) {
        MongoCollection<Document> collection=MongoUtils.getCollection(MONGO_COLLECTION_NAME);
        Document playerKits=collection.find(new Document("_id", playerUuid.toString())).first();

        if (playerKits != null) {
            List kits=playerKits.get("kits", List.class);
            Type listKit=new TypeToken<List<Kit>>() {
            }.getType();

            kitData.put(playerUuid, PotPvPSI.getGson().fromJson(JSON.serialize(kits), listKit));
        }
    }

    public void loadJSONKits() {
        final File file = new File(PotPvPSI.getInstance().getDataFolder(), "kitTypes.json");
        if (file.exists()) {
            try (final Reader schematicsFileReader = Files.newReader(file, Charsets.UTF_8)) {
                final Type schematicListType = new com.google.gson.reflect.TypeToken<List<KitType>>() {}.getType();
                final List<KitType> kitTypes = PotPvPSI.plainGson.fromJson(schematicsFileReader, schematicListType);
                for (final KitType kitType : kitTypes) {
                    KitType.getAllTypes().removeIf(otherKitType -> otherKitType.getId().equals(kitType.getId()));
                    KitType.getAllTypes().add(kitType);
                    kitType.saveAsync();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to import kits from kitTypes.json!");
            }
        }
    }

    public void saveJSONKits() {
        String json=PotPvPSI.plainGson.toJson(KitType.getAllTypes());
        try {
            Files.write(json, new File(PotPvPSI.getInstance().getDataFolder(), "kitTypes.json"), Charsets.UTF_8);
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Saved Kits Successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to save Kits!");
        }
    }

    public void unloadKits(Player player) {
        kitData.remove(player.getUniqueId());
    }

}
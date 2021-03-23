package net.frozenorb.potpvp.kit;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kit.listener.KitEditorListener;
import net.frozenorb.potpvp.kit.listener.KitItemListener;
import net.frozenorb.potpvp.kit.listener.KitLoadListener;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.util.ItemBuilder;
import net.frozenorb.potpvp.util.MongoUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class KitHandler {

    public static final String MONGO_COLLECTION_NAME="playerKits";
    public static final int KITS_PER_TYPE=4;

    private final Map<UUID, List<Kit>> kitData=new ConcurrentHashMap<>();

    public KitHandler() {
        Bukkit.getPluginManager().registerEvents(new KitEditorListener(), PotPvPND.getInstance());
        Bukkit.getPluginManager().registerEvents(new KitItemListener(), PotPvPND.getInstance());
        Bukkit.getPluginManager().registerEvents(new KitLoadListener(), PotPvPND.getInstance());
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
        PotPvPND.getInstance().getKitsConfig().getConfiguration().set("kits." + kitType.getName(), null);
        try {
            PotPvPND.getInstance().getKitsConfig().getConfiguration().save(PotPvPND.getInstance().getKitsConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveKitsAsync(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPND.getInstance(), () -> {
            MongoCollection<Document> collection=MongoUtils.getCollection(MONGO_COLLECTION_NAME);
            List kitJson=(List) JSON.parse(PotPvPND.getGson().toJson(kitData.getOrDefault(player.getUniqueId(), ImmutableList.of())));

            Document query=new Document("_id", player.getUniqueId().toString());
            Document kitUpdate=new Document("$set", new Document("kits", kitJson));

            collection.updateOne(query, kitUpdate, MongoUtils.UPSERT_OPTIONS);
        });
        this.saveConfigKits();
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

            kitData.put(playerUuid, PotPvPND.getGson().fromJson(JSON.serialize(kits), listKit));
        }
    }

    public void loadConfigKits() {
        final YamlConfiguration configFile = PotPvPND.getInstance().getKitsConfig().getConfiguration();
        for (String key : configFile.getConfigurationSection("kits").getKeys(false)) {
            String path="kits." + key;
            KitType kitType = new KitType(key.toUpperCase());
            kitType.setDisplayName(configFile.getString(path + ".display-name"));
            kitType.setHidden(!Boolean.parseBoolean(configFile.getString(path + ".enabled")));
            kitType.setIcon(new ItemBuilder(Material.getMaterial(configFile.getString(path + "icon.material"))).data((short) configFile.getInt(path + "icon.durability")).build().getData());
            kitType.setSupportsRanked(Boolean.parseBoolean(configFile.getString(path + ".ranked-allowed")));
            kitType.setBuildingAllowed(Boolean.parseBoolean(configFile.getString(path + ".build-allowed")));
            kitType.setEditorSpawnAllowed(Boolean.parseBoolean(configFile.getString(path + "editor-spawn")));
            kitType.setPearlDamage(Boolean.parseBoolean(configFile.getString(path + "pearl-damage")));
            kitType.setHealthShown(Boolean.parseBoolean(configFile.getString(path + ".health-above-name")));
            kitType.setHardcoreHealing(Boolean.parseBoolean(configFile.getString(path + ".hardcore-healing")));
        }
    }

    public void saveConfigKits() {
        final YamlConfiguration configFile = PotPvPND.getInstance().getKitsConfig().getConfiguration();
        for ( KitType kitType : KitType.getAllTypes() ) {
            String path = "kits." + kitType.getName();
            configFile.set(path + ".enabled", !kitType.isHidden());
            configFile.set(path + ".display-name", kitType.getDisplayName());
            configFile.set(path + "icon.material", kitType.getIcon().toItemStack().getType());
            configFile.set(path + "icon.durability", kitType.getIcon().toItemStack().getDurability());
            configFile.set(path + ".ranked-allowed", kitType.isSupportsRanked());
            configFile.set(path + ".pearl-damage", kitType.isPearlDamage());
            configFile.set(path + ".hardcore-healing", kitType.isHardcoreHealing());
            configFile.set(path + ".health-above-name", kitType.isHealthShown());
            configFile.set(path + ".editor-spawn", kitType.isEditorSpawnAllowed());
            configFile.set(path + ".build-allowed", kitType.isBuildingAllowed());
        }
        try {
            configFile.save(PotPvPND.getInstance().getKitsConfig().getFile());
        } catch (IOException e) {
            //
        }

    }

    public void unloadKits(Player player) {
        kitData.remove(player.getUniqueId());
    }

}
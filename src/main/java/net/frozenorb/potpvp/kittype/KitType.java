package net.frozenorb.potpvp.kittype;

import com.mongodb.client.MongoCollection;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.util.MongoUtils;
import net.frozenorb.potpvp.util.CC;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.SerializedName;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Denotes a type of Kit, under which players can queue, edit kits,
 * have elo, etc.
 */
// This class purposely uses qLib Gson (as we want to actualy serialize
// the fields within a KitType instead of pretending it's an enum) instead of ours.
public final class KitType {

    private static final String MONGO_COLLECTION_NAME = "kitTypes";
    @Getter private static final List<KitType> allTypes = new ArrayList<>();
    public static KitType teamFight = new KitType("teamfight");

    static {
        MongoCollection<Document> collection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);

        collection.find().iterator().forEachRemaining(doc -> {
            allTypes.add(PotPvPND.plainGson.fromJson(doc.toJson(), KitType.class));
        });

        teamFight.icon = new MaterialData(Material.BEACON);
        teamFight.id = "teamfight";
        teamFight.displayName = "HCF Team Fight";
        teamFight.displayColor = ChatColor.RED;

        allTypes.add(teamFight);

        allTypes.sort(Comparator.comparing(KitType::getSort));
    }

    public KitType(String id) {
        this.id = id;
    }

    /**
     * Id of this KitType, will be used when serializing the KitType for
     * database storage. Ex: "WIZARD", "NO_ENCHANTS", "SOUP"
     */
    @Getter @SerializedName("_id") private String id;

    /**
     * Display name of this KitType, will be used when communicating a KitType
     * to playerrs. Ex: "Wizard", "No Enchants", "Soup"
     */
    @Setter private String name;

    /**
     * Display color for this KitType, will be used in messages
     * or scoreboards sent to players.
     */
    @Getter @Setter private ChatColor displayColor;

    /**
     * Material info which will be used when rendering this
     * kit in selection menus and such.
     */
    @Setter private MaterialData icon;

    /**
     * Items which will be available for players to grab in the kit
     * editor, when making kits for this kit type.
     */
    @Getter @Setter private ItemStack[] editorItems = new ItemStack[0];

    /**
     * The armor that will be applied to players for this kit type.
     * Currently players are not allowed to edit their armor, they are
     * always given this armor.
     */
    @Setter private ItemStack[] defaultArmor = new ItemStack[0];

    /**
     * The default inventory that will be applied to players for this kit type.
     * Players are always allowed to rearange this inventory, so this only serves
     * as a default (in contrast to defaultArmor)
     */
    @Setter private ItemStack[] defaultInventory = new ItemStack[0];

    /**
     * Determines if players are allowed to spawn in items while editing their kits.
     * For some kit types (ex archer and axe) players can only rearange items in kits,
     * whereas some kit types (ex HCTeams and soup) allow spawning in items as well.
     */
    @Getter @Setter private boolean editorSpawnAllowed = true;

    /**
     * Determines if normal, non-admin players should be able to see this KitType.
     */
    @Getter @Setter private boolean hidden = false;

    @Getter @Setter public String displayName;

    /**
     * Determines how players regain health in matches using this KitType.
     * This is used primarily for applying logic for souping + rendering
     * heals remaining in the post match inventory
     */
    @Getter @Setter private HealingMethod healingMethod = HealingMethod.POTIONS;

    /**
     * Determines if players are allowed to build in matches using this KitType.
     */
    @Getter @Setter private boolean buildingAllowed = false;

    /**
     * Determines if health is shown below the player's name-tags in matches using this KitType.
     */
    @Getter @Setter private boolean healthShown = false;

    /**
     * Determines if natural health regeneration should happen in matches using this KitType.
     */
    @Getter @Setter private boolean hardcoreHealing = false;

    /**
     * Determines if players playing a match using this KitType should take damage when their ender pearl lands.
     */
    @Getter @Setter private boolean pearlDamage = true;

    /**
     * Determines the order used when displaying lists of KitTypes to players.
     * (Lowest to highest)
     */
    @Getter @Setter private int sort = 0;

    @Getter @Setter private boolean supportsRanked = false;

    public static KitType byId(String id) {
        for (KitType kitType : allTypes) {
            if (kitType.getId().equalsIgnoreCase(id)) {
                return kitType;
            }
        }

        return null;
    }


    public String getColoredDisplayName() {
        return CC.GREEN + this.name;
    }

    public void saveAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPND.getInstance(), () -> {
            MongoCollection<Document> collection=MongoUtils.getCollection(MONGO_COLLECTION_NAME);
            Document kitTypeDoc=Document.parse(PotPvPND.plainGson.toJson(this));
            kitTypeDoc.remove("_id");
            Document query=new Document("_id", this.id);
            Document kitUpdate=new Document("$set", kitTypeDoc);
            collection.updateOne(query, kitUpdate, MongoUtils.UPSERT_OPTIONS);
        });
    }

    public void deleteAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPND.getInstance(), () -> {
            MongoCollection<Document> collection=MongoUtils.getCollection(MONGO_COLLECTION_NAME);
            collection.deleteOne(new Document("_id", this.id));
        });
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.displayName;
    }

    public MaterialData getIcon() {
        return this.icon;
    }

    public ItemStack[] getDefaultArmor() {
        return this.defaultArmor;
    }

    public ItemStack[] getDefaultInventory() {
        return this.defaultInventory;
    }
}


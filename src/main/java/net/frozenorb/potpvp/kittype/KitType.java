package net.frozenorb.potpvp.kittype;

import com.mongodb.client.MongoCollection;
import net.frozenorb.potpvp.PotPvPSI;
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

public final class KitType {
    private static final String MONGO_COLLECTION_NAME="kitTypes";
    private static final List<KitType> allTypes=new ArrayList<>();
    public static KitType teamFight=new KitType("team_fight");
    @SerializedName(value="_id")
    private String id;
    private String displayName;
    private ChatColor displayColor;
    private MaterialData icon;
    private ItemStack[] editorItems=new ItemStack[0];
    private ItemStack[] defaultArmor=new ItemStack[0];
    private ItemStack[] defaultInventory=new ItemStack[0];
    private boolean editorSpawnAllowed=true;
    private boolean hidden=false;
    private HealingMethod healingMethod=HealingMethod.POTIONS;
    private boolean buildingAllowed=false;
    private boolean healthShown=false;
    private boolean hardcoreHealing=false;
    private boolean pearlDamage=true;
    private int sort=0;
    private boolean supportsRanked=false;

    public KitType(String id) {
        this.id=id;
    }

    public static KitType byId(String id) {
        for ( KitType kitType : allTypes ) {
            if (!kitType.getId().equalsIgnoreCase(id)) continue;
            return kitType;
        }
        return null;
    }

    public String getColoredDisplayName() {
        return CC.GREEN + this.displayName;
    }

    public void saveAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            MongoCollection<Document> collection=MongoUtils.getCollection(MONGO_COLLECTION_NAME);
            Document kitTypeDoc=Document.parse(PotPvPSI.plainGson.toJson(this));
            kitTypeDoc.remove("_id");
            Document query=new Document("_id", this.id);
            Document kitUpdate=new Document("$set", kitTypeDoc);
            collection.updateOne(query, kitUpdate, MongoUtils.UPSERT_OPTIONS);
        });
    }

    public void deleteAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            MongoCollection<Document> collection=MongoUtils.getCollection(MONGO_COLLECTION_NAME);
            collection.deleteOne(new Document("_id", this.id));
        });
    }

    public String toString() {
        return this.displayName;
    }

    public String getDisplayName() {
        return CC.GOLD + CC.BOLD + this.displayName;
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

    public static List<KitType> getAllTypes() {
        return allTypes;
    }

    public String getId() {
        return this.id;
    }

    public void setDisplayName(String displayName) {
        this.displayName=displayName;
    }

    public ChatColor getDisplayColor() {
        return this.displayColor;
    }

    public void setDisplayColor(ChatColor displayColor) {
        this.displayColor=displayColor;
    }

    public void setIcon(MaterialData icon) {
        this.icon=icon;
    }

    public ItemStack[] getEditorItems() {
        return this.editorItems;
    }

    public void setEditorItems(ItemStack[] editorItems) {
        this.editorItems=editorItems;
    }

    public void setDefaultArmor(ItemStack[] defaultArmor) {
        this.defaultArmor=defaultArmor;
    }

    public void setDefaultInventory(ItemStack[] defaultInventory) {
        this.defaultInventory=defaultInventory;
    }

    public boolean isEditorSpawnAllowed() {
        return this.editorSpawnAllowed;
    }

    public void setEditorSpawnAllowed(boolean editorSpawnAllowed) {
        this.editorSpawnAllowed=editorSpawnAllowed;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden=hidden;
    }

    public HealingMethod getHealingMethod() {
        return this.healingMethod;
    }

    public void setHealingMethod(HealingMethod healingMethod) {
        this.healingMethod=healingMethod;
    }

    public boolean isBuildingAllowed() {
        return this.buildingAllowed;
    }

    public void setBuildingAllowed(boolean buildingAllowed) {
        this.buildingAllowed=buildingAllowed;
    }

    public boolean isHealthShown() {
        return this.healthShown;
    }

    public void setHealthShown(boolean healthShown) {
        this.healthShown=healthShown;
    }

    public boolean isHardcoreHealing() {
        return this.hardcoreHealing;
    }

    public void setHardcoreHealing(boolean hardcoreHealing) {
        this.hardcoreHealing=hardcoreHealing;
    }

    public boolean isPearlDamage() {
        return this.pearlDamage;
    }

    public void setPearlDamage(boolean pearlDamage) {
        this.pearlDamage=pearlDamage;
    }

    public int getSort() {
        return this.sort;
    }

    public void setSort(int sort) {
        this.sort=sort;
    }

    public boolean isSupportsRanked() {
        return this.supportsRanked;
    }

    public void setSupportsRanked(boolean supportsRanked) {
        this.supportsRanked=supportsRanked;
    }

    static {
        final MongoCollection<Document> collection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
        collection.find().iterator().forEachRemaining(doc -> KitType.allTypes.add(PotPvPSI.plainGson.fromJson(doc.toJson(), KitType.class)));
        KitType.teamFight.icon=new MaterialData(Material.BEACON);
        KitType.teamFight.id="alex is a god xd";
        KitType.teamFight.displayName="HCF Team Fight";
        KitType.teamFight.displayColor=ChatColor.AQUA;
        allTypes.sort(Comparator.comparing(KitType::getSort));
    }
}


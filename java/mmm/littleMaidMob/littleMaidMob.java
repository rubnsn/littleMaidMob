package mmm.littleMaidMob;

import java.io.File;

import mmm.lib.EntitySelect;
import mmm.lib.ProxyCommon;
import mmm.lib.multiModel.MultiModelHandler;
import mmm.lib.multiModel.texture.MultiModelData;
import mmm.littleMaidMob.entity.EntityLittleMaidBase;
import mmm.littleMaidMob.gui.GuiHandler;
import mmm.littleMaidMob.item.ItemLMMSpawnEgg;
import mmm.littleMaidMob.mode.IFF;
import mmm.littleMaidMob.mode.ModeManager;
import mmm.littleMaidMob.net.NetworkHandler;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = littleMaidMob.MOD_ID, name = "LittleMaidMob")
public class littleMaidMob {

    @SidedProxy(clientSide = "mmm.littleMaidMob.ProxyClient",
            serverSide = "mmm.lib.ProxyCommon")
    public static ProxyCommon proxy;
    @Instance(littleMaidMob.MOD_ID)
    public static littleMaidMob instance;

    public static final String MOD_ID = "littleMaidMob";
    public static boolean isDebugMessage = true;

    public static int spawnWeight = 5;
    public static int spawnMin = 1;
    public static int spawnMax = 3;
    public static String[] spawnBiomes;
    public static boolean canDespawn = false;
    public static boolean addSpawnEggRecipe = false;
    public static boolean isNetherLand = false;

    private static int gueid = 0;
    public static boolean cfg_Aggressive;
    public static final String MMULTI_MODEL_DOMAIN = "multimodel:";

    public static void Debug(String pText, Object... pData) {
        // デバッグメッセージ
        if (isDebugMessage || true) {
            System.out.println(String.format("littleMaidMob-" + pText, pData));
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent pEvent) {
        NetworkHandler.init();
        NetworkRegistry.INSTANCE.newSimpleChannel("littleMaid");
        // コンフィグの解析・設定
        String ls = "littleMaidMob";
        File configFile = pEvent.getSuggestedConfigurationFile();
        Configuration lconf = new Configuration(configFile);
        lconf.load();
        isDebugMessage = lconf.get(ls, "isDebugMessage", false).getBoolean(false);

        spawnWeight = lconf.get(ls, "spawnWeight", 5).getInt();
        spawnMin = lconf.get(ls, "spawnMin", 1).getInt();
        spawnMax = lconf.get(ls, "spawnMax", 3).getInt();
        spawnBiomes = lconf.get(ls, "spawnBiomes", new String[] { "FOREST", "PLAINS", "MOUNTAIN", "HILLS", "SWAMP", "WATER", "DESERT", "FROZEN", "JUNGLE", "WASTELAND", "BEACH",
                //				"NETHER",
                //				"END",
        "MUSHROOM", "MAGICAL" }).getStringList();
        canDespawn = lconf.get(ls, "canDespawn", false).getBoolean(false);
        addSpawnEggRecipe = lconf.get(ls, "addSpawnEggRecipe", false).getBoolean(false);
        lconf.save();

        //2重登録のため除去、スポーンエッグは…うーん、独自で追加してもえんちゃうか
        //gueid = EntityRegistry.findGlobalUniqueEntityId();
        //EntityRegistry.registerGlobalEntityID(EntityLittleMaidBase.class, ls, gueid, 0xefffef, 0x9f5f5f);
        Item egg = new ItemLMMSpawnEgg().setUnlocalizedName("monsterPlacer").setTextureName("spawn_egg");
        GameRegistry.registerItem(egg, "lmmegg", MOD_ID);
        EntityRegistry.registerModEntity(EntityLittleMaidBase.class, ls, gueid, this, 80, 1, true);
        ModeManager.instance.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent pEvent) {
        // レンダラの登録
        proxy.init();

        // GUIハンドラ
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        // スポーンエッグのレシピを追加
        if (addSpawnEggRecipe) {
            GameRegistry.addRecipe(new ItemStack(Items.spawn_egg, 1, gueid), new Object[] { "scs", "sbs", " e ", Character.valueOf('s'), Items.sugar, Character.valueOf('c'), new ItemStack(Items.dye, 1, 3), Character.valueOf('b'), Items.slime_ball, Character.valueOf('e'), Items.egg, });
        }

        MultiModelHandler.init();
        MultiModelHandler.instance.registerEntityClass(EntityLittleMaidBase.class, MultiModelData.class, "default");
        MultiModelHandler.instance.registerEntityClass(EntitySelect.class, MultiModelData.class, "default");
    }

    @Mod.EventHandler
    public void loaded(FMLPostInitializationEvent pEvent) {
        // IFFのロード
        IFF.loadIFFs();
        //デバッグの邪魔
        //addSpawns();
    }

    private void addSpawns() {
        // スポーン領域の登録
        if (spawnWeight > 0) {
            BiomeGenBase[] lbiome;
            for (String ls : spawnBiomes) {
                BiomeDictionary.Type ltype = BiomeDictionary.Type.valueOf(ls);
                if (ltype != null) {
                    lbiome = BiomeDictionary.getBiomesForType(ltype);
                    EntityRegistry.addSpawn(EntityLittleMaidBase.class, spawnWeight, spawnMin, spawnMax, EnumCreatureType.creature, lbiome);
                    Debug("addSpawn:%s", lbiome.toString());
                }
            }
            if (isNetherLand) {
                lbiome = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.NETHER);
                EntityRegistry.addSpawn(EntityLittleMaidBase.class, spawnWeight, spawnMin, spawnMax, EnumCreatureType.creature, lbiome);
                Debug("addSpawn:%s", lbiome.toString());
            }
        }
    }
}

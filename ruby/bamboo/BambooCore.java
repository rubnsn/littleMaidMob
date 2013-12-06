package ruby.bamboo;

import java.util.logging.Level;

import net.minecraft.block.BlockDispenser;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.CoreModManager;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import ruby.bamboo.dispenser.DispenserBehaviorBambooSpear;
import ruby.bamboo.dispenser.DispenserBehaviorDirtySnowball;
import ruby.bamboo.dispenser.DispenserBehaviorFireCracker;
import ruby.bamboo.gui.GuiHandler;
import ruby.bamboo.proxy.CommonProxy;

@Mod(modid = "BambooMod", name = "BambooMod",
        version = "Minecraft1.6.4 ver2.6.3.4")
@NetworkMod(channels = { "B_Entity", "bamboo", "bamboo2" },
        packetHandler = NetworkHandler.class,
        connectionHandler = NetworkHandler.class)
public class BambooCore {
    public static final String MODID;
    public static final String resourceDomain;
    public static final boolean DEBUGMODE;
    private static Config conf;

    @SidedProxy(serverSide = "ruby.bamboo.proxy.CommonProxy",
            clientSide = "ruby.bamboo.proxy.ClientProxy")
    public static CommonProxy proxy;

    @Instance("BambooMod")
    public static BambooCore instance;
    
    static {
        MODID = "BambooMod";
        resourceDomain = "bamboo:";
        DEBUGMODE = isDevelopment();
        conf = new Config();
    }
    
    public static Config getConf() {
        return conf;
    }

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent e) {
        proxy.preInit();
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent e) {
        proxy.init();
        proxy.registerTESTileEntity();
        registDispencer();

        // debug
        if (DEBUGMODE) {
            System.out.println("DEBUG MODE Enable");
        }
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartedEvent event) {
        getConf().serverInit();
        ManekiHandler.instance.clearManekiList();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        NetworkRegistry.instance().registerGuiHandler(BambooCore.getInstance(), new GuiHandler());
    }

    private void registDispencer() {
        BlockDispenser.dispenseBehaviorRegistry.putObject(BambooInit.snowBallIID, new DispenserBehaviorDirtySnowball());
        BlockDispenser.dispenseBehaviorRegistry.putObject(BambooInit.firecrackerIID, new DispenserBehaviorFireCracker());
        BlockDispenser.dispenseBehaviorRegistry.putObject(BambooInit.bambooSpearIID, new DispenserBehaviorBambooSpear());
    }

    public static BambooCore getInstance() {
        return instance;
    }

    private static boolean isDevelopment() {
        boolean result;
        try {
            result = ReflectionHelper.getPrivateValue(CoreModManager.class, null, "deobfuscatedEnvironment");
        } catch (UnableToAccessFieldException e) {
            FMLLog.log(Level.WARNING, "Debug mode forced false!");
            result = false;
        }
        return result;
    }
}
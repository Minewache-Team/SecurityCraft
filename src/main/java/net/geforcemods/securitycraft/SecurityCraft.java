package net.geforcemods.securitycraft;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.commands.SCCommand;
import net.geforcemods.securitycraft.compat.cyclic.CyclicCompat;
import net.geforcemods.securitycraft.compat.icbmclassic.ICBMClassicEMPCompat;
import net.geforcemods.securitycraft.compat.versionchecker.VersionUpdateChecker;
import net.geforcemods.securitycraft.itemgroups.SCDecorationTab;
import net.geforcemods.securitycraft.itemgroups.SCTechnicalTab;
import net.geforcemods.securitycraft.misc.CommonDoorActivator;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.IProxy;
import net.geforcemods.securitycraft.screen.ScreenHandler;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = SecurityCraft.MODID, name = "SecurityCraft", dependencies = "required-after:forge@[14.23.5.2826,)", updateJSON = "https://www.github.com/Geforce132/SecurityCraft/raw/master/Updates/Forge.json", acceptedMinecraftVersions = "[1.12.2]")
public class SecurityCraft {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "securitycraft";
	public static final Random RANDOM = new Random();
	public static final CreativeTabs TECHNICAL_TAB = new SCTechnicalTab();
	public static final CreativeTabs DECORATION_TAB = new SCDecorationTab();
	@SidedProxy(clientSide = "net.geforcemods.securitycraft.network.ClientProxy", serverSide = "net.geforcemods.securitycraft.network.ServerProxy")
	public static IProxy proxy;
	@Instance(MODID)
	public static SecurityCraft instance = new SecurityCraft();
	public static SimpleNetworkWrapper network;
	private ScreenHandler guiHandler = new ScreenHandler();

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new SCCommand());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		SecurityCraft.network = NetworkRegistry.INSTANCE.newSimpleChannel(SecurityCraft.MODID);
		RegistrationHandler.registerPackets(SecurityCraft.network);
		SetupHandler.init();
		proxy.registerEntityRenderingHandlers();

		if (Loader.isModLoaded("icbmclassic"))
			MinecraftForge.EVENT_BUS.register(new ICBMClassicEMPCompat());
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, KeypadBlock.Convertible.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, KeypadChestBlock.Convertible.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, KeypadFurnaceBlock.Convertible.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_DOOR_ACTIVATOR_MSG, CommonDoorActivator.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_DOOR_ACTIVATOR_MSG, InventoryScannerBlock.DoorActivator.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_DOOR_ACTIVATOR_MSG, SecureRedstoneInterfaceBlock.DoorActivator.class.getName());
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "net.geforcemods.securitycraft.compat.hudmods.TOPDataProvider");


		if (ConfigHandler.checkForUpdates) {
			NBTTagCompound vcUpdateTag = VersionUpdateChecker.getNBTTagCompound();

			if (vcUpdateTag != null)
				FMLInterModComms.sendRuntimeMessage(MODID, "VersionChecker", "addUpdate", vcUpdateTag);
		}

		NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
		ModuleType.refresh();
		proxy.registerRenderThings();
		FMLCommonHandler.instance().getDataFixer().init(SecurityCraft.MODID, TileEntityIDDataFixer.VERSION).registerFix(FixTypes.BLOCK_ENTITY, new TileEntityIDDataFixer());

	}

	@EventHandler
	public void onIMC(IMCEvent event) {
		SecurityCraftAPI.onIMC(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (Loader.isModLoaded("cyclicmagic"))
			MinecraftForge.EVENT_BUS.register(new CyclicCompat());




		ForgeChunkManager.setForcedChunkLoadingCallback(instance, (tickets, world) -> { //this will only check against SecurityCraft's camera chunks, so no need to add an (instanceof SecurityCameraEntity) somewhere
			tickets.forEach(ticket -> {
				if (ticket.getType() == Type.ENTITY && ((WorldServer) ticket.world).getEntityFromUuid(ticket.getEntity().getPersistentID()) == null)
					ForgeChunkManager.releaseTicket(ticket);
			});
		});
		ConfigHandler.loadEffects();
	}

	@EventHandler
	public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
		PasscodeUtils.startHashingThread(event.getServer());
	}

	@EventHandler
	public static void onServerStop(FMLServerStoppedEvent event) {
		PasscodeUtils.stopHashingThread();
	}

	public static String getVersion() {
		return Loader.instance().activeModContainer().getVersion();
	}
}

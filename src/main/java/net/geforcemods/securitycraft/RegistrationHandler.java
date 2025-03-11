package net.geforcemods.securitycraft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.NamedBlockEntity;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blockentities.CageTrapBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blockentities.FloorTrapBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.IronFenceBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeyPanelBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeycardLockBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.blockentities.MotionActivatedLightBlockEntity;
import net.geforcemods.securitycraft.blockentities.PortableRadarBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProtectoBlockEntity;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.ScannerDoorBlockEntity;

import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.blockentities.ValidationOwnableBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.itemblocks.ItemBlockCrystalQuartzSlab;
import net.geforcemods.securitycraft.itemblocks.ItemBlockCustomQuartz;
import net.geforcemods.securitycraft.items.SCManualItem;
import net.geforcemods.securitycraft.misc.DyeItemRecipe;
import net.geforcemods.securitycraft.misc.LimitedUseKeycardRecipe;
import net.geforcemods.securitycraft.misc.PageGroup;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.BlockPocketManagerFailedActivation;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.PlayAlarmSound;
import net.geforcemods.securitycraft.network.client.RefreshDiguisedModel;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.geforcemods.securitycraft.network.client.SpawnInterfaceHighlightParticle;
import net.geforcemods.securitycraft.network.client.UpdateLaserColors;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.network.server.AssembleBlockPocket;
import net.geforcemods.securitycraft.network.server.CheckBriefcasePasscode;
import net.geforcemods.securitycraft.network.server.CheckPasscode;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.network.server.RemovePositionFromSSS;
import net.geforcemods.securitycraft.network.server.SetBriefcasePasscodeAndOwner;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetDefaultCameraViewingDirection;
import net.geforcemods.securitycraft.network.server.SetGhostSlot;
import net.geforcemods.securitycraft.network.server.SetKeycardUses;
import net.geforcemods.securitycraft.network.server.SetListModuleData;
import net.geforcemods.securitycraft.network.server.SetPasscode;
import net.geforcemods.securitycraft.network.server.SetStateOnDisguiseModule;
import net.geforcemods.securitycraft.network.server.SyncAlarmSettings;
import net.geforcemods.securitycraft.network.server.SyncBlockPocketManager;
import net.geforcemods.securitycraft.network.server.SyncKeycardSettings;
import net.geforcemods.securitycraft.network.server.SyncLaserSideConfig;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncRiftStabilizer;
import net.geforcemods.securitycraft.network.server.SyncSSSSettingsOnServer;
import net.geforcemods.securitycraft.network.server.SyncSecureRedstoneInterface;
import net.geforcemods.securitycraft.network.server.SyncTENBTTag;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.network.server.ToggleModule;
import net.geforcemods.securitycraft.network.server.ToggleNightVision;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.network.server.UpdateSliderValue;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.DataSerializerEntry;

@EventBusSubscriber
public class RegistrationHandler {
	private static List<Item> itemBlocks = new ArrayList<>();
	private static List<Block> blockPages = new ArrayList<>();
	private static Map<PageGroup, List<Block>> pageTypeBlocks = new EnumMap<>(PageGroup.class);
	private static Map<PageGroup, List<ItemStack>> pageTypeStacks = new EnumMap<>(PageGroup.class);
	private static Map<Block, String> blocksDesignedBy = new HashMap<>();
	private static Map<Block, Supplier<Boolean>> blockConfigValues = new HashMap<>();
	private static final Supplier<Boolean> ABLE_TO_CRAFT_MINES = () -> ConfigHandler.ableToCraftMines;

	private RegistrationHandler() {}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		registerBlock(event, SCContent.laserBlock);
		event.getRegistry().register(SCContent.laserField);
		registerBlock(event, SCContent.keypad);
		registerBlock(event, SCContent.retinalScanner);
		registerBlock(event, SCContent.keycardReader);
		registerBlock(event, SCContent.inventoryScanner);
		event.getRegistry().register(SCContent.inventoryScannerField);
		registerBlock(event, SCContent.cageTrap);
		registerBlock(event, SCContent.portableRadar);
		registerBlock(event, SCContent.keypadChest);
		registerBlock(event, SCContent.usernameLogger);
		registerBlock(event, SCContent.alarm);
		event.getRegistry().register(SCContent.alarmLit);
		registerBlock(event, SCContent.electrifiedIronFenceGate);
		registerBlock(event, SCContent.panicButton);
		registerBlock(event, SCContent.frame);
		registerBlock(event, SCContent.keypadFurnace);
		registerBlock(event, SCContent.securityCamera);
		registerBlock(event, SCContent.electrifiedIronFence);
		registerBlock(event, SCContent.protecto);
		event.getRegistry().register(SCContent.scannerDoor);
		event.getRegistry().register(SCContent.secretSignWall);
		event.getRegistry().register(SCContent.secretSignStanding);
		registerBlock(event, SCContent.motionActivatedLight);
		registerBlock(event, SCContent.crystalQuartz, new ItemBlockCustomQuartz(SCContent.crystalQuartz), PageGroup.SINGLE_ITEM);
		registerBlock(event, SCContent.crystalQuartzSlab, new ItemBlockCrystalQuartzSlab(SCContent.crystalQuartzSlab), PageGroup.NO_PAGE);
		event.getRegistry().register(SCContent.doubleCrystalQuartzSlab);
		registerBlock(event, SCContent.stairsCrystalQuartz, PageGroup.NO_PAGE);
		registerBlock(event, SCContent.blockPocketWall);
		registerBlock(event, SCContent.blockPocketManager, "Henzoid");
		registerBlock(event, SCContent.projector);
		event.getRegistry().register(SCContent.keypadDoor);
		event.getRegistry().register(SCContent.keyPanelFloorCeilingBlock);
		event.getRegistry().register(SCContent.keyPanelWallBlock);
		registerBlock(event, SCContent.sonicSecuritySystem, (ItemBlock) SCContent.sonicSecuritySystemItem, PageGroup.SINGLE_ITEM);
		registerBlock(event, SCContent.riftStabilizer);
		registerBlock(event, SCContent.displayCase);
		registerBlock(event, SCContent.floorTrap);
		event.getRegistry().register(SCContent.keycardLockFloorCeilingBlock);
		event.getRegistry().register(SCContent.keycardLockWallBlock);
		registerBlock(event, SCContent.secureRedstoneInterface);

	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		//register item blocks
		for (Item item : itemBlocks) {
			event.getRegistry().register(item);
		}

		//init block sc manual pages
		for (Block block : blockPages) {
			//@formatter:off
			SCManualItem.PAGES.add(new SCManualPage(
					Item.getItemFromBlock(block),
					PageGroup.SINGLE_ITEM,
					Utils.localize(block),
					Utils.localize("help." + block.getTranslationKey().substring(5) + ".info"),
					blocksDesignedBy.getOrDefault(block, ""),
					false,
					blockConfigValues.getOrDefault(block, () -> true)));
			//@formatter:on
		}

		registerItem(event, SCContent.codebreaker);
		registerItem(event, SCContent.scannerDoorItem);
		registerItem(event, SCContent.universalBlockRemover);
		registerItem(event, SCContent.keycardLvl1, PageGroup.KEYCARDS, () -> ConfigHandler.ableToCraftKeycard1);
		registerItem(event, SCContent.keycardLvl2, PageGroup.KEYCARDS, () -> ConfigHandler.ableToCraftKeycard2);
		registerItem(event, SCContent.keycardLvl3, PageGroup.KEYCARDS, () -> ConfigHandler.ableToCraftKeycard3);
		registerItem(event, SCContent.keycardLvl4, PageGroup.KEYCARDS, () -> ConfigHandler.ableToCraftKeycard4);
		registerItem(event, SCContent.keycardLvl5, PageGroup.KEYCARDS, () -> ConfigHandler.ableToCraftKeycard5);
		registerItem(event, SCContent.limitedUseKeycard, PageGroup.SINGLE_ITEM, () -> ConfigHandler.ableToCraftLUKeycard);
		registerItem(event, SCContent.universalBlockModifier);
		registerItem(event, SCContent.redstoneModule);
		registerItem(event, SCContent.allowlistModule);
		registerItem(event, SCContent.denylistModule);
		registerItem(event, SCContent.harmingModule);
		registerItem(event, SCContent.smartModule);
		registerItem(event, SCContent.storageModule);
		registerItem(event, SCContent.disguiseModule);
		registerItem(event, SCContent.speedModule);
		registerItem(event, SCContent.wireCutters);
		registerItem(event, SCContent.adminTool);
		registerItem(event, SCContent.keyPanel);
		registerItem(event, SCContent.cameraMonitor);
		registerItem(event, SCContent.taser);
		registerItem(event, SCContent.scManual);
		registerItem(event, SCContent.universalOwnerChanger);
		registerItem(event, SCContent.briefcase);
		registerItem(event, SCContent.universalKeyChanger);
		event.getRegistry().register(SCContent.taserPowered); //won't show up in the manual
		registerItem(event, SCContent.secretSignItem);
		registerItem(event, SCContent.crystalQuartzItem);
		registerItem(event, SCContent.keypadDoorItem);
		registerItem(event, SCContent.portableTunePlayer);
		registerItem(event, SCContent.keycardHolder);
		registerItem(event, SCContent.lens);
		registerItem(event, SCContent.keycardLock);

		SecurityCraft.proxy.registerVariants();
		pageTypeBlocks.forEach((pageType, list) -> {
			if (!pageTypeStacks.containsKey(pageType))
				pageTypeStacks.put(pageType, new ArrayList<>());

			list.stream().map(Item::getItemFromBlock).forEach(item -> {
				if (item != null && item.getHasSubtypes()) {
					NonNullList<ItemStack> subStacks = NonNullList.create();

					item.getSubItems(item.getCreativeTab(), subStacks);
					pageTypeStacks.get(pageType).addAll(subStacks);
				}
				else
					pageTypeStacks.get(pageType).add(new ItemStack(item));
			});
		});
		pageTypeStacks.remove(PageGroup.SINGLE_ITEM);
		pageTypeStacks.forEach((pageType, list) -> {
			pageType.setItems(Ingredient.fromStacks(list.toArray(new ItemStack[list.size()])));
			SCManualItem.PAGES.add(new SCManualPage(list.get(0).getItem(), pageType, Utils.localize(pageType.getTitle()), Utils.localize(pageType.getSpecialInfoKey()), "", !pageType.hasRecipeGrid()));
		});
		//clear unused memory
		itemBlocks = null;
		blockPages = null;
		pageTypeBlocks = null;
		pageTypeStacks = null;
		blocksDesignedBy = null;
		blockConfigValues = null;
	}

	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<Block> event) {
		GameRegistry.registerTileEntity(OwnableBlockEntity.class, new ResourceLocation("securitycraft:ownable"));
		GameRegistry.registerTileEntity(NamedBlockEntity.class, new ResourceLocation("securitycraft:abstract"));
		GameRegistry.registerTileEntity(KeypadBlockEntity.class, new ResourceLocation("securitycraft:keypad"));
		GameRegistry.registerTileEntity(LaserBlockBlockEntity.class, new ResourceLocation("securitycraft:laser_block"));
		GameRegistry.registerTileEntity(CageTrapBlockEntity.class, new ResourceLocation("securitycraft:cage_trap"));
		GameRegistry.registerTileEntity(KeycardReaderBlockEntity.class, new ResourceLocation("securitycraft:keycard_reader"));
		GameRegistry.registerTileEntity(InventoryScannerBlockEntity.class, new ResourceLocation("securitycraft:inventory_scanner"));
		GameRegistry.registerTileEntity(PortableRadarBlockEntity.class, new ResourceLocation("securitycraft:portable_radar"));
		GameRegistry.registerTileEntity(SecurityCameraBlockEntity.class, new ResourceLocation("securitycraft:security_camera"));
		GameRegistry.registerTileEntity(UsernameLoggerBlockEntity.class, new ResourceLocation("securitycraft:username_logger"));
		GameRegistry.registerTileEntity(RetinalScannerBlockEntity.class, new ResourceLocation("securitycraft:retinal_scanner"));
		GameRegistry.registerTileEntity(KeypadChestBlockEntity.class, new ResourceLocation("securitycraft:keypad_chest"));
		GameRegistry.registerTileEntity(AlarmBlockEntity.class, new ResourceLocation("securitycraft:alarm"));
		GameRegistry.registerTileEntity(KeypadFurnaceBlockEntity.class, new ResourceLocation("securitycraft:keypad_furnace"));
		GameRegistry.registerTileEntity(ProtectoBlockEntity.class, new ResourceLocation("securitycraft:protecto"));
		GameRegistry.registerTileEntity(CustomizableBlockEntity.class, new ResourceLocation("securitycraft:customizable"));
		GameRegistry.registerTileEntity(ScannerDoorBlockEntity.class, new ResourceLocation("securitycraft:scanner_door"));
		GameRegistry.registerTileEntity(SecretSignBlockEntity.class, new ResourceLocation("securitycraft:secret_sign"));
		GameRegistry.registerTileEntity(MotionActivatedLightBlockEntity.class, new ResourceLocation("securitycraft:motion_light"));
		GameRegistry.registerTileEntity(BlockPocketManagerBlockEntity.class, new ResourceLocation("securitycraft:block_pocket_manager"));
		GameRegistry.registerTileEntity(BlockPocketBlockEntity.class, new ResourceLocation("securitycraft:block_pocket"));
		GameRegistry.registerTileEntity(ProjectorBlockEntity.class, new ResourceLocation("securitycraft:projector"));
		GameRegistry.registerTileEntity(IronFenceBlockEntity.class, new ResourceLocation("securitycraft:iron_fence"));
		GameRegistry.registerTileEntity(KeypadDoorBlockEntity.class, new ResourceLocation("securitycraft:keypad_door"));
		GameRegistry.registerTileEntity(ValidationOwnableBlockEntity.class, new ResourceLocation("securitycraft:validation_ownable"));
		GameRegistry.registerTileEntity(KeyPanelBlockEntity.class, new ResourceLocation("securitycraft:key_panel"));
		GameRegistry.registerTileEntity(SonicSecuritySystemBlockEntity.class, new ResourceLocation("securitycraft:sonic_security_system"));
		GameRegistry.registerTileEntity(RiftStabilizerBlockEntity.class, new ResourceLocation("securitycraft:rift_stabilizer"));
		GameRegistry.registerTileEntity(DisguisableBlockEntity.class, new ResourceLocation("securitycraft:disguisable"));
		GameRegistry.registerTileEntity(DisplayCaseBlockEntity.class, new ResourceLocation("securitycraft:display_case"));
		GameRegistry.registerTileEntity(FloorTrapBlockEntity.class, new ResourceLocation("securitycraft:floor_trap"));
		GameRegistry.registerTileEntity(KeycardLockBlockEntity.class, new ResourceLocation("securitycraft:keycard_lock"));
		GameRegistry.registerTileEntity(SecureRedstoneInterfaceBlockEntity.class, new ResourceLocation("securitycraft:secure_redstone_interface"));
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		//@formatter:off
		event.getRegistry().register(EntityEntryBuilder.create()
				.id(new ResourceLocation(SecurityCraft.MODID, "securitycamera"), 4)
				.entity(SecurityCamera.class)
				.name("SecurityCamera")
				.tracker(256, 20, true).build());
	}

	public static void registerPackets(SimpleNetworkWrapper network) {
		network.registerMessage(SetCameraPowered.Handler.class, SetCameraPowered.class, 1, Side.SERVER);
		network.registerMessage(SyncKeycardSettings.Handler.class, SyncKeycardSettings.class, 3, Side.SERVER);
		network.registerMessage(UpdateLogger.Handler.class, UpdateLogger.class, 4, Side.CLIENT);
		network.registerMessage(UpdateNBTTagOnClient.Handler.class, UpdateNBTTagOnClient.class, 5, Side.CLIENT);
		network.registerMessage(ToggleNightVision.Handler.class, ToggleNightVision.class, 9, Side.SERVER);
		network.registerMessage(SetPasscode.Handler.class, SetPasscode.class, 12, Side.SERVER);
		network.registerMessage(CheckPasscode.Handler.class, CheckPasscode.class, 13, Side.SERVER);
		network.registerMessage(SyncTENBTTag.Handler.class, SyncTENBTTag.class, 14, Side.SERVER);
		network.registerMessage(MountCamera.Handler.class, MountCamera.class, 15, Side.SERVER);
		network.registerMessage(CheckBriefcasePasscode.Handler.class, CheckBriefcasePasscode.class, 18, Side.SERVER);
		network.registerMessage(ToggleOption.Handler.class, ToggleOption.class, 19, Side.SERVER);
		network.registerMessage(UpdateSliderValue.Handler.class, UpdateSliderValue.class, 22, Side.SERVER);
		network.registerMessage(RemoveCameraTag.Handler.class, RemoveCameraTag.class, 23, Side.SERVER);
		network.registerMessage(ToggleBlockPocketManager.Handler.class, ToggleBlockPocketManager.class, 25, Side.SERVER);
		network.registerMessage(ClearLoggerServer.Handler.class, ClearLoggerServer.class, 27, Side.SERVER);
		network.registerMessage(RefreshDiguisedModel.Handler.class, RefreshDiguisedModel.class, 28, Side.CLIENT);
		network.registerMessage(AssembleBlockPocket.Handler.class, AssembleBlockPocket.class, 30, Side.SERVER);
		network.registerMessage(SyncProjector.Handler.class, SyncProjector.class, 31, Side.SERVER);
		network.registerMessage(SyncBlockPocketManager.Handler.class, SyncBlockPocketManager.class, 32, Side.SERVER);
		network.registerMessage(SetKeycardUses.Handler.class, SetKeycardUses.class, 35, Side.SERVER);
		network.registerMessage(SetCameraView.Handler.class, SetCameraView.class, 36, Side.CLIENT);
		network.registerMessage(DismountCamera.Handler.class, DismountCamera.class, 37, Side.SERVER);
		network.registerMessage(SyncSSSSettingsOnServer.Handler.class, SyncSSSSettingsOnServer.class, 38, Side.SERVER);
		network.registerMessage(ToggleModule.Handler.class, ToggleModule.class, 41, Side.SERVER);
		network.registerMessage(SetGhostSlot.Handler.class, SetGhostSlot.class, 42, Side.SERVER);
		network.registerMessage(RemovePositionFromSSS.Handler.class, RemovePositionFromSSS.class, 44, Side.SERVER);
		network.registerMessage(SetBriefcasePasscodeAndOwner.Handler.class, SetBriefcasePasscodeAndOwner.class, 46, Side.SERVER);
		network.registerMessage(SetListModuleData.Handler.class, SetListModuleData.class, 47, Side.SERVER);
		network.registerMessage(SetStateOnDisguiseModule.Handler.class, SetStateOnDisguiseModule.class, 48, Side.SERVER);
		network.registerMessage(SyncRiftStabilizer.Handler.class, SyncRiftStabilizer.class, 49, Side.SERVER);
		network.registerMessage(UpdateLaserColors.Handler.class, UpdateLaserColors.class, 50, Side.CLIENT);
		network.registerMessage(SyncLaserSideConfig.Handler.class, SyncLaserSideConfig.class, 51, Side.SERVER);
		network.registerMessage(PlayAlarmSound.Handler.class, PlayAlarmSound.class, 52, Side.CLIENT);
		network.registerMessage(SyncAlarmSettings.Handler.class, SyncAlarmSettings.class, 53, Side.SERVER);
		network.registerMessage(OpenScreen.Handler.class, OpenScreen.class, 55, Side.CLIENT);
		network.registerMessage(BlockPocketManagerFailedActivation.Handler.class, BlockPocketManagerFailedActivation.class, 56, Side.SERVER);
		network.registerMessage(SetDefaultCameraViewingDirection.Handler.class, SetDefaultCameraViewingDirection.class, 57, Side.SERVER);
		network.registerMessage(SyncSecureRedstoneInterface.Handler.class, SyncSecureRedstoneInterface.class, 58, Side.SERVER);
		network.registerMessage(SpawnInterfaceHighlightParticle.Handler.class, SpawnInterfaceHighlightParticle.class, 59, Side.CLIENT);
	}

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		for (int i = 0; i < SCSounds.values().length; i++) {
			event.getRegistry().register(SCSounds.values()[i].event);
		}
	}

	@SubscribeEvent
	public static void registerDataSerializerEntries(RegistryEvent.Register<DataSerializerEntry> event) {
		event.getRegistry().register(new DataSerializerEntry(new DataSerializer<Owner>() {
			@Override
			public void write(PacketBuffer buf, Owner value) {
				ByteBufUtils.writeUTF8String(buf, value.getName());
				ByteBufUtils.writeUTF8String(buf, value.getUUID());
			}

			@Override
			public Owner read(PacketBuffer buf) throws IOException {
				String name = ByteBufUtils.readUTF8String(buf);
				String uuid = ByteBufUtils.readUTF8String(buf);

				return new Owner(name, uuid);
			}

			@Override
			public DataParameter<Owner> createKey(int id) {
				return new DataParameter<>(id, this);
			}

			@Override
			public Owner copyValue(Owner value) {
				return new Owner(value.getName(), value.getUUID());
			}
		}).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "owner")));
	}

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		event.getRegistry().register(new DyeItemRecipe().setRegistryName(new ResourceLocation(SecurityCraft.MODID, "dye_briefcase")));
		event.getRegistry().register(new LimitedUseKeycardRecipe().setRegistryName(new ResourceLocation(SecurityCraft.MODID, "limited_use_keycard_conversion")));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerResourceLocations(ModelRegistryEvent event) {
		//blocks
		registerInventoryModel(SCContent.keypad, 0, "keypad");
		registerInventoryModel(SCContent.frame, 0, "keypad_frame");
		registerInventoryModel(SCContent.laserBlock, 0, "laser_block");
		registerInventoryModel(SCContent.laserField, 0, "laser");
		registerInventoryModel(SCContent.keypadChest, 0, "keypad_chest");
		registerInventoryModel(SCContent.keycardReader, 0, "keycard_reader");
		registerInventoryModel(SCContent.inventoryScanner, 0, "inventory_scanner");
		registerInventoryModel(SCContent.cageTrap, 0, "cage_trap");
		registerInventoryModel(SCContent.inventoryScannerField, 0, "inventory_scanner_field");
		registerInventoryModel(SCContent.retinalScanner, 0, "retinal_scanner");
		registerInventoryModel(SCContent.portableRadar, 0, "portable_radar");
		registerInventoryModel(SCContent.alarm, 0, "alarm");
		registerInventoryModel(SCContent.alarmLit, 0, "alarm_lit");
		registerInventoryModel(SCContent.usernameLogger, 0, "username_logger");
		registerInventoryModel(SCContent.electrifiedIronFenceGate, 0, "reinforced_fence_gate");
		registerInventoryModel(SCContent.electrifiedIronFence, 0, "electrified_iron_fence");
		registerInventoryModel(SCContent.keypadChest, 0, "keypad_chest");
		registerInventoryModel(SCContent.keypadFurnace, 0, "keypad_furnace");
		registerInventoryModel(SCContent.panicButton, 0, "panic_button");
		registerInventoryModel(SCContent.securityCamera, 0, "security_camera");
		registerInventoryModel(SCContent.protecto, 0, "protecto");
		registerInventoryModel(SCContent.scannerDoor, 0, "scanner_door");
		registerInventoryModel(SCContent.motionActivatedLight, 0, "motion_activated_light");
		registerInventoryModel(SCContent.crystalQuartz, 0, "crystal_quartz_default");
		registerInventoryModel(SCContent.crystalQuartz, 1, "crystal_quartz_chiseled");
		registerInventoryModel(SCContent.crystalQuartz, 2, "crystal_quartz_pillar");
		registerInventoryModel(SCContent.crystalQuartzSlab, 0, "crystal_quartz_slab");
		registerInventoryModel(SCContent.stairsCrystalQuartz, 0, "stairs_crystal_quartz");
		registerInventoryModel(SCContent.blockPocketWall, 0, "block_pocket_wall");
		registerInventoryModel(SCContent.blockPocketManager, 0, "block_pocket_manager");
		registerInventoryModel(SCContent.projector, 0, "projector");
		registerInventoryModel(SCContent.keypadDoor, 0, "keypad_door");
		registerInventoryModel(SCContent.riftStabilizer, 0, "rift_stabilizer");
		registerInventoryModel(SCContent.displayCase, 0, "display_case");
		registerInventoryModel(SCContent.floorTrap, 0, "floor_trap");
		registerInventoryModel(SCContent.secureRedstoneInterface, 0, "secure_redstone_interface");

		//items
		registerInventoryModel(SCContent.codebreaker, 0, "codebreaker");
		registerInventoryModel(SCContent.keycardLvl1, 0, "keycard_lv1");
		registerInventoryModel(SCContent.keycardLvl2, 0, "keycard_lv2");
		registerInventoryModel(SCContent.keycardLvl3, 0, "keycard_lv3");
		registerInventoryModel(SCContent.keycardLvl4, 0, "keycard_lv4");
		registerInventoryModel(SCContent.keycardLvl5, 0, "keycard_lv5");
		registerInventoryModel(SCContent.limitedUseKeycard, 0, "limited_use_keycard");
		registerInventoryModel(SCContent.universalBlockRemover, 0, "universal_block_remover");
		registerInventoryModel(SCContent.universalBlockModifier, 0, "universal_block_modifier");
		registerInventoryModel(SCContent.allowlistModule, 0, "whitelist_module");
		registerInventoryModel(SCContent.denylistModule, 0, "blacklist_module");
		registerInventoryModel(SCContent.redstoneModule, 0, "redstone_module");
		registerInventoryModel(SCContent.harmingModule, 0, "harming_module");
		registerInventoryModel(SCContent.storageModule, 0, "storage_module");
		registerInventoryModel(SCContent.smartModule, 0, "smart_module");
		registerInventoryModel(SCContent.disguiseModule, 0, "disguise_module");
		registerInventoryModel(SCContent.speedModule, 0, "speed_module");
		registerInventoryModel(SCContent.wireCutters, 0, "wire_cutters");
		registerInventoryModel(SCContent.keyPanel, 0, "keypad_item");
		registerInventoryModel(SCContent.adminTool, 0, "admin_tool");
		registerInventoryModel(SCContent.cameraMonitor, 0, "camera_monitor");
		registerInventoryModel(SCContent.scManual, 0, "sc_manual");
		registerInventoryModel(SCContent.taser, 0, "taser");
		registerInventoryModel(SCContent.taserPowered, 0, "taser_powered");
		registerInventoryModel(SCContent.universalOwnerChanger, 0, "universal_owner_changer");
		registerInventoryModel(SCContent.briefcase, 0, "briefcase");
		registerInventoryModel(SCContent.universalKeyChanger, 0, "universal_key_changer");
		registerInventoryModel(SCContent.scannerDoorItem, 0, "scanner_door_item");
		registerInventoryModel(SCContent.secretSignItem, 0, "secret_sign_item");
		registerInventoryModel(SCContent.crystalQuartzItem, 0, "crystal_quartz_item");
		registerInventoryModel(SCContent.keypadDoorItem, 0, "keypad_door_item");
		registerInventoryModel(SCContent.sonicSecuritySystemItem, 0, "sonic_security_system");
		registerInventoryModel(SCContent.portableTunePlayer, 0, "portable_tune_player");
		registerInventoryModel(SCContent.keycardHolder, 0, "keycard_holder");
		registerInventoryModel(SCContent.lens, 0, "lens");
		registerInventoryModel(SCContent.keycardLock, 0, "keycard_lock");

	}

	private static void registerInventoryModel(Block block, int metadata, String name) {
		registerInventoryModel(Item.getItemFromBlock(block), metadata, name);
	}

	private static void registerInventoryModel(Item item, int metadata, String name) {
		ModelLoader.setCustomModelResourceLocation(item, metadata, new ModelResourceLocation("securitycraft:" + name, "inventory"));
	}

	/**
	 * Registers a block and its ItemBlock and adds the help info for the block to the SecurityCraft manual item
	 *
	 * @param block The block to register
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, String designedBy) {
		registerBlock(event, block, new ItemBlock(block), designedBy);
	}

	/**
	 * Registers a block and its ItemBlock and adds the help info for the block to the SecurityCraft manual item
	 *
	 * @param block The block to register
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block) {
		registerBlock(event, block, new ItemBlock(block), PageGroup.SINGLE_ITEM);
	}

	/**
	 * Registers a block and its ItemBlock and adds the help info for the block to the SecurityCraft manual item. Additionally, a
	 * configuration value can be set to have this block's recipe show as disabled in the manual.
	 *
	 * @param block The block to register
	 * @param configValue The config value
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, Supplier<Boolean> configValue) {
		registerBlock(event, block, new ItemBlock(block), PageGroup.SINGLE_ITEM);
		blockConfigValues.put(block, configValue);
	}

	/**
	 * Registers a block and its ItemBlock
	 *
	 * @param block The Block to register

	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, PageGroup pageGroup) {
		registerBlock(event, block, new ItemBlock(block), pageGroup);
	}

	/**
	 * Registers a block with a custom ItemBlock
	 *
	 * @param block The Block to register
	 * @param itemBlock The ItemBlock to register
	 * @param pageType The type of the manual page from this block
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, ItemBlock itemBlock, PageGroup pageType) {
		event.getRegistry().register(block);

		if (itemBlock != null)
			itemBlocks.add(itemBlock.setRegistryName(block.getRegistryName().toString()));

		if (pageType == PageGroup.SINGLE_ITEM)
			blockPages.add(block);
		else if (pageType != PageGroup.NO_PAGE) {
			if (!pageTypeBlocks.containsKey(pageType))
				pageTypeBlocks.put(pageType, new ArrayList<>());

			pageTypeBlocks.get(pageType).add(block);
		}
	}

	/**
	 * Registers a block with a custom ItemBlock
	 *
	 * @param block The Block to register
	 * @param itemBlock The ItemBlock to register
	 * @param designedBy The name of the person who designed this block
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, ItemBlock itemBlock, String designedBy) {
		event.getRegistry().register(block);

		if (itemBlock != null)
			itemBlocks.add(itemBlock.setRegistryName(block.getRegistryName().toString()));

		blockPages.add(block);

		if (designedBy != null)
			blocksDesignedBy.put(block, designedBy);
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft
	 * manual item.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item) {
		registerItem(event, item, PageGroup.SINGLE_ITEM, () -> true, "");
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft
	 * manual item. Additionally, a configuration value can be set to have this item's recipe show as disabled in the manual.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item, PageGroup pageType, Supplier<Boolean> configValue) {
		registerItem(event, item, pageType, configValue, "");
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft
	 * manual item.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item, PageGroup pageType) {
		registerItem(event, item, pageType, () -> true, "");
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft
	 * manual item. Additionally, a configuration value can be set to have this item's recipe show as disabled in the manual.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item, PageGroup pageType, Supplier<Boolean> configValue, String designedBy) {
		event.getRegistry().register(item);

		if (pageType == PageGroup.NO_PAGE)
			return;

		if (pageType != PageGroup.SINGLE_ITEM) {
			if (!pageTypeStacks.containsKey(pageType))
				pageTypeStacks.put(pageType, new ArrayList<>());

			if (item != null && item.getHasSubtypes()) {
				NonNullList<ItemStack> subStacks = NonNullList.create();

				item.getSubItems(item.getCreativeTab(), subStacks);
				pageTypeStacks.get(pageType).addAll(subStacks);
			}
			else
				pageTypeStacks.get(pageType).add(new ItemStack(item));
		}
		else {
			TextComponentTranslation title = Utils.localize(item);
			TextComponentTranslation helpInfo = Utils.localize("help." + item.getTranslationKey().substring(5) + ".info");

			SCManualItem.PAGES.add(new SCManualPage(item, pageType, title, helpInfo, designedBy, false, configValue));
		}
	}

	public static class PublicCompoundIngredient extends CompoundIngredient { //Constructor of CompoundIngredient is protected, so this surrogate class is needed
		public PublicCompoundIngredient(List<Ingredient> children) {
			super(children);
		}

		public static PublicCompoundIngredient of(Ingredient... ingredients) {
			return new PublicCompoundIngredient(Arrays.asList(ingredients));
		}
	}
}

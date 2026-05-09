package net.geforcemods.securitycraft;

import java.io.IOException;
import java.util.ArrayList;
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
import net.geforcemods.securitycraft.blockentities.CageTrapBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.IronFenceBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeyPanelBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeycardLockBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadTrapdoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.MotionActivatedLightBlockEntity;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.ScannerDoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.ScannerTrapdoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.blockentities.ValidationOwnableBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.entity.sentry.Bullet;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.items.SCManualItem;
import net.geforcemods.securitycraft.misc.PageGroup;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.InteractWithFrame;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.PlayAlarmSound;
import net.geforcemods.securitycraft.network.client.RefreshDiguisedModel;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.network.client.UpdateTeamPrecedence;
import net.geforcemods.securitycraft.network.server.CheckBriefcasePasscode;
import net.geforcemods.securitycraft.network.server.CheckPasscode;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.network.server.RemoveSentryFromSRAT;
import net.geforcemods.securitycraft.network.server.SetBriefcasePasscodeAndOwner;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetDefaultCameraViewingDirection;
import net.geforcemods.securitycraft.network.server.SetKeycardUses;
import net.geforcemods.securitycraft.network.server.SetListModuleData;
import net.geforcemods.securitycraft.network.server.SetPasscode;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.network.server.SetStateOnDisguiseModule;
import net.geforcemods.securitycraft.network.server.SyncAlarmSettings;
import net.geforcemods.securitycraft.network.server.SyncFrame;
import net.geforcemods.securitycraft.network.server.SyncKeycardSettings;
import net.geforcemods.securitycraft.network.server.SyncTENBTTag;
import net.geforcemods.securitycraft.network.server.ToggleModule;
import net.geforcemods.securitycraft.network.server.ToggleNightVision;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.network.server.UpdateSliderValue;
import net.geforcemods.securitycraft.recipe.CopyPositionComponentItemRecipe;
import net.geforcemods.securitycraft.recipe.LimitedUseKeycardRecipe;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
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

	private RegistrationHandler() {}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		registerBlock(event, SCContent.keypad);
		registerBlock(event, SCContent.retinalScanner);
		registerBlock(event, SCContent.keycardReader);
		registerBlock(event, SCContent.cageTrap);
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
		event.getRegistry().register(SCContent.scannerDoor);
		registerBlock(event, SCContent.motionActivatedLight);
		registerBlock(event, SCContent.displayCase);
		registerBlock(event, SCContent.keypadTrapdoor);
		event.getRegistry().register(SCContent.keycardLockFloorCeilingBlock);
		event.getRegistry().register(SCContent.keycardLockWallBlock);
		registerBlock(event, SCContent.scannerTrapdoor);
		event.getRegistry().register(SCContent.keypadDoor);
		event.getRegistry().register(SCContent.keyPanelFloorCeilingBlock);
		event.getRegistry().register(SCContent.keyPanelWallBlock);
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
		registerItem(event, SCContent.sentryRemoteAccessTool);
		registerItem(event, SCContent.incognitoMask);
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
		registerItem(event, SCContent.sentry, PageGroup.SINGLE_ITEM, () -> true, "Henzoid");
		registerItem(event, SCContent.keypadDoorItem);
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
			pageType.setItems(net.minecraft.item.crafting.Ingredient.fromStacks(list.toArray(new ItemStack[list.size()])));
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
		GameRegistry.registerTileEntity(CageTrapBlockEntity.class, new ResourceLocation("securitycraft:cage_trap"));
		GameRegistry.registerTileEntity(KeycardReaderBlockEntity.class, new ResourceLocation("securitycraft:keycard_reader"));
		GameRegistry.registerTileEntity(SecurityCameraBlockEntity.class, new ResourceLocation("securitycraft:security_camera"));
		GameRegistry.registerTileEntity(UsernameLoggerBlockEntity.class, new ResourceLocation("securitycraft:username_logger"));
		GameRegistry.registerTileEntity(RetinalScannerBlockEntity.class, new ResourceLocation("securitycraft:retinal_scanner"));
		GameRegistry.registerTileEntity(KeypadChestBlockEntity.class, new ResourceLocation("securitycraft:keypad_chest"));
		GameRegistry.registerTileEntity(AlarmBlockEntity.class, new ResourceLocation("securitycraft:alarm"));
		GameRegistry.registerTileEntity(KeypadFurnaceBlockEntity.class, new ResourceLocation("securitycraft:keypad_furnace"));
		GameRegistry.registerTileEntity(CustomizableBlockEntity.class, new ResourceLocation("securitycraft:customizable"));
		GameRegistry.registerTileEntity(ScannerDoorBlockEntity.class, new ResourceLocation("securitycraft:scanner_door"));
		GameRegistry.registerTileEntity(MotionActivatedLightBlockEntity.class, new ResourceLocation("securitycraft:motion_light"));
		GameRegistry.registerTileEntity(IronFenceBlockEntity.class, new ResourceLocation("securitycraft:iron_fence"));
		GameRegistry.registerTileEntity(KeypadDoorBlockEntity.class, new ResourceLocation("securitycraft:keypad_door"));
		GameRegistry.registerTileEntity(ValidationOwnableBlockEntity.class, new ResourceLocation("securitycraft:validation_ownable"));
		GameRegistry.registerTileEntity(KeyPanelBlockEntity.class, new ResourceLocation("securitycraft:key_panel"));
		GameRegistry.registerTileEntity(DisguisableBlockEntity.class, new ResourceLocation("securitycraft:disguisable"));
		GameRegistry.registerTileEntity(DisplayCaseBlockEntity.class, new ResourceLocation("securitycraft:display_case"));
		GameRegistry.registerTileEntity(KeypadTrapdoorBlockEntity.class, new ResourceLocation("securitycraft:keypad_trapdoor"));
		GameRegistry.registerTileEntity(KeycardLockBlockEntity.class, new ResourceLocation("securitycraft:keycard_lock"));
		GameRegistry.registerTileEntity(ScannerTrapdoorBlockEntity.class, new ResourceLocation("securitycraft:scanner_trapdoor"));
		GameRegistry.registerTileEntity(FrameBlockEntity.class, new ResourceLocation("securitycraft:frame"));
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		//@formatter:off
		event.getRegistry().register(EntityEntryBuilder.create()
				.id(new ResourceLocation(SecurityCraft.MODID, "securitycamera"), 4)
				.entity(SecurityCamera.class)
				.name("SecurityCamera")
				.tracker(256, 20, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.id(new ResourceLocation(SecurityCraft.MODID, "sentry"), 5)
				.entity(Sentry.class)
				.name("Sentry")
				.tracker(256, 1, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.id(new ResourceLocation(SecurityCraft.MODID, "bullet"), 6)
				.entity(Bullet.class)
				.name("SentryBullet")
				.tracker(256, 1, true).build());
		//@formatter:on
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
		network.registerMessage(ClearLoggerServer.Handler.class, ClearLoggerServer.class, 27, Side.SERVER);
		network.registerMessage(RefreshDiguisedModel.Handler.class, RefreshDiguisedModel.class, 28, Side.CLIENT);
		network.registerMessage(SetSentryMode.Handler.class, SetSentryMode.class, 29, Side.SERVER);
		network.registerMessage(SetKeycardUses.Handler.class, SetKeycardUses.class, 35, Side.SERVER);
		network.registerMessage(SetCameraView.Handler.class, SetCameraView.class, 36, Side.CLIENT);
		network.registerMessage(DismountCamera.Handler.class, DismountCamera.class, 37, Side.SERVER);
		network.registerMessage(ToggleModule.Handler.class, ToggleModule.class, 41, Side.SERVER);
		network.registerMessage(RemoveSentryFromSRAT.Handler.class, RemoveSentryFromSRAT.class, 45, Side.SERVER);
		network.registerMessage(SetBriefcasePasscodeAndOwner.Handler.class, SetBriefcasePasscodeAndOwner.class, 46, Side.SERVER);
		network.registerMessage(SetListModuleData.Handler.class, SetListModuleData.class, 47, Side.SERVER);
		network.registerMessage(SetStateOnDisguiseModule.Handler.class, SetStateOnDisguiseModule.class, 48, Side.SERVER);
		network.registerMessage(PlayAlarmSound.Handler.class, PlayAlarmSound.class, 52, Side.CLIENT);
		network.registerMessage(SyncAlarmSettings.Handler.class, SyncAlarmSettings.class, 53, Side.SERVER);
		network.registerMessage(OpenScreen.Handler.class, OpenScreen.class, 55, Side.CLIENT);
		network.registerMessage(SetDefaultCameraViewingDirection.Handler.class, SetDefaultCameraViewingDirection.class, 57, Side.SERVER);
		network.registerMessage(InteractWithFrame.Handler.class, InteractWithFrame.class, 60, Side.CLIENT);
		network.registerMessage(SyncFrame.Handler.class, SyncFrame.class, 61, Side.SERVER);
		network.registerMessage(UpdateTeamPrecedence.Handler.class, UpdateTeamPrecedence.class, 62, Side.CLIENT);
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
		event.getRegistry().register(new net.geforcemods.securitycraft.misc.DyeItemRecipe().setRegistryName(new ResourceLocation(SecurityCraft.MODID, "dye_briefcase")));
		event.getRegistry().register(CopyPositionComponentItemRecipe.cameraMonitor().setRegistryName(new ResourceLocation(SecurityCraft.MODID, "copy_camera_monitor")));
		event.getRegistry().register(CopyPositionComponentItemRecipe.sentryRemoteAccessTool().setRegistryName(new ResourceLocation(SecurityCraft.MODID, "copy_sentry_remote_access_tool")));
		event.getRegistry().register(new LimitedUseKeycardRecipe().setRegistryName(new ResourceLocation(SecurityCraft.MODID, "limited_use_keycard_conversion")));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerResourceLocations(ModelRegistryEvent event) {
		//blocks
		registerInventoryModel(SCContent.keypad, 0, "keypad");
		registerInventoryModel(SCContent.frame, 0, "keypad_frame");
		registerInventoryModel(SCContent.keypadChest, 0, "keypad_chest");
		registerInventoryModel(SCContent.retinalScanner, 0, "retinal_scanner");
		registerInventoryModel(SCContent.alarm, 0, "alarm");
		registerInventoryModel(SCContent.alarmLit, 0, "alarm_lit");
		registerInventoryModel(SCContent.usernameLogger, 0, "username_logger");
		registerInventoryModel(SCContent.electrifiedIronFenceGate, 0, "reinforced_fence_gate");
		registerInventoryModel(SCContent.electrifiedIronFence, 0, "electrified_iron_fence");
		registerInventoryModel(SCContent.keypadFurnace, 0, "keypad_furnace");
		registerInventoryModel(SCContent.panicButton, 0, "panic_button");
		registerInventoryModel(SCContent.securityCamera, 0, "security_camera");
		registerInventoryModel(SCContent.scannerDoor, 0, "scanner_door");
		registerInventoryModel(SCContent.motionActivatedLight, 0, "motion_activated_light");
		registerInventoryModel(SCContent.displayCase, 0, "display_case");
		registerInventoryModel(SCContent.keypadTrapdoor, 0, "keypad_trapdoor");
		registerInventoryModel(SCContent.scannerTrapdoor, 0, "scanner_trapdoor");
		registerInventoryModel(SCContent.keypadDoor, 0, "keypad_door");
		registerInventoryModel(SCContent.keycardReader, 0, "keycard_reader");

		//items
		registerInventoryModel(SCContent.codebreaker, 0, "codebreaker");
		registerInventoryModel(SCContent.sentryRemoteAccessTool, 0, "remote_access_sentry");
		registerInventoryModel(SCContent.incognitoMask, 0, "incognito_mask");
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
		registerInventoryModel(SCContent.sentry, 0, "sentry");
		registerInventoryModel(SCContent.keypadDoorItem, 0, "keypad_door_item");
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
	 * @param pageType The type of the manual page from this block
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
}

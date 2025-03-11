package net.geforcemods.securitycraft;

import net.geforcemods.securitycraft.blocks.AlarmBlock;
import net.geforcemods.securitycraft.items.LensItem;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class SCContent {
	//Blocks
	public static Block alarm;
	/**
	 * @deprecated Use {@link //alarm} and its LIT property
	 * @see {@link AlarmBlock}
	 */
	@Deprecated
	public static Block alarmLit;
	public static Block blockPocketManager;
	public static Block blockPocketWall;
	public static Block cageTrap;
	public static Block crystalQuartz;
	public static Block crystalQuartzSlab;
	public static Block displayCase;
	public static Block doubleCrystalQuartzSlab;
	public static Block electrifiedIronFence;
	public static Block electrifiedIronFenceGate;
	public static Block floorTrap;
	public static Block frame;
	public static Block inventoryScanner;
	public static Block inventoryScannerField;
	public static Block keycardLockFloorCeilingBlock;
	public static Block keycardLockWallBlock;
	public static Block keycardReader;
	public static Block keyPanelFloorCeilingBlock;
	public static Block keyPanelWallBlock;
	public static Block keypad;
	public static Block keypadChest;
	public static Block keypadDoor;
	public static Block keypadFurnace;
	public static Block laserBlock;
	public static Block laserField;
	public static Block motionActivatedLight;
	public static Block panicButton;
	public static Block portableRadar;
	public static Block projector;
	public static Block protecto;


	public static Block retinalScanner;
	public static Block riftStabilizer;
	public static Block scannerDoor;

	public static Block secretSignStanding;
	public static Block secretSignWall;
	public static Block secureRedstoneInterface;
	public static Block securityCamera;
	public static Block sonicSecuritySystem;
	public static Block stairsCrystalQuartz;
	public static Block usernameLogger;

	//Items
	public static Item adminTool;
	public static Item briefcase;
	public static Item cameraMonitor;
	public static Item codebreaker;
	public static Item crystalQuartzItem;
	public static Item keycardLvl1;
	public static Item keycardLvl2;
	public static Item keycardLvl3;
	public static Item keycardLvl4;
	public static Item keycardLvl5;
	public static Item keypadDoorItem;
	public static Item keyPanel;
	public static Item limitedUseKeycard;
	public static Item portableTunePlayer;
	public static Item scannerDoorItem;
	public static Item scManual;
	public static Item secretSignItem;
	public static Item sonicSecuritySystemItem;
	public static Item taser;
	public static Item taserPowered;
	public static Item universalBlockModifier;
	public static Item universalBlockRemover;
	public static Item universalKeyChanger;
	public static Item universalOwnerChanger;
	public static Item wireCutters;
	public static Item keycardHolder;
	public static LensItem lens;
	public static Item keycardLock;

	//Modules
	public static ModuleItem denylistModule;
	public static ModuleItem disguiseModule;
	public static ModuleItem harmingModule;
	public static ModuleItem redstoneModule;
	public static ModuleItem smartModule;
	public static ModuleItem storageModule;
	public static ModuleItem allowlistModule;
	public static ModuleItem speedModule;

	private SCContent() {}
}

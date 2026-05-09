package net.geforcemods.securitycraft.network;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.CageTrapBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadTrapdoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.entity.sentry.Bullet;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.items.ColorableItem;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.renderers.BlockEntityItemRenderer;
import net.geforcemods.securitycraft.renderers.BulletRenderer;
import net.geforcemods.securitycraft.renderers.DisguisableBlockEntityRenderer;
import net.geforcemods.securitycraft.renderers.DisplayCaseRenderer;
import net.geforcemods.securitycraft.renderers.FrameBlockEntityRenderer;
import net.geforcemods.securitycraft.renderers.KeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.RetinalScannerRenderer;
import net.geforcemods.securitycraft.renderers.SecurityCameraRenderer;
import net.geforcemods.securitycraft.renderers.SentryRenderer;
import net.geforcemods.securitycraft.screen.CameraSelectScreen;
import net.geforcemods.securitycraft.screen.FrameScreen;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Tinted;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public class ClientProxy implements IProxy {
	private static Map<Block, Pair<IBlockColor, IItemColor>> toTint = new HashMap<>();

	@SubscribeEvent
	public static void onTextureStitch(TextureStitchEvent.Pre event) {
		event.getMap().registerSprite(new ResourceLocation(SecurityCraft.MODID, "entity/frame/noise_background"));
		event.getMap().registerSprite(new ResourceLocation(SecurityCraft.MODID, "particle/floor_trap_cloud"));
	}

	@Override
	public void registerVariants() {
		ModelLoader.setCustomStateMapper(SCContent.frame, new StateMap.Builder().ignore(FrameBlock.POWERED).build());
	}

	@Override
	public void registerEntityRenderingHandlers() {
		RenderingRegistry.registerEntityRenderingHandler(Sentry.class, SentryRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(Bullet.class, BulletRenderer::new);
	}

	@Override
	public void registerRenderThings() {
		KeyBindings.init();

		//normal tile entity renderers
		ClientRegistry.bindTileEntitySpecialRenderer(KeypadChestBlockEntity.class, new KeypadChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(SecurityCameraBlockEntity.class, new SecurityCameraRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(RetinalScannerBlockEntity.class, new RetinalScannerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(DisplayCaseBlockEntity.class, new DisplayCaseRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(FrameBlockEntity.class, new FrameBlockEntityRenderer());
		//disguisable tile entity renderers
		ClientRegistry.bindTileEntitySpecialRenderer(DisguisableBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(CageTrapBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(KeycardReaderBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(KeypadBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(KeypadFurnaceBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(KeypadTrapdoorBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(UsernameLoggerBlockEntity.class, new DisguisableBlockEntityRenderer<>());

		Item.getItemFromBlock(SCContent.keypadChest).setTileEntityItemStackRenderer(new BlockEntityItemRenderer(new KeypadChestBlockEntity()));
		Item.getItemFromBlock(SCContent.displayCase).setTileEntityItemStackRenderer(new BlockEntityItemRenderer(new DisplayCaseBlockEntity()));
	}

	private static void initTint() {
		if (toTint != null) { //apparently some mods post the color handler events again, after forge already posted them.
			for (Field field : SCContent.class.getFields()) {
				if (field.isAnnotationPresent(Tinted.class)) {
					int tint = field.getAnnotation(Tinted.class).customTint();
					boolean hasReinforcedTint = field.getAnnotation(Tinted.class).hasReinforcedTint();

					try {
						Block block = (Block) field.get(null);

						//@formatter:off
						//registering reinforced blocks color overlay for world
						toTint.put(block, Pair.of(
							(state, world, pos, tintIndex) -> {
								if (tintIndex == 0)
									return hasReinforcedTint ? mixWithReinforcedTintIfEnabled(tint) : tint;
								else
									return 0xFFFFFF;
							},
							//same thing for inventory
							(stack, tintIndex) -> {
								if (tintIndex == 0)
									return hasReinforcedTint ? mixWithReinforcedTintIfEnabled(tint) : tint;
								else
									return 0xFFFFFF;
							}
						));
						//@formatter:on
					}
					catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onColorHandlerBlock(ColorHandlerEvent.Block event) {
		initTint();

		if (toTint != null) { //apparently some mods post the color handler events again, after forge already posted them.
			toTint.forEach((block, pair) -> event.getBlockColors().registerBlockColorHandler(pair.getLeft(), block));
		}
	}

	@SubscribeEvent
	public static void onColorHandlerItem(ColorHandlerEvent.Item event) {
		if (toTint != null) { //apparently some mods post the color handler events again, after forge already posted them.
			toTint.forEach((block, pair) -> event.getItemColors().registerItemColorHandler(pair.getRight(), block));
			event.getItemColors().registerItemColorHandler((stack, tintIndex) -> tintIndex == 0 ? ((ColorableItem) stack.getItem()).getColor(stack) : -1, SCContent.briefcase, SCContent.lens);
			toTint = null;
		}
	}

	private static int mixWithReinforcedTintIfEnabled(int tint1) {
		return ConfigHandler.reinforcedBlockTint ? mixTints(tint1, ConfigHandler.reinforcedBlockTintColor) : tint1;
	}

	private static int mixTints(int tint1, int tint2) {
		int red = (tint1 >> 0x10) & 0xFF;
		int green = (tint1 >> 0x8) & 0xFF;
		int blue = tint1 & 0xFF;

		red *= (float) (tint2 >> 0x10 & 0xFF) / 0xFF;
		green *= (float) (tint2 >> 0x8 & 0xFF) / 0xFF;
		blue *= (float) (tint2 & 0xFF) / 0xFF;

		return ((red << 8) + green << 8) + blue;
	}

	@Override
	public void addEffect(IParticleFactory factory, World level, double x, double y, double z) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity entity = mc.getRenderViewEntity();
		World world = mc.world;

		if (entity != null && mc.effectRenderer != null) {
			int particleSetting = mc.gameSettings.particleSetting;

			if (particleSetting == 1 && world.rand.nextInt(3) == 0)
				particleSetting = 2;

			if (particleSetting > 1)
				return;

			double xDistance = entity.posX - x;
			double yDistance = entity.posY - y;
			double zDistance = entity.posZ - z;

			if (xDistance * xDistance + yDistance * yDistance + zDistance * zDistance <= 1024.0D)
				mc.effectRenderer.addEffect(factory.createParticle(0, world, x, y, z, 0.0D, 0.0D, 0.0D, 0));
		}
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().player;
	}

	@Override
	public World getClientLevel() {
		return Minecraft.getMinecraft().world;
	}

	@Override
	public void updateBlockColorAroundPosition(BlockPos pos) {
		Minecraft.getMinecraft().renderGlobal.notifyBlockUpdate(Minecraft.getMinecraft().world, pos, null, null, 0);
	}

	public static boolean isPlayerMountedOnCamera() {
		return Minecraft.getMinecraft().getRenderViewEntity() instanceof SecurityCamera;
	}

	public static CameraSelectScreen frameScreen(EntityPlayer player, FrameBlockEntity be, boolean readOnly) {
		ItemStack heldStack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.cameraMonitor);

		return heldStack.isEmpty() ? new FrameScreen(readOnly, be) : null;
	}
}

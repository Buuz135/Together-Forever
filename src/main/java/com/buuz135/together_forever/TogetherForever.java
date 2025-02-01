package com.buuz135.together_forever;

import com.buuz135.together_forever.api.*;
import com.buuz135.together_forever.api.annotation.PlayerInformation;
import com.buuz135.together_forever.api.annotation.SyncAction;
import com.buuz135.together_forever.api.annotation.TogetherTeam;
import com.buuz135.together_forever.api.command.TogetherForeverCommand;
import com.buuz135.together_forever.api.data.DataManager;
import com.buuz135.together_forever.api.data.DefaultPlayerInformation;
import com.buuz135.together_forever.api.data.TogetherRegistries;
import com.buuz135.together_forever.command.*;
import com.buuz135.together_forever.config.TogetherForeverConfig;
import com.buuz135.together_forever.util.AnnotationUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.HelpCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.util.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TogetherForever.MODID)
public class TogetherForever {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "together_forever";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static HashMap<String, Long> joinedPlayers = new HashMap<>();


    public TogetherForever() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);


        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TogetherForeverConfig.SPEC);

        try {
            registerSyncActions();
            registerTogetherTeams();
            registerPlayerInformations();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }

    }


    @SubscribeEvent
    public void serverLoad(ServerStartingEvent event) {
        TogetherForeverCommand command = new TogetherForeverCommand(Arrays.asList(new InviteCommand(), new AcceptInviteCommand(), new DeclineInviteCommand(),
                new TeamKickCommand(), new TeamLeaveCommand(), new TeamInfoCommand(), new ForceSyncCommand(), new TogetherForeverDebug()));
        command.register(event.getServer().getCommands().getDispatcher());
    }

    @SubscribeEvent
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer && TogetherForeverAPI.getInstance().getPlayerTeam(event.getEntity().getUUID()) != null) {
            joinedPlayers.put(event.getEntity().getUUID().toString(), System.currentTimeMillis());
            if (TogetherForeverConfig.syncDataSecondsDelay.get() > 0)
                event.getEntity().sendSystemMessage(Component.literal(ChatFormatting.GOLD + "Syncing team data in " + TogetherForeverConfig.syncDataSecondsDelay.get() + " seconds!"));
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        List<String> remove = new ArrayList<>();
        for (String uuid : joinedPlayers.keySet()) {
            if (System.currentTimeMillis() - joinedPlayers.get(uuid) >= TogetherForeverConfig.syncDataSecondsDelay.get() * 1000) {
                remove.add(uuid);
                ServerPlayer playerMP = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(UUID.fromString(uuid));
                if (playerMP != null) {
                    playerMP.sendSystemMessage(Component.literal(ChatFormatting.GOLD + "Trying to sync team data now!"));
                    DefaultPlayerInformation information = DefaultPlayerInformation.createInformation(playerMP);
                    DataManager manager = TogetherForeverAPI.getInstance().getDataManager(playerMP.getServer().overworld());
                    for (IOfflineSyncRecovery recovery : manager.getRecoveries()) {
                        recovery.recoverMissingPlayer(information);
                    }
                    manager.setDirty();
                }
            }
        }
        for (String s : remove) {
            joinedPlayers.remove(s);
        }
    }

    private void registerSyncActions() throws IllegalAccessException, InstantiationException {
        for (Class<?> aClass : AnnotationUtil.getAnnotatedClasses(SyncAction.class)) {
            if (ISyncAction.class.isAssignableFrom(aClass)) {
                SyncAction syncAction = aClass.getAnnotation(SyncAction.class);
                if (syncAction.dependencies().length == 0 || areDependenciesLoaded(syncAction.dependencies())) {
                    Object object = aClass.newInstance();
                    TogetherRegistries.registerSyncAction(syncAction.id(), (ISyncAction<?, ? extends IOfflineSyncRecovery>) object);
                }
            }
        }
    }

    private void registerTogetherTeams() {
        for (Class aClass : AnnotationUtil.getAnnotatedClasses(TogetherTeam.class)) {
            if (ITogetherTeam.class.isAssignableFrom(aClass)) {
                TogetherTeam togetherTeam = (TogetherTeam) aClass.getAnnotation(TogetherTeam.class);
                TogetherRegistries.registerTogetherTeam(togetherTeam.id(), aClass);
            }
        }
    }

    private void registerPlayerInformations() {
        for (Class aClass : AnnotationUtil.getAnnotatedClasses(PlayerInformation.class)) {
            if (IPlayerInformation.class.isAssignableFrom(aClass)) {
                PlayerInformation playerInformation = (PlayerInformation) aClass.getAnnotation(PlayerInformation.class);
                TogetherRegistries.registerPlayerInformation(playerInformation.id(), aClass);
            }
        }
    }

    private boolean areDependenciesLoaded(String[] deps) {
        for (String dep : deps) {
            if (!ModList.get().isLoaded(dep)) return false;
        }
        return true;
    }
}

package com.buuz135.togetherforever;

import com.buuz135.togetherforever.api.*;
import com.buuz135.togetherforever.api.annotation.PlayerInformation;
import com.buuz135.togetherforever.api.annotation.SyncAction;
import com.buuz135.togetherforever.api.annotation.TogetherTeam;
import com.buuz135.togetherforever.api.command.TogetherForeverCommand;
import com.buuz135.togetherforever.api.data.DataManager;
import com.buuz135.togetherforever.api.data.DefaultPlayerInformation;
import com.buuz135.togetherforever.api.data.TogetherRegistries;
import com.buuz135.togetherforever.command.*;
import com.buuz135.togetherforever.utils.AnnotationHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;


@Mod(
        modid = TogetherForever.MOD_ID,
        name = TogetherForever.MOD_NAME,
        version = TogetherForever.VERSION,
        dependencies = "required:forge@[14.23.1.2560,);after:gamestages@[1.0.76,);"
)
public class TogetherForever {

    public static final String MOD_ID = "togetherforever";
    public static final String MOD_NAME = "TogetherForever";
    public static final String VERSION = "1.0-SNAPSHOT";

    public static Logger LOGGER;

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static TogetherForever INSTANCE;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        if (event.getSide() == Side.SERVER) {
            LOGGER = event.getModLog();
            try {
                registerSyncActions(event.getAsmData());
                registerTogetherTeams(event.getAsmData());
                registerPlayerInformations(event.getAsmData());
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        TogetherForeverCommand command = new TogetherForeverCommand(Arrays.asList(new InviteCommand(), new AcceptInviteCommand(), new DeclineInviteCommand(), new TeamKickCommand(), new TeamLeaveCommand(), new TeamInfoCommand()));
        event.registerServerCommand(command);
        event.registerServerCommand(new TogetherForeverDebug());
    }

    @SubscribeEvent
    public void playerJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP) {
            DefaultPlayerInformation information = DefaultPlayerInformation.createInformation((EntityPlayerMP) event.getEntity());
            DataManager manager = TogetherForeverAPI.getInstance().getDataManager(event.getWorld());
            for (IOfflineSyncRecovery recovery : manager.getRecoveries()) {
                recovery.recoverMissingPlayer(information);
            }
            manager.markDirty();
        }
    }

    private void registerSyncActions(ASMDataTable data) throws IllegalAccessException, InstantiationException {
        for (Class aClass : AnnotationHelper.getAnnotatedClasses(data, SyncAction.class)) {
            if (ISyncAction.class.isAssignableFrom(aClass)) {
                SyncAction syncAction = (SyncAction) aClass.getAnnotation(SyncAction.class);
                if (syncAction.dependencies().length == 0 || areDependenciesLoaded(syncAction.dependencies())) {
                    Object object = aClass.newInstance();
                    TogetherRegistries.registerSyncAction(syncAction.id(), (ISyncAction) object);
                }
            }
        }
    }

    private void registerTogetherTeams(ASMDataTable data) {
        for (Class aClass : AnnotationHelper.getAnnotatedClasses(data, TogetherTeam.class)) {
            if (ITogetherTeam.class.isAssignableFrom(aClass)) {
                TogetherTeam togetherTeam = (TogetherTeam) aClass.getAnnotation(TogetherTeam.class);
                TogetherRegistries.registerTogetherTeam(togetherTeam.id(), aClass);
            }
        }
    }

    private void registerPlayerInformations(ASMDataTable data) {
        for (Class aClass : AnnotationHelper.getAnnotatedClasses(data, PlayerInformation.class)) {
            if (IPlayerInformation.class.isAssignableFrom(aClass)) {
                PlayerInformation playerInformation = (PlayerInformation) aClass.getAnnotation(PlayerInformation.class);
                TogetherRegistries.registerPlayerInformation(playerInformation.id(), aClass);
            }
        }
    }

    private boolean areDependenciesLoaded(String[] deps) {
        for (String dep : deps) {
            if (!Loader.isModLoaded(dep)) return false;
        }
        return true;
    }
}

package com.buuz135.togetherforever.api.action;

import com.buuz135.togetherforever.api.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/**
 * Represent a SyncAction based in a event
 *
 * @param <T> The {@link PlayerEvent} type of the SyncAction
 * @param <S> The {@link IOfflineSyncRecovery} type of the SyncAction
 */
public abstract class EventSyncAction<T extends PlayerEvent, S extends IOfflineSyncRecovery> implements ISyncAction<T, S> {

    private final Class<S> recovery;
    private final Class<T> eventClass;

    /**
     * Default constructor of the class
     *
     * @param eventClass      The Event class of the SyncAction that will get automatically registered
     * @param offlineRecovery The Offline Recovery class for the this SyncAction
     */
    public EventSyncAction(Class<T> eventClass, Class<S> offlineRecovery) {
        this.recovery = offlineRecovery;
        this.eventClass = eventClass;
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Triggers the event, gets the Player of the PlayerEvent, gets the {@link ITogetherTeam} from the Player,
     * triggers the SyncAction for all the team and stores all players offline in the OfflineSyncRecovery for later use
     *
     * @param event The event with same class as T
     */
    @SubscribeEvent
    public void onEvent(T event) {
        if (!event.getClass().equals(eventClass)) return;
        if (TogetherForeverAPI.getInstance().getWorld() == null) return;
        ITogetherTeam team = TogetherForeverAPI.getInstance().getPlayerTeam(event.getEntityPlayer().getUniqueID());
        if (team != null) {
            List<IPlayerInformation> playerLeft = triggerSync(event, team);
            NBTTagCompound compound = transformEventToNBT(event);
            for (IPlayerInformation information : playerLeft) {
                TogetherForeverAPI.getInstance().addPlayerToOfflineRecovery(recovery, information, compound);
            }
        }
    }

    /**
     * Getter of the IOfflineRecovery
     *
     * @return The class
     */
    @Override
    public Class<S> getOfflineRecovery() {
        return recovery;
    }

    /**
     * Transforms an event into a NBTTagCompound so it can be stored in the world for the IOfflineSyncRecovey
     *
     * @param event The event to transform
     * @return The transformed NBTTagCompound of the Event
     */
    public abstract NBTTagCompound transformEventToNBT(T event);

}

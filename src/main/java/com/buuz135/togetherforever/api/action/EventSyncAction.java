package com.buuz135.togetherforever.api.action;

import com.buuz135.togetherforever.api.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public abstract class EventSyncAction<T extends PlayerEvent, S extends IOfflineSyncRecovery> implements ISyncAction<T, S> {

    private final Class<S> recovery;
    private final Class<T> eventClass;

    public EventSyncAction(Class<S> offlineRecovery, Class<T> eventClass) {
        this.recovery = offlineRecovery;
        this.eventClass = eventClass;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEvent(T event) {
        if (!event.getClass().equals(eventClass)) return;
        ITogetherTeam team = TogetherForeverAPI.getInstance().getPlayerTeam(event.getEntityPlayer().getUniqueID());
        if (team != null) {
            List<IPlayerInformation> playerLeft = triggerSync(event, team);
            NBTTagCompound compound = transformEventToNBT(event);
            for (IPlayerInformation information : playerLeft) {
                TogetherForeverAPI.getInstance().addPlayerToOfflineRecovery(recovery, information, compound);
            }
        }
    }

    @Override
    public Class<S> getOfflineRecovery() {
        return recovery;
    }

    public abstract NBTTagCompound transformEventToNBT(T event);

}

package com.buuz135.togetherforever.api;

import com.buuz135.togetherforever.api.event.TeamEvent;
import com.buuz135.togetherforever.data.DataManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.server.FMLServerHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TogetherForeverAPI {

    private static TogetherForeverAPI ourInstance = new TogetherForeverAPI();
    private List<TeamInvite> teamInvites;

    private TogetherForeverAPI() {
        teamInvites = new ArrayList<>();
    }

    public static TogetherForeverAPI getInstance() {
        return ourInstance;
    }

    public List<ITogetherTeam> getTeams() {
        return getDataManager(getWorld()).getTeams();
    }

    public void addTeam(ITogetherTeam togetherTeam) {
        DataManager dataManager = getDataManager(getWorld());
        TeamEvent.Create create = new TeamEvent.Create(togetherTeam);
        MinecraftForge.EVENT_BUS.post(create);
        if (!create.isCanceled()) {
            dataManager.getTeams().add(create.getTogetherTeam());
            dataManager.markDirty();
        }
    }

    public void addPlayerToTeam(ITogetherTeam team, IPlayerInformation playerInformation) {
        DataManager manager = getDataManager(getWorld());
        for (ITogetherTeam togetherTeam : manager.getTeams()) {
            if (togetherTeam.getOwner().equals(team.getOwner()) && togetherTeam.getTeamName().equalsIgnoreCase(team.getTeamName())) {
                TeamEvent.PlayerAdd playerAdd = new TeamEvent.PlayerAdd(togetherTeam, playerInformation);
                MinecraftForge.EVENT_BUS.post(playerAdd);
                if (!playerAdd.isCanceled()) {
                    playerAdd.getTogetherTeam().addPlayer(playerAdd.getPlayerInformation());
                    manager.markDirty();
                }
            }
        }
    }

    public void removePlayerFromTeam(ITogetherTeam team, IPlayerInformation playerInformation) {
        DataManager manager = getDataManager(getWorld());
        for (ITogetherTeam togetherTeam : manager.getTeams()) {
            if (togetherTeam.getOwner().equals(team.getOwner()) && togetherTeam.getTeamName().equalsIgnoreCase(team.getTeamName())) {
                TeamEvent.RemovePlayer removePlayer = new TeamEvent.RemovePlayer(togetherTeam, playerInformation);
                if (!removePlayer.isCanceled()) {
                    removePlayer.getTogetherTeam().removePlayer(removePlayer.getPlayerInformation());
                    manager.markDirty();
                }
            }
        }
    }

    @Nullable
    public ITogetherTeam getPlayerTeam(UUID playerUUID) {
        for (ITogetherTeam togetherTeam : getTeams()) {
            for (IPlayerInformation playerInformation : togetherTeam.getPlayers()) {
                if (playerInformation.getUUID().equals(playerUUID)) return togetherTeam;
            }
        }
        return null;
    }

    public TeamInvite createTeamInvite(IPlayerInformation sender, IPlayerInformation reciever, boolean announceInvite) {
        TeamInvite invite = new TeamInvite(sender, reciever);
        if (announceInvite) {
            ITextComponent accept = new TextComponentString("[ACCEPT]");
            accept.getStyle().setBold(true).setColor(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tf accept " + sender.getName())).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to accept")));
            ITextComponent decline = new TextComponentString("[DECLINE]");
            decline.getStyle().setBold(true).setColor(TextFormatting.RED).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tf decline " + sender.getName())).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to accept")));
            reciever.getPlayer().sendMessage(new TextComponentString("You have been invited to join " + sender.getName() + "'s team. Click ")
                    .appendSibling(accept).appendText(" ").appendSibling(decline));
        }
        teamInvites.add(invite);
        return invite;
    }

    public void addPlayerToOfflineRecovery(Class<? extends IOfflineSyncRecovery> recoveryClass, IPlayerInformation iPlayerInformation, NBTTagCompound compound) {
        DataManager manager = getDataManager(getWorld());
        for (IOfflineSyncRecovery recovery : manager.getRecoveries()) {
            if (recovery.getClass().equals(recoveryClass)) {
                recovery.storeMissingPlayer(iPlayerInformation, compound);
                manager.markDirty();
                return;
            }
        }
        try {
            IOfflineSyncRecovery recovery = recoveryClass.newInstance();
            recovery.storeMissingPlayer(iPlayerInformation, compound);
            manager.getRecoveries().add(recovery);
            manager.markDirty();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public DataManager getDataManager(World world) {
        DataManager dataManager = (DataManager) world.getMapStorage().getOrLoadData(DataManager.class, DataManager.NAME);
        if (dataManager == null) {
            dataManager = new DataManager();
            world.getMapStorage().setData(DataManager.NAME, dataManager);
        }
        return dataManager;
    }

    @Nullable
    public EntityPlayerMP getPlayer(String string) {
        return FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUsername(string);
    }

    @Nullable
    public EntityPlayerMP getPlayer(UUID uuid) {
        return FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(uuid);
    }

    public List<TeamInvite> getTeamInvites() {
        return teamInvites;
    }

    public World getWorld() {
        return FMLServerHandler.instance().getServer().getWorld(0);
    }
}

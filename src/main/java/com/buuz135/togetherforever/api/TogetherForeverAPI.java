package com.buuz135.togetherforever.api;

import com.buuz135.togetherforever.api.data.DataManager;
import com.buuz135.togetherforever.api.data.TeamInvite;
import com.buuz135.togetherforever.api.event.TeamEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A collection of useful methods of the API
 */
public class TogetherForeverAPI {

    public static final String MOD_ID = "togetherforever";
    public static final String API_VERSION = "1";
    public static final String API_ID = MOD_ID + "api";

    private static TogetherForeverAPI ourInstance = new TogetherForeverAPI();
    private List<TeamInvite> teamInvites;

    private TogetherForeverAPI() {
        teamInvites = new ArrayList<>();
    }

    public static TogetherForeverAPI getInstance() {
        return ourInstance;
    }

    /**
     * Gets the list of teams. Changes done to this list won't be saved
     *
     * @return The list of teams
     */
    public List<ITogetherTeam> getTeams() {
        return getDataManager(getWorld()).getTeams();
    }

    /**
     * Adds a team to the API
     *
     * @param togetherTeam The team to add
     */
    public void addTeam(ITogetherTeam togetherTeam) {
        DataManager dataManager = getDataManager(getWorld());
        TeamEvent.Create create = new TeamEvent.Create(togetherTeam);
        MinecraftForge.EVENT_BUS.post(create);
        if (!create.isCanceled()) {
            dataManager.getTeams().add(create.getTogetherTeam());
            dataManager.markDirty();
        }
    }

    /**
     * Adds a player to a team using the team unique identifiers to search it.
     *
     * @param team              The team to add the player to
     * @param playerInformation The information of the player that needs to be added to the team
     */
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

    /**
     * Removes a player from a team using the team unique identifiers to search it
     *
     * @param team              The team to remove the player from
     * @param playerInformation The information of the player that needs to be removed from the team
     */
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

    /**
     * Gets the team of a player
     *
     * @param playerUUID The UUID of the player to get the team from
     * @return The team of the player, null if the player isn't in a team
     */
    @Nullable
    public ITogetherTeam getPlayerTeam(UUID playerUUID) {
        for (ITogetherTeam togetherTeam : getTeams()) {
            for (IPlayerInformation playerInformation : togetherTeam.getPlayers()) {
                if (playerInformation.getUUID().equals(playerUUID)) return togetherTeam;
            }
        }
        return null;
    }

    /**
     * Creates an invite to join a team
     *
     * @param sender         The player that sends the invite
     * @param reciever       The player that recieves the invite
     * @param announceInvite true if the player that gets the invite needs to get a notification
     * @return The created invite
     */
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

    /**
     * Adds a player to a OfflineSyncRecovery
     *
     * @param recoveryClass      The class of the OfflineSyncRecovery
     * @param iPlayerInformation The PlayerInformation that need to be stored
     * @param compound           The recovery information that needs to be stored
     */
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

    /**
     * Gets the generic DataManager
     *
     * @param world A world of to get the DataManager, it can be any world
     * @return the DataManager
     */
    public DataManager getDataManager(World world) {
        DataManager dataManager = (DataManager) world.getMapStorage().getOrLoadData(DataManager.class, DataManager.NAME);
        if (dataManager == null) {
            dataManager = new DataManager();
            world.getMapStorage().setData(DataManager.NAME, dataManager);
        }
        return dataManager;
    }

    /**
     * Gets the player entity
     *
     * @param string The name of the player
     * @return The EntityPlayerMP, null if the player is offline
     */
    @Nullable
    public EntityPlayerMP getPlayer(String string) {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(string);
    }

    /**
     * Gets the player entity
     *
     * @param uuid The UUID of the player
     * @return The EntityPlayerMP, null if the player is offline
     */
    @Nullable
    public EntityPlayerMP getPlayer(UUID uuid) {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid);
    }

    /**
     * Gets the open team invites
     *
     * @return The list of team invites
     */
    public List<TeamInvite> getTeamInvites() {
        return teamInvites;
    }

    /**
     * Gets the world overworld
     *
     * @return The overworld
     */
    public World getWorld() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
    }
}

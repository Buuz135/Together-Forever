package com.buuz135.togetherforever.api;

import java.util.List;

/**
 * Represents a Sync Action based in an Object with an OfflineSyncRecovery. THE FINAL USED CLASS NEEDS A CONSTRUCTOR WITHOUT PARAMETERS!
 *
 * @param <T> The Object type that the action is based on
 * @param <S> The IOfflineRecovery for the ISyncAction
 */
public interface ISyncAction<T, S extends IOfflineSyncRecovery> {

    /**
     * Triggers a sync of the action returning a list of PlayersInformation the were offline for the sync
     *
     * @param object       The object of the action
     * @param togetherTeam The team the will get the action triggered
     * @return A list of PlayersInformation that were offline
     */
    List<IPlayerInformation> triggerSync(T object, ITogetherTeam togetherTeam);

    /***
     * A getter for the IOfflineSyncRecovery
     * @return The class of the IOfflineSyncRecovery
     */
    Class<S> getOfflineRecovery();

    /**
     * Sync everything from one player into another
     *
     * @param toBeSynced The player that needs to be synced
     * @param teamMember The player that has the information to sync
     */
    void syncJoinPlayer(IPlayerInformation toBeSynced, IPlayerInformation teamMember);
}

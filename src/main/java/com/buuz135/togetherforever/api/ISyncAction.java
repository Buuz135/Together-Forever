package com.buuz135.togetherforever.api;

import java.util.List;

public interface ISyncAction<T extends Object, S extends IOfflineSyncRecovery> {

    /**
     * Returns a list of players that werent able to sync
     *
     * @param togetherTeam
     * @return
     */
    List<IPlayerInformation> triggerSync(T object, ITogetherTeam togetherTeam);

    Class<S> getOfflineRecovery();

}

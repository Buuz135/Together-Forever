/*
 * This file is part of Hot or Not.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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

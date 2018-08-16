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
package com.buuz135.togetherforever.api.event;

import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.ITogetherTeam;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * TeamEvents for creating a team or adding or removing a player from a team
 */
public class TeamEvent extends Event {

    private ITogetherTeam iTogetherTeam;

    public TeamEvent(ITogetherTeam iTogetherTeam) {
        this.iTogetherTeam = iTogetherTeam;
    }

    public ITogetherTeam getTogetherTeam() {
        return iTogetherTeam;
    }

    public void setTogetherTeam(ITogetherTeam iTogetherTeam) {
        this.iTogetherTeam = iTogetherTeam;
    }

    @Cancelable
    public static class Create extends TeamEvent {

        public Create(ITogetherTeam iTogetherTeam) {
            super(iTogetherTeam);
        }
    }

    @Cancelable
    public static class PlayerAdd extends TeamEvent {

        private IPlayerInformation playerInformation;

        public PlayerAdd(ITogetherTeam iTogetherTeam, IPlayerInformation playerInformation) {
            super(iTogetherTeam);
            this.playerInformation = playerInformation;
        }

        public IPlayerInformation getPlayerInformation() {
            return playerInformation;
        }

        public void setPlayerInformation(IPlayerInformation playerInformation) {
            this.playerInformation = playerInformation;
        }
    }

    @Cancelable
    public static class RemovePlayer extends TeamEvent {

        private IPlayerInformation playerInformation;

        public RemovePlayer(ITogetherTeam iTogetherTeam, IPlayerInformation playerInformation) {
            super(iTogetherTeam);
            this.playerInformation = playerInformation;
        }

        public IPlayerInformation getPlayerInformation() {
            return playerInformation;
        }

        public void setPlayerInformation(IPlayerInformation playerInformation) {
            this.playerInformation = playerInformation;
        }
    }
}

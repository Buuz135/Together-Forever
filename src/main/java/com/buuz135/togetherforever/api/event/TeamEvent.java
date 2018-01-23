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

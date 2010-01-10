package com.sawdust.engine.controller.entities;

import com.sawdust.engine.model.players.Player;

public interface SessionMember
{

    Account getAccount();

    Player getPlayer();

}

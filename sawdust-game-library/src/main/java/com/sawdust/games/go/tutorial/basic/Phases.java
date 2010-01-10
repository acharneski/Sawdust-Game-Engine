package com.sawdust.games.go.tutorial.basic;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.GameLogicException;
import com.sawdust.engine.model.TutorialPhase;
import com.sawdust.engine.model.basetypes.TutorialGameBase;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.view.game.GameFrame;
import com.sawdust.engine.view.game.Notification;
import com.sawdust.games.blackjack.BlackjackGame;
import com.sawdust.games.go.GoGame;

public class Phases implements TutorialPhase<GoGame>
{
   protected Phases()
   {
   }
   
   @Override
   public boolean allowCommand(TutorialGameBase<GoGame> game, GameCommand m)
   {
      return true;
   }
   
   @Override
   public GameFrame filterDisplay(GameFrame gwt)
   {
      return gwt;
   }
   
   @Override
   public TutorialPhase<GoGame> preCommand(TutorialGameBase<GoGame> game, GameCommand m, Participant p) throws GameLogicException
   {
      return this;
   }
   
   @Override
   public TutorialPhase<GoGame> postCommand(TutorialGameBase<GoGame> game, GameCommand m, Participant p) throws GameLogicException
   {
      return null;
   }
   
   @Override
   public void onStartPhase(TutorialGameBase<GoGame> game) throws GameException
   {
   }

protected void setGameLayout(GoGame innerGame, char[][] data)
{
    for (int i = 0; i < data.length; i++)
    {
        for (int j = 0; j < data[i].length; j++)
        {
            if (data[i][j] == 'w')
            {
                innerGame.setBoardData(i, j, 1);
            }
            else if (data[i][j] == 'b')
            {
                innerGame.setBoardData(i, j, 0);
            }
        }
    }
}
}

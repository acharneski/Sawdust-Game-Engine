package com.sawdust.games.euchre;

import java.io.Serializable;
import java.util.ArrayList;

import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.players.PlayerManager;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.view.geometry.ParametricLine;
import com.sawdust.engine.view.geometry.ParametricPosition;
import com.sawdust.engine.view.geometry.Position;
import com.sawdust.engine.view.geometry.Vector;

public class EuchreLayout implements Serializable
{
    public static final Vector OFFSET_CARD = new Vector(20, 20);
    public static final Vector OFFSET_CARD_COMMAND = new Vector(0, 100);
    public static final Vector OFFSET_COMMAND_COLUMN = new Vector(125, 0);
    public static final Vector OFFSET_COMMAND_ROW = new Vector(0, 30);
    public static final Vector OFFSET_LABEL_NAME = new Vector(0, -38);
    public static final Vector OFFSET_LABEL_TEAM = new Vector(0, -18);
    public static final Vector OFFSET_PLAYED_CARDS = new Vector(30, 0);
    public static final Vector OFFSET_PLAYER = new Vector(150, 0);
    public static final Vector OFFSET_PLAYER_CARD = new Vector(90, 0);

    public static final int POS_CARDPLAY_LABEL = -2;
    public static final int POS_GENERAL_CMDS = -4;
    public static final int POS_IN_PLAY = -1;
    public static final int POS_TEAM_LABEL = -5;
    public static final int POS_MAX = POS_TEAM_LABEL;
    public static final int POS_PLAYER_LABEL = -3;

    protected static final Position POSITION_COMMANDS = new Position(350, 220);
    protected static final Position POSITION_PLAYED_CARDS = new Position(50, 225);
    protected static final Position POSITION_PLAYER = new Position(100, 360);
    protected static final Position POSITION_PRIMARY = new Position(50, 40);

    protected Vector _handVector;
    protected ParametricPosition _mainHand = null;
    protected ArrayList<ParametricPosition> _playerHands = new ArrayList<ParametricPosition>();

    public EuchreLayout(final int playerCount, final int cardCount)
    {
        _handVector = OFFSET_CARD.scale(cardCount);
        Position currentPosition = POSITION_PRIMARY;
        for (int i = 0; i < playerCount; i++)
        {
            _playerHands.add(new ParametricLine(currentPosition, currentPosition.add(_handVector), cardCount));
            currentPosition = currentPosition.add(OFFSET_PLAYER);
        }
        _mainHand = new ParametricLine(POSITION_PLAYER, POSITION_PLAYER.add(OFFSET_PLAYER_CARD.scale(cardCount)), cardCount);
    }

    public Position getPosition(final PlayerManager playerManager, final IndexPosition key, final Player player) throws GameException
    {
        final int curveIndex = key.getCurveIndex();
        final int memberCount = playerManager.memberCount();

        if (curveIndex < POS_MAX) return null;
        if (curveIndex > memberCount) return null;

        final int playerIndex = playerManager.findPlayer(player);
        int relativePlayer = (curveIndex - playerIndex) % memberCount;
        if (relativePlayer < 0)
        {
            relativePlayer += memberCount; // Java does
        }
        // modulus
        // INCORRECTLY
        final int cardIndex = key.getCardIndex();

        if ((0 <= curveIndex) && (0 == relativePlayer)) return _mainHand.getPositionN(cardIndex).setZ(cardIndex);
        else if ((0 <= curveIndex) && (curveIndex < memberCount)) return _playerHands.get(relativePlayer - 1).getPositionN(cardIndex).setZ(cardIndex);
        else if (curveIndex == POS_IN_PLAY) return POSITION_PLAYED_CARDS.add(OFFSET_PLAYED_CARDS.scale(cardIndex)).setZ(cardIndex);
        else if (curveIndex == POS_CARDPLAY_LABEL) return _mainHand.getPositionN(cardIndex).add(OFFSET_CARD_COMMAND).setZ(cardIndex);
        else if (curveIndex == POS_PLAYER_LABEL)
        {
            if (playerIndex == cardIndex) return POSITION_PLAYER.add(OFFSET_LABEL_NAME).setZ(cardIndex);
            relativePlayer = (cardIndex - playerIndex - 1) % memberCount;
            if (relativePlayer < 0)
            {
                relativePlayer += memberCount; // Java does
            }
            // modulus
            // INCORRECTLY
            return POSITION_PRIMARY.add(OFFSET_PLAYER.scale(relativePlayer)).add(OFFSET_LABEL_NAME).setZ(relativePlayer);
        }
        else if (curveIndex == POS_TEAM_LABEL)
        {
            if (playerIndex == cardIndex) return POSITION_PLAYER.add(OFFSET_LABEL_TEAM).setZ(cardIndex);
            relativePlayer = (cardIndex - playerIndex - 1) % memberCount;
            if (relativePlayer < 0)
            {
                relativePlayer += memberCount; // Java does
            }
            // modulus
            // INCORRECTLY
            return POSITION_PRIMARY.add(OFFSET_PLAYER.scale(relativePlayer)).add(OFFSET_LABEL_TEAM).setZ(relativePlayer);
        }
        else if (curveIndex == POS_GENERAL_CMDS)
        {
            final int row = cardIndex % 5;
            final int col = (cardIndex - row) / 5;
            return POSITION_COMMANDS.add(OFFSET_COMMAND_ROW.scale(row)).add(OFFSET_COMMAND_COLUMN.scale(col));
        }
        return null;
    }
}

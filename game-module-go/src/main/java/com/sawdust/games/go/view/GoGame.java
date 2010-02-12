package com.sawdust.games.go.view;

import java.util.ArrayList;
import java.util.Collection;

import com.sawdust.engine.NotImplemented;
import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.controller.exceptions.SawdustSystemError;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.basetypes.TokenGame;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.model.state.GameCommand;
import com.sawdust.engine.model.state.GameLabel;
import com.sawdust.engine.model.state.IndexPosition;
import com.sawdust.engine.model.state.Token;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.geometry.Position;
import com.sawdust.engine.view.geometry.Vector;
import com.sawdust.games.go.PlayerScore;
import com.sawdust.games.go.controller.GoBoard;
import com.sawdust.games.go.model.BoardMove;
import com.sawdust.games.go.model.GoPlayer;
import com.sawdust.games.go.model.GoScore;
import com.sawdust.games.stop.BoardData;
import com.sawdust.games.stop.StopGame.GamePhase;
import com.sawdust.games.wordHunt.BoardToken;

public abstract class GoGame extends TokenGame
{
    public static final int NUM_ROWS = 9;
    public static final int NUMBER_OF_PLAYERS = 2;
    public static final int OFFSET_BOARD = 20;
    public static final int ROW_PLAYERTOKEN = -2;
    public static final int ROW_SCORES = -3;

    private static final Position scorePosition = new Position(525, 150);
    private static final Vector scroreOffset = new Vector(0, 35);
    private static final Position basePosition      = new Position(30, 10);
    private static final Vector   columnOffset      = new Vector(50, 0);
    private static final Position playPosition      = new Position(525, 50);
    private static final Vector   playTokenOffset   = new Vector(0, 75);
    private static final Vector   rowOffset         = new Vector(0, 50);
    
    private IndexPosition _lastPosition;
    transient GoBoard board = null;

    @Override
    public Collection<GameLabel> getLabels(Player access) throws GameException
    {
        ArrayList<GameLabel> labels = new ArrayList<GameLabel>();
        int card = 0;

        labels.add(new GameLabel("PASS_CMD", new IndexPosition(ROW_SCORES, card++), "Pass").setCommand("Pass"));
        GoScore blackScore = board.getScore(board.getPlayers()[0]);

        if (null != blackScore)
        {
            labels.add(new GameLabel("BLACK_PRISONERS_LABEL", new IndexPosition(ROW_SCORES, card++), "Black Prisoners:"));

            labels.add(new GameLabel("BLACK_PRISONERS_VALUE", new IndexPosition(ROW_SCORES, card++), Integer
                            .toString(blackScore.prisoners)));

            labels.add(new GameLabel("BLACK_TERRITORY_LABEL", new IndexPosition(ROW_SCORES, card++), "Black Territory:"));

            labels.add(new GameLabel("BLACK_TERRITORY_VALUE", new IndexPosition(ROW_SCORES, card++), Integer
                            .toString(blackScore.territory)));
        }

        GoScore whiteScore = board.getScore(board.getPlayers()[1]);

        if (null != whiteScore)
        {
            labels.add(new GameLabel("WHITE_PRISONERS_LABEL", new IndexPosition(ROW_SCORES, card++), "White Prisoners:"));

            labels.add(new GameLabel("WHITE_PRISONERS_VALUE", new IndexPosition(ROW_SCORES, card++), Integer
                            .toString(whiteScore.prisoners)));

            labels.add(new GameLabel("WHITE_TERRITORY_LABEL", new IndexPosition(ROW_SCORES, card++), "White Territory:"));

            labels.add(new GameLabel("WHITE_TERRITORY_VALUE", new IndexPosition(ROW_SCORES, card++), Integer
                            .toString(whiteScore.territory)));
        }
        return labels;
    }

    @Override
    public Position getPosition(IndexPosition key, Player access) throws GameException
    {
        if (null == key) return null;
        
        if ((key.getCurveIndex() >= 0) && (key.getCurveIndex() < NUM_ROWS))
        {
            return basePosition.add(rowOffset.scale(key.getCurveIndex())).add(columnOffset.scale(key.getCardIndex()));
        }
        
        final int offsetIndex = key.getCurveIndex() - OFFSET_BOARD;
        if ((offsetIndex >= 0) && (offsetIndex < NUM_ROWS))
        {
           final Position add = basePosition.add(rowOffset.scale(offsetIndex)).add(columnOffset.scale(key.getCardIndex()));
           add.setZ(-1);
           return add;
        }

        if (key.getCurveIndex() == ROW_SCORES) 
        {
            return scorePosition.add(scroreOffset.scale(key.getCardIndex()));
        }
        
        if (key.getCurveIndex() == ROW_PLAYERTOKEN)
        {
            return playPosition.add(playTokenOffset.scale(key.getCardIndex()));
        }

        return null;
    }

    @Override
    public ArrayList<Token> getTokens()
    {
        ArrayList<Token> returnValue = new ArrayList<Token>();
        int cardIdCounter = 0;
        IndexPosition lastPosition = _lastPosition;
        if (null != lastPosition)
        {
            BoardToken token = new BoardToken(0X1001, "GO1", "GO:HIGHLIGHT", null, null, false, lastPosition);
            token.getPosition().setZ(2);
            token.setText("Last-moved piece");
            returnValue.add(token);
        }

        cardIdCounter = 0x3000;
        for (int i = 0; i < NUM_ROWS; i++)
        {
            for (int j = 0; j < NUM_ROWS; j++)
            {
                IndexPosition position = new IndexPosition(i + OFFSET_BOARD, j, 0);
                BoardToken token = new BoardToken(++cardIdCounter, "GO1", "GO:BOARD", null, null, false, position);
                token.getPosition().setZ(1);
                token.setText("Board Tile");
                returnValue.add(token);

                cardIdCounter++;
                // BoardData playerIdx = getBoardData(i, j);
                // if (null != playerIdx && -1 != playerIdx.value)
                // {
                // position = new IndexPosition(i, j, 3);
                // Participant player = _mplayerManager.getPlayerManager().playerName(playerIdx.value);
                // String tokenType = getPlayerTokenType(playerIdx.value);
                // token = new BoardToken(cardIdCounter, "GO1", tokenType, player, null, false, position);
                // token.getPosition().setZ(3);
                // // token.setText(tokenType + " (placed @ " +
                // // this.versionNumber + ")");
                // returnValue.add(token);
                // }
            }
        }

        cardIdCounter = 0x4000;
        ArrayList<IndexPosition> openPos = new ArrayList<IndexPosition>();
        for (int i = 0; i < NUM_ROWS; i++)
        {
            for (int j = 0; j < NUM_ROWS; j++)
            {
                final IndexPosition position = new IndexPosition(i, j);
                // BoardData boardData = getBoardData(i, j);
                // if (null == boardData || -1 == boardData.value)
                // {
                // openPos.add(position);
                // }
            }
        }
        for (final Participant p : new Participant[]
        { new Participant("1") })
        {
            final IndexPosition position = new IndexPosition(ROW_PLAYERTOKEN, 0, 1);
            final String art = p.getId().equals("1") ? "GO:BLACK" : "GO:WHITE";
            final BoardToken token = new BoardToken(++cardIdCounter, "GO1", art, p, "", true, position);
            token.getPosition().setZ(4);
            token.setText("Place this piece on the board to move");
            for (final IndexPosition pos : openPos)
            {
                final String cmd = String.format("Move %d, %d", pos.getCurveIndex(), pos.getCardIndex());
                token.getMoveCommands().put(pos, cmd);
            }
            returnValue.add(token);
        }
        return returnValue;
    }

    protected GoGame()
    {
        super();
    }

    public GoGame(GameConfig config)
    {
        super(config);
    }

    @Override
    public void doReset()
    {
        board = new GoBoard();
    }

    @Override
    public abstract GameSession getSession();

    @Override
    public void doStart() throws GameException
    {
        doReset();
    }

    @Override
    public void doUpdate() throws GameException
    {
    }

    @Override
    public Participant getCurrentPlayer()
    {
        GoPlayer currentPlayer = (GoPlayer) board.getCurrentPlayer();
        return new Participant(currentPlayer.getName());
    }

    @Override
    public String getDisplayName(Participant userId)
    {
        return userId.getId();
    }

    @Override
    public GameType<?> getGameType()
    {
        return new GoGameType();
    }

    @Override
    public ArrayList<GameCommand> getMoves(Participant access) throws GameException
    {
        if(null == board) this.doStart(); //HACK
        ArrayList<GameCommand> arrayList = new ArrayList<GameCommand>();
        assert (access.getId().equals(board.getCurrentPlayer()));
        int moveId = 0;
        for (BoardMove move : board.getMoves(board.getCurrentPlayer()))
        {
            final int thisMoveId = moveId++;
            arrayList.add(new GameCommand()
            {

                @Override
                public String getHelpText()
                {
                    return "Help not availible";
                }

                @Override
                public String getCommandText()
                {
                    return Integer.toString(thisMoveId);
                }

                @Override
                public boolean doCommand(Participant access, String commandText) throws GameException
                {
                    assert (access.getId().equals(board.getCurrentPlayer()));
                    return false;
                }
            });
        }
        return arrayList;
    }

    @Override
    public int getUpdateTime()
    {
        return 90;
    }

}

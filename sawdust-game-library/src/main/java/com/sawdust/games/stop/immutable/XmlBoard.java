package com.sawdust.games.stop.immutable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement
public class XmlBoard
{
    @XmlValue
    String text;

    @XmlAttribute
    int rows;

    @XmlAttribute
    int cols;

    static final Player NON_INITIALIZED = null;

    public XmlBoard()
    {}
    
    public XmlBoard(Board board)
    {
        rows = board.rows;
        cols = board.cols;
        Player[][] matrix = extractMatrix(board);
         text = matrixToString(matrix);
    }

    public Player[][] getMatrix()
    {
        Player[][] matrix = null;
        String[] lines = text.split("\n");
        int w = countNonEmpty(lines);
        matrix = new Player[lines.length][];
        assert(rows == w);
        int y = 0;
        for(String line : lines)
        {
            if(line.isEmpty()) continue;
            int x = 0;
            String[] tokens = line.split(" ");
            int h = countNonEmpty(tokens);
            matrix[y] = new Player[h];
            assert(cols == h);
            for(String token : tokens)
            {
                if(token.isEmpty()) continue;
                matrix[y][x++] = Player.parse(token);
            }
            y++;
        }
        return matrix;
    }

    private int countNonEmpty(String[] lines)
    {
        int r = 0;
        for(String s : lines) if(!s.isEmpty()) r++;
        return r;
    }

    private Player[][] extractMatrix(Board board)
    {
        Player matrix[][] = new Player[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                matrix[i][j] = NON_INITIALIZED;
        for (Island isl : board.open)
            for (TokenPosition tok : isl.tokens)
                matrix[tok.x][tok.y] = isl.player;
        for (Island isl : board.islands)
            for (TokenPosition tok : isl.tokens)
                matrix[tok.x][tok.y] = isl.player;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                assert (matrix[i][j] != NON_INITIALIZED);
        return matrix;
    }

    private String matrixToString(Player[][] matrix)
    {
        StringBuffer sb = new StringBuffer("\n");
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                sb.append(matrix[i][j]);
                sb.append(" ");
            }
            sb.append("\n");
        }
        String txtMatrix = sb.toString();
        return txtMatrix;
    }

}

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

    static final player NON_INITIALIZED = null;

    public XmlBoard()
    {}
    
    public XmlBoard(Board board)
    {
        rows = board.rows;
        cols = board.cols;
        player[][] matrix = extractMatrix(board);
         text = matrixToString(matrix);
    }

    public player[][] getMatrix()
    {
        player[][] matrix = null;
        String[] lines = text.split("\n");
        int w = countNonEmpty(lines);
        matrix = new player[lines.length][];
        assert(rows == w);
        int y = 0;
        for(String line : lines)
        {
            if(line.isEmpty()) continue;
            int x = 0;
            String[] tokens = line.split(" ");
            int h = countNonEmpty(tokens);
            matrix[y] = new player[h];
            assert(cols == h);
            for(String token : tokens)
            {
                if(token.isEmpty()) continue;
                matrix[y][x++] = player.parse(token);
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

    private player[][] extractMatrix(Board board)
    {
        player matrix[][] = new player[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                matrix[i][j] = NON_INITIALIZED;
        for (island isl : board.open)
            for (tokenPosition tok : isl.tokens)
                matrix[tok.x][tok.y] = isl.player;
        for (island isl : board.islands)
            for (tokenPosition tok : isl.tokens)
                matrix[tok.x][tok.y] = isl.player;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                assert (matrix[i][j] != NON_INITIALIZED);
        return matrix;
    }

    private String matrixToString(player[][] matrix)
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

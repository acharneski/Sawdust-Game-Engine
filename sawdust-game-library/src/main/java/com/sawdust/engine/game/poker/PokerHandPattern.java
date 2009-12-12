package com.sawdust.engine.game.poker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import com.sawdust.engine.common.cards.Card;
import com.sawdust.engine.game.state.IndexCard;
import com.sawdust.engine.service.debug.GameLogicException;

public enum PokerHandPattern
{
    /*
     * R-P-0052 Individual cards are ranked A (high), K, Q, J, 10, 9, 8, 7, 6, 5, 4, 3, R-P-0053 2 (low). Aces can appear low when part of
     * an A-2-3-4-5 straight or straight R-P-0054 flush. Individual card ranks are used to compare hands that contain no R-P-0055 pairs or
     * other special combinations, or to compare the kickers of otherwise R-P-0056 equal hands. The ace plays low only in ace-to-five and
     * ace-to-six lowball R-P-0057 games, and plays high only in deuce-to-seven lowball. R-P-0058 Suits have no value. The suits of the
     * cards are mainly used in determining R-P-0059 whether a hand fits a certain category (specifically the flush and straight R-P-0060
     * flush hands). In most variants, if two players have hands that are identical R-P-0061 except for suit, then they are tied and split
     * the pot (so 3♠ 4♠ 5♠ R-P-0062 6♠ 7♠ does not beat 3♦ 4♦ 5♦ 6♦ 7♦). Sometimes a ranking R-P-0063 called high card by suit is used for
     * randomly selecting a player to deal. R-P-0064 Low card by suit usually determines the bring-in bettor in stud games. R-P-0065
     * R-P-0066 A hand always consists of five cards. In games where more than five cards R-P-0067 are available to each player, the best
     * five-card combination of those R-P-0068 cards plays. R-P-0069 Hands are ranked first by category, then by individual card ranks: even
     * R-P-0070 the lowest qualifying hand in a certain category defeats all hands in R-P-0071 all lower categories. The smallest two pair
     * hand (2♦ 2♠ 3♦ 3♣ R-P-0072 4♠), for example, defeats all hands with just one pair or high card. R-P-0073 Only between two hands in
     * the same category are card ranks used to break R-P-0074 ties.
     */

    Flush
    {
        /*
         * R-P-0133 Flush R-P-0134 R-P-0135 A flush is a poker hand such as Q♣ 10♣ 7♣ 6♣ 4♣, which contains R-P-0136 five cards of the same
         * suit, not in rank sequence. It ranks above a straight R-P-0137 and below a full house. Two flushes are compared as if they were
         * high R-P-0138 card hands; the highest ranking card of each is compared to determine R-P-0139 the winner. If both hands have the
         * same highest card, then the second-highest R-P-0140 ranking card is compared, and so on until a difference is found. If the
         * R-P-0141 two flushes contain the same five ranks of cards, they are tied – suits R-P-0142 are not used to differentiate them.
         * Flushes are described by their highest R-P-0143 card, as in "queen-high flush" to describe Q♦ 9♦ 7♦ 4♦ 3♦. If R-P-0144 the rank
         * of the second card is important, it can also be included: K♠ R-P-0145 10♠ 5♠ 3♠ 2♠ is a "king-ten-high flush" or just a
         * "king-ten flush", R-P-0146 while K♥ Q♥ 9♥ 5♥ 4♥ is a "king-queen-high flush". R-P-0147 There are 5,148 possible flushes, of which
         * 40 are also straight flushes; R-P-0148 the probability of being dealt a flush in a five-card hand is
         */
        @Override
        ArrayList<PokerHand> Find(final ArrayList<IndexCard> cards) throws GameLogicException
        {
            final HashMap<String, Integer> list = new HashMap<String, Integer>();
            for (final IndexCard primary : cards)
            {
                int prev = 0;
                final String key = primary.getCard().getSuit().fullString();
                if (list.containsKey(key))
                {
                    prev = list.get(key);
                }
                list.put(key, prev + 1);
            }
            final ArrayList<PokerHand> returnValue = new ArrayList<PokerHand>();
            for (final Entry<String, Integer> e : list.entrySet())
            {
                if (((int) e.getValue()) >= 5)
                {
                    final PokerHand hand = new PokerHand(String.format("Flush of %s", e.getKey()));
                    hand.setOdds(649740);
                    for (final IndexCard primary : cards)
                    {
                        hand.add(primary.getCard());
                    }
                    hand.add(ORDER_FLUSH);
                    if (e.getKey().equals("Spades"))
                    {
                        hand.add(1);
                    }
                    else if (e.getKey().equals("Diamonds"))
                    {
                        hand.add(2);
                    }
                    else if (e.getKey().equals("Clubs"))
                    {
                        hand.add(3);
                    }
                    else if (e.getKey().equals("Hearts"))
                    {
                        hand.add(4);
                    }
                    else throw new GameLogicException("Flush: " + e.getKey());
                    returnValue.add(hand);
                }
            }
            return returnValue;
        }
    },
    MultiPair
    {
        /*
         * R-P-0115 Full house R-P-0116 R-P-0117 A full house, also known as a full boat, is a hand such as 3♣ 3♠ 3♦ R-P-0118 6♣ 6♥, which
         * contains three matching cards of one rank, and two matching R-P-0119 cards of another rank. It ranks below a four of a kind and
         * above a flush. R-P-0120 Between two full houses, the one with the higher ranking set of three R-P-0121 wins, so 7♠ 7♥ 7♦ 4♠ 4♣
         * defeats 6♠ 6♥ 6♦ A♠ A♣. If R-P-0122 two hands have the same set of three (possible in wild card and community R-P-0123 card
         * games), the hand with the higher pair wins, so 5♥ 5♦ 5♠ Q♥ R-P-0124 Q♣ defeats 5♣ 5♦ 5♠ J♠ J♦. Full houses are described as
         * "Three R-P-0125 full of Pair" or occasionally "Three over Pair"; Q♣ Q♦ Q♠ 9♥ 9♣ R-P-0126 could be described as
         * "Queens over nines", "Queens full of nines", or R-P-0127 simply "Queens full". However, "Queens over nines" is more commonly used
         * R-P-0128 to describe the hand containing two pairs, one pair of queens and one R-P-0129 pair of nines, as in Q♠ Q♥ 9♣ 9♠ J♦.
         * R-P-0130 There are 3,744 possible full houses; the probability of being dealt one R-P-0131 in a five-card hand is
         */
        @Override
        ArrayList<PokerHand> Find(final ArrayList<IndexCard> cards) throws GameLogicException
        {
            final ArrayList<PokerHand> returnValue = new ArrayList<PokerHand>();
            final ArrayList<PokerHand> dups = Pair.Find(cards);
            for (final PokerHand pair : dups)
            {
                if (!pair.getName().startsWith("Pair of"))
                {
                    continue;
                }
                for (final PokerHand three : dups)
                {
                    if (!three.getName().startsWith("Three of a kind"))
                    {
                        continue;
                    }
                    if (0 == three.popItem().compareTo(pair.popItem()))
                    {
                        continue;
                    }
                    final PokerHand hand = new PokerHand(String.format("Full House: %s and %s", three.getName(), pair.getName()));
                    hand.setOdds(693);
                    hand.add(ORDER_FULL_HOUSE);
                    hand.add(three.getCards());
                    hand.add(pair.getCards());
                    hand.addAll(three);
                    hand.addAll(pair);
                    returnValue.add(hand);
                }
                for (final PokerHand anotherPair : dups)
                {
                    if (!anotherPair.getName().startsWith("Pair of"))
                    {
                        continue;
                    }
                    if (0 == anotherPair.popItem().compareTo(pair.popItem()))
                    {
                        continue;
                    }
                    final PokerHand hand = new PokerHand(String.format("Two Pair: %s and %s", anotherPair.getName(), pair.getName()));
                    hand.add(ORDER_TWO_PAIR);
                    hand.setOdds(20);
                    hand.addAll(anotherPair);
                    hand.addAll(pair);
                    hand.add(anotherPair.getCards());
                    hand.add(pair.getCards());
                    returnValue.add(hand);
                }
            }
            return returnValue;
        }
    },
    Pair
    {
        /*
         * R-P-0102 Four of a kind R-P-0103 R-P-0104 Four of a kind, also known as quads, is a poker hand such as 9♣ 9♠ R-P-0105 9♦ 9♥ J♥,
         * which contains four cards of one rank, and an unmatched R-P-0106 card of another rank. It ranks above a full house and below a
         * straight R-P-0107 flush. Higher ranking quads defeat lower ranking ones. In community-card R-P-0108 games (such as Texas Hold
         * 'em) or games with wildcards it is possible R-P-0109 for two or more players to obtain the same quad; in this instance, the
         * R-P-0110 unmatched card acts as a kicker, so 7♣ 7♠ 7♦ 7♥ J♥ defeats 7♣ R-P-0111 7♠ 7♦ 7♥ 10♣.R-P-0112 There are 624 possible
         * hands including four of a kind; the probability R-P-0187 Two pair R-P-0188 R-P-0189 A poker hand such as J♥ J♣ 4♣ 4♠ 9♥, which
         * contains two cards R-P-0190 of the same rank, plus two cards of another rank (that match each other R-P-0191 but not the first
         * pair), plus one unmatched card, is called two pair. R-P-0192 It ranks above one pair and below three of a kind. To rank two hands
         * both R-P-0193 containing two pair, the higher ranking pair of each is first compared, R-P-0194 and the higher pair wins (so 10♠
         * 10♣ 8♥ 8♣ 4♠ defeats 8♥ 8♣ R-P-0195 4♠ 4♣ 10♠). If both hands have the same "top pair", then the second R-P-0196 pair of each is
         * compared, such that 10♠ 10♣ 8♥ 8♣ 4♠ defeats R-P-0197 10♠ 10♣ 4♠ 4♥ 8♥. Finally, if both hands have the same two pairs, R-P-0198
         * the kicker determines the winner: 10♠ 10♣ 8♥ 8♣ 4♠ loses to R-P-0199 10♠ 10♣ 8♥ 8♣ A♦. Two pair are described by the higher pair
         * R-P-0200 first, followed by the lower pair if necessary; K♣ K♦ 9♠ 9♥ 5♥ R-P-0201 could be described as "Kings over nines",
         * "Kings and nines" or simply R-P-0202 "Kings up" if the nines are not important. R-P-0203 There are 123,552 possible two pair
         * hands that are not also full houses; R-P-0204 the probability of being dealt one in a five-card hand is R-P-0205 R-P-0206 One
         * pair R-P-0207 R-P-0208 One pair is a poker hand such as 4♥ 4♠ K♠ 10♦ 5♠, which contains R-P-0209 two cards of the same rank, plus
         * three other unmatched cards. It ranks R-P-0210 above any high card hand, but below all other poker hands. Higher ranking R-P-0211
         * pairs defeat lower ranking pairs; if two hands have the same pair, the R-P-0212 non-paired cards (the kickers) are compared in
         * descending order to determine R-P-0213 the winner. R-P-0214 There are 1,098,240 possible one pair hands; the probability of being
         * R-P-0215 dealt one in a five-card hand is R-P-0216 R-P-0217 High card R-P-0218 R-P-0219 A high-card or no-pair hand is a poker
         * hand such as K♥ J♣ 8♣ 7♦ R-P-0220 3♠, in which no two cards have the same rank, the five cards are not R-P-0221 in sequence, and
         * the five cards are not all the same suit. It is also R-P-0222 referred to as "no pair", as well as "nothing", "garbage," and
         * various R-P-0223 other derogatory terms. High card ranks below all other poker hands; two R-P-0224 such hands are ranked by
         * comparing the highest ranking card. If those R-P-0225 are equal, then the next highest ranking card from each hand is compared,
         * R-P-0226 and so on until a difference is found. High card hands are described by R-P-0227 the one or two highest cards in the
         * hand, such as "king high", "ace-queen R-P-0228 high", or by as many cards as are necessary to break a tie. R-P-0229 The lowest
         * possible high card is seven-high (such as 7♠ 5♣ 4♦ 3♦ R-P-0230 2♣), because a hand such as 6♦ 5♣ 4♠ 3♦ 2♥ would be a straight.
         * R-P-0231 R-P-0232 Of the 2,598,960 possible hands, 1,302,540 do not contain any pairs and R-P-0233 are neither straights nor
         * flushes.
         */
        @Override
        ArrayList<PokerHand> Find(final ArrayList<IndexCard> cards)
        {
            final ArrayList<PokerHand> returnValue = new ArrayList<PokerHand>();
            for (final IndexCard primary : cards)
            {
                int totalCards = 0;
                final ArrayList<Card> set = new ArrayList<Card>();
                for (final IndexCard a : cards)
                {
                    if (a.getCard().getRank() == primary.getCard().getRank())
                    {
                        set.add(a.getCard());
                        totalCards++;
                    }
                }
                if (1 == totalCards)
                {
                    final PokerHand hand = new PokerHand(String.format("High Card: %s", primary.getCard().getRank().name()));
                    hand.setOdds(1);
                    hand.add(ORDER_HIGH_CARD);
                    hand.add(set);
                    hand.add(primary.getCard().getRank().getOrder());
                    returnValue.add(hand);
                }
                else if (2 == totalCards)
                {
                    final PokerHand hand = new PokerHand(String.format("Pair of %s", primary.getCard().getRank().name()));
                    hand.setOdds(3);
                    hand.add(ORDER_PAIR);
                    hand.add(set);
                    hand.add(primary.getCard().getRank().getOrder());
                    returnValue.add(hand);
                }
                else if (3 == totalCards)
                {
                    final PokerHand hand = new PokerHand(String.format("Three of a kind: %s", primary.getCard().getRank().name()));
                    hand.add(ORDER_THREE_OF_KIND);
                    hand.setOdds(46);
                    hand.add(set);
                    hand.add(primary.getCard().getRank().getOrder());
                    returnValue.add(hand);
                }
                else if (4 == totalCards)
                {
                    final PokerHand hand = new PokerHand(String.format("Four of a kind: %s", primary.getCard().getRank().name()));
                    hand.setOdds(4164);
                    hand.add(ORDER_FOUR_OF_KIND);
                    hand.add(set);
                    hand.add(primary.getCard().getRank().getOrder());
                    returnValue.add(hand);
                }
            }
            return returnValue;
        }
    },
    Straight
    {
        /*
         * R-P-0150 Straight R-P-0151 R-P-0152 A straight is a poker hand such as Q♣ J♠ 10♠ 9♥ 8♥, which contains R-P-0153 five cards of
         * sequential rank but in more than one suit. It ranks above R-P-0154 three of a kind and below a flush. Two straights are ranked by
         * comparing R-P-0155 the highest card of each. Two straights with the same high card are of R-P-0156 equal value, suits are not
         * used to separate them. Straights are described R-P-0157 by their highest card, as in "ten-high straight" or "straight to the ten"
         * R-P-0158 for 10♣ 9♦ 8♥ 7♣ 6♠. R-P-0159 A hand such as A♣ K♣ Q♦ J♠ 10♠ is an ace-high straight (also R-P-0160 known as Broadway),
         * and ranks above a king-high straight such as K♥ R-P-0161 Q♠ J♥ 10♥ 9♣. The ace may also be played as a low card in a five-high
         * R-P-0162 straight such as 5♠ 4♦ 3♦ 2♠ A♥, which is colloquially known R-P-0163 as a wheel. The ace may not "wrap around", or play
         * both high and low: R-P-0164 3♣ 2♦ A♥ K♠ Q♣ is not a straight, just an ace-high high card. R-P-0165 R-P-0166 There are 10,240
         * possible straights, of which 40 are also straight flushes; R-P-0167 the probability of being dealt a straight in a five-card hand
         * is .
         */
        @Override
        ArrayList<PokerHand> Find(final ArrayList<IndexCard> cards)
        {
            final HashMap<Integer, Card> map = new HashMap<Integer, Card>();
            final TreeSet<Integer> ranks = new TreeSet<Integer>();
            for (final IndexCard primary : cards)
            {
                final Card card = primary.getCard();
                final int order = card.getRank().getOrder();
                map.put(order, card);
                ranks.add(order);
            }
            int lastRank = -10;
            int numberInRow = 1;
            int highestRankWithStraight = 0;
            for (final Integer rank : ranks)
            {
                if (lastRank + 1 == rank)
                {
                    numberInRow++;
                    if (numberInRow >= 5)
                    {
                        highestRankWithStraight = rank;
                    }
                }
                else
                {
                    numberInRow = 1;
                }
                lastRank = rank;
            }
            final ArrayList<PokerHand> returnValue = new ArrayList<PokerHand>();
            if (0 < highestRankWithStraight)
            {
                final PokerHand hand = new PokerHand(String.format("Straight To %s", map.get(highestRankWithStraight).getRank().name()));
                hand.setOdds(253);
                for (final IndexCard primary : cards)
                {
                    hand.add(primary.getCard());
                }
                hand.add(ORDER_STRAIGHT);
                hand.add(highestRankWithStraight);
                returnValue.add(hand);
            }
            return returnValue;
        }
    },
    StraightFlush
    {
        /*
         * R-P-0090 A straight flush is a poker hand which contains five cards in sequence, R-P-0091 all of the same suit, such as Q♣ J♣ 10♣
         * 9♣ 8♣. Two such hands R-P-0092 are compared by their highest card; since suits have no relative value, R-P-0093 two otherwise
         * identical straight flushes tie (so 10♣ 9♣ 8♣ 7♣ R-P-0094 6♣ ties with 10♥ 9♥ 8♥ 7♥ 6♥). Aces can play low in straights R-P-0095
         * and straight flushes: 5♦ 4♦ 3♦ 2♦ A♦ is a 5-high straight flush, R-P-0096 also known as a "steel wheel".[1][2] An ace-high
         * straight flush such as R-P-0097 A♠ K♠ Q♠ J♠ 10♠ is known as a royal flush, and is the highest R-P-0098 ranking standard poker
         * hand. R-P-0099 There are 40 possible straight flushes, including the four royal flushes.
         */
        @Override
        ArrayList<PokerHand> Find(final ArrayList<IndexCard> cards)
        {
            final HashMap<Integer, Card> map = new HashMap<Integer, Card>();
            final TreeSet<Integer> ranks = new TreeSet<Integer>();
            for (final IndexCard primary : cards)
            {
                final Card card = primary.getCard();
                final int order = (card.getSuit().index() * 20) + card.getRank().getOrder();
                map.put(order, card);
                ranks.add(order);
            }
            int lastRank = -10;
            int numberInRow = 1;
            int highestRankWithStraight = 0;
            for (final Integer rank : ranks)
            {
                if (lastRank + 1 == rank)
                {
                    if (++numberInRow >= 5)
                    {
                        highestRankWithStraight = rank;
                    }
                }
                else
                {
                    numberInRow = 1;
                }
                lastRank = rank;
            }
            final ArrayList<PokerHand> returnValue = new ArrayList<PokerHand>();
            if (0 < highestRankWithStraight)
            {
                final PokerHand hand = new PokerHand(String.format("Straight Flush To %s", map.get(highestRankWithStraight)));
                hand.add(ORDER_STRAIGHT_FLUSH);
                hand.setOdds(72193);
                hand.add(highestRankWithStraight);
                for (final IndexCard primary : cards)
                {
                    hand.add(primary.getCard());
                }
                returnValue.add(hand);
            }
            return returnValue;
        }
    };

    private static final int ORDER_FLUSH = 6;
    private static final int ORDER_FOUR_OF_KIND = 8;
    private static final int ORDER_FULL_HOUSE = 7;
    /*
     * R-P-0076 Standard ranking R-P-0085 Straight flush R-P-0102 Four of a kind R-P-0115 Full house R-P-0133 Flush R-P-0150 Straight
     * R-P-0169 Three of a kind R-P-0187 Two pair R-P-0206 One pair R-P-0217 High card
     */
    private static final int ORDER_HIGH_CARD = 1;
    private static final int ORDER_PAIR = 2;
    private static final int ORDER_STRAIGHT = 5;
    private static final int ORDER_STRAIGHT_FLUSH = 9;
    private static final int ORDER_THREE_OF_KIND = 4;
    private static final int ORDER_TWO_PAIR = 3;

    public static ArrayList<PokerHand> FindAll(final ArrayList<IndexCard> cards) throws GameLogicException
    {
        final ArrayList<PokerHand> returnValue = new ArrayList<PokerHand>();
        for (final PokerHandPattern hand : PokerHandPattern.values())
        {
            returnValue.addAll(hand.Find(cards));
        }
        return returnValue;
    }

    public static PokerHand FindHighest(final ArrayList<IndexCard> cards) throws GameLogicException
    {
        final ArrayList<PokerHand> allHands = FindAll(cards);
        return PokerHand.GetHighest(allHands);
    }

    abstract ArrayList<PokerHand> Find(ArrayList<IndexCard> cards) throws GameLogicException;
}

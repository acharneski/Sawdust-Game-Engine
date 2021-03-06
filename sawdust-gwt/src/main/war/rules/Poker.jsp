<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error.jsp"%>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser" scope="request" />
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

<wc:pageTemplate title="Euchre Rules">

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>
This page outlines the "5 Card Draw" Poker specification used to implement the game. It was copied from
the Wikipedia entries 
<a href="http://en.wikipedia.org/wiki/Poker">Poker</a>, 
<a href="http://en.wikipedia.org/wiki/Five-card_draw">Five-card draw</a>, and 
<a href="http://en.wikipedia.org/wiki/List_of_poker_hands">List of poker hands</a>
 on Septempber 4th, 2009. It is
annotated where implementation diverges from the published wikipedia specification.
</B></FONT></FONT>

<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0001 In casino play the first betting round begins with the player to the left</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0002 of the big blind, and subsequent rounds begin with the player to the dealer's</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0003 left. Home games typically use an ante; the first betting round begins</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0004 with the player to the dealer's left, and the second round begins with</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0005 the player who opened the first round.</FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>In this version, the running bet is started at the level of the game ante and betting </B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>goes from Player 1 onward. Each player can raise, see the current bet level, or fold.</B></FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0006 Play begins with each player being dealt five cards, one at a time, all</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0007 face down. The remaining deck is placed aside, often protected by placing</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0008 a chip or other marker on it. Players pick up the cards and hold them</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0009 in their hands, being careful to keep them concealed from the other players,</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0010 then a round of betting occurs.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0011 If more than one player remains after the first round, the &quot;draw&quot; phase</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0012 begins. Each player specifies how many of their cards they wish to replace</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0013 and discards them. The deck is retrieved, and each player is dealt in </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0014 turn from the deck the same number of cards they discarded so that each</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0015 player again has five cards.</FONT></FONT> 
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0016 A second &quot;after the draw&quot; betting round occurs beginning with the player</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0017 to the dealer's left or else beginning with the player who opened the</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0018 first round (the latter is common when antes are used instead of blinds).</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0019 This is followed by a showdown if more than one player remains, in which</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0020 the player with the best hand wins the pot.</FONT></FONT>
<BR> 
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>There is no second-round betting before the showdown</B></FONT></FONT> 
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0021 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0022 <B>House rules</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0023 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0024 A common &quot;house rule&quot; in some places is that a player may not replace </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0025 more than three cards, unless they draw four cards while keeping an ace</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0026 (or wild card). This rule is only needed for low-stakes social games where</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0027 many players will stay for the draw, and will help avoid depletion of </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0028 the deck. In more serious games such as those played in casinos it is </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0029 unnecessary and generally not used. A rule that is used by many casinos</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0030 is that a player is not allowed to draw five consecutive cards from the</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0031 deck. In this case, if a player wishes to replace all five of their cards,</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0032 that player is given four of them in turn, the other players are given</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0033 their draws, and then the dealer returns to that player to give the fifth</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0034 replacement card; if no other player draws it is necessary to deal a burn</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0035 card first.</FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>Players may draw any number of cards</B></FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0036 Another common house rule is that the bottom card of the deck is never</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0037 given as a replacement, to avoid the possibility of someone who might </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0038 have seen it during the deal using that information. If the deck is depleted</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0039 during the draw before all players have received their replacements, the</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0040 last players can receive cards chosen randomly from among those discarded</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0041 by previous players. For example, if the last player to draw wants three</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0042 replacements but there are only two cards remaining in the deck, the dealer</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0043 gives the player the one top card he can give, then shuffles together </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0044 the bottom card of the deck, the burn card, and the earlier players' discards</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0045 (but not the player's own discards), and finally deals two more replacements</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0046 to the last player.</FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>This is not applicable</B></FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0047</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0048 <B>Hand ranking rules</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0049 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0050 The following general rules apply to evaluating poker hands, whatever </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0051 set of hand values are used.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0052 Individual cards are ranked A (high), K, Q, J, 10, 9, 8, 7, 6, 5, 4, 3,</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0053 2 (low). Aces can appear low when part of an A-2-3-4-5 straight or straight</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0054 flush. Individual card ranks are used to compare hands that contain no</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0055 pairs or other special combinations, or to compare the kickers of otherwise</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0056 equal hands. The ace plays low only in ace-to-five and ace-to-six lowball</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0057 games, and plays high only in deuce-to-seven lowball.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0058 Suits have no value. The suits of the cards are mainly used in determining</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0059 whether a hand fits a certain category (specifically the flush and straight</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0060 flush hands). In most variants, if two players have hands that are identical</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0061 except for suit, then they are tied and split the pot (so 3&spades; 4&spades; 5&spades;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0062 6&spades; 7&spades; does not beat 3&diams; 4&diams; 5&diams; 6&diams; 7&diams;). Sometimes a ranking</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0063 called high card by suit is used for randomly selecting a player to deal.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0064 Low card by suit usually determines the bring-in bettor in stud games.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0065 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0066 A hand always consists of five cards. In games where more than five cards</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0067 are available to each player, the best five-card combination of those </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0068 cards plays.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0069 Hands are ranked first by category, then by individual card ranks: even</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0070 the lowest qualifying hand in a certain category defeats all hands in </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0071 all lower categories. The smallest two pair hand (2&diams; 2&spades; 3&diams; 3&clubs; </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0072 4&spades;), for example, defeats all hands with just one pair or high card.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0073 Only between two hands in the same category are card ranks used to break</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0074 ties.</FONT></FONT> 
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0075 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0076 <B>Standard ranking</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0077 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0078 For ease of recognition, poker hands are usually presented with the most</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0079 important cards on the left, with cards descending in importance towards</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0080 the right. However, a poker hand still has the same value however it is</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0081 arranged. There are 311,875,200 ways (permutations) of being dealt five</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0082 cards from a 52 card deck,[Note 1] but since the order of cards does not</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0083 matter there are  possible distinct hands (combinations).</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0084 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0085 <B>Straight flush</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0086 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0089 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0090 A straight flush is a poker hand which contains five cards in sequence,</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0091 all of the same suit, such as Q&clubs; J&clubs; 10&clubs; 9&clubs; 8&clubs;. Two such hands</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0092 are compared by their highest card; since suits have no relative value,</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0093 two otherwise identical straight flushes tie (so 10&clubs; 9&clubs; 8&clubs; 7&clubs; </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0094 6&clubs; ties with 10&hearts; 9&hearts; 8&hearts; 7&hearts; 6&hearts;). Aces can play low in straights</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0095 and straight flushes: 5&diams; 4&diams; 3&diams; 2&diams; A&diams; is a 5-high straight flush,</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0096 also known as a &quot;steel wheel&quot;.[1][2] An ace-high straight flush such as</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0097 A&spades; K&spades; Q&spades; J&spades; 10&spades; is known as a royal flush, and is the highest</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0098 ranking standard poker hand.</FONT></FONT> 
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0099 There are 40 possible straight flushes, including the four royal flushes.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0100 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0101 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0102 <B>Four of a kind</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0103 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0104 Four of a kind, also known as quads, is a poker hand such as 9&clubs; 9&spades;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0105 9&diams; 9&hearts; J&hearts;, which contains four cards of one rank, and an unmatched</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0106 card of another rank. It ranks above a full house and below a straight</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0107 flush. Higher ranking quads defeat lower ranking ones. In community-card</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0108 games (such as Texas Hold 'em) or games with wildcards it is possible</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0109 for two or more players to obtain the same quad; in this instance, the</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0110 unmatched card acts as a kicker, so 7&clubs; 7&spades; 7&diams; 7&hearts; J&hearts; defeats 7&clubs;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0111 7&spades; 7&diams; 7&hearts; 10&clubs;.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0112 There are 624 possible hands including four of a kind; the probability</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0113 of being dealt one is </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0114 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0115 <B>Full house</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0116 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0117 A full house, also known as a full boat, is a hand such as 3&clubs; 3&spades; 3&diams;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0118 6&clubs; 6&hearts;, which contains three matching cards of one rank, and two matching</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0119 cards of another rank. It ranks below a four of a kind and above a flush.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0120 Between two full houses, the one with the higher ranking set of three</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0121 wins, so 7&spades; 7&hearts; 7&diams; 4&spades; 4&clubs; defeats 6&spades; 6&hearts; 6&diams; A&spades; A&clubs;. If</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0122 two hands have the same set of three (possible in wild card and community</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0123 card games), the hand with the higher pair wins, so 5&hearts; 5&diams; 5&spades; Q&hearts;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0124 Q&clubs; defeats 5&clubs; 5&diams; 5&spades; J&spades; J&diams;. Full houses are described as &quot;Three</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0125 full of Pair&quot; or occasionally &quot;Three over Pair&quot;; Q&clubs; Q&diams; Q&spades; 9&hearts; 9&clubs;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0126 could be described as &quot;Queens over nines&quot;, &quot;Queens full of nines&quot;, or </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0127 simply &quot;Queens full&quot;. However, &quot;Queens over nines&quot; is more commonly used</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0128 to describe the hand containing two pairs, one pair of queens and one </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0129 pair of nines, as in Q&spades; Q&hearts; 9&clubs; 9&spades; J&diams;.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0130 There are 3,744 possible full houses; the probability of being dealt one</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0131 in a five-card hand is </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0132 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0133 <B>Flush</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0134 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0135 A flush is a poker hand such as Q&clubs; 10&clubs; 7&clubs; 6&clubs; 4&clubs;, which contains</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0136 five cards of the same suit, not in rank sequence. It ranks above a straight</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0137 and below a full house. Two flushes are compared as if they were high </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0138 card hands; the highest ranking card of each is compared to determine </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0139 the winner. If both hands have the same highest card, then the second-highest</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0140 ranking card is compared, and so on until a difference is found. If the</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0141 two flushes contain the same five ranks of cards, they are tied &ndash; suits</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0142 are not used to differentiate them. Flushes are described by their highest</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0143 card, as in &quot;queen-high flush&quot; to describe Q&diams; 9&diams; 7&diams; 4&diams; 3&diams;. If</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0144 the rank of the second card is important, it can also be included: K&spades;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0145 10&spades; 5&spades; 3&spades; 2&spades; is a &quot;king-ten-high flush&quot; or just a &quot;king-ten flush&quot;,</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0146 while K&hearts; Q&hearts; 9&hearts; 5&hearts; 4&hearts; is a &quot;king-queen-high flush&quot;.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0147 There are 5,148 possible flushes, of which 40 are also straight flushes;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0148 the probability of being dealt a flush in a five-card hand is </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0149 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0150 <B>Straight</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0151 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0152 A straight is a poker hand such as Q&clubs; J&spades; 10&spades; 9&hearts; 8&hearts;, which contains</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0153 five cards of sequential rank but in more than one suit. It ranks above</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0154 three of a kind and below a flush. Two straights are ranked by comparing</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0155 the highest card of each. Two straights with the same high card are of</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0156 equal value, suits are not used to separate them. Straights are described</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0157 by their highest card, as in &quot;ten-high straight&quot; or &quot;straight to the ten&quot;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0158 for 10&clubs; 9&diams; 8&hearts; 7&clubs; 6&spades;.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0159 A hand such as A&clubs; K&clubs; Q&diams; J&spades; 10&spades; is an ace-high straight (also</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0160 known as Broadway), and ranks above a king-high straight such as K&hearts;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0161 Q&spades; J&hearts; 10&hearts; 9&clubs;. The ace may also be played as a low card in a five-high</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0162 straight such as 5&spades; 4&diams; 3&diams; 2&spades; A&hearts;, which is colloquially known</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0163 as a wheel. The ace may not &quot;wrap around&quot;, or play both high and low: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0164 3&clubs; 2&diams; A&hearts; K&spades; Q&clubs; is not a straight, just an ace-high high card.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0165 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0166 There are 10,240 possible straights, of which 40 are also straight flushes;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0167 the probability of being dealt a straight in a five-card hand is .</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0168 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0169 <B>Three of a kind</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0170 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0171 Three of a kind, also called trips, set or a prile (the last of these</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0172 from its use in three card poker[3]), is a poker hand such as 2&diams; 2&spades;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0173 2&clubs; K&spades; 6&hearts;, which contains three cards of the same rank, plus two </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0174 unmatched cards. It ranks above two pair and below a straight. In Texas</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0175 hold 'em and other flop games, a &quot;set&quot; refers specifically to a three </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0176 of a kind composed of a pocket pair and one card of matching rank on the</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0177 board (as opposed to two matching cards on the board and a third in the</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0178 player's hand).[4] Higher-valued three of a kind defeat lower-valued three</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0179 of a kind, so Q&spades; Q&hearts; Q&diams; 7&spades; 4&clubs; defeats J&spades; J&clubs; J&diams; A&diams; K&clubs;.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0180 If two hands contain threes of a kind of the same value, possible in games</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0181 with wild cards or community cards, the kickers are compared to break </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0182 the tie, so 4&diams; 4&clubs; 4&spades; 9&diams; 2&clubs; defeats 4&diams; 4&clubs; 4&spades; 8&clubs; 7&diams;.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0183 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0184 There are 54,912 possible three of a kind hands which are not also full</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0185 houses; the probability of being dealt one in a five-card hand is </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0186 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0187 <B>Two pair</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0188 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0189 A poker hand such as J&hearts; J&clubs; 4&clubs; 4&spades; 9&hearts;, which contains two cards</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0190 of the same rank, plus two cards of another rank (that match each other</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0191 but not the first pair), plus one unmatched card, is called two pair. </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0192 It ranks above one pair and below three of a kind. To rank two hands both</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0193 containing two pair, the higher ranking pair of each is first compared,</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0194 and the higher pair wins (so 10&spades; 10&clubs; 8&hearts; 8&clubs; 4&spades; defeats 8&hearts; 8&clubs;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0195 4&spades; 4&clubs; 10&spades;). If both hands have the same &quot;top pair&quot;, then the second</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0196 pair of each is compared, such that 10&spades; 10&clubs; 8&hearts; 8&clubs; 4&spades; defeats</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0197 10&spades; 10&clubs; 4&spades; 4&hearts; 8&hearts;. Finally, if both hands have the same two pairs,</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0198 the kicker determines the winner: 10&spades; 10&clubs; 8&hearts; 8&clubs; 4&spades; loses to </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0199 10&spades; 10&clubs; 8&hearts; 8&clubs; A&diams;. Two pair are described by the higher pair </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0200 first, followed by the lower pair if necessary; K&clubs; K&diams; 9&spades; 9&hearts; 5&hearts;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0201 could be described as &quot;Kings over nines&quot;, &quot;Kings and nines&quot; or simply </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0202 &quot;Kings up&quot; if the nines are not important.</FONT></FONT> 
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0203 There are 123,552 possible two pair hands that are not also full houses;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0204 the probability of being dealt one in a five-card hand is </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0205 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0206 <B>One pair</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0207 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0208 One pair is a poker hand such as 4&hearts; 4&spades; K&spades; 10&diams; 5&spades;, which contains</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0209 two cards of the same rank, plus three other unmatched cards. It ranks</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0210 above any high card hand, but below all other poker hands. Higher ranking</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0211 pairs defeat lower ranking pairs; if two hands have the same pair, the</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0212 non-paired cards (the kickers) are compared in descending order to determine</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0213 the winner.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0214 There are 1,098,240 possible one pair hands; the probability of being </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0215 dealt one in a five-card hand is </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0216 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0217 <B>High card</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0218 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0219 A high-card or no-pair hand is a poker hand such as K&hearts; J&clubs; 8&clubs; 7&diams;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0220 3&spades;, in which no two cards have the same rank, the five cards are not</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0221 in sequence, and the five cards are not all the same suit. It is also </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0222 referred to as &quot;no pair&quot;, as well as &quot;nothing&quot;, &quot;garbage,&quot; and various</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0223 other derogatory terms. High card ranks below all other poker hands; two</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0224 such hands are ranked by comparing the highest ranking card. If those </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0225 are equal, then the next highest ranking card from each hand is compared,</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0226 and so on until a difference is found. High card hands are described by</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0227 the one or two highest cards in the hand, such as &quot;king high&quot;, &quot;ace-queen</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0228 high&quot;, or by as many cards as are necessary to break a tie.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0229 The lowest possible high card is seven-high (such as 7&spades; 5&clubs; 4&diams; 3&diams;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0230 2&clubs;), because a hand such as 6&diams; 5&clubs; 4&spades; 3&diams; 2&hearts; would be a straight.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0231 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0232 Of the 2,598,960 possible hands, 1,302,540 do not contain any pairs and</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-P-0233 are neither straights nor flushes. </FONT></FONT>

</PRE>
</wc:pageTemplate>



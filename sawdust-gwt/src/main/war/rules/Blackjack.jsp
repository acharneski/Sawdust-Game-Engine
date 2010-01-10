<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error.jsp"%>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser"/>
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

<wc:pageTemplate title="Euchre Rules">

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>
This page outlines the Blackjack specification used to implement the game. It was copied from
<a href="http://en.wikipedia.org/wiki/Blackjack">Wikipedia</a> on Septempber 4th, 2009. It is
annotated where implementation diverges from the published wikipedia specification.
</B></FONT></FONT>

<PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0001 In casino blackjack, the dealer faces one to seven players from behind</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0002 a kidney-shaped table. Each player plays his hand independently against</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0003 the dealer. At the beginning of each round, the player places a bet in</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0004 the &quot;betting box&quot; and receives an initial hand of two cards. The object</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0005 of the game is to get a higher card total than the dealer, but without</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0006 going over 21 which is called &quot;busting&quot;, &quot;breaking&quot;, or many other terms.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0007 (The spot cards count 2 to 9; the 10, jack, queen, and king count as ten;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0008 an ace can be either 1 or 11 at the player's choice). The player goes</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0009 first and plays his hand by taking additional cards if he desires. If</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0010 he busts, he loses. Then the dealer plays his or her hand. If the dealer</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0011 busts, he loses to all remaining players. If neither busts, the higher</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0012 hand total wins. In case of a tie, no one wins - the hand is a &quot;push&quot;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0013 and all bets are returned. It is possible for the dealer to lose to some</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0014 players but still beat other players in the same round.</FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>In this version, house takes all ties.</B></FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0015 Example of a Blackjack game. The top half of the picture shows the beginning</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0016 of the round, with bets placed and an initial two cards for each player.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0017 The bottom half shows the end of the round, with the associated losses</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0018 or payoffs.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0019</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0020 Cards are dealt in three ways, either from one or two hand-held decks,</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0021 from a box containing four to eight decks called a &quot;shoe,&quot; or from a shuffling</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0022 machine. When dealt by hand, the player's two initial cards are face-down,</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0023 while the dealer has one face-up card called the &quot;upcard&quot; and one face-down</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0024 card called the &quot;hole card.&quot; (In European blackjack, the dealer's hole</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0025 card is not actually dealt until the players all play their hands.) When</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0026 dealt from a shoe, all player cards are normally dealt face-up, with minor</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0027 exceptions. It shouldn't matter to the player whether his cards are dealt</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0028 face-down or face-up since the dealer must play according to predetermined</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0029 rules. If the dealer has less than 17, he must hit. If the dealer has </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0030 17 or more, he must stand (take no more cards), unless it is a &quot;soft 17&quot;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0031 (a hand that includes an ace valued as &quot;11,&quot; for example a hand consisting</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0032 of Ace+6, or Ace+2+4). With a soft 17, the dealer follows the casino rules</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0033 printed on the blackjack table, either to &quot;hit soft 17&quot; or to &quot;stand on</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0034 all 17's.&quot;</FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>Our dealer stands on all 17's</B></FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0035</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0036 The highest possible hand is a &quot;blackjack&quot; or &quot;natural,&quot; meaning an initial</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0037 two-card total of 21 (an ace and a ten-value card). A player blackjack</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0038 is an automatic winner unless the dealer also has blackjack, in which</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0039 case the hand is a &quot;push&quot; (a tie). When the dealer upcard is an ace, the</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0040 player is allowed to make a side bet called &quot;insurance,&quot; supposedly to</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0041 guard against the risk that the dealer has a blackjack (i.e., a ten-value</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0042 card as his hole card). The insurance bet pays 2-to-1 if the dealer has</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0043 a blackjack. Whenever the dealer has a blackjack, he wins against all</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0044 player hands except those that also have a blackjack (which are a &quot;push&quot;).</FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>Dealer wins all ties and pushes. </B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>There is no insurance.</B></FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0045</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0046</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0047 The minimum and maximum bets are posted on the table. The payoff on most</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0048 bets is 1:1, meaning that the player wins the same amount as he bets.</FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>All bets are fixed at the ante level set at the game beginning.</B></FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0049 The payoff for a player blackjack is 3:2, meaning that the casino pays</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0050 $3 for each $2 originally bet. (There are many single-deck games which</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0051 pay only 6:5 for a blackjack.)</FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>Blackjack pays the same as all wins, at 1:1.</B></FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0052</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0053 Player decisions</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0054</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0055 After receiving his initial two cards, the player has four standard options:</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0056 he can &quot;Hit,&quot; &quot;Stand,&quot; &quot;Double Down,&quot; or &quot;Split a pair.&quot; Each option requires</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0057 the use of a hand signal. At some casinos or tables, the player may have</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0058 a fifth option called &quot;Surrender.&quot;</FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>At this time, only hit and stand are supported.</B></FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0059</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0060 * Hit: Take another card.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0061</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0062 * Stand: Take no more cards, also &quot;stick&quot; or &quot;stay&quot;.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0063</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0064 * Double down: After receiving his first two cards and before any more</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0065 are dealt to him, a player has the option to &quot;double down&quot;. To &quot;double</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0066 down&quot; means the player is allowed to double his initial bet in exchange</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0067 for limiting himself to getting only one more card from the dealer. The</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0068 hand played consists of his original two cards plus one more from the</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0069 dealer. To do this he moves a second bet equal to the first into the betting</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0070 box next to his original bet. (If desired, the player is usually allowed</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0071 to &quot;double down for less,&quot; although this is generally not a good idea </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0072 as the player should only double in favorable situations but should then</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0073 increase the bet as much as possible.)</FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>Not implemented</B></FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0074</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0075 * Split a pair: If his first two cards are a &quot;pair,&quot; meaning two cards</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0076 of the same value, the player can &quot;split the pair.&quot; To do this, he moves</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0077 a second bet equal to the first into the betting box next to his original</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0078 bet. The dealer splits the cards to create two hands, placing one bet</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0079 with each hand. The player then plays two separate hands.</FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>Not implemented</B></FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0080</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0081 * Surrender: Some casinos offer a fifth option called &quot;Surrender.&quot; After</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0082 the dealer has checked for blackjack, the player may &quot;surrender&quot; by giving</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0083 up half his bet and not playing out the hand.</FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>Not implemented</B></FONT></FONT>
<BR>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0084</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0085 The reason for requiring hand signals is to assist the &quot;eye in the sky,&quot;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0086 a person or video camera located above the table but concealed behind </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0087 one-way glass. It is used in order to protect the casino against dealers</FONT></FONT> 
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0088 or players who cheat. (It may also be used to protect the casino against</FONT></FONT> 
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0089 card-counters, even though card-counting is not illegal.)</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0090 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0091 The player can take as many hits as he wants as long as the total is not</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0092 above hard-20. However, if he busts, he loses that hand. After all the</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0093 players have finished making their decisions, the dealer then reveals</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0094 his hole card and plays out his or her hand according to predetermined</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-B-0095 rules.</FONT></FONT>
</PRE>

</wc:pageTemplate>



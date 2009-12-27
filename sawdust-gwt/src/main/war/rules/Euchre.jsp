<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error.jsp"%>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser" scope="request" />
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

<wc:pageTemplate title="Euchre Rules">

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>
This page outlines the Euchre specification used to implement the game. It was copied from
<a href="http://en.wikipedia.org/wiki/Euchre">Wikipedia</a> on Septempber 4th, 2009. It is
annotated where implementation diverges from the published wikipedia specification.
</B></FONT></FONT>

<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0001: <B>= Euchre =</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0002: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0003: Conventional euchre is a four-player trump game, wherein the players are paired </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0004: to form two partnerships. Partners face each other from across the table so that</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0005:  the play of the cards in conventional clockwise order alternates between the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0006:  two partnerships.</FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>Players are laid out in an alternating team 1 - team 2 - team 1 - team 2 pattern</B></FONT></FONT>
<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0007: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0008: Conventional euchre uses a deck of 24 standard playing cards consisting of A, K, </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0009: Q, J, 10, and 9 of each of the four suits. A standard 52-card deck can be used, </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0010: omitting the cards from 2 to 8, or a Pinochle deck may be divided in half to </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0011: form two euchre decks. In some countries, the common 32-card piquet or Skat </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0012: deck is used, which includes the 7s and 8s.</FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>We use the 9-10-J-G-K-A version.</B></FONT></FONT>
<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0013: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0014: To determine the first deal, many players use a first Jack deals or first black </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0015: Jack deals rule. Using the euchre deck, one player will distribute the cards, </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0016: one at a time, face up in front of each player. The player dealt the first </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0017: (black) jack becomes the dealer for the first hand. In subsequent hands, the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0018: deal is rotated clockwise. Out of courtesy, the dealer should offer a cut to </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0019: the player on his right after shuffling and immediately before dealing.</FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>In this version, &quot;Player 1&quot; is always dealer</B></FONT></FONT>
<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0020: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0021: Each player is dealt five cards (or seven if using the 32-card deck) in </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0022: clockwise order, usually in groups of two or three cards each. The dealer may </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0023: alternate, first giving two cards to the player to his left, three cards to his </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0024: partner, two cards to the player on his right and three cards to himself. The </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0025: dealer then repeats, this time giving three cards to the player on his left, two </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0026: cards to his partner and so on, to give each player the requisite five cards. </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0027: Some dealers prefer to deal going down or up such as 4-3-2-1 then 1-2-3-4, </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0028: although it doesn't matter what order the cards are dealt in as long as each </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0029: person gets 5 cards.</FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>Each person gets 5 random cards. We don't emulate card deal order.</B></FONT></FONT>
<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0030: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0031: The remaining four cards are called the kitty, but are sometimes referred to as </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0032: the kit, the widow, the blind, the dead hand, the grave, or buried and are </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0033: placed face down in front of the dealer toward the center on the table. The top </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0034: card of the kitty, sometimes referred to as the deck head, or the &quot;up card&quot; is </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0035: then turned face up, and bidding begins. The dealer asks each of the other </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0036: players in turn if they would like the suit of the top card to be trump, which </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0037: they indicate saying &quot;pick it up&quot; and the top card becomes part of the dealer's </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0038: hand, who then discards to return his hand to five cards. If no one &quot;orders up&quot;</FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>The dealer does not pick up or discard any cards at this point in this implementation.</B></FONT></FONT>
<PRE>
 
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0039: the top card, each player is given the opportunity in turn to call a different </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0040: suit as trump. If no trump is selected, it is a misdeal, and the deal is passed </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0041: clockwise unless it was agreed upon to play screw the dealer, an option that </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0042: involves forcing the dealer to choose a trump (see the Bidding section in Euchre </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0043: variations).</FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>In this variation, the turn to call suit just goes around the party ad naseum.</B></FONT></FONT>
<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0044: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0045: When a suit is named trump, any card of that suit outranks any card of a </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0046: non-trump suit. The highest ranking card in euchre is the jack of the trump suit </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0047: and is referred to as the right bower, or simply the right. Next highest is the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0048: other jack of the same color, the left bower. The right and left may also be </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0049: known as the &quot;jack&quot; and the &quot;jick&quot;, the &quot;right bauer&quot; and &quot;left bauer,&quot; or </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0050: &quot;jack&quot; and &quot;off jack&quot; respectively. Remaining cards of the trump suit rank from </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0051: high to low as A, K, Q, 10, and 9.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0052: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0053: In non-trump suits (except for the next suit), the jacks are not special, and </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0054: the cards of those suits rank from high to low as A, K, Q, J, 10, and 9.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0055: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0056: <B>== Example ==</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0057: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0058: Assume a hand is dealt and that spades are named as trump. In this event, the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0059: trump cards are as follows, from highest ranking to lowest:</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0060: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0061: Jack of spades (right bower)</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0062: Jack of clubs (left bower)</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0063: Ace of spades</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0064: King of spades</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0065: Queen of spades</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0066: 10 of spades</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0067: 9 of spades</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0068: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0069: For the purpose of play, the jack of clubs becomes a spade during the playing of </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0070: this hand. This expands the trump suit to the seven cards named above and </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0071: reduces the suit of the same color (sometimes referred to as the next suit) by </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0072: one card (the jack is &quot;loaned&quot; to the trump suit). The same principles are </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0073: observed for whatever suit is named trump. Remembering this temporary transfer </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0074: of the next suit's jack is one of the principal difficulties newcomers have with </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0075: the game of euchre (See Cheating: Renege, below).</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0076: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0077: Once the above hand is finished, the jack of clubs ceases to be a spade and </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0078: becomes a club again unless spades are again named as trump during the playing </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0079: of the subsequent hand.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0080: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0081: <B>= Play =</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0082: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0083: <B>== Objective and scoring ==</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0084: In euchre, naming trump is sometimes referred to as &quot;making,&quot; &quot;calling,&quot; or </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0085: &quot;declaring trump&quot;. When naming a suit, a player asserts that his or her </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0086: partnership intends to win the majority of tricks in the hand (3 of 5 with a </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0087: 24-card deck, 4 of 7 with 32 cards). A single point is scored when the bid </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0088: succeeds, and two points are scored if the team that declared trump takes all </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0089: five tricks. A failure of the calling partnership to win three tricks is </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0090: referred to as being euchred (also called &quot;getting set&quot; or &quot;getting bumped,&quot; </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0091: again depending on geographical location) and is penalized by giving the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0092: opposing partnership two points. A caller with exceptionally good cards can go </FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>Currently there is no logic to favor the side not calling suit or to reward domination.</B></FONT></FONT>
<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0093: alone, or take a loner hand, in which case he or she seeks to win all five </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0094: tricks without a partner. The partner of a caller in a 'go alone' hand does not </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0095: play, and if all five tricks are won by the caller the winning team scores four </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0096: points. If only three or four of the tricks are taken while going alone, then </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0097: only one point is scored. If euchred while playing alone, the opposing team </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0098: still only receives two points. (In some places, a euchred lone player is worth </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0099: 3 points.) There is a recognized option to defend alone, i.e. to attempt to </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0100: euchre the player going alone by a single player - while difficult, successfully </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0101: done this is an 8 point hand and will virtually guarantee a win.</FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>&quot;Going alone&quot; is not supported</B></FONT></FONT>
<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0102: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0103: The primary rule to remember when playing euchre is that one is never required </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0104: to trump, but one is required to follow suit if possible to do so: if diamonds </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0105: are led, a player with diamonds is required to play a diamond. This differs from </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0106: games such as pinochle.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0107: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0108: <B>= Calling (naming trump) =</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0109: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0110: Once the cards are dealt and the top card in the kitty is turned over, the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0111: upturned card's suit is offered as trump to the players in clockwise order </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0112: beginning with the player to the left of the dealer. If a player wishes the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0113: proposed suit to be trump and has a card of that suit their hand (Note: the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0114: opposite jack, not yet being of the same suit, does not qualify), he orders up </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0115: the dealer (or the dealer picks up).</FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>The dealer does not draw the face-up card. It only seeds the first hand.</B></FONT></FONT>
<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0116: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0117: If each player passes in this round, the top card is turned face down and that </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0118: suit may no longer be chosen as trump. Trump selection proceeds clockwise </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0119: beginning with the player to the left of the dealer. The dealer is not ordered </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0120: up in this round. If no suit is chosen in this round, the cards are reshuffled </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0121: and the deal passes to the player on the dealer's left.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0122: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0123: The team that selects trump is known as the &quot;makers&quot; for the remainder of the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0124: hand. The opposing team is known as the &quot;defenders&quot; for the remainder of the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0125: hand. The makers must take at least three of the five tricks in the hand in </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0126: order to avoid being euchred. If one of the players does not have an ace or a </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0127: face card or any trump cards, they may choose to call &quot;no ace, no face, no </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0128: trump&quot;, in which case the hand is dissolved and the deal is passed on to the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0129: player to the left of the current dealer (This rule is not observed in many </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0130: areas and is purely optional).</FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>There is no &quot;euchred&quot; rule, and no redraw based on &quot;no ace, ...&quot;</B></FONT></FONT>
<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0131: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0132: In the second round rules are:</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0133: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0134: The suit is required to be in the hand of the calling player </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0135: (Not recognized in some regions)</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0136: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0137: In the final round of bidding, the dealer is forced to choose a suit instead of </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0138: passing the deal to the person on his left (called &quot;stick it to the dealer&quot;)</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0139: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0140: = Winning tricks =</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0141: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0142: The player to the dealer's left begins play by leading a card. (In some </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0143: variations, if any player is going alone, the player to that person's left will </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0144: lead.)</FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>The first hand is led by the person to the left of the person who makes the trump suit.</B></FONT></FONT>
<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0145: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0146: Play continues in clockwise order; each player must follow suit if they have a </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0147: card of the suit led. The left bower is considered a member of the trump suit </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0148: and not a member of its native suit.</FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>The left bower does not act as trump suit.</B></FONT></FONT>
<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0149: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0150: The player who played the highest trump wins the trick. If no trump were played, </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0151: the highest card of the suit led wins the trick. Players who play neither the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0152: suit led nor trump cannot win the trick. The player that won the trick collects </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0153: the played cards from the table and then leads the next trick.</FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>Currently any suit can win, a undocumented suit order is used when there is no traditional</B></FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>winner</B></FONT></FONT>
<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0154: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0155: After all five tricks have been played, the hand is scored. The player to the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0156: left of the previous dealer then deals the next hand, and the deal moves </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0157: clockwise around the table until one partnership scores 10 points and wins the game.</FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>Play stops at the 5 tricks, where winnings are dispersed and players can redeal.  </B></FONT></FONT>
<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0158: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0159: = Going alone/solo =</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0160: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0161: If the player bidding (making trump) has an exceptionally good hand, or if his </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0162: or her partnership is in danger of losing the game unless they are able to score </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0163: points quickly, the player making trump has the option of playing without his or </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0164: her partner. If the bidder playing alone wins all five tricks in the hand, the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0165: team scores four points.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0166: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0167: &quot;Going alone&quot;, &quot;Going Solo&quot;, or &quot;playing a lone hand&quot; is initiated at the time </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0168: the bidder orders the upturned card on the kitty to the dealer (on the first </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0169: round of bidding) or names a suit (during the second round of bidding). The </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0170: bidder signifies his/her desire to play alone by stating &quot;alone&quot; or (for </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0171: example) &quot;clubs alone&quot; or &quot;clubs solo&quot; after bidding. If the dealer selects the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0172: top card, she may also declare a loner hand by sliding her discard to her </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0173: partner. The bidder must make this call before play begins. During a loner, the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0174: bidder's partner discards his or her cards, and does not participate in play of </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0175: the hand.</FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>There is no &quot;going alone&quot; option in this game</B></FONT></FONT>
<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0176: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0177: Another regional variation, especially popular in Canada, and therefore </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0178: sometimes referred to as 'Canadian Rules', is that if the partner of the dealer </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0179: &quot;orders him/her up&quot; (forcing the dealer to pick up the turned card) during the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0180: initial bidding, then the dealer is automatically forced out, and the dealer's </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0181: partner plays a lone hand.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0182: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0183: Depending on regional rules, the lead on the first trick will either remain with </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0184: the player to the left of the dealer, or switch to the player to the left of </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0185: the bidder.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0186: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0187: The odds of success of a loner bid depend on the lay of the cards and the </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0188: inactive cards held by the bidder's partner. Nine cards out of twenty-four do </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0189: not participate in play, making the hand less predictable than otherwise. A </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0190: hand consisting of the top five cards of the trump suit is mathematically </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0191: unbeatable from any position; this is sometimes referred to as a lay-down, as a </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0192: player with such a hand may often simply lay all five cards on the table at </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0193: once.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0194: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0195: The rules of an individual game may state that a player who &quot;sweeps,&quot; or wins </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0196: all 5 tricks while going alone/solo gets 4 points, 2 for sweeping and 2 for </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0197: going alone.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0198: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0199: One of the opponents of the lone bidder may say &quot;I defend alone&quot;, and his </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0200: partner must stay out. The lone defender will play alone. Scoring is similar </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0201: in such a case to a loner hand. Any &quot;set&quot; or &quot;euchre&quot; by a single defender </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0202: going alone is worth 4 points to the defending partnership, or 3 in some </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0203: regions.</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0204: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0205: Scoring</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0206: Scoring in Euchre	Points</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0207: Bidding partnership (makers) wins 3 or 4 tricks	1</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0208: Bidding partnership (makers) wins 5 tricks	2</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0209: Bidder goes alone and wins 5 tricks	4</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0210: Bidder goes alone and wins 4 tricks	2 &dagger;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0211: Bidder goes alone and wins 3 tricks	1</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0212: Defenders win 3 or 4 tricks	2</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0213: Defenders win 5 tricks	4 &dagger;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0214: Lone defender wins 3 or more tricks	6 &dagger;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0215: Lone defender (vs. lone bidder) wins 3 or more tricks	8 &dagger;</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0216: &dagger; regional variation</FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0217: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0218: The first team to score 15 (sometimes 5, 7, 10, or 11) points wins the game </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0219: (sometimes called a round). While score can be kept by using a tally sheet, most </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0220: euchre players traditionally use the pair of 5 cards for one member of each </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0221: partnership to keep score. In Western New York and parts of Ohio, it is </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0222: traditional to use 2 and 3 cards, crossing them to show scores higher than 5. </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0223: In some parts of Ohio a 6 and a 4 are used. In all cases, one card is used to </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0224: cover the other so as to expose the number of pips corresponding to the team's </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0225: score. A lone defender winning 3, 4, or 5 tricks (known as a march) gets 4 </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0226: points.</FONT></FONT>

</PRE>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt"><B>Score is not kept beyond the 3-out-of-5-tricks match. </B></FONT></FONT>
<PRE>

<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0227: </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0228: An alternative scoring system removes the point system entirely. Instead of </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0229: points, only euchres (or &quot;Euchs&quot;) are counted. These are when the &quot;defenders&quot; </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0230: fail to earn a single trick, or when the &quot;makers&quot; fail to get three tricks (or </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0231: two tricks if trump was forced). A match can be the first to 3 or 5 &quot;Euchs&quot;. </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0232: This simplicity can enhance the enjoyment of play. The &quot;all or nothing&quot; element </FONT></FONT>
<FONT FACE="Courier New, monospace"><FONT SIZE=2 STYLE="font-size: 9pt">R-E-0233: of this system may enhance the game's drama.<BR></FONT></FONT>

</PRE>

</wc:pageTemplate>



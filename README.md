<img src="src/main/resources/calvin.png" width="120">

A chess engine written in Java. Named after my favourite comic book character.

This is a personal project. I am a Java developer and amateur chess player, and so I decided to combine these two things and dive into the world of chess programming. I have never had so much fun writing code in my life.

Calvin is rated about 1700 on Lichess as of October 2023.

Current features:

### Board representation

- Bitboards of each piece type/colour are used for internal board representation.

### Move Generation

- Hybrid legal/pseudo-legal move generation: check, pin and attack masks are generated first. Single and double-check are resolved, and then a final filter for moves that do not put the king in a 'new' check.
- Magic bitboards are used for sliding piece move generatiton.

### Search
- Iterative deepening search within a negamax framework.
- Quiescence search to filter out 'noisy' positions.
- Transposition table with Zobrist hashing.

### Move Ordering
- Previous best move, MVV-LVA, killer move heuristic, history heuristic.

### Evaluation
- Basic material count
- Piece square tables: start- and end- tables for king and pawns, with tapered eval based on the 'endgame weight'.
- Pawn structure: passed pawn bonuses, isolated/doubled pawn penalties.

### Opening Book / Endgame Tablebase
- Not yet! I would like to implement these soon.

### Communication
- UCI protocol implemented with time management support.
- Calvin is connected to Lichess where he plays regularly in the bot pool: https://lichess.org/@/Calvin_Bot

### Perft results (starting position):

| 	Depth	 | 	Nodes	 | 	Speed	 | 
| 	:-----:	 | 	:-----:	 | 	:-----:	 | 
| 1     | 20        | PT0.000016S  |
| 2     | 400       | PT0.000052S  |
| 3     | 8902      | PT0.010105S  |
| 4     | 197281    | PT0.024139S  |
| 5     | 4865609   | PT1.114394S  |
| 6     | 119060324 | PT8.162227S |

If you would like to contribute, or just talk about chess/chess programming, get in touch!

<img src="src/main/resources/hobbes.png" width="140">

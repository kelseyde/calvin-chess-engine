<img src="src/main/resources/calvin.png" width="120">

Calvin is a chess engine written in Java. 

This is a personal project. I am a Java developer and amateur chess player, and so I decided to combine these two things and dive into the world of chess programming. I have never had so much fun writing code in my life. 

Calvin is rated about ~2100 on Lichess as of November 2023.

## Features

### Board representation

- [Bitboards](https://www.chessprogramming.org/Bitboards) of each piece type/colour are used for internal board representation.

### Move Generation

- [Legal move generation](https://www.chessprogramming.org/Move_Generation): check, pin and attack masks are generated first. If double-check, only king moves are generated. Pseudo-legal moves which leave the king in check are not generated.
- [Magic bitboards](https://www.chessprogramming.org/Magic_Bitboards) are used for sliding piece move generatiton.

### Search
- [Iterative deepening search](https://www.chessprogramming.org/Magic_Bitboards) + [negamax](https://www.chessprogramming.org/Negamax).
- [Quiescence search](https://www.chessprogramming.org/Quiescence_Search) to combat the horizon effect.
- [Transposition table](https://www.chessprogramming.org/Transposition_Table) with [Zobrist hashing](https://www.chessprogramming.org/Zobrist_Hashing).
- [Null-Move Pruning](https://www.chessprogramming.org/Null_Move_Pruning)

### Move Ordering
- TT-move ordering, [MVV-LVA](https://www.chessprogramming.org/MVV-LVA), [killer move heuristic](https://www.chessprogramming.org/Killer_Move), [history heuristic](https://www.chessprogramming.org/History_Heuristic).

### Evaluation
- Basic material count
- [Piece square tables](https://www.chessprogramming.org/Piece-Square_Tables): start- and end- tables for king and pawns, with [tapered eval](https://www.chessprogramming.org/Tapered_Eval) based on the 'endgame weight'.
- Pawn structure: passed pawn bonuses, isolated/doubled pawn penalties.
- King safety: bonus for a pawn shield around the king, penalty for a pawn storm towards the king, penalty for open file around the king.

### Opening Book / Endgame Tablebase
- Not yet! I would like to implement these soon.

### Communication
- [UCI protocol](https://www.chessprogramming.org/UCI) implemented with time management support.
- Calvin is connected to Lichess where he plays regularly in the bot pool: https://lichess.org/@/Calvin_Bot

### Perft results (starting position):

| 	Depth	 | 	Nodes	 | 	Speed	 | 
| 	:-----:	 | 	:-----:	 | 	:-----:	 | 
| 1     | 20        | PT0.000004S  |
| 2     | 400       | PT0.000009S  |
| 3     | 8902      | PT0.003844S  |
| 4     | 197281    | PT0.025309S  |
| 5     | 4865609   | PT0.123557S  |
| 6     | 119060324 | PT1.363415S |

If you would like to contribute, or just talk about chess/chess programming, get in touch!

<img src="src/main/resources/hobbes.png" width="140">

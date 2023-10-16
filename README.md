<img src="src/main/resources/calvin.png" width="120">

A chess engine written in Java. Named after my favourite comic book character.

This is a personal project. I am a Java developer and amateur chess player, and so I decided to combine these two things and dive into the world of chess programming. I have never had so much fun writing code in my life.

Calvin is rated about 1700 on Lichess as of October 2023.

## Features

### Board representation

- [Bitboards](https://www.chessprogramming.org/Bitboards) of each piece type/colour are used for internal board representation.

### Move Generation

- [Hybrid legal/pseudo-legal move generation](https://www.chessprogramming.org/Move_Generation): check, pin and attack masks are generated first. Single and double-check are resolved, and then a final filter for moves that do not put the king in a 'new' check.
- [Magic bitboards](https://www.chessprogramming.org/Magic_Bitboards) are used for sliding piece move generatiton.

### Search
- [Iterative deepening search](https://www.chessprogramming.org/Magic_Bitboards) within a [negamax](https://www.chessprogramming.org/Negamax) framework.
- [Quiescence search](https://www.chessprogramming.org/Quiescence_Search) to filter out 'noisy' positions.
- [Transposition table](https://www.chessprogramming.org/Transposition_Table) with [Zobrist hashing](https://www.chessprogramming.org/Zobrist_Hashing).

### Move Ordering
- [Previous best move](https://www.chessprogramming.org/Principal_Variation_Search), [MVV-LVA](https://www.chessprogramming.org/MVV-LVA), [killer move heuristic](https://www.chessprogramming.org/Killer_Move), [history heuristic](https://www.chessprogramming.org/History_Heuristic).

### Evaluation
- Basic material count
- [Piece square tables](https://www.chessprogramming.org/Piece-Square_Tables): start- and end- tables for king and pawns, with [tapered eval](https://www.chessprogramming.org/Tapered_Eval) based on the 'endgame weight'.
- Pawn structure: passed pawn bonuses, isolated/doubled pawn penalties.

### Opening Book / Endgame Tablebase
- Not yet! I would like to implement these soon.

### Communication
- [UCI protocol](https://www.chessprogramming.org/UCI) implemented with time management support.
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

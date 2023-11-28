<p align="center"><img src="src/main/resources/calvin.png" width="200"></p>

Calvin is a UCI-compliant chess engine written in Java. 

Currently playing on Lichess: https://lichess.org/@/Calvin_Bot

## Features

### Board representation

- [Bitboards](https://www.chessprogramming.org/Bitboards) of each piece type/colour are used for internal board representation.

### Move Generation

- [Legal move generation](https://www.chessprogramming.org/Move_Generation): check, pin and attack masks are generated first. If double-check, only king moves are generated. Pseudo-legal moves which leave the king in check are not generated.
- [Magic bitboards](https://www.chessprogramming.org/Magic_Bitboards): magic bitboards are used for sliding piece move generation.

### Search
- [Iterative deepening search](https://www.chessprogramming.org/Magic_Bitboards) + [negamax](https://www.chessprogramming.org/Negamax).
- [Quiescence search](https://www.chessprogramming.org/Quiescence_Search) to combat the horizon effect.
- [Lazy SMP](https://www.chessprogramming.org/Lazy_SMP) multi-threaded parallel search.
- [Transposition table](https://www.chessprogramming.org/Transposition_Table) with [Zobrist hashing](https://www.chessprogramming.org/Zobrist_Hashing).
- [Null-Move Pruning](https://www.chessprogramming.org/Null_Move_Pruning)
- [Futility Pruning](https://www.chessprogramming.org/Futility_Pruning)
- [Reverse Futility Pruning](https://www.chessprogramming.org/Reverse_Futility_Pruning)
- [Late Move Reductions](https://www.chessprogramming.org/Late_Move_Reductions)
- [Delta Pruning](https://www.chessprogramming.org/Delta_Pruning)

### Move Ordering
- [MVV-LVA](https://www.chessprogramming.org/MVV-LVA)
- [Killer move heuristic](https://www.chessprogramming.org/Killer_Move)
- [History heuristic](https://www.chessprogramming.org/History_Heuristic).

### Evaluation
- [Material](https://www.chessprogramming.org/Material): basic material count, bishop pair bonus
- [Piece square tables](https://www.chessprogramming.org/Piece-Square_Tables): asymettrical PSTs
- [Tapered eval](https://www.chessprogramming.org/Tapered_Eval): evaluation tapered based on opening/middlegame/endgame phase
- [Pawn structure](https://www.chessprogramming.org/Pawn_Structure): passed pawn bonuses, isolated/doubled pawn penalties.
- [King safety](https://www.chessprogramming.org/King_Safety): bonus for a pawn shield around the king, penalty for a pawn storm towards the king, penalty for open file around the king.
- [Incremental updates](https://www.chessprogramming.org/Incremental_Updates): Evaluation is updated incrementally with make/unmake move

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

## Special Thanks To...

- The [Chess Programming Wiki](https://www.chessprogramming.org)
- The [TalkChess forums](https://talkchess.com/)

If you would like to contribute, or just talk about chess/chess programming, get in touch!

<p align="center"><img src="src/main/resources/hobbes.png" width="200"></p>

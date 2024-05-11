<p align="center"><img src="src/main/resources/calvin.png" width="160"></p>

Calvin is a UCI-compliant chess engine written in Java. 

Currently playing on Lichess: https://lichess.org/@/Calvin_Bot

## Strength

The table below tracks the strength of release versions, on the CCRL computer chess leaderboards and on Lichess.

| 	Version	 | 	Release date | [Lichess](https://lichess.org/)	 | 	[CCRL Blitz](https://www.computerchess.org.uk/ccrl/404/)	 | 
| 	:-----:	 | 	:-----:	 | 	:-----:	 | :-----:	 | 
| [3.3.0](https://github.com/kelseyde/calvin-chess-engine/releases/tag/3.3.0) | 2024-05-10 | ~2540 | - |
| [3.2.0](https://github.com/kelseyde/calvin-chess-engine/releases/tag/3.2.0) | 2023-12-09 | ~2400 | 2233 |
| [3.1.0](https://github.com/kelseyde/calvin-chess-engine/releases/tag/3.1.0) | 2023-12-05 | ~2390 | - |
| [3.0.0](https://github.com/kelseyde/calvin-chess-engine/releases/tag/3.0.0) | 2023-12-02 | ~2380 | - |
| [2.6.2](https://github.com/kelseyde/calvin-chess-engine/releases/tag/2.6.2) | 2023-11-12 | ~2300 | 2173 |

## Features

### Board representation

- [Bitboards](https://www.chessprogramming.org/Bitboards) of each piece type/colour are used for internal board representation.

### Move Generation

- [Legal move generation](https://www.chessprogramming.org/Move_Generation) - pseudo-legal moves are not generated.
- [Magic bitboards](https://www.chessprogramming.org/Magic_Bitboards) are used for sliding piece move generation.

### Search
- [Iterative deepening search](https://www.chessprogramming.org/Magic_Bitboards) + [negamax](https://www.chessprogramming.org/Negamax).
- [Quiescence search](https://www.chessprogramming.org/Quiescence_Search) to combat the horizon effect.
- [Principal Variation Search](https://www.chessprogramming.org/Principal_Variation_Search)
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
- [Texel Tuning](https://www.chessprogramming.org/Texel%27s_Tuning_Method): all evaluation parameters are tuned using Texel's tuning method.
- [Material](https://www.chessprogramming.org/Material): basic material count, bishop pair bonus
- [Piece square tables](https://www.chessprogramming.org/Piece-Square_Tables): asymettrical PSTs
- [Tapered eval](https://www.chessprogramming.org/Tapered_Eval): evaluation tapered based on opening/middlegame/endgame phase
- [Pawn structure](https://www.chessprogramming.org/Pawn_Structure): passed pawn bonuses, isolated/doubled pawn penalties.
- [King safety](https://www.chessprogramming.org/King_Safety): bonus for a pawn shield around the king, penalty for a pawn storm towards the king, penalty for open file around the king.
- [Incremental updates](https://www.chessprogramming.org/Incremental_Updates): Evaluation is updated incrementally with make/unmake move

### Opening Book / Endgame Tablebase
- Simple opening book loaded from a .txt file on startup. Can be disabled using the 'OwnBook' UCI option
- No endgame tablebases implemented yet.

### Communication
- Calvin communicates using the Universal Chess Interface [(UCI) protocol](https://www.chessprogramming.org/UCI).
- [Pondering](https://www.chessprogramming.org/Pondering), where the engine thinks on the opponent's move. Can be disabled via the UCI.
- Hash size and number of Lazy SMP threads are also configurable via the UCI.
- Calvin is connected to Lichess where he plays regularly in the engine pool: https://lichess.org/@/Calvin_Bot

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

<p align="center"><img src="src/main/resources/hobbes.png" width="160"></p>

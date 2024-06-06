<p align="center"><img src="src/main/resources/calvin.png" width="160"></p>

# <div align="center"> Calvin </div>

<div align="center">

[![release][release-badge]][release-link]
[![lichess-badge]][lichess-link]

</div>

Calvin is a chess engine written in Java. 

It features a bitboard-based board representation, a traditional iterative deepening + negamax search algorithm, and a hand-crafted evaluation function. 

Calvin is ranked ~300th on the [Computer Chess Rating Lists](https://www.computerchess.org.uk/ccrl/404/) blitz leaderboards, and is currently playing on [Lichess](https://lichess.org/@/Calvin_Bot).

My aim with this project was to combine my passion (playing mediocre chess) with my profession (writing mediocre code). It didn't take long before Calvin became much, much stronger than I am - which either says a fair bit for my coding skills, or not all that much for my chess...

My secondary goal was to learn about chess programming. I have certainly learned a great deal, and I hope that my code is well-documented such that first-time readers can learn too. If you find some information is missing or poorly explained, please don't hesitate to reach out and ask!

## How to Play

Like most modern chess engines, Calvin does not implement its own user interface. Instead, it communicates using the [UCI](https://en.wikipedia.org/wiki/Universal_Chess_Interface) protocol, meaning it can either be interacted with directly from the command line, or by hooking it up to any popular chess GUI, such as [Arena Chess](http://www.playwitharena.de/), [Banksia](https://banksiagui.com/) or [Cute Chess](https://cutechess.com/).

To run Calvin locally, you will need >= Java 21 installed on your machine. The binary calvin.jar can be downloaded from the [Releases](https://github.com/kelseyde/calvin-chess-engine/releases/tag/3.4.0) section. Start up Calvin by executing the command:

```
java -jar /path/to/calvin-{version-number}.jar
```
From there, use the "help" option or refer to UCI documentation for further information on available commands.

## Strength

The table below tracks the strength of previous Calvin releases, both on the CCRL leaderboards and on Lichess.

| 	Version	 | 	Release date | [Lichess](https://lichess.org/)	 | 	[CCRL Blitz](https://www.computerchess.org.uk/ccrl/404/)	 | 
| 	:-----:	 | 	:-----:	 | 	:-----:	 | :-----:	 | 
| [3.4.0](https://github.com/kelseyde/calvin-chess-engine/releases/tag/3.4.0) | 2024-05-19 | ~2580 | - |
| [3.3.0](https://github.com/kelseyde/calvin-chess-engine/releases/tag/3.3.0) | 2024-05-10 | ~2550 | 2453 |
| [3.2.0](https://github.com/kelseyde/calvin-chess-engine/releases/tag/3.2.0) | 2023-12-09 | ~2400 | 2233 |
| [3.1.0](https://github.com/kelseyde/calvin-chess-engine/releases/tag/3.1.0) | 2023-12-05 | ~2390 | - |
| [3.0.0](https://github.com/kelseyde/calvin-chess-engine/releases/tag/3.0.0) | 2023-12-02 | ~2380 | - |
| [2.6.2](https://github.com/kelseyde/calvin-chess-engine/releases/tag/2.6.2) | 2023-11-12 | ~2300 | 2173 |

## Features

### Board representation

- [Bitboards](https://www.chessprogramming.org/Bitboards) - Calvin uses bitboards to keep track of the state of the board. Bitboards are up-to-64 bit integers which represent different features of the board, such as the positions of the pieces for each colour, legal moves, attacked/defended squares, and so on. 

### Move Generation

- [Move generation](https://www.chessprogramming.org/Move_Generation) - Move generation algorithms can be divided into roughly two camps: legal and pseudo-legal. Pseudo-legal move generators generate all moves regardless of whether they leave the king in check, and then only later check for legality. Legal move generators take into account the position of the king - and pieces pinned to the king - and don't generate any illegal moves. Calvin uses the latter approach.
- [Sliding pieces](https://www.chessprogramming.org/Sliding_Pieces) - Generating moves for the sliding pieces (bishops, rooks & queens) is notoriously the most computationally-expensive part of any move generation algorithm. Calvin uses [Magic Bitboards](https://www.chessprogramming.org/Magic_Bitboards) for sliding piece move generation. 

### Search

- [Alpha-Beta](https://www.chessprogramming.org/Alpha-Beta) - Calvin uses a classical alpha-beta minimax search algorithm to traverse the game tree. This is enhanced by [Principal Variation Search](https://www.chessprogramming.org/Principal_Variation_Search), combined with an [Iterative Deepening](https://www.chessprogramming.org/Iterative_Deepening) depth-first approach to managing time, and finally a [Quiescence Search](https://www.chessprogramming.org/Quiescence_Search) at the tips of the tree to filter out noisy/tactical positions. 
- [Transposition table](https://www.chessprogramming.org/Transposition_Table) - An transposition table is an in-memory hashtable recording information of all the previously visited positions in the search, which helps drastically cut down on the search space, since the searcher will encounter the same positions from multiple different move orders. [Zobrist hashing](https://www.chessprogramming.org/Zobrist_Hashing) is used to create the hash index.
- [Parallel Search](https://www.chessprogramming.org/Parallel_Search) - [Lazy SMP](https://www.chessprogramming.org/Lazy_SMP) is implemented for multi-threaded parallel search.
- [Pruning](https://www.chessprogramming.org/Pruning) - Calvin uses multiple pruning techniques to cut down on the search space, including [Null-Move Pruning](https://www.chessprogramming.org/Null_Move_Pruning), [Futility Pruning](https://www.chessprogramming.org/Futility_Pruning), [Reverse Futility Pruning](https://www.chessprogramming.org/Reverse_Futility_Pruning), [Late Move Pruning](https://www.chessprogramming.org/Late_Move_Reductions) and [Delta Pruning](https://www.chessprogramming.org/Delta_Pruning)
- [Search Extensions](https://www.chessprogramming.org/Extensions) - Calvin uses the popular [Check Extension](https://www.chessprogramming.org/Check_Extensions) to extend the search when in check, as well as an extension when trading into a pawn endgame (to avoid potentially trading into a drawn/lost ending). 
- [Search Reductions](https://www.chessprogramming.org/Reductions) - Calvin features [Late Move Reductions](https://www.chessprogramming.org/Late_Move_Reductions) for reducing search depth for moves ordered late in the list. 


### Move Ordering
- Captures are ordered using the [MVV-LVA](https://www.chessprogramming.org/MVV-LVA) (Most-Valuable-Victim, Least-Valuable-Attacker) heuristic.
- Non-captures are ordered using the [Killer move](https://www.chessprogramming.org/Killer_Move) and [History](https://www.chessprogramming.org/History_Heuristic) heuristics.

### Evaluation

- [Hand-Crafted Evaluation](https://www.chessprogramming.org/Evaluation) While most cutting-edge modern engines use neural network-based NNUE evaluations, for now, Calvin features a classical hand-crafted evaluation (HCE) function. 
- [Material](https://www.chessprogramming.org/Material) - basic material count, bishop pair bonus etc.
- [Piece square tables](https://www.chessprogramming.org/Piece-Square_Tables) - asymettrical PSTs
- [Pawn structure](https://www.chessprogramming.org/Pawn_Structure) - passed pawn bonuses, isolated/doubled pawn penalties.
- [King safety](https://www.chessprogramming.org/King_Safety) - bonus for a pawn shield around the king, penalty for a pawn storm towards the king, penalty for open file around the king.
- [Tuning](https://www.chessprogramming.org/Automated_Tuning) - All evaluation parameters are [tapered](https://www.chessprogramming.org/Tapered_Eval) based on the opening/middlegame/endgame phase, and tuned using [Texel's Tuning Method](https://www.chessprogramming.org/Texel%27s_Tuning_Method)

### Opening Book / Endgame Tablebase
- Simple opening book loaded from a .txt file on startup. Can be disabled using the 'OwnBook' UCI option.
- Calvin can probe the [Lichess Tablebase API](https://github.com/lichess-org/lila-tablebase) for endgames of 7 men or fewer. Can be disabled using the 'OwnTablebase' UCI option.

### Communication
- Calvin communicates using the Universal Chess Interface [(UCI) protocol](https://www.chessprogramming.org/UCI).
- [Pondering](https://www.chessprogramming.org/Pondering), where the engine thinks on the opponent's move. Can be disabled via the UCI.
- Hash size and number of Lazy SMP threads are also configurable via the UCI.
- Calvin is connected to Lichess where he plays regularly in the engine pool: https://lichess.org/@/Calvin_Bot

## Special Thanks To...

- The [Chess Programming Wiki](https://www.chessprogramming.org) - An absolutely brilliant resource for all chess engine programmers, this wiki has been my go-to reference for every new topic. 
- The [TalkChess forums](https://talkchess.com/) - The home for chess engine geeks to talk about geeky chess engine stuff.
- Other engines - I have drawn inspiration from countless others' engines, including but not limited to: [Chess Coding Adventure](https://github.com/SebLague/Chess-Coding-Adventure) (whose Youtube video inspired me to write my own engine); [Stockfish](https://github.com/official-stockfish/Stockfish) (the queen of all engines); [Leorik](https://github.com/lithander/Leorik) (whose author keeps an excellent devlog on the TalkChess forum); [Lynx](https://github.com/lynx-chess/Lynx) (my frequent Lichess rival); [Rustic](https://github.com/mvanthoor/rustic), [Simbelyne](https://github.com/sroelants/simbelmyne) and [Mantissa](https://github.com/jtheardw/mantissa) (who taught me that Rust is Cool); and many others.

If you would like to contribute, or just talk about chess/chess programming, get in touch!

<p align="center"><img src="src/main/resources/hobbes.png" width="160"></p>

[release-badge]: https://img.shields.io/github/v/release/kelseyde/calvin-chess-engine?style=for-the-badge&color=D20101
[release-link]: https://github.com/kelseyde/calvin-chess-engine/releases/latest

[lichess-badge]: https://img.shields.io/badge/Play-v3.4.0-FEB800?logo=lichess&style=for-the-badge
[lichess-link]: https://lichess.org/@/Calvin_Bot

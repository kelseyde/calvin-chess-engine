# calvin-chess-engine
A chess engine written in Java. Named after my favourite comic book character.

This is a personal project. I am a professional Java developer and I love chess, and so I decided to combine these two things and dive into the world of chess programming. 

Calvin is still a pretty average chess player (~1500 on Lichess as of October 2023), but his rating slowly climbs as I add things here and there. 

Current feautures include:

- Bitboard board representation
- Pseudo-legal move generation
- Magic bitboards for sliding piece move generation
- An iterative deepening search framework using negamax, with an added quiescence search at the end to handle 'noisy' positions
- Move ordering: previous best move, PVV-LVA + killer moves.
- Transposition tables
- Zobrist hashing
- Evaluation: basic material count and piece square tables, pawn structure, passed pawn bonuses, isolated/doubled pawn penalties
- UCI protocol implemented and Calvin is connected to Lichess where he participates regularly in the bot pool: https://lichess.org/@/Calvin_Bot

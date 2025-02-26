# TODO

### General

- [ ] OpenBench
- [ ] Cloud-hosted OB solution (with pythonanywhere?)
- [x] FRC/DFRC support
- [ ] Improve javadoc
- [ ] Review all code comments
- [x] Reduce artifact size (remove plugins, dependencies, delete old nets)
- [ ] Refactor/improve TranspositionTable code
- [ ] Move all stacks (board state, accumulator, history etc.) to single thread stack
- [ ] Check LazySMP still works
- [ ] Put Calvin back on Lichess
- [ ] Stop using int everywhere (use byte/short where possible)
- [ ] Test invariants: alpha < beta && best score <= alpha
- [x] Find solution to CCRL/ChessGUI/Vector API issue

### Search

- [x] Use int16 for history scores
- [x] SPRT SPSA tune branch
- [x] LTC 1024 net
- [ ] Threefold repetition ( CCRL bug ? )
- [ ] TT cut after RFP if depth - quietReduction <= ttDepth?
- [ ] Fractional depth
- [ ] Improving rate
- [ ] PV table
- [x] Exponentially widening aspiration window
- [x] Write to TT during QS stand-pat cutoff
- [ ] Singular extensions ( tried )
- [ ] Double extensions
- [ ] Triple extensions
- [ ] Negative extensions
- [ ] MultiCut ( tried )
- [ ] ProbCut ( tried )
- [ ] Use improving in NMP
- [x] Pawn corrhist
- [x] Non-pawn corrhist
- [ ] Material corrhist
- [ ] Major corrhist ( tried )
- [ ] Minor corrhist ( tried )
- [x] Contcorrhist
- [ ] Threat corrhist ( tried )
- [ ] Countermove corrhist?
- [ ] Follow-up move corrhist?
- [ ] TT-move corrhist? ( tried )
- [x] TT score eval correction
- [ ] LMP table
- [ ] Reduce killers less
- [x] Razoring
- [x] History pruning
- [x] Reverse futility reductions
- [x] Futility reductions
- [x] SEE pruning
- [x] Faster SEE 
- [ ] Syzygy TB
- [ ] Cuckoo hashing repetition detection
- [x] Check root legal moves, if only one, play instantly
- [ ] Merge Searcher and ParallelSearcher

### Move generation / Move ordering

- [ ] Pseudo-legal movegen 
- [ ] Implement isLegal(move) re-using pin/checker info from movegen
- [x] The Great `MovePicker` Rewrite`™`
- [ ] Copy/make
- [x] 1-ply Conthist
- [x] 2-ply Conthist
- [ ] 4-ply Conthist
- [x] Capthist
- [ ] Don't update killers during null-move search?
- [ ] Countermoves ( tried )
- [ ] Threat quiet history ( tried )
- [ ] Threat capture history 
- [x] Increase history bonus/penalty when alpha > beta + 50
- [ ] Score-based history bonuses
- [ ] PSQT-based move ordering bonus
- [ ] Add Killers, History etc. stages to `MovePicker`. ( tried )
- [ ] Other magic bitboard impls? PEXT/Black Magic/Kindergarten/whatever?
- [ ] Add checkers bitboard to `Board`?
- [ ] Check if move is check before makeMove

### Evaluation

- [ ] Lazy NNUE updates ( tried )
- [x] New net, bigger HL size
- [x] Screlu
- [ ] Output buckets ( tried, material scaling seems stronger )
- [x] Horizontal mirroring
- [ ] Datagen
- [x] Input (king) buckets
- [x] Finny tables

### Transposition Table

- [x] Always store eval in TT (should be 5-ish elo?)
- [ ] Tune TT depth cut-off
- [ ] PV node flag in TT
- [ ] 16-bits zobrist in TT? (SF does it)
- [ ] Fully compress all fields to minimum size
- [ ] Remove bucketing?
- [ ] Other replacement schemes?

### Time management

- [x] Node TM
- [ ] Legal move based TM?
- [ ] Futility TM?
- [ ] Game phase TM?
- [ ] Single reply/forced move TM?
- [ ] Best move changes TM? 

### UCI

- [x] Add engine author/version
- [x] Add perft to UCI options
- [x] Implement display command
- [x] Improve display visuals + add info
- [ ] Implement perft divide
- [ ] Standardise 'help' info
- [x] Add seldepth to info
- [x] Add hashfull to info
- [ ] MultiPV
- [x] Coloured output


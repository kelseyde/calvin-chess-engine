# TODO

### General

- [ ] FRC/DFRC support
- [ ] Improve javadoc
- [ ] Review all code comments
- [x] Reduce artifact size (remove plugins, dependencies, delete old nets)
- [ ] Refactor/improve TranspositionTable code
- [ ] Move all stacks (board state, accumulator, history etc.) to single thread stack
- [ ] Check LazySMP still works
- [ ] Put Calvin back on Lichess
- [ ] Stop using int everywhere (use byte/short where possible)

### Search

- [x] SPSA tune
- [ ] PV table
- [ ] Singular extensions ( tried )
- [ ] MultiCut ( tried )
- [ ] ProbCut ( tried )
- [x] Pawn corrhist
- [x] Non-pawn corrhist
- [ ] Material corrhist
- [ ] Major/minor corrhist ( tried, try them separately? )
- [ ] Contcorrhist
- [ ] Threat corrhist
- [x] TT score eval correction 
- [x] Razoring
- [x] History pruning
- [ ] SEE pruning ( tried )
- [ ] Faster SEE ( tried )
- [ ] Syzygy TB
- [ ] Cuckoo hashing repetition detection
- [x] Check root legal moves, if only one, play instantly
- [ ] Merge Searcher and ParallelSearcher

### Transposition Table

- [ ] PV node flag in TT
- [ ] 16-bit key in TT
- [ ] Remove bucketing?
- [ ] Other replacement schemes?

### Move generation / Move ordering

- [ ] Implement isLegal(move) re-using pin/checker info from movegen
- [ ] The Great `MovePicker` Rewrite`™`
- [ ] Try copy/make rather than make/unmake
- [x] 1-ply Conthist
- [x] 2-ply Conthist
- [ ] 4-ply Conthist
- [x] Capthist
- [ ] Threats ( tried )
- [ ] Countermoves ( tried )
- [ ] Add Killers, History etc. stages to MovePicker. ( tried )
- [ ] Other magic bitboard impls? PEXT/Black Magic/Kindergarten/whatever?

### Evaluation

- [ ] Find solution to CCRL/ChessGUI/Vector API issue
- [ ] Lazy NNUE updates ( tried )
- [x] New net, bigger HL size
- [x] Screlu
- [ ] Output buckets ( tried, material scaling seems stronger )
- [ ] Horizontal mirroring
- [ ] Datagen
- [ ] Input (king) buckets
- [ ] Finny tables

### Time management

- [x] Node TM
- [ ] Legal move based TM?
- [ ] Futility TM?
- [ ] Game phase TM?
- [ ] Single reply/forced move TM?

### UCI

- [x] Add engine author/version
- [x] Add perft to UCI options
- [x] Implement display command
- [ ] Implement perft divide
- [ ] Standardise 'help' info
- [ ] Add seldepth to info
- [x] Add hashfull to info
- [ ] MultiPV
- [x] Coloured output


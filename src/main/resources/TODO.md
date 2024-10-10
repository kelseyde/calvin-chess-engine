# TODO

### General

- [ ] FRC/DFRC support
- [ ] Improve javadoc
- [ ] Review all code comments
- [x] Reduce artifact size (remove plugins, dependencies, delete old nets)
- [ ] Refactor/improve TranspositionTable code

### Search

- [x] SPSA tune
- [ ] PV table
- [ ] Singular extensions ( won't gain )
- [ ] MultiCut ( won't gain )
- [ ] ProbCut
- [x] TT score eval correction 
- [x] Razoring
- [x] History pruning
- [ ] SEE pruning ( won't gain )
- [ ] Faster SEE ( won't gain )
- [ ] Syzygy TB
- [x] Check root legal moves, if only one, play instantly
- [ ] Merge Searcher and ParallelSearcher

### Move generation / Move ordering

- [ ] Implement isLegal(move) re-using pin/checker info from movegen
- [ ] The Great `MovePicker` Rewrite`™`
- [ ] Try copy/make rather than make/unmake
- [x] 1-ply Conthist
- [x] 2-ply Conthist
- [ ] 4-ply Conthist
- [x] Capthist
- [ ] Threats ( won't gain )
- [ ] Countermoves ( won't gain )
- [ ] Add Killers, History etc. stages to MovePicker. ( won't gain )

### Evaluation

- [ ] Find solution to CCRL/ChessGUI/Vector API issue
- [ ] Lazy NNUE updates ( won't gain )
- [x] New net, bigger HL size
- [x] Screlu
- [ ] Output buckets ( won't gain )
- [ ] Horizontal mirroring
- [ ] Datagen
- [ ] Pawn corrhist ( won't gain )
- [ ] Material corrhist
- [ ] Output buckets ( won't gain )
- [ ] Finny tables

### Time management

- [x] Node TM
- [ ] Legal move based TM?
- [ ] Futility TM?
- [ ] Game phase TM?

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


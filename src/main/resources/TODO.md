# TODO

### Search

- [ ] SPSA tune
- [ ] PV table
- [ ] Singular extensions
- [ ] MultiCut
- [ ] ProbCut
- [ ] TT score eval correction
- [ ] Razoring
- [ ] History leaf pruning
- [ ] SEE pruning
- [ ] Faster SEE
- [ ] Syzygy TB
- [x] Check root legal moves, if only one, play instantly

### Move generation / Move ordering

- [ ] Implement isLegal(move) re-using pin/checker info from movegen
- [ ] The Great `MovePicker` Rewrite`™`
- [ ] Try copy/make rather than make/unmake
- [ ] 1-ply Conthist
- [ ] 2-ply Conthist
- [x] Capthist
- [ ] Threats
- [ ] Countermoves
- [ ] Add Killers, History etc. stages to MovePicker.

### Evaluation

- [ ] Find solution to CCRL/ChessGUI/Vector API issue
- [ ] Lazy NNUE updates
- [x] New net, bigger HL size
- [ ] Screlu
- [ ] Output buckets
- [ ] Datagen
- [ ] Pawn corrhist
- [ ] Material corrhist

### Time management

- [x] Node TM

### UCI

- [ ] Add engine author/version
- [ ] Add perft to UCI options
- [ ] Implement display command
- [ ] Implement perft divide
- [ ] Standardise 'help' info
- [ ] Add seldepth to info
- [ ] Add hashfull to info

### Code maturity

- [ ] Improve javadoc
- [ ] Review all code comments


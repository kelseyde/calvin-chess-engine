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
- [x] Faster SEE
- [ ] Syzygy TB
- [ ] Check root legal moves, if only one, play instantly

### Move generation / Move ordering

- [ ] Implement isLegal(move) re-using pin/checker info from movegen
- [ ] The Great `MovePicker` Rewrite`™`
- [ ] Try copy/make rather than make/unmake
- [ ] Conthist
- [ ] Capthist
- [ ] Threats
- [ ] Countermoves
- [ ] Add Killers, History etc. stages to MovePicker.

### Evaluation

- [ ] Lazy NNUE updates
- [ ] New net, bigger HL size
- [ ] Screlu
- [ ] Datagen
- [ ] Pawn corrhist
- [ ] Material corrhist

### Time management

- [ ] Node TM

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


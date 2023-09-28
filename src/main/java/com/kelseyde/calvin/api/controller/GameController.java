package com.kelseyde.calvin.api.controller;

import com.kelseyde.calvin.api.repository.EngineRepository;
import com.kelseyde.calvin.api.request.MoveResponse;
import com.kelseyde.calvin.api.request.NewGameResponse;
import com.kelseyde.calvin.api.request.PlayRequest;
import com.kelseyde.calvin.api.request.PlayResponse;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.iterative.IterativeDeepeningSearch;
import com.kelseyde.calvin.utils.NotationUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping(value = "/game")
@Slf4j
public class GameController {

    @Resource
    private EngineRepository engineRepository;

    private final MoveGenerator moveGenerator = new MoveGenerator();

    @Resource
    private ResultCalculator resultCalculator;

    @RequestMapping(value = "/new/white", method = RequestMethod.GET)
    public ResponseEntity<NewGameResponse> getNewWhiteGame() {
        log.info("GET /game/new/white");
        Board board = new Board();
        IterativeDeepeningSearch search = new IterativeDeepeningSearch(board);
        engineRepository.putEngine(search);
        log.info("Created new board with id {}", board.getId());
        return ResponseEntity.ok(new NewGameResponse(board.getId()));
    }

    @RequestMapping(value = "/new/black", method = RequestMethod.GET)
    public ResponseEntity<NewGameResponse> getNewBlackGame() {
        log.info("GET /game/new/black");
        Board board = new Board();
        IterativeDeepeningSearch search = new IterativeDeepeningSearch(board);
        log.info("Created new game with id {}", board.getId());
        Move move = search.search(Duration.ofSeconds(2)).move();
        log.info("Engine selects move {}", NotationUtils.toNotation(move));
        board.makeMove(move);
        engineRepository.putEngine(search);
        NewGameResponse response = new NewGameResponse(board.getId());
        response.setMove(MoveResponse.fromMove(move));
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/play", method = RequestMethod.POST)
    public ResponseEntity<PlayResponse> playMove(@RequestBody PlayRequest moveRequest) {
        log.info("POST /game/play");
        log.info("Received move request {}", moveRequest);

        Optional<IterativeDeepeningSearch> existingEngine = engineRepository.getEngine(moveRequest.getGameId());
        IterativeDeepeningSearch engine;
        if (existingEngine.isPresent()) {
            engine = existingEngine.get();
        } else {
            Board board = new Board();
            engine = new IterativeDeepeningSearch(board);
            engineRepository.putEngine(engine);
            log.info("Created new board with id {}", board.getId());
        }

        Move playerMove = Move.builder()
                .startSquare(NotationUtils.fromNotation(moveRequest.getFrom()))
                .endSquare(NotationUtils.fromNotation(moveRequest.getTo()))
                .promotionPieceType(moveRequest.getPromotionPieceType())
                .build();

        log.info("Player selects move {}", NotationUtils.toNotation(playerMove));

        Optional<Move> legalMove = Arrays.stream(moveGenerator.generateLegalMoves(engine.getBoard(), false))
                .filter(lm -> lm.matches(playerMove))
                .findAny();
        if (legalMove.isEmpty()) {
            log.error("Illegal move: {}", moveRequest);
            return ResponseEntity.ok(PlayResponse.builder().build());
        }
        engine.getBoard().makeMove(legalMove.get());
        GameResult result = resultCalculator.calculateResult(engine.getBoard());
        if (!result.equals(GameResult.IN_PROGRESS)) {
            log.info("Game over! Result: {}", result);
            return ResponseEntity.ok(PlayResponse.builder().gameId(engine.getBoard().getId()).result(result).build());
        }

        Move engineMove = engine.search(Duration.ofSeconds(2)).move();
        engine.getBoard().makeMove(engineMove);
        result = resultCalculator.calculateResult(engine.getBoard());
        if (!result.equals(GameResult.IN_PROGRESS)) {
            log.info("Game over! Result: {}", result);
            return ResponseEntity.ok(PlayResponse.builder().gameId(engine.getBoard().getId()).result(result).build());
        }
        log.info("Engine selects move {}", NotationUtils.toNotation(engineMove));
        return ResponseEntity.ok(PlayResponse.builder()
                .gameId(engine.getBoard().getId())
                .move(MoveResponse.fromMove(engineMove))
                .result(GameResult.IN_PROGRESS)
                .build());
    }

}

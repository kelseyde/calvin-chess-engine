package com.kelseyde.calvin.api.controller;

import com.kelseyde.calvin.api.repository.BoardRepository;
import com.kelseyde.calvin.api.request.NewGameResponse;
import com.kelseyde.calvin.api.request.PlayRequest;
import com.kelseyde.calvin.api.request.PlayResponse;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.engine.MinimaxSearch;
import com.kelseyde.calvin.utils.NotationUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping(value = "/game")
@Slf4j
public class GameController {

    @Resource
    private BoardRepository boardRepository;

    private final MoveGenerator moveGenerator = new MoveGenerator();

    @Resource
    private MinimaxSearch search;

    @Resource
    private ResultCalculator resultCalculator;

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public ResponseEntity<NewGameResponse> getNewGame() {
        log.info("GET /game/new");
        Board board = new Board();
        boardRepository.putBoard(board);
        log.info("Created new board with id {}", board.getId());
        return ResponseEntity.ok(new NewGameResponse(board.getId()));
    }

    @RequestMapping(value = "/play", method = RequestMethod.POST)
    public ResponseEntity<PlayResponse> playMove(@RequestBody PlayRequest moveRequest) {
        log.info("POST /game/play");
        log.info("Received move request {}", moveRequest);

        Board board = boardRepository.getBoard(moveRequest.getGameId())
                .orElseThrow(() -> new NoSuchElementException(String.format("Invalid game id %s!", moveRequest.getGameId())));

        Move playerMove = Move.builder()
                .startSquare(NotationUtils.fromNotation(moveRequest.getStartSquare()))
                .endSquare(NotationUtils.fromNotation(moveRequest.getEndSquare()))
                .promotionPieceType(moveRequest.getPromotionPieceType())
                .build();

        log.info("Player selects move {}", NotationUtils.toNotation(playerMove));

        Optional<Move> legalMove = moveGenerator.generateLegalMoves(board).stream()
                .filter(lm -> lm.matches(playerMove))
                .findAny();
        if (legalMove.isEmpty()) {
            return ResponseEntity.ok(PlayResponse.fromBoard(board)
                    .result(GameResult.ILLEGAL_MOVE)
                    .build());
        }
        board.makeMove(legalMove.get());
        GameResult result = resultCalculator.calculateResult(board);

        if (!result.equals(GameResult.IN_PROGRESS)) {
            return ResponseEntity.ok(PlayResponse.fromBoard(board)
                    .result(result)
                    .build());
        }

        Move engineMove = search.search(board, 3).move();
        board.makeMove(engineMove);
        log.info("Engine selects move {}", NotationUtils.toNotation(engineMove));
        result = resultCalculator.calculateResult(board);
        return ResponseEntity.ok(PlayResponse.fromBoard(board)
                .result(result)
                .build());
    }

}

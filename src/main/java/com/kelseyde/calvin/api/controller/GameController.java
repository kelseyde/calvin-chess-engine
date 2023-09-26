package com.kelseyde.calvin.api.controller;

import com.kelseyde.calvin.api.repository.BoardRepository;
import com.kelseyde.calvin.api.request.MoveResponse;
import com.kelseyde.calvin.api.request.NewGameResponse;
import com.kelseyde.calvin.api.request.PlayRequest;
import com.kelseyde.calvin.api.request.PlayResponse;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitBoardUtils;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.minimax.MinimaxSearch;
import com.kelseyde.calvin.utils.NotationUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping(value = "/game")
@Slf4j
public class GameController {

    @Resource
    private BoardRepository boardRepository;

    private final MoveGenerator moveGenerator = new MoveGenerator();

    // TODO fix, make new Game class
    @Resource
    private MinimaxSearch search;

    @Resource
    private ResultCalculator resultCalculator;

    @RequestMapping(value = "/new/white", method = RequestMethod.GET)
    public ResponseEntity<NewGameResponse> getNewWhiteGame() {
        log.info("GET /game/new/white");
        Board board = new Board();
        boardRepository.putBoard(board);
        log.info("Created new board with id {}", board.getId());
        return ResponseEntity.ok(new NewGameResponse(board.getId()));
    }

    @RequestMapping(value = "/new/black", method = RequestMethod.GET)
    public ResponseEntity<NewGameResponse> getNewBlackGame() {
        log.info("GET /game/new/black");
        Board board = new Board();
        log.info("Created new board with id {}", board.getId());
        Move move = search.search(4).move();
        log.info("Engine selects move {}", NotationUtils.toNotation(move));
        board.makeMove(move);
        boardRepository.putBoard(board);
        NewGameResponse response = new NewGameResponse(board.getId());
        response.setMove(MoveResponse.fromMove(move));
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/play", method = RequestMethod.POST)
    public ResponseEntity<PlayResponse> playMove(@RequestBody PlayRequest moveRequest) {
        log.info("POST /game/play");
        log.info("Received move request {}", moveRequest);

        Optional<Board> existingBoard = boardRepository.getBoard(moveRequest.getGameId());
        Board board;
        if (existingBoard.isPresent()) {
            board = existingBoard.get();
            if (log.isTraceEnabled()) {
                log.trace("Found existing board:");
                BitBoardUtils.print(board.getOccupied());
            }
        } else {
            board = new Board();
            boardRepository.putBoard(board);
        }

        Move playerMove = Move.builder()
                .startSquare(NotationUtils.fromNotation(moveRequest.getFrom()))
                .endSquare(NotationUtils.fromNotation(moveRequest.getTo()))
                .promotionPieceType(moveRequest.getPromotionPieceType())
                .build();

        log.info("Player selects move {}", NotationUtils.toNotation(playerMove));

        Optional<Move> legalMove = Arrays.stream(moveGenerator.generateLegalMoves(board))
                .filter(lm -> lm.matches(playerMove))
                .findAny();
        if (legalMove.isEmpty()) {
            log.error("Illegal move: {}", moveRequest);
            return ResponseEntity.ok(PlayResponse.builder().build());
        }
        board.makeMove(legalMove.get());
        GameResult result = resultCalculator.calculateResult(board);
        if (!result.equals(GameResult.IN_PROGRESS)) {
            log.info("Game over! Result: {}", result);
            return ResponseEntity.ok(PlayResponse.builder().gameId(board.getId()).result(result).build());
        }

        Move engineMove = search.search(4).move();
        board.makeMove(engineMove);
        result = resultCalculator.calculateResult(board);
        if (!result.equals(GameResult.IN_PROGRESS)) {
            log.info("Game over! Result: {}", result);
            return ResponseEntity.ok(PlayResponse.builder().gameId(board.getId()).result(result).build());
        }
        log.info("Engine selects move {}", NotationUtils.toNotation(engineMove));
        return ResponseEntity.ok(PlayResponse.builder()
                .gameId(board.getId())
                .move(MoveResponse.fromMove(engineMove))
                .result(GameResult.IN_PROGRESS)
                .build());
    }

}

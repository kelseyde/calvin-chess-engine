package com.kelseyde.calvin.api.controller;

import com.kelseyde.calvin.api.repository.GameRepository;
import com.kelseyde.calvin.api.request.*;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.DepthSearch;
import com.kelseyde.calvin.search.negamax.NegamaxSearch;
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
    private GameRepository gameRepository;

    private final MoveGenerator moveGenerator = new MoveGenerator();

    @Resource
    private ResultCalculator resultCalculator;

    @RequestMapping(value = "/new/white", method = RequestMethod.GET)
    public ResponseEntity<NewGameResponse> getNewWhiteGame() {
        log.info("GET /game/new/white");
        Board board = new Board();
        NegamaxSearch search = new NegamaxSearch(board);
        Game game = new Game(board.getId(), board, search);
        gameRepository.putGame(game);
        log.info("Created new board with id {}", board.getId());
        return ResponseEntity.ok(new NewGameResponse(board.getId()));
    }

    @RequestMapping(value = "/new/black", method = RequestMethod.GET)
    public ResponseEntity<NewGameResponse> getNewBlackGame() {
        log.info("GET /game/new/black");
        Board board = new Board();
        NegamaxSearch search = new NegamaxSearch(board);
        Game game = new Game(board.getId(), board, search);
        log.info("Created new game with id {}", board.getId());
        Move move = search.search(4).move();
        log.info("Engine selects move {}", NotationUtils.toNotation(move));
        board.makeMove(move);
        gameRepository.putGame(game);
        NewGameResponse response = new NewGameResponse(board.getId());
        response.setMove(MoveResponse.fromMove(move));
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/play", method = RequestMethod.POST)
    public ResponseEntity<PlayResponse> playMove(@RequestBody PlayRequest moveRequest) {
        log.info("POST /game/play");
        log.info("Received move request {}", moveRequest);

        Optional<Game> existingGame = gameRepository.getGame(moveRequest.getGameId());
        Game game;
        if (existingGame.isPresent()) {
            game = existingGame.get();
        } else {
            Board board = new Board();
            DepthSearch search = new NegamaxSearch(board);
            game = new Game(board.getId(), board, search);
            gameRepository.putGame(game);
            log.info("Created new board with id {}", board.getId());
        }

        Move playerMove = Move.builder()
                .startSquare(NotationUtils.fromNotation(moveRequest.getFrom()))
                .endSquare(NotationUtils.fromNotation(moveRequest.getTo()))
                .promotionPieceType(moveRequest.getPromotionPieceType())
                .build();

        log.info("Player selects move {}", NotationUtils.toNotation(playerMove));

        Optional<Move> legalMove = Arrays.stream(moveGenerator.generateLegalMoves(game.getBoard()))
                .filter(lm -> lm.matches(playerMove))
                .findAny();
        if (legalMove.isEmpty()) {
            log.error("Illegal move: {}", moveRequest);
            return ResponseEntity.ok(PlayResponse.builder().build());
        }
        game.getBoard().makeMove(legalMove.get());
        GameResult result = resultCalculator.calculateResult(game.getBoard());
        if (!result.equals(GameResult.IN_PROGRESS)) {
            log.info("Game over! Result: {}", result);
            return ResponseEntity.ok(PlayResponse.builder().gameId(game.getId()).result(result).build());
        }

        Move engineMove = game.getEngine().search(4).move();
        game.getBoard().makeMove(engineMove);
        result = resultCalculator.calculateResult(game.getBoard());
        if (!result.equals(GameResult.IN_PROGRESS)) {
            log.info("Game over! Result: {}", result);
            return ResponseEntity.ok(PlayResponse.builder().gameId(game.getId()).result(result).build());
        }
        log.info("Engine selects move {}", NotationUtils.toNotation(engineMove));
        return ResponseEntity.ok(PlayResponse.builder()
                .gameId(game.getId())
                .move(MoveResponse.fromMove(engineMove))
                .result(GameResult.IN_PROGRESS)
                .build());
    }

}

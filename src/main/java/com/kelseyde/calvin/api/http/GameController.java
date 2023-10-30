package com.kelseyde.calvin.api.http;

import com.kelseyde.calvin.api.http.request.MoveResponse;
import com.kelseyde.calvin.api.http.request.NewGameResponse;
import com.kelseyde.calvin.api.http.request.PlayRequest;
import com.kelseyde.calvin.api.http.request.PlayResponse;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.bot.Bot;
import com.kelseyde.calvin.bot.CalvinBot;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.utils.notation.NotationUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

@RestController
@ConditionalOnProperty(name = "http.enabled", havingValue = "true")
@RequestMapping(value = "/game")
@Slf4j
public class GameController {

    private final Bot bot = new CalvinBot();

    @Resource
    private ResultCalculator resultCalculator;

    private final MoveGenerator moveGenerator = new MoveGenerator();

    @RequestMapping(value = "/new/white", method = RequestMethod.GET)
    public ResponseEntity<NewGameResponse> getNewWhiteGame() {
        log.info("GET /game/new/white");
        bot.gameOver();
        bot.newGame();
        bot.setPosition(FEN.STARTING_POSITION, Collections.emptyList());
        log.info("Created new game with id {}", bot.getBoard().getId());
        Move move = bot.think(1000);
        bot.applyMove(move);
        NewGameResponse response = new NewGameResponse(bot.getBoard().getId());
        response.setMove(MoveResponse.fromMove(move));
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/new/black", method = RequestMethod.GET)
    public ResponseEntity<NewGameResponse> getNewBlackGame() {
        log.info("GET /game/new/black");
        bot.gameOver();
        bot.newGame();
        bot.setPosition(FEN.STARTING_POSITION, Collections.emptyList());
        log.info("Created new game with id {}", bot.getBoard().getId());
        return ResponseEntity.ok(new NewGameResponse(bot.getBoard().getId()));
    }

    @RequestMapping(value = "/play", method = RequestMethod.POST)
    public ResponseEntity<PlayResponse> playMove(@RequestBody PlayRequest moveRequest) {
        log.info("POST /game/play");
        log.info("Received move request {}", moveRequest);

        int startSquare = NotationUtils.fromNotation(moveRequest.getFrom());
        int endSquare = NotationUtils.fromNotation(moveRequest.getTo());
        Move playerMove = new Move(startSquare, endSquare, Move.getPromotionFlag(moveRequest.getPromotionPieceType()));

        log.info("Player selects move {}", NotationUtils.toNotation(playerMove));
        Optional<Move> legalMove = moveGenerator.generateMoves(bot.getBoard(), false).stream()
                .filter(m -> m.matches(playerMove))
                .findAny();
        if (legalMove.isEmpty()) {
            log.warn("Illegal move! {}", NotationUtils.toNotation(playerMove));
            log.warn("Board: {}", bot.getBoard());
            return ResponseEntity.badRequest().build();
        }

        bot.applyMove(legalMove.get());

        GameResult result = resultCalculator.calculateResult(bot.getBoard());
        if (!result.equals(GameResult.IN_PROGRESS)) {
            log.info("Game over! Result: {}", result);
            return ResponseEntity.ok(PlayResponse.builder().gameId(bot.getBoard().getId()).result(result).build());
        }

        Move engineMove = bot.think(moveRequest.getThinkTimeMs());
        bot.applyMove(engineMove);

        result = resultCalculator.calculateResult(bot.getBoard());
        if (!result.equals(GameResult.IN_PROGRESS)) {
            log.info("Game over! Result: {}", result);
            return ResponseEntity.ok(PlayResponse.builder().gameId(bot.getBoard().getId()).result(result).build());
        }

        log.info("Engine selects move {}", NotationUtils.toNotation(engineMove));
        return ResponseEntity.ok(PlayResponse.builder()
                .gameId(bot.getBoard().getId())
                .move(MoveResponse.fromMove(engineMove))
                .result(GameResult.IN_PROGRESS)
                .build());
    }

}

package com.kelseyde.calvin.api.http;

import com.kelseyde.calvin.api.http.request.MoveResponse;
import com.kelseyde.calvin.api.http.request.NewGameResponse;
import com.kelseyde.calvin.api.http.request.PlayRequest;
import com.kelseyde.calvin.api.http.request.PlayResponse;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.bot.Bot;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@ConditionalOnProperty(name = "http.enabled", havingValue = "true")
@RequestMapping(value = "/game")
@Slf4j
public class GameController {

    private final MoveGenerator moveGenerator = new MoveGenerator();

    @Resource
    private Bot bot;

    @Resource
    private ResultCalculator resultCalculator;

    @RequestMapping(value = "/new/white", method = RequestMethod.GET)
    public ResponseEntity<NewGameResponse> getNewWhiteGame() {
        log.info("GET /game/new/white");
        bot.newGame();
        bot.setPosition(FEN.STARTING_POSITION, Collections.emptyList());
        log.info("Created new game with id {}", bot.getBoard().getId());
        return ResponseEntity.ok(new NewGameResponse(bot.getBoard().getId()));
    }

    @RequestMapping(value = "/new/black", method = RequestMethod.GET)
    public ResponseEntity<NewGameResponse> getNewBlackGame() {
        log.info("GET /game/new/black");
        bot.newGame();
        bot.setPosition(FEN.STARTING_POSITION, Collections.emptyList());
        log.info("Created new game with id {}", bot.getBoard().getId());
        Move move = bot.think(1000);
        bot.applyMove(move);
        NewGameResponse response = new NewGameResponse(bot.getBoard().getId());
        response.setMove(MoveResponse.fromMove(move));
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/play", method = RequestMethod.POST)
    public ResponseEntity<PlayResponse> playMove(@RequestBody PlayRequest moveRequest) {
        log.info("POST /game/play");
        log.info("Received move request {}", moveRequest);

        Move playerMove = Move.builder()
                .startSquare(NotationUtils.fromNotation(moveRequest.getFrom()))
                .endSquare(NotationUtils.fromNotation(moveRequest.getTo()))
                .promotionPieceType(moveRequest.getPromotionPieceType())
                .build();

        log.info("Player selects move {}", NotationUtils.toNotation(playerMove));
        bot.applyMove(playerMove);

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

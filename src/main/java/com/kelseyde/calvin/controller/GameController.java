package com.kelseyde.calvin.controller;

import com.kelseyde.calvin.model.api.MoveRequest;
import com.kelseyde.calvin.model.api.MoveResponse;
import com.kelseyde.calvin.model.api.NewGameResponse;
import com.kelseyde.calvin.model.game.ActionType;
import com.kelseyde.calvin.model.game.Game;
import com.kelseyde.calvin.model.game.GameAction;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.repository.GameRepository;
import com.kelseyde.calvin.service.engine.Engine;
import com.kelseyde.calvin.utils.MoveUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/game")
@Slf4j
public class GameController {

    @Resource
    private GameRepository gameRepository;

    @Resource
    private Engine engine;

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public ResponseEntity<NewGameResponse> getNewGame() {
        log.info("GET /game/new");
        Game game = new Game();
        gameRepository.putGame(game);
        log.info("Created new game with id {}", game.getId());
        return ResponseEntity.ok(new NewGameResponse(game.getId()));
    }

    @RequestMapping(value = "/play", method = RequestMethod.POST)
    public ResponseEntity<MoveResponse> playMove(@RequestBody MoveRequest moveRequest) {
        log.info("POST /game/play");
        log.info("Received move request {}", moveRequest);

        Game game = gameRepository.getGame(moveRequest.getGameId())
                .orElseThrow(() -> new NoSuchElementException(String.format("Invalid game id %s!", moveRequest.getGameId())));

        GameAction playerAction = GameAction.builder()
                .actionType(ActionType.MOVE)
                .move(Move.builder()
                        .startSquare(MoveUtils.fromNotation(moveRequest.getStartSquare()))
                        .endSquare(MoveUtils.fromNotation(moveRequest.getEndSquare()))
                        .build())
                .build();
        log.info("Player selects action {}", playerAction);
        game.executeAction(playerAction);

        GameAction engineAction = GameAction.builder()
                .actionType(ActionType.MOVE)
                .move(engine.selectMove(game))
                .build();
        log.info("Engine selects action {}", engineAction);
        game.executeAction(engineAction);

        MoveResponse response = MoveResponse.fromBoard(game.getBoard());

        return ResponseEntity.ok(response);
    }

}

package com.kelseyde.calvin.controller;

import com.kelseyde.calvin.model.game.ActionType;
import com.kelseyde.calvin.model.game.Game;
import com.kelseyde.calvin.model.game.GameAction;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.service.engine.Engine;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/game")
public class GameController {

    @Resource
    private Engine engine;

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public ResponseEntity<Game> getNewGame() {
        return ResponseEntity.ok(new Game());
    }

    @RequestMapping(value = "/play", method = RequestMethod.POST)
    public ResponseEntity<Game> playMove(Game game) {
        Move move = engine.selectMove(game);
        game.executeAction(GameAction.builder()
                .actionType(ActionType.MOVE)
                .move(move)
                .build());
        return ResponseEntity.ok(game);
    }

}

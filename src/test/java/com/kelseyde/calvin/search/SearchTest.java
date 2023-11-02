package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.GameState;
import com.kelseyde.calvin.evaluation.SimpleEvaluator;
import com.kelseyde.calvin.evaluation.see.StaticExchangeEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;
import com.kelseyde.calvin.search.transposition.TranspositionTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;

// TODO why the hell is this not working
@Disabled
//@ExtendWith(MockitoExtension.class)
public class SearchTest {

//    @Mock
    MoveGenerator moveGenerator = Mockito.mock(MoveGenerator.class);

//    @Mock
    MoveOrderer moveOrderer = Mockito.mock(MoveOrderer.class);

//    @Mock
    SimpleEvaluator evaluator = Mockito.mock(SimpleEvaluator.class);

//    @Mock
    StaticExchangeEvaluator see = Mockito.mock(StaticExchangeEvaluator.class);

//    @Mock
    TranspositionTable transpositionTable = Mockito.mock(TranspositionTable.class);

    Board board;

//    @InjectMocks
    Searcher search;

    @BeforeEach
    public void beforeEach() {
        board = Mockito.mock(Board.class);

        Mockito.when(board.getGameState()).thenReturn(new GameState());
        Mockito.when(board.getGameStateHistory()).thenReturn(new ArrayDeque<>());
        search = new Searcher(board);
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(search, "moveGenerator", moveGenerator);
        ReflectionTestUtils.setField(search, "moveOrderer", moveOrderer);
        ReflectionTestUtils.setField(search, "evaluator", evaluator);
        ReflectionTestUtils.setField(search, "see", see);
        ReflectionTestUtils.setField(search, "transpositionTable", transpositionTable);
        ReflectionTestUtils.setField(search, "timeout", Instant.now().plus(Duration.ofHours(1)));
    }

    @Test
    public void threefoldRepetitionWithEvalInsideContemptWindow() {


        Mockito.when(evaluator.get())
                .thenReturn(10);

        int eval = search.search(3, 0, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1);

        Assertions.assertEquals(0, eval);

    }

    @Test
    public void threefoldRepetitionWithEvalBelowContemptWindow() {

    }

    @Test
    public void threefoldRepetitionWithEvalAboveContemptWindow() {

    }

    @Test
    public void fiftyMoveRuleWithEvalInsideContemptWindow() {

    }

    @Test
    public void fiftyMoveRuleWithEvalBelowContemptWindow() {

    }

    @Test
    public void fiftyMoveRuleWithEvalAboveContemptWindow() {

    }

    @Test
    public void positiveCheckmateFoundAtEarlierDepth() {

    }

    @Test
    public void negativeCheckmateFoundAtEarlierDepth() {

    }

    @Test
    public void transpositionToExactNode() {

    }

    @Test
    public void transpositionToUpperBoundNode() {

    }

    @Test
    public void transpositionToLowerBoundNode() {

    }

    @Test
    public void transpositionWithDepthGreaterThanRootDoesNotSetResult() {

    }

    @Test
    public void checkmateFoundAtCurrentDepth() {

    }

    @Test
    public void stalemateWithEvalInsideContemptWindow() {

    }

    @Test
    public void stalemateWithEvalBelowContemptWindow() {

    }

    @Test
    public void stalemateWithEvalAboveContemptWindow() {

    }


}
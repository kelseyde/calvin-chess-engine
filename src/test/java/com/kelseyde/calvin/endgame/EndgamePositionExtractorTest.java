package com.kelseyde.calvin.endgame;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.Material;
import com.kelseyde.calvin.evaluation.Phase;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.PGN;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Disabled
public class EndgamePositionExtractorTest {

    private static final String INPUT_FILE = "src/test/resources/texel/quiet_positions.epd";
    private static final String OUTPUT_FILE = "src/test/resources/texel/quiet_endgames.epd";

    private static final int MAX_POSITIONS = 100;

    @Test
    public void writeToEndgamesFile() throws IOException {

        List<String> endgameFens = loadFens().stream()
                .filter(fen -> {
                    Board board = FEN.toBoard(fen.split("\"")[0]);
                    Material whiteMaterial = Material.fromBoard(board, true);
                    Material blackMaterial = Material.fromBoard(board, false);
                    float phase = Phase.fromMaterial(whiteMaterial, blackMaterial, TestUtils.PRD_CONFIG);
                    return phase < 0.5f;
                })
                .toList()
                .subList(0, MAX_POSITIONS);
        System.out.println(endgameFens.size());

        Path path = Paths.get(OUTPUT_FILE);
        Files.deleteIfExists(path);
        Files.createFile(path);
        endgameFens.forEach(fen -> {
            try {
                Files.writeString(path, fen + "\n", StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


    }

    private List<String> loadFens() throws IOException {
        Path path = Paths.get(INPUT_FILE);
        return Files.readAllLines(path);
    }

}

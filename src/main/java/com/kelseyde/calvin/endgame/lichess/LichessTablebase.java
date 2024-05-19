package com.kelseyde.calvin.endgame.lichess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.endgame.Tablebase;
import com.kelseyde.calvin.endgame.TablebaseException;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.Notation;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;

public class LichessTablebase implements Tablebase {

    private final EngineConfig config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private Instant rateLimitReachedTimestamp;

    public LichessTablebase(EngineConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder().build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Move getTablebaseMove(Board board) throws TablebaseException {
        try {
            HttpRequest request = buildRequest(board, Duration.ofSeconds(1));
            HttpResponse<String> response = probeLichess(request);
            LichessTablebaseEntry tablebaseEntry = parseResponse(response);
            return parseBestMove(tablebaseEntry);
        }
        catch (Exception e) {
            throw new TablebaseException("Failed to probe Lichess tablebase!", e);
        }
    }

    @Override
    public boolean canProbeTablebase(long timeoutMs) {
        int overheadMs = 50;
        if (timeoutMs - overheadMs <= config.getLichessTablebaseTimeoutMs()) {
            return false;
        }
        Duration coolDown = Duration.ofMinutes(1);
        boolean canProbe = rateLimitReachedTimestamp == null || Instant.now().isAfter(rateLimitReachedTimestamp.plus(coolDown));
        if (config.isLichessTablebaseDebugEnabled()) {
            System.out.println("Can probe Lichess tablebase? " + canProbe);
        }
        return canProbe;
    }

    private HttpRequest buildRequest(Board board, Duration timeout) throws URISyntaxException {
        String fen = FEN.toFEN(board).replace(" ", "_");
        String url = config.getLichessTablebaseBaseUrl() + "/standard?fen=" + fen;
        if (config.isLichessTablebaseDebugEnabled()) {
            System.out.printf("Lichess url: %s%n", url);
        }
        return HttpRequest.newBuilder().uri(new URI(url)).timeout(timeout).GET().build();
    }

    private HttpResponse<String> probeLichess(HttpRequest httpRequest) throws IOException, InterruptedException {
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        int status = httpResponse.statusCode();
        if (config.isLichessTablebaseDebugEnabled()) {
            System.out.printf("Lichess response: status %s, body %s%n", status, objectMapper.writeValueAsString(httpResponse.body()));
        }
        if (status == 429) {
            rateLimitReachedTimestamp = Instant.now();
            throw new TablebaseException("Lichess API request limit reached!");
        }
        if (status != 200) {
            throw new TablebaseException(String.format("Lichess tablebase probe failed with status %s!", status));
        }
        return httpResponse;
    }

    private LichessTablebaseEntry parseResponse(HttpResponse<String> response) throws JsonProcessingException {
        String json = response.body();
        return objectMapper.readValue(json, LichessTablebaseEntry.class);
    }

    private Move parseBestMove(LichessTablebaseEntry tablebaseEntry) {
        if (tablebaseEntry.moves().isEmpty()) {
            throw new TablebaseException("Lichess tablebase response was empty!");
        }
        LichessTablebaseMove bestMove = tablebaseEntry.moves().get(0);
        String uci = bestMove.uci();
        return Notation.fromCombinedNotation(uci);
    }

}

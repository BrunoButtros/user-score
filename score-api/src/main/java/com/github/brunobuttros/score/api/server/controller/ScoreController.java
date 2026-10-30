    package com.github.brunobuttros.score.api.server.controller;

    import com.github.brunobuttros.score.api.server.model.CpfRequest;
    import com.github.brunobuttros.score.api.server.model.ScoreResponse;
    import com.github.brunobuttros.score.api.server.service.ScoreService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/score")
    public class ScoreController {

        private final ScoreService scoreService;

        @Autowired
        public ScoreController(ScoreService scoreService) {
            this.scoreService = scoreService;
        }

        @PostMapping
        public ResponseEntity<ScoreResponse> calculateScore(@RequestBody CpfRequest cpfRequest) {
            int score = scoreService.getScore(cpfRequest.getCpf());
            ScoreResponse response = new ScoreResponse(score);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @GetMapping("/test")
        public String testEndpoint() {
            return "API Score está funcionando corretamente!";
        }
    }

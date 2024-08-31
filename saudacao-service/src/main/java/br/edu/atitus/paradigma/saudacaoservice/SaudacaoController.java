package br.edu.atitus.paradigma.saudacaoservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("saudacao-service")
public class SaudacaoController {

    @GetMapping({"","/", "/{nomePath}"})
    public ResponseEntity<String> getSaudacao(
            @RequestParam(required = false) String nome,
            @PathVariable(required = false) String nomePath) {

        String template = "%s %s!";
        if (nome == null) nome = nomePath == null ? "World" : nomePath;

        return ResponseEntity.ok(String.format(template, "Hello", nome));
    }
}

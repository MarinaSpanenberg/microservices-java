package br.edu.atitus.paradigma.saudacaoservice;

import br.edu.atitus.paradigma.saudacaoservice.configs.SaudacaoConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("saudacao-service")
public class SaudacaoController {

//    @Value("${saudacao-service.saudacao}")
//    private String saudacao;
//
//    @Value("${saudacao-service.nome-padrao}")
//    private String nomePadrao;

    private final SaudacaoConfig saudacaoConfig;

    public SaudacaoController(SaudacaoConfig saudacaoConfig) {
        this.saudacaoConfig = saudacaoConfig;
    }

    @GetMapping({"","/", "/{nomePath}"})
    public ResponseEntity<String> getSaudacao(
            @RequestParam(required = false) String nome,
            @PathVariable(required = false) String nomePath) {

        String template = "%s %s!";
        if (nome == null) nome = nomePath == null ? saudacaoConfig.getNomePadrao() : nomePath;
        return ResponseEntity.ok(String.format(template, saudacaoConfig.getSaudacao(), nome));

    }
}

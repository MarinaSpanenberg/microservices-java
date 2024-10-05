package br.edu.atitus.paradigma.cambioservice.controllers;

import br.edu.atitus.paradigma.cambioservice.entities.CambioEntity;
import br.edu.atitus.paradigma.cambioservice.repositories.CambioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cambio-service")
public class CambioController {

    private final CambioRepository repository;

    public CambioController(CambioRepository repository) {
        this.repository = repository;
    }

    @Value("${server.port}")
    private int porta;


    @GetMapping("/{valor}/{origem}/{destino}")
    public ResponseEntity<CambioEntity> getCambio(
            @PathVariable double valor,
            @PathVariable String origem,
            @PathVariable String destino) throws Exception {
        CambioEntity cambio = repository.findByOrigemAndDestino(origem, destino)
                .orElseThrow(() -> new Exception("Câmbio não encontrado"));
        cambio.setAmbiente("Servidor rodando na porta: " + porta);
        cambio.setValorConvertido(cambio.getFator() * valor);
        return ResponseEntity.ok(cambio);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handler(Exception e) throws Exception {
        String message = e.getMessage().replaceAll("[\\r\\n]","");
        return ResponseEntity.badRequest().body(message);
    }
}

package br.edu.atitus.paradigma.cambioservice.controllers;

import br.edu.atitus.paradigma.cambioservice.clients.CotacaoClient;
import br.edu.atitus.paradigma.cambioservice.clients.CotacaoResponse;
import br.edu.atitus.paradigma.cambioservice.entities.CambioEntity;
import br.edu.atitus.paradigma.cambioservice.repositories.CambioRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cambio-service")
public class CambioController {

    private final CambioRepository repository;

    private final CotacaoClient cotacaoClient;

    private final CacheManager cacheManager;

    public CambioController(CambioRepository repository, CotacaoClient cotacaoBCB, CacheManager cacheManager) {
        super();
        this.repository = repository;
        this.cotacaoClient = cotacaoBCB;
        this.cacheManager = cacheManager;
    }

    @Value("${server.port}")
    private int porta;


    @GetMapping("/{valor}/{origem}/{destino}")
    @CircuitBreaker(name="cotacaoClient", fallbackMethod = "getCambioFromLocal")
    public ResponseEntity<CambioEntity> getCambio(
            @PathVariable double valor,
            @PathVariable String origem,
            @PathVariable String destino) throws Exception {

        CambioEntity cambio = cacheManager.getCache("cambioCache").get(origem + destino, CambioEntity.class);

        if (cambio == null) {
            cambio = getCambioFromBC(origem, destino);
            cacheManager.getCache("cambioCache").put(origem + destino, cambio);
        }

        cambio.setValorConvertido(valor * cambio.getFator());
        cambio.setAmbiente("Cambio-Service run in port: " + porta);
        return ResponseEntity.ok(cambio);
    }

    public ResponseEntity<CambioEntity> getCambioFromLocal(double valor, String origem, String destino, Throwable e) throws Exception {
        CambioEntity cambio = repository.findByOrigemAndDestino(origem, destino)
        		.orElseThrow(() -> new Exception("Câmbio não encontrado para esta origem e destino"));
        cambio.setValorConvertido(valor * cambio.getFator());
        cambio.setAmbiente("Cambio-Service run in port: " + porta);
        return ResponseEntity.ok(cambio);
    }

    public CambioEntity getCambioFromBC(String origem, String destino) {
        // Aqui vamos criar um novo objeto "cambio" pois nesse momento não vamos buscar do banco de dados
        CambioEntity cambio = new CambioEntity();
        cambio.setOrigem(origem);
        cambio.setDestino(destino);

        CotacaoResponse cotacaoOrigem = cotacaoClient.getCotacaoMoedaDia(origem, "10-10-2024");
        double fator;
        if (destino.equals("BRL")) {
            fator = cotacaoOrigem.getValue().get(0).getCotacaoVenda();
        } else {
            // Se o destino NÃO é o Real (BRL), então temos que fazer o cálculo,
            //    já que a API do Banco Central retorna sempre a cotação em relação a moeda brasileira
            CotacaoResponse cotacaoDestino = cotacaoClient.getCotacaoMoedaDia(destino, "10-10-2024");
            fator = cotacaoOrigem.getValue().get(0).getCotacaoVenda()
                    / cotacaoDestino.getValue().get(0).getCotacaoVenda();
        }

        cambio.setFator(fator);
        return cambio;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handler(Exception e) throws Exception {
        String message = e.getMessage().replaceAll("[\\r\\n]","");
        return ResponseEntity.badRequest().body(message);
    }
}

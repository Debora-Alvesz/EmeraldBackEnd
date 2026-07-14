package com.emerald.infrastructure.metodopagamento.controller;

import com.emerald.infrastructure.metodopagamento.dto.MetodoPagamentoRequestDTO;
import com.emerald.infrastructure.metodopagamento.dto.MetodoPagamentoResponseDTO;
import com.emerald.infrastructure.metodopagamento.service.MetodoPagamentoIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metodos-pagamento")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MetodoPagamentoController {

    private final MetodoPagamentoIService metodoPagamentoService;

    @PostMapping
    public ResponseEntity<MetodoPagamentoResponseDTO> save(@Valid @RequestBody MetodoPagamentoRequestDTO request) {
        // Realiza a persistência de um novo método de pagamento no sistema.
        MetodoPagamentoResponseDTO response = metodoPagamentoService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MetodoPagamentoResponseDTO>> findAll() {
        // Retorna a listagem completa de todos os métodos de pagamento cadastrados.
        List<MetodoPagamentoResponseDTO> metodos = metodoPagamentoService.findAll();
        return ResponseEntity.ok(metodos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetodoPagamentoResponseDTO> findById(@PathVariable Long id) {
        // Busca os detalhes de um método de pagamento específico por meio do ID.
        MetodoPagamentoResponseDTO response = metodoPagamentoService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetodoPagamentoResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody MetodoPagamentoRequestDTO request) {
        // Atualiza a nomenclatura de um método de pagamento existente no sistema.
        MetodoPagamentoResponseDTO response = metodoPagamentoService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Remove fisicamente um método de pagamento do banco de dados do sistema.
        metodoPagamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
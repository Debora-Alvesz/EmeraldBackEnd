package com.emerald.infrastructure.contabancaria.controller;

import com.emerald.infrastructure.contabancaria.dto.ContaBancariaRequestDTO;
import com.emerald.infrastructure.contabancaria.dto.ContaBancariaResponseDTO;
import com.emerald.infrastructure.contabancaria.service.ContaBancariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contas-bancarias")
@RequiredArgsConstructor // Injeta o ContaBancariaService automaticamente via construtor do Lombok
public class ContaBancariaController {

    private final ContaBancariaService contaBancariaService;

    // Rota para cadastrar uma nova conta bancária vinculada a um usuário
    @PostMapping
    public ResponseEntity<ContaBancariaResponseDTO> criar(@Valid @RequestBody ContaBancariaRequestDTO request) {
        ContaBancariaResponseDTO novaConta = contaBancariaService.criar(request);
        // Retorna Status 201 Created com a conta recém-criada
        return ResponseEntity.status(HttpStatus.CREATED).body(novaConta);
    }

    // Rota para listar todas as contas de um usuário específico (Garante o isolamento dos dados)
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ContaBancariaResponseDTO>> buscarPorUsuario(@PathVariable UUID usuarioId) {
        List<ContaBancariaResponseDTO> contas = contaBancariaService.buscarPorUsuario(usuarioId);
        return ResponseEntity.ok(contas);
    }

    // Rota para buscar os detalhes de uma conta específica pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<ContaBancariaResponseDTO> buscarPorId(@PathVariable UUID id) {
        ContaBancariaResponseDTO conta = contaBancariaService.buscarPorId(id);
        return ResponseEntity.ok(conta);
    }

    // Rota para atualizar os dados gerais de uma conta bancária
    @PutMapping("/{id}")
    public ResponseEntity<ContaBancariaResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ContaBancariaRequestDTO request) {
        ContaBancariaResponseDTO contaAtualizada = contaBancariaService.atualizar(id, request);
        return ResponseEntity.ok(contaAtualizada);
    }

    // Rota correspondente ao método (+ inativarConta) do diagrama
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativarConta(@PathVariable UUID id) {
        contaBancariaService.inativarConta(id);
        // Retorna Status 204 No Content (Sucesso sem corpo de resposta)
        return ResponseEntity.noContent().build();
    }

    // Rota correspondente ao método (+ obterExtratoMensal) do diagrama
    @GetMapping("/{id}/extrato")
    public ResponseEntity<List<String>> obterExtratoMensal(
            @PathVariable UUID id,
            @RequestParam Integer mes,
            @RequestParam Integer ano) {
        List<String> extrato = contaBancariaService.obterExtratoMensal(id, mes, ano);
        return ResponseEntity.ok(extrato);
    }
}
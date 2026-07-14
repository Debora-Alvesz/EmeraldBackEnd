package com.emerald.infrastructure.transacao.mapper;

import com.emerald.infrastructure.transacao.dto.TransacaoRequestDTO;
import com.emerald.infrastructure.transacao.dto.TransacaoResponseDTO;
import com.emerald.infrastructure.transacao.entity.Transacao;
import org.springframework.stereotype.Component;

@Component // Permite que o Spring gerencie e injete este Mapper nas classes de serviço
public class TransacaoMapper {

    // Converte o DTO recebido nas requisições para a entidade JPA correspondente
    public Transacao toEntity(TransacaoRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Transacao transacao = new Transacao();
        transacao.setDescricao(dto.getDescricao());
        transacao.setValor(dto.getValor());
        transacao.setData(dto.getData());

        // Os relacionamentos complexos (Conta, Categoria, Método) são mapeados no Service por segurança
        return transacao;
    }

    // Converte a entidade JPA persistida em um DTO seguro e legível para o Front-end
    public TransacaoResponseDTO toResponseDto(Transacao transacao) {
        if (transacao == null) {
            return null;
        }

        TransacaoResponseDTO dto = new TransacaoResponseDTO();
        dto.setId(transacao.getId());
        dto.setDescricao(transacao.getDescricao());
        dto.setValor(transacao.getValor());
        dto.setData(transacao.getData());

        // Mapeamento seguro das informações da Conta Bancária associada
        if (transacao.getContaBancaria() != null) {
            dto.setContaBancariaId(transacao.getContaBancaria().getId());
            dto.setNomeContaBancaria(transacao.getContaBancaria().getNomeConta());
        }

        // Mapeamento seguro das informações da Categoria associada
        if (transacao.getCategoria() != null) {
            dto.setCategoriaId(transacao.getCategoria().getId());
            dto.setNomeCategoria(transacao.getCategoria().getNome());
        }

        // Mapeamento seguro das informações do Método de Pagamento associado
        if (transacao.getMetodoPagamento() != null) {
            dto.setMetodoPagamentoId(transacao.getMetodoPagamento().getId());
            dto.setNomeMetodoPagamento(transacao.getMetodoPagamento().getNomeMetodo());
        }

        return dto;
    }
}
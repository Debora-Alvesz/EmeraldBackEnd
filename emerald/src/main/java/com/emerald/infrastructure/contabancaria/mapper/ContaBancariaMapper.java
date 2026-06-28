package com.emerald.infrastructure.contabancaria.mapper;

import com.emerald.infrastructure.contabancaria.dto.ContaBancariaRequestDTO;
import com.emerald.infrastructure.contabancaria.dto.ContaBancariaResponseDTO;
import com.emerald.infrastructure.contabancaria.entity.ContaBancaria;
import org.springframework.stereotype.Component;

@Component // Permite que o Spring injete este Mapper no ContaBancariaService
public class ContaBancariaMapper {

    // Converte os dados recebidos do React (DTO) para a Entidade que vai para o banco de dados
    public ContaBancaria toEntity(ContaBancariaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        ContaBancaria conta = new ContaBancaria();
        conta.setNomeConta(dto.getNomeConta());
        conta.setSaldo(dto.getSaldo());
        conta.setTipoConta(dto.getTipoConta());

        // O relacionamento com o Usuário será injetado diretamente no Service para maior segurança
        return conta;
    }

    // Converte a Entidade do banco para o DTO que será devolvido com segurança para o React
    public ContaBancariaResponseDTO toResponseDto(ContaBancaria conta) {
        if (conta == null) {
            return null;
        }

        ContaBancariaResponseDTO dto = new ContaBancariaResponseDTO();
        dto.setId(conta.getId());
        dto.setNomeConta(conta.getNomeConta());
        dto.setSaldo(conta.getSaldo());
        dto.setTipoConta(conta.getTipoConta());

        // Mapeia apenas o ID do usuário dono da conta em vez de expor a entidade Usuário inteira
        if (conta.getUsuario() != null) {
            dto.setUsuarioId(conta.getUsuario().getId());
        }

        return dto;
    }
}
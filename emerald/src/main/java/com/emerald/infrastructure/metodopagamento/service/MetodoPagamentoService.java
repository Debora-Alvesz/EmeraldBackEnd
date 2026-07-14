package com.emerald.infrastructure.metodopagamento.service;

import com.emerald.infrastructure.exception.BusinessException;
import com.emerald.infrastructure.metodopagamento.dto.MetodoPagamentoRequestDTO;
import com.emerald.infrastructure.metodopagamento.dto.MetodoPagamentoResponseDTO;
import com.emerald.infrastructure.metodopagamento.entity.MetodoPagamento;
import com.emerald.infrastructure.metodopagamento.mapper.MetodoPagamentoMapper;
import com.emerald.infrastructure.metodopagamento.repository.MetodoPagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetodoPagamentoService implements MetodoPagamentoIService {

    private final MetodoPagamentoRepository metodoPagamentoRepository;
    private final MetodoPagamentoMapper metodoPagamentoMapper;

    @Override
    @Transactional
    public MetodoPagamentoResponseDTO save(MetodoPagamentoRequestDTO request) {
        // Impede o cadastro de métodos de pagamento duplicados com o mesmo nome.
        if (metodoPagamentoRepository.findByNomeMetodo(request.getNomeMetodo()).isPresent()) {
            throw new BusinessException("Já existe um método de pagamento cadastrado com este nome.");
        }

        MetodoPagamento metodo = metodoPagamentoMapper.toEntity(request);
        MetodoPagamento metodoSalvo = metodoPagamentoRepository.save(metodo);

        return metodoPagamentoMapper.toResponseDto(metodoSalvo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetodoPagamentoResponseDTO> findAll() {
        // Recupera todas as opções de pagamento cadastradas no sistema global.
        return metodoPagamentoRepository.findAll().stream()
                .map(metodoPagamentoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MetodoPagamentoResponseDTO findById(Long id) {
        // Localiza um método de pagamento específico pelo identificador numérico.
        MetodoPagamento metodo = metodoPagamentoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Método de pagamento não encontrado com o ID: " + id));

        return metodoPagamentoMapper.toResponseDto(metodo);
    }

    @Override
    @Transactional
    public MetodoPagamentoResponseDTO update(Long id, MetodoPagamentoRequestDTO request) {
        // Verifica se o método de pagamento solicitado realmente existe no banco de dados.
        MetodoPagamento metodoExistente = metodoPagamentoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Método de pagamento não encontrado com o ID: " + id));

        // Impede a colisão cadastral caso a nova nomenclatura já pertença a outro registro.
        metodoPagamentoRepository.findByNomeMetodo(request.getNomeMetodo())
                .ifPresent(metodoComMesmoNome -> {
                    if (!metodoComMesmoNome.getId().equals(id)) {
                        throw new BusinessException("O nome deste método de pagamento já está em uso.");
                    }
                });

        metodoExistente.setNomeMetodo(request.getNomeMetodo());

        MetodoPagamento metodoAtualizado = metodoPagamentoRepository.save(metodoExistente);
        return metodoPagamentoMapper.toResponseDto(metodoAtualizado);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // Remove fisicamente o método de pagamento se localizado pelo ID.
        MetodoPagamento metodo = metodoPagamentoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Método de pagamento não encontrado com o ID: " + id));

        metodoPagamentoRepository.delete(metodo);
    }
}
package com.emerald.infrastructure.perfil.service;

import com.emerald.infrastructure.exception.BusinessException;
import com.emerald.infrastructure.perfil.dto.PerfilRequestDTO;
import com.emerald.infrastructure.perfil.dto.PerfilResponseDTO;
import com.emerald.infrastructure.perfil.entity.Perfil;
import com.emerald.infrastructure.perfil.mapper.PerfilMapper;
import com.emerald.infrastructure.perfil.repository.PerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfilService implements PerfilIService {

    private final PerfilRepository perfilRepository;
    private final PerfilMapper perfilMapper;

    @Override
    @Transactional
    public PerfilResponseDTO save(PerfilRequestDTO request) {
        // Impede a duplicação de registros de perfil com o mesmo nome identificador.
        if (perfilRepository.findByNomePerfil(request.getNomePerfil()).isPresent()) {
            throw new BusinessException("Já existe um perfil cadastrado com este nome.");
        }

        Perfil perfil = perfilMapper.toEntity(request);
        Perfil perfilSalvo = perfilRepository.save(perfil);

        return perfilMapper.toResponseDto(perfilSalvo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerfilResponseDTO> findAll() {
        // Recupera a listagem completa de todos os perfis parametrizados no sistema.
        List<Perfil> perfis = perfilRepository.findAll();
        return perfis.stream()
                .map(perfilMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PerfilResponseDTO findById(Long id) {
        // Busca as propriedades de um perfil específico mapeado pelo identificador único.
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Perfil não encontrado com o ID: " + id));

        return perfilMapper.toResponseDto(perfil);
    }

    @Override
    @Transactional
    public PerfilResponseDTO update(Long id, PerfilRequestDTO request) {
        // Localiza o perfil existente para validação de integridade dos dados cadastrais.
        Perfil perfilExistente = perfilRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Perfil não encontrado com o ID: " + id));

        // Impede que a alteração de nome resulte em duplicidade de registros globais no banco.
        perfilRepository.findByNomePerfil(request.getNomePerfil())
                .ifPresent(perfilComMesmoNome -> {
                    if (!perfilComMesmoNome.getId().equals(id)) {
                        throw new BusinessException("O nome deste perfil já está em uso por outro registro.");
                    }
                });

        perfilExistente.setNomePerfil(request.getNomePerfil());

        Perfil perfilAtualizado = perfilRepository.save(perfilExistente);
        return perfilMapper.toResponseDto(perfilAtualizado);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // Remove fisicamente o registro de perfil da base de dados se localizado pelo ID.
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Perfil não encontrado com o ID: " + id));

        perfilRepository.delete(perfil);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> obterPermissoes(Long id) {
        // Mapeia e retorna os escopos e autorizações atrelados ao perfil solicitado.
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Perfil não encontrado com o ID: " + id));

        List<String> permissoes = new ArrayList<>();

        if ("ADMIN".equalsIgnoreCase(perfil.getNomePerfil())) {
            permissoes.add("ABRIR_TELA_USUARIOS");
            permissoes.add("GERENCIAR_SISTEMA");
        } else {
            permissoes.add("ACESSAR_CONTA_PADRAO");
        }

        return permissoes;
    }
}
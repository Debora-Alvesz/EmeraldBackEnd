package com.emerald.infrastructure.perfil.service;

import com.emerald.infrastructure.exception.BusinessException; // Pacote que criaremos no próximo passo
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
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final PerfilMapper perfilMapper;

    @Transactional
    public PerfilResponseDTO criar(PerfilRequestDTO request) {
        // Verifica se já existe um perfil com esse nome ("ADMIN" ou "USER")
        if (perfilRepository.findByNomePerfil(request.getNomePerfil()).isPresent()) {
            throw new BusinessException("Já existe um perfil cadastrado com este nome.");
        }

        Perfil perfil = perfilMapper.toEntity(request);
        Perfil perfilSalvo = perfilRepository.save(perfil);

        return perfilMapper.toResponseDto(perfilSalvo);
    }

    @Transactional(readOnly = true)
    public List<PerfilResponseDTO> buscarTodos() {
        List<Perfil> perfis = perfilRepository.findAll();
        return perfis.stream()
                .map(perfilMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PerfilResponseDTO buscarPorId(Long id) {
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Perfil não encontrado com o ID: " + id));

        return perfilMapper.toResponseDto(perfil);
    }

    @Transactional
    public PerfilResponseDTO atualizar(Long id, PerfilRequestDTO request) {
        Perfil perfilExistente = perfilRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Perfil não encontrado com o ID: " + id));

        // Verifica se o novo nome que estão tentando colocar já pertence a outro perfil
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

    @Transactional
    public void deletar(Long id) {
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Perfil não encontrado com o ID: " + id));

        perfilRepository.delete(perfil);
    }

    @Transactional(readOnly = true)
    public List<String> obterPermissoes(Long id) {
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
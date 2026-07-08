package com.emerald.infrastructure.usuario.service;

import com.emerald.infrastructure.exception.BusinessException;
import com.emerald.infrastructure.perfil.entity.Perfil;
import com.emerald.infrastructure.perfil.repository.PerfilRepository;
import com.emerald.infrastructure.usuario.dto.LoginRequestDTO;
import com.emerald.infrastructure.usuario.dto.UsuarioRequestDTO;
import com.emerald.infrastructure.usuario.dto.UsuarioResponseDTO;
import com.emerald.infrastructure.usuario.entity.Usuario;
import com.emerald.infrastructure.usuario.mapper.UsuarioMapper;
import com.emerald.infrastructure.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public UsuarioResponseDTO save(UsuarioRequestDTO request) {
        // Impede a criação de registros duplicados utilizando o mesmo endereço de e-mail.
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Já existe um usuário cadastrado com este e-mail.");
        }

        Usuario usuario = usuarioMapper.toEntity(request);
        atribuirPerfilPadrao(usuario);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDto(usuarioSalvo);
    }

    private void atribuirPerfilPadrao(Usuario usuario) {
        // Associa o escopo básico de permissões do sistema a novos usuários cadastrados de forma automática.
        Perfil perfilPadrao = perfilRepository.findByNomePerfil("USER")
                .orElseThrow(() -> new BusinessException("Erro interno: O perfil padrão 'USER' não foi inicializado no banco."));
        usuario.setPerfil(perfilPadrao);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO autenticar(LoginRequestDTO loginRequest) {
        // Valida as credenciais de acesso informadas contra os registros armazenados no banco de dados.
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas. E-mail ou senha incorretos."));

        if (!usuario.getSenha().equals(loginRequest.getSenha())) {
            throw new BusinessException("Credenciais inválidas. E-mail ou senha incorretos.");
        }

        return usuarioMapper.toResponseDto(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findAll() {
        // Recupera a listagem completa de todos os usuários registrados no sistema.
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findById(UUID id) {
        // Localiza as propriedades cadastrais de um determinado usuário por meio do seu ID único.
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o ID: " + id));
        return usuarioMapper.toResponseDto(usuario);
    }

    @Transactional
    public UsuarioResponseDTO update(UUID id, UsuarioRequestDTO request) {
        // Verifica a existência do usuário para validação e modificação do estado da entidade.
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o ID: " + id));

        // Impede a colisão cadastral caso o novo e-mail sugerido já pertença a outra conta.
        usuarioRepository.findByEmail(request.getEmail())
                .ifPresent(usuarioComMesmoEmail -> {
                    if (!usuarioComMesmoEmail.getId().equals(id)) {
                        throw new BusinessException("Este e-mail já está em uso por outro usuário.");
                    }
                });

        usuarioExistente.setNome(request.getNome());
        usuarioExistente.setEmail(request.getEmail());
        usuarioExistente.setSenha(request.getSenha());

        Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);
        return usuarioMapper.toResponseDto(usuarioAtualizado);
    }

    @Transactional
    public void delete(UUID id) {
        // Remove fisicamente o registro do usuário da base de dados se localizado pelo ID.
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o ID: " + id));
        usuarioRepository.delete(usuario);
    }
}
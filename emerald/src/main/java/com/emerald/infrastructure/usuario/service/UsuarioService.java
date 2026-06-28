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
    public UsuarioResponseDTO criar(UsuarioRequestDTO request) {
        // Valida se o e-mail já está cadastrado
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Já existe um usuário cadastrado com este e-mail.");
        }

        Usuario usuario = usuarioMapper.toEntity(request);

        // Método do seu diagrama executado de forma interna e segura
        atribuirPerfilPadrao(usuario);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDto(usuarioSalvo);
    }

    /**
     * Método do seu diagrama (- atribuirPerfilPadrao)
     * Como ele é privado (-), roda internamente no Service blindando a escolha do perfil.
     */
    private void atribuirPerfilPadrao(Usuario usuario) {
        Perfil perfilPadrao = perfilRepository.findByNomePerfil("USER")
                .orElseThrow(() -> new BusinessException("Erro interno: O perfil padrão 'USER' não foi inicializado no banco."));
        usuario.setPerfil(perfilPadrao);
    }

    /**
     * Método do seu diagrama (+ autenticar)
     * Realiza o login validando e-mail e senha.
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO autenticar(LoginRequestDTO loginRequest) {
        // Busca o usuário pelo e-mail
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas. E-mail ou senha incorretos."));

        // Valida a senha (Em texto limpo por enquanto. Quando colocar o BCrypt, mudará para passwordEncoder.matches)
        if (!usuario.getSenha().equals(loginRequest.getSenha())) {
            throw new BusinessException("Credenciais inválidas. E-mail ou senha incorretos.");
        }

        return usuarioMapper.toResponseDto(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarTodos() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o ID: " + id));
        return usuarioMapper.toResponseDto(usuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizar(UUID id, UsuarioRequestDTO request) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o ID: " + id));

        // Se alterou o e-mail, verifica se o novo e-mail já pertence a outro usuário
        usuarioRepository.findByEmail(request.getEmail())
                .ifPresent(usuarioComMesmoEmail -> {
                    if (!usuarioComMesmoEmail.getId().equals(id)) {
                        throw new BusinessException("Este e-mail já está em uso por outro usuário.");
                    }
                });

        usuarioExistente.setNome(request.getNome());
        usuarioExistente.setEmail(request.getEmail());
        usuarioExistente.setSenha(request.getSenha()); // Atualiza a senha

        Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);
        return usuarioMapper.toResponseDto(usuarioAtualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o ID: " + id));
        usuarioRepository.delete(usuario);
    }
}
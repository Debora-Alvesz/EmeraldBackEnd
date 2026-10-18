package com.emerald.infrastructure.categoria.service;

import com.emerald.infrastructure.categoria.dto.CategoriaRequestDTO;
import com.emerald.infrastructure.categoria.dto.CategoriaResponseDTO;
import com.emerald.infrastructure.categoria.entity.Categoria;
import com.emerald.infrastructure.categoria.repository.CategoriaRepository;
import com.emerald.infrastructure.exception.BusinessException;
import com.emerald.infrastructure.perfil.entity.Perfil;
import com.emerald.infrastructure.perfil.repository.PerfilRepository;
import com.emerald.infrastructure.usuario.entity.Usuario;
import com.emerald.infrastructure.usuario.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de Testes de Integração para a camada CategoriaService.
 *
 * Anotação 1: @SpringBootTest
 * Carrega o contexto completo da aplicação Spring Boot, permitindo testar a integração
 * real entre a camada Service e o banco de dados.
 *
 * Anotação 2: @Transactional
 * Garante o isolamento dos testes executando cada método dentro de uma transação com
 * rollback automático ao final. Permite consultar e gravar dados reais sem poluir o banco.
 */
@SpringBootTest
@Transactional
@DisplayName("Testes de Integração - CategoriaService (Banco Real)")
class CategoriaServiceTest {

    /*
     * Anotação 3: @Autowired
     * Injeta automaticamente as instâncias reais registradas pelo Spring
     * (serviço e repositórios JPA de banco de dados).
     */
    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    private Usuario usuario;
    private UUID usuarioId;
    private CategoriaRequestDTO requestDTO;

    /**
     * Anotação 4: @BeforeEach
     * Executa antes de cada teste para preparar o cenário básico do banco:
     * cria um usuário válido e inicializa um DTO de requisição padrão.
     */
    @BeforeEach
    void setUp() {
        usuario = criarUsuarioNoBanco();
        usuarioId = usuario.getId();

        requestDTO = new CategoriaRequestDTO();
        requestDTO.setUsuarioId(usuarioId);
        requestDTO.setNome("Alimentação");
        requestDTO.setTipo("despesa");
    }

    /**
     * Método auxiliar de infraestrutura.
     * Garante a integridade referencial salvando um Perfil e um Usuario no banco.
     * Utiliza e-mail dinâmico (UUID) para evitar erro de violação de chave única.
     */
    private Usuario criarUsuarioNoBanco() {
        Perfil perfil = perfilRepository.findByNomePerfil("USER_TESTE")
                .orElseGet(() -> {
                    Perfil novoPerfil = new Perfil();
                    novoPerfil.setNomePerfil("USER_TESTE");
                    return perfilRepository.save(novoPerfil);
                });

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome("Usuário de Teste");
        novoUsuario.setEmail("teste." + UUID.randomUUID() + "@emerald.com");
        novoUsuario.setSenha("senha123");
        novoUsuario.setPerfil(perfil);

        return usuarioRepository.save(novoUsuario);
    }

    /**
     * Método auxiliar para testes de leitura, alteração e exclusão.
     * Persiste uma categoria diretamente no banco vinculada ao usuário informado.
     */
    private Categoria criarCategoriaNoBanco(Usuario dono) {
        Categoria categoria = new Categoria();
        categoria.setNome("Categoria Teste");
        categoria.setTipo("DESPESA");
        categoria.setUsuario(dono);
        return categoriaRepository.save(categoria);
    }

    // ===============================================================
    // Operação: CREATE (save)
    // ===============================================================

    /**
     * Anotação 5: @Test
     * Identifica o método como um caso de teste executável pelo runner do JUnit.
     */
    @Test
    @DisplayName("Deve salvar categoria no banco quando os dados são válidos")
    void deveSalvarCategoriaComSucesso() {
        // Executa a criação da categoria via Service
        CategoriaResponseDTO resultado = categoriaService.save(requestDTO);

        // Valida o retorno do DTO
        assertNotNull(resultado.getId());
        assertEquals("DESPESA", resultado.getTipo());

        // Consulta diretamente o banco para comprovar a persistência física do registro
        Categoria categoriaSalva = categoriaRepository.findById(resultado.getId()).orElseThrow();
        assertEquals("Alimentação", categoriaSalva.getNome());
        assertEquals(usuarioId, categoriaSalva.getUsuario().getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar com usuarioId nulo")
    void deveLancarExcecaoQuandoUsuarioIdNuloAoSalvar() {
        requestDTO.setUsuarioId(null);

        // Valida se o serviço lança a exceção esperada antes de tentar gravar no banco
        BusinessException excecao = assertThrows(
                BusinessException.class,
                () -> categoriaService.save(requestDTO)
        );

        assertEquals("O ID do usuário é obrigatório.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar com tipo inválido")
    void deveLancarExcecaoQuandoTipoInvalidoAoSalvar() {
        requestDTO.setTipo("INVESTIMENTO");

        BusinessException excecao = assertThrows(
                BusinessException.class,
                () -> categoriaService.save(requestDTO)
        );

        assertTrue(excecao.getMessage().contains("RECEITA"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar para usuário inexistente")
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoAoSalvar() {
        requestDTO.setUsuarioId(UUID.randomUUID());

        assertThrows(BusinessException.class, () -> categoriaService.save(requestDTO));
    }

    // ===============================================================
    // Operação: READ (findByUsuarioId / findById)
    // ===============================================================

    @Test
    @DisplayName("Deve listar todas as categorias pertencentes ao usuário")
    void deveRetornarListaDeCategoriasDoUsuario() {
        criarCategoriaNoBanco(usuario);
        criarCategoriaNoBanco(usuario);

        List<CategoriaResponseDTO> resultado = categoriaService.findByUsuarioId(usuarioId);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar categorias com usuarioId nulo")
    void deveLancarExcecaoQuandoUsuarioIdNuloAoListar() {
        assertThrows(BusinessException.class, () -> categoriaService.findByUsuarioId(null));
    }

    @Test
    @DisplayName("Deve buscar categoria por ID quando pertencer ao usuário")
    void deveRetornarCategoriaQuandoPertenceAoUsuario() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);

        CategoriaResponseDTO resultado = categoriaService.findById(categoriaSalva.getId(), usuarioId);

        assertEquals(categoriaSalva.getId(), resultado.getId());
        assertEquals(categoriaSalva.getNome(), resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção de segurança ao buscar categoria de outro usuário")
    void deveLancarExcecaoAoBuscarCategoriaDeOutroUsuario() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);
        Usuario outroUsuario = criarUsuarioNoBanco();

        // Valida o controle de acesso (IDOR) impedindo a leitura de recursos alheios
        BusinessException excecao = assertThrows(
                BusinessException.class,
                () -> categoriaService.findById(categoriaSalva.getId(), outroUsuario.getId())
        );

        assertTrue(excecao.getMessage().contains("Acesso negado"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar categoria inexistente por ID")
    void deveLancarExcecaoQuandoCategoriaNaoEncontradaPorId() {
        assertThrows(
                BusinessException.class,
                () -> categoriaService.findById(UUID.randomUUID(), usuarioId)
        );
    }

    // ===============================================================
    // Operação: UPDATE (update)
    // ===============================================================

    @Test
    @DisplayName("Deve atualizar dados da categoria no banco com sucesso")
    void deveAtualizarCategoriaComSucesso() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);

        CategoriaRequestDTO atualizacao = new CategoriaRequestDTO();
        atualizacao.setUsuarioId(usuarioId);
        atualizacao.setNome("Transporte");
        atualizacao.setTipo("receita");

        categoriaService.update(categoriaSalva.getId(), usuarioId, atualizacao);

        // Valida no banco se as alterações foram alteradas e salvas de fato
        Categoria categoriaAtualizada = categoriaRepository.findById(categoriaSalva.getId()).orElseThrow();
        assertEquals("Transporte", categoriaAtualizada.getNome());
        assertEquals("RECEITA", categoriaAtualizada.getTipo());
    }

    @Test
    @DisplayName("Deve lançar exceção de segurança ao tentar atualizar categoria de outro usuário")
    void deveLancarExcecaoAoAtualizarCategoriaDeOutroUsuario() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);
        Usuario outroUsuario = criarUsuarioNoBanco();

        assertThrows(
                BusinessException.class,
                () -> categoriaService.update(categoriaSalva.getId(), outroUsuario.getId(), requestDTO)
        );

        // Confirma que os dados originais no banco permaneceram inalterados
        Categoria categoriaInalterada = categoriaRepository.findById(categoriaSalva.getId()).orElseThrow();
        assertEquals("Categoria Teste", categoriaInalterada.getNome());
    }

    // ===============================================================
    // Operação: DELETE (delete)
    // ===============================================================

    @Test
    @DisplayName("Deve remover categoria do banco com sucesso")
    void deveExcluirCategoriaComSucesso() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);
        UUID categoriaId = categoriaSalva.getId();

        categoriaService.delete(categoriaId, usuarioId);

        // Confirma que o registro deixou de existir na tabela do banco
        assertTrue(categoriaRepository.findById(categoriaId).isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção de segurança ao tentar excluir categoria de outro usuário")
    void deveLancarExcecaoAoExcluirCategoriaDeOutroUsuario() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);
        Usuario outroUsuario = criarUsuarioNoBanco();

        assertThrows(
                BusinessException.class,
                () -> categoriaService.delete(categoriaSalva.getId(), outroUsuario.getId())
        );

        // Confirma que a categoria permanece intacta no banco de dados
        assertTrue(categoriaRepository.findById(categoriaSalva.getId()).isPresent());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir com IDs nulos")
    void deveLancarExcecaoQuandoIdsNulosAoExcluir() {
        assertThrows(BusinessException.class, () -> categoriaService.delete(null, usuarioId));
        assertThrows(BusinessException.class, () -> categoriaService.delete(UUID.randomUUID(), null));
    }
}
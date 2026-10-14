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

// Importações do JUnit 5 (Jupiter) - o framework de testes em si.
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Importações do Spring para teste de INTEGRAÇÃO (sobe o contexto real da aplicação).
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

// Importação estática dos métodos de asserção do JUnit (assertEquals, assertThrows, etc.)
import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de INTEGRAÇÃO do CategoriaService.
 *
 * Diferença para um teste unitário (com @Mock):
 * Aqui NÃO fingimos as dependências. O Spring sobe o contexto real da aplicação
 * (@SpringBootTest) e o CategoriaService usa os repositórios JPA de verdade,
 * que leem e gravam no banco Postgres real (Supabase) configurado no application.properties.
 * Isso prova que o CRUD funciona de ponta a ponta, não só que a lógica do service
 * "chama os métodos certos" como faríamos com mocks.
 *
 * ANOTAÇÃO 1: @SpringBootTest
 * Sobe o contexto completo do Spring (os mesmos beans que sobem quando a aplicação
 * roda de verdade). É isso que permite usar @Autowired para injetar o CategoriaService
 * e os repositórios reais, já conectados ao banco.
 *
 * ANOTAÇÃO 2: @Transactional (na classe)
 * Faz CADA método de teste rodar dentro de uma transação que é DESFEITA (rollback)
 * automaticamente assim que o teste termina. Ou seja: o teste grava dados de verdade
 * no Postgres durante a execução (por isso conseguimos consultar e validar), mas no
 * final tudo volta como estava antes. Sem isso, cada execução deixaria usuários e
 * categorias "lixo" acumulados no banco do Supabase.
 */
@SpringBootTest
@Transactional
@DisplayName("Testes de integração do CategoriaService (banco real)")
class CategoriaServiceTest {

    /*
     * ANOTAÇÃO 3: @Autowired
     * Pede ao Spring para injetar a instância REAL desses beans (não um mock).
     * O categoriaService aqui é o objeto de produção, com os repositórios de
     * verdade dentro dele.
     */
    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    // Dados reais criados e salvos no banco antes de cada teste.
    private Usuario usuario;
    private UUID usuarioId;
    private CategoriaRequestDTO requestDTO;

    /**
     * ANOTAÇÃO 4: @BeforeEach
     * Roda antes de CADA teste. Aqui montamos o "cenário mínimo" que todo teste
     * precisa: um Perfil válido e um Usuario válido já salvos no banco, porque
     * Usuario exige um Perfil (nullable = false) e Categoria exige um Usuario.
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
     * Método auxiliar (não é um teste): cria e salva de verdade, no banco,
     * um Perfil (reaproveitando um já existente com o mesmo nome, se houver,
     * para não violar a constraint "unique" de nomePerfil) e um Usuario vinculado
     * a ele. Usamos um e-mail aleatório (UUID) para nunca colidir com o "unique"
     * do campo email, já que este método pode ser chamado várias vezes.
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
     * Método auxiliar: grava uma categoria de verdade no banco, vinculada
     * ao usuário informado. Usado pelos testes de findById/update/delete,
     * que precisam de uma categoria já existente para trabalhar em cima dela.
     */
    private Categoria criarCategoriaNoBanco(Usuario dono) {
        Categoria categoria = new Categoria();
        categoria.setNome("Categoria Teste");
        categoria.setTipo("DESPESA");
        categoria.setUsuario(dono);
        return categoriaRepository.save(categoria);
    }

    // ---------------------------------------------------------------
    // Testes do método save()
    // ---------------------------------------------------------------

    /**
     * ANOTAÇÃO 5 (repetida em cada caso de teste): @Test
     */
    @Test
    @DisplayName("Deve salvar categoria de verdade no banco quando os dados são válidos")
    void deveSalvarCategoriaComSucesso() {
        // ACT: chama o service real, que por sua vez grava no Postgres de verdade.
        CategoriaResponseDTO resultado = categoriaService.save(requestDTO);

        // ASSERT: confere o retorno do service.
        assertNotNull(resultado.getId());
        assertEquals("DESPESA", resultado.getTipo());

        // Prova real de integração: buscamos DIRETO no banco (sem passar pelo service)
        // para confirmar que o registro realmente foi persistido.
        Categoria categoriaSalva = categoriaRepository.findById(resultado.getId()).orElseThrow();
        assertEquals("Alimentação", categoriaSalva.getNome());
        assertEquals(usuarioId, categoriaSalva.getUsuario().getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar quando usuarioId é nulo")
    void deveLancarExcecaoQuandoUsuarioIdNuloAoSalvar() {
        requestDTO.setUsuarioId(null);

        // assertThrows: verifica se o método lança a exceção esperada,
        // SEM nada ser gravado no banco (a validação barra antes).
        BusinessException excecao = assertThrows(
                BusinessException.class,
                () -> categoriaService.save(requestDTO)
        );

        assertEquals("O ID do usuário é obrigatório.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar quando tipo é inválido")
    void deveLancarExcecaoQuandoTipoInvalidoAoSalvar() {
        requestDTO.setTipo("INVESTIMENTO");

        BusinessException excecao = assertThrows(
                BusinessException.class,
                () -> categoriaService.save(requestDTO)
        );

        assertTrue(excecao.getMessage().contains("RECEITA"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar quando usuário não existe no banco")
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoAoSalvar() {
        // Um UUID aleatório quase certamente não existe na tabela tb_usuarios.
        requestDTO.setUsuarioId(UUID.randomUUID());

        assertThrows(BusinessException.class, () -> categoriaService.save(requestDTO));
    }

    // ---------------------------------------------------------------
    // Testes do método findByUsuarioId()
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar do banco a lista de categorias do usuário")
    void deveRetornarListaDeCategoriasDoUsuario() {
        criarCategoriaNoBanco(usuario);
        criarCategoriaNoBanco(usuario);

        List<CategoriaResponseDTO> resultado = categoriaService.findByUsuarioId(usuarioId);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuarioId é nulo em findByUsuarioId")
    void deveLancarExcecaoQuandoUsuarioIdNuloAoListar() {
        assertThrows(BusinessException.class, () -> categoriaService.findByUsuarioId(null));
    }

    // ---------------------------------------------------------------
    // Testes do método findById()
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar categoria do banco quando ela pertence ao usuário")
    void deveRetornarCategoriaQuandoPertenceAoUsuario() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);

        CategoriaResponseDTO resultado = categoriaService.findById(categoriaSalva.getId(), usuarioId);

        assertEquals(categoriaSalva.getId(), resultado.getId());
        assertEquals(categoriaSalva.getNome(), resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção (IDOR) ao buscar categoria de outro usuário")
    void deveLancarExcecaoAoBuscarCategoriaDeOutroUsuario() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);
        Usuario outroUsuario = criarUsuarioNoBanco();

        // Testamos a proteção contra IDOR: a categoria pertence a "usuario",
        // mas estamos consultando como se fôssemos "outroUsuario" -> deve negar acesso.
        BusinessException excecao = assertThrows(
                BusinessException.class,
                () -> categoriaService.findById(categoriaSalva.getId(), outroUsuario.getId())
        );

        assertTrue(excecao.getMessage().contains("Acesso negado"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando categoria não existe no banco")
    void deveLancarExcecaoQuandoCategoriaNaoEncontradaPorId() {
        assertThrows(
                BusinessException.class,
                () -> categoriaService.findById(UUID.randomUUID(), usuarioId)
        );
    }

    // ---------------------------------------------------------------
    // Testes do método update()
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve atualizar categoria no banco quando pertence ao usuário")
    void deveAtualizarCategoriaComSucesso() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);

        CategoriaRequestDTO atualizacao = new CategoriaRequestDTO();
        atualizacao.setUsuarioId(usuarioId);
        atualizacao.setNome("Transporte");
        atualizacao.setTipo("receita");

        categoriaService.update(categoriaSalva.getId(), usuarioId, atualizacao);

        // Buscamos direto no banco para confirmar que a alteração foi persistida de verdade.
        Categoria categoriaAtualizada = categoriaRepository.findById(categoriaSalva.getId()).orElseThrow();
        assertEquals("Transporte", categoriaAtualizada.getNome());
        assertEquals("RECEITA", categoriaAtualizada.getTipo());
    }

    @Test
    @DisplayName("Deve lançar exceção (IDOR) ao atualizar categoria de outro usuário")
    void deveLancarExcecaoAoAtualizarCategoriaDeOutroUsuario() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);
        Usuario outroUsuario = criarUsuarioNoBanco();

        assertThrows(
                BusinessException.class,
                () -> categoriaService.update(categoriaSalva.getId(), outroUsuario.getId(), requestDTO)
        );

        // Confirma que o nome original NÃO foi alterado no banco, já que a operação foi negada.
        Categoria categoriaInalterada = categoriaRepository.findById(categoriaSalva.getId()).orElseThrow();
        assertEquals("Categoria Teste", categoriaInalterada.getNome());
    }

    // ---------------------------------------------------------------
    // Testes do método delete()
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve excluir categoria do banco quando pertence ao usuário")
    void deveExcluirCategoriaComSucesso() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);
        UUID categoriaId = categoriaSalva.getId();

        categoriaService.delete(categoriaId, usuarioId);

        // Confirma, direto no banco, que o registro deixou de existir.
        assertTrue(categoriaRepository.findById(categoriaId).isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção (IDOR) ao excluir categoria de outro usuário")
    void deveLancarExcecaoAoExcluirCategoriaDeOutroUsuario() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);
        Usuario outroUsuario = criarUsuarioNoBanco();

        assertThrows(
                BusinessException.class,
                () -> categoriaService.delete(categoriaSalva.getId(), outroUsuario.getId())
        );

        // Confirma que a categoria CONTINUA no banco, já que a exclusão foi negada.
        assertTrue(categoriaRepository.findById(categoriaSalva.getId()).isPresent());
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir quando ids são nulos")
    void deveLancarExcecaoQuandoIdsNulosAoExcluir() {
        assertThrows(BusinessException.class, () -> categoriaService.delete(null, usuarioId));
        assertThrows(BusinessException.class, () -> categoriaService.delete(UUID.randomUUID(), null));
    }
}
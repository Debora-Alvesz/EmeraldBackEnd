
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
 * Classe responsável pelos testes de integração da camada CategoriaService.
 *
 * @SpringBootTest:
 * Carrega o contexto completo da aplicação Spring Boot, permitindo utilizar
 * os componentes reais da aplicação, como Services e Repositories.
 * Dessa forma, os testes verificam a integração entre as camadas do sistema
 * e o banco de dados, em vez de utilizar objetos simulados (mocks).
 *
 * @Transactional:
 * Executa cada método de teste dentro de uma transação.
 * Ao finalizar cada teste, o Spring realiza automaticamente o rollback,
 * desfazendo as alterações realizadas no banco e evitando que os dados
 * de um teste interfiram na execução dos demais.
 */
@SpringBootTest
@Transactional
@DisplayName("Testes de Integração - CategoriaService (Banco Real)")
class CategoriaServiceTest {

    /*
     * @Autowired:
     * Realiza a injeção de dependência dos componentes gerenciados pelo Spring.
     * São utilizadas instâncias reais do Service e dos Repositories,
     * permitindo executar as operações diretamente no banco de dados.
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
     * @BeforeEach:
     * Executa este método antes de cada teste.
     *
     * Sua finalidade é preparar um cenário inicial independente para cada
     * execução, criando um usuário válido no banco e configurando um DTO
     * com os dados padrão que serão utilizados nos testes de categoria.
     */
    @BeforeEach
    void setUp() {
        usuario = criarUsuarioNoBanco();
        usuarioId = usuario.getId();

        requestDTO = new CategoriaRequestDTO();
        requestDTO.setUsuarioId(usuarioId);
        requestDTO.setNome("Alimentação");
        requestDTO.setTipo("despesa");
        requestDTO.setDescricao("Despesas diárias com comida");
        requestDTO.setCor("#FF0000");
    }

    /**
     * Método auxiliar responsável por criar um usuário válido no banco.
     *
     * Primeiro, verifica se já existe um perfil chamado USER_TESTE.
     * Caso não exista, cria e salva um novo perfil.
     *
     * Em seguida, cria um usuário associado a esse perfil.
     * O e-mail recebe um UUID aleatório para garantir sua unicidade,
     * evitando conflitos com a restrição de e-mails únicos do banco.
     *
     * Esse método garante que os testes tenham um usuário válido
     * para estabelecer o relacionamento entre Usuario e Categoria.
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
     * Método auxiliar responsável por criar e persistir uma categoria
     * diretamente no banco de dados, vinculando-a ao usuário informado.
     *
     * É utilizado principalmente nos testes de consulta, atualização
     * e exclusão, nos quais é necessário ter uma categoria previamente salva.
     */
    private Categoria criarCategoriaNoBanco(Usuario dono) {
        Categoria categoria = new Categoria();
        categoria.setNome("Categoria Teste");
        categoria.setTipo("DESPESA");
        categoria.setDescricao("Descrição da categoria teste");
        categoria.setCor("#00FF00");
        categoria.setUsuario(dono);

        return categoriaRepository.save(categoria);
    }

    // ===============================================================
    // OPERAÇÃO: CREATE (SAVE)
    // ===============================================================

    /**
     * Testa o cadastro de uma categoria quando todos os dados fornecidos
     * são válidos.
     *
     * O teste verifica se o Service consegue realizar o cadastro,
     * retornar os dados da categoria criada e persistir corretamente
     * as informações no banco de dados.
     */
    @Test
    @DisplayName("Deve salvar categoria no banco quando os dados são válidos")
    void deveSalvarCategoriaComSucesso() {
        CategoriaResponseDTO resultado = categoriaService.save(requestDTO);

        // assertNotNull:
        // Verifica se o ID retornado não é nulo.
        // Isso indica que a categoria foi cadastrada e recebeu um identificador.
        assertNotNull(resultado.getId());

        // assertEquals:
        // Compara o valor esperado com o valor realmente retornado pelo Service.
        // Verifica se o tipo, a descrição e a cor foram processados corretamente.
        assertEquals("DESPESA", resultado.getTipo());
        assertEquals("Despesas diárias com comida", resultado.getDescricao());
        assertEquals("#FF0000", resultado.getCor());

        // Consulta diretamente o banco utilizando o ID retornado pelo Service.
        // Essa consulta permite verificar se a categoria foi realmente persistida,
        // e não apenas retornada corretamente pelo método de cadastro.
        Categoria categoriaSalva = categoriaRepository.findById(resultado.getId()).orElseThrow();

        // Confere se os dados armazenados na entidade correspondem aos valores enviados.
        // O assertEquals verifica se o valor esperado é igual ao valor encontrado no banco.
        assertEquals("Alimentação", categoriaSalva.getNome());
        assertEquals("Despesas diárias com comida", categoriaSalva.getDescricao());
        assertEquals("#FF0000", categoriaSalva.getCor());

        // Verifica se a categoria foi associada ao usuário que realizou o cadastro.
        // Garante que o relacionamento entre Categoria e Usuario foi persistido corretamente.
        assertEquals(usuarioId, categoriaSalva.getUsuario().getId());
    }

    /**
     * Testa o comportamento do cadastro quando o ID do usuário não é informado.
     *
     * Como o usuário é obrigatório para vincular a categoria ao seu proprietário,
     * o Service deve interromper a operação e lançar uma BusinessException.
     */
    @Test
    @DisplayName("Deve lançar exceção ao salvar com usuarioId nulo")
    void deveLancarExcecaoQuandoUsuarioIdNuloAoSalvar() {
        requestDTO.setUsuarioId(null);

        // assertThrows:
        // Verifica se a execução do método informado lança a exceção esperada.
        // O primeiro argumento define o tipo de exceção que deve ocorrer.
        // O segundo argumento contém a operação que será executada.
        BusinessException excecao = assertThrows(
                BusinessException.class,
                () -> categoriaService.save(requestDTO)
        );

        // Verifica se a mensagem da exceção corresponde exatamente
        // à mensagem definida na regra de validação do Service.
        assertEquals("O ID do usuário é obrigatório.", excecao.getMessage());
    }

    /**
     * Testa a validação do campo tipo durante o cadastro.
     *
     * O sistema aceita apenas os tipos previstos pela regra de negócio,
     * como RECEITA e DESPESA.
     * Ao receber um tipo inválido, deve impedir o cadastro e lançar uma exceção.
     */
    @Test
    @DisplayName("Deve lançar exceção ao salvar com tipo inválido")
    void deveLancarExcecaoQuandoTipoInvalidoAoSalvar() {
        requestDTO.setTipo("INVESTIMENTO");

        // Verifica se o Service rejeita o tipo informado e lança BusinessException.
        BusinessException excecao = assertThrows(
                BusinessException.class,
                () -> categoriaService.save(requestDTO)
        );

        // assertTrue:
        // Verifica se uma condição é verdadeira.
        // Neste caso, confirma se a mensagem de erro contém a palavra RECEITA,
        // indicando que a mensagem apresenta os tipos permitidos pelo sistema.
        assertTrue(excecao.getMessage().contains("RECEITA"));
    }

    /**
     * Testa o cadastro de uma categoria utilizando um ID de usuário inexistente.
     *
     * O sistema deve verificar se o usuário informado realmente está cadastrado.
     * Caso não encontre o registro correspondente, deve impedir a operação.
     */
    @Test
    @DisplayName("Deve lançar exceção ao salvar para usuário inexistente")
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoAoSalvar() {
        requestDTO.setUsuarioId(UUID.randomUUID());

        // Gera um UUID aleatório que não corresponde ao usuário criado no cenário.
        // Verifica se o Service lança BusinessException ao tentar localizar
        // um usuário inexistente no banco de dados.
        assertThrows(BusinessException.class, () -> categoriaService.save(requestDTO));
    }

    // ===============================================================
    // OPERAÇÃO: READ (FINDBYUSUARIOID / FINDBYID)
    // ===============================================================

    /**
     * Testa a listagem de categorias pertencentes a um usuário específico.
     *
     * São cadastradas duas categorias para o mesmo usuário.
     * Em seguida, o Service é chamado para recuperar os registros vinculados
     * a esse usuário.
     */
    @Test
    @DisplayName("Deve listar todas as categorias pertencentes ao usuário")
    void deveRetornarListaDeCategoriasDoUsuario() {
        // Prepara o cenário salvando duas categorias para o mesmo usuário.
        criarCategoriaNoBanco(usuario);
        criarCategoriaNoBanco(usuario);

        List<CategoriaResponseDTO> resultado = categoriaService.findByUsuarioId(usuarioId);

        // Verifica se o Service retornou uma lista válida, evitando um resultado nulo.
        assertNotNull(resultado);

        // Confirma que foram retornadas exatamente duas categorias,
        // correspondentes aos dois registros criados para o usuário.
        assertEquals(2, resultado.size());
    }

    /**
     * Testa a validação da listagem quando o ID do usuário é nulo.
     *
     * O Service não deve permitir consultas sem um identificador de usuário,
     * pois esse dado é necessário para filtrar as categorias corretamente.
     */
    @Test
    @DisplayName("Deve lançar exceção ao listar categorias com usuarioId nulo")
    void deveLancarExcecaoQuandoUsuarioIdNuloAoListar() {
        // Verifica se a ausência do ID do usuário provoca uma BusinessException,
        // impedindo que a consulta seja executada sem o parâmetro obrigatório.
        assertThrows(BusinessException.class, () -> categoriaService.findByUsuarioId(null));
    }

    /**
     * Testa a busca de uma categoria pelo seu ID quando ela pertence
     * ao usuário que está realizando a consulta.
     *
     * O teste verifica se o Service retorna os dados corretos da categoria,
     * respeitando a associação entre o registro e seu proprietário.
     */
    @Test
    @DisplayName("Deve buscar categoria por ID quando pertencer ao usuário")
    void deveRetornarCategoriaQuandoPertenceAoUsuario() {
        // Cria previamente uma categoria vinculada ao usuário do teste.
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);

        // Realiza a consulta informando o ID da categoria e o ID do proprietário.
        CategoriaResponseDTO resultado = categoriaService.findById(categoriaSalva.getId(), usuarioId);

        // Compara os dados retornados pelo Service com os dados da categoria original.
        // Garante que o identificador e as informações da categoria foram recuperados corretamente.
        assertEquals(categoriaSalva.getId(), resultado.getId());
        assertEquals(categoriaSalva.getNome(), resultado.getNome());
        assertEquals(categoriaSalva.getDescricao(), resultado.getDescricao());
        assertEquals(categoriaSalva.getCor(), resultado.getCor());
    }

    /**
     * Testa o controle de acesso durante a consulta de uma categoria.
     *
     * Uma categoria é criada para um usuário e outro usuário tenta acessá-la.
     * O sistema deve bloquear essa tentativa, impedindo que um usuário consulte
     * informações pertencentes a outra conta.
     *
     * Esse cenário também verifica a proteção contra IDOR
     * (Insecure Direct Object Reference).
     */
    @Test
    @DisplayName("Deve lançar exceção de segurança ao buscar categoria de outro usuário")
    void deveLancarExcecaoAoBuscarCategoriaDeOutroUsuario() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);
        Usuario outroUsuario = criarUsuarioNoBanco();

        // Verifica se a tentativa de consulta com o ID de outro usuário
        // é bloqueada pelo Service por meio de uma BusinessException.
        BusinessException excecao = assertThrows(
                BusinessException.class,
                () -> categoriaService.findById(categoriaSalva.getId(), outroUsuario.getId())
        );

        // Confirma que a mensagem da exceção indica uma tentativa de acesso negado,
        // garantindo que a operação foi barrada pela regra de autorização.
        assertTrue(excecao.getMessage().contains("Acesso negado"));
    }

    /**
     * Testa a busca por uma categoria que não existe no banco de dados.
     *
     * É utilizado um UUID aleatório para simular uma consulta com um identificador
     * que não corresponde a nenhum registro cadastrado.
     * O Service deve informar que a categoria não foi encontrada.
     */
    @Test
    @DisplayName("Deve lançar exceção ao buscar categoria inexistente por ID")
    void deveLancarExcecaoQuandoCategoriaNaoEncontradaPorId() {
        // Gera um identificador aleatório e verifica se a busca por esse ID
        // resulta em uma BusinessException, pois não existe categoria correspondente.
        assertThrows(
                BusinessException.class,
                () -> categoriaService.findById(UUID.randomUUID(), usuarioId)
        );
    }

    // ===============================================================
    // OPERAÇÃO: UPDATE
    // ===============================================================

    /**
     * Testa a atualização de uma categoria existente.
     *
     * Primeiro, uma categoria é persistida no banco.
     * Depois, são enviados novos dados por meio de um DTO de atualização.
     * Por fim, o registro é consultado diretamente no banco para verificar
     * se os valores antigos foram substituídos pelos novos.
     */
    @Test
    @DisplayName("Deve atualizar dados da categoria no banco com sucesso")
    void deveAtualizarCategoriaComSucesso() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);

        // Prepara um DTO contendo os novos dados que deverão substituir
        // as informações atualmente armazenadas na categoria.
        CategoriaRequestDTO atualizacao = new CategoriaRequestDTO();
        atualizacao.setUsuarioId(usuarioId);
        atualizacao.setNome("Transporte");
        atualizacao.setTipo("receita");
        atualizacao.setDescricao("Reembolso de viagens");
        atualizacao.setCor("#0000FF");

        // Executa a operação de atualização informando o ID da categoria,
        // o ID do usuário responsável e os novos dados.
        categoriaService.update(categoriaSalva.getId(), usuarioId, atualizacao);

        // Consulta novamente o registro no banco para verificar o resultado
        // da atualização, garantindo que as alterações foram persistidas.
        Categoria categoriaAtualizada = categoriaRepository.findById(categoriaSalva.getId()).orElseThrow();

        // Compara os novos valores esperados com os valores efetivamente
        // armazenados no banco de dados após a atualização.
        assertEquals("Transporte", categoriaAtualizada.getNome());
        assertEquals("RECEITA", categoriaAtualizada.getTipo());
        assertEquals("Reembolso de viagens", categoriaAtualizada.getDescricao());
        assertEquals("#0000FF", categoriaAtualizada.getCor());
    }

    /**
     * Testa a proteção contra atualização de categorias pertencentes a outros usuários.
     *
     * O cenário cria uma categoria para um usuário e tenta modificá-la utilizando
     * o identificador de outra conta.
     *
     * A operação deve ser bloqueada e os dados originais devem permanecer inalterados.
     */
    @Test
    @DisplayName("Deve lançar exceção de segurança ao tentar atualizar categoria de outro usuário")
    void deveLancarExcecaoAoAtualizarCategoriaDeOutroUsuario() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);
        Usuario outroUsuario = criarUsuarioNoBanco();

        // Tenta atualizar a categoria utilizando o ID de um usuário que não é seu proprietário.
        // O assertThrows verifica se a tentativa é bloqueada com BusinessException.
        assertThrows(
                BusinessException.class,
                () -> categoriaService.update(categoriaSalva.getId(), outroUsuario.getId(), requestDTO)
        );

        // Consulta o registro após a tentativa de atualização não autorizada.
        Categoria categoriaInalterada = categoriaRepository.findById(categoriaSalva.getId()).orElseThrow();

        // Verifica se os dados originais continuam no banco.
        // Isso garante que a tentativa não autorizada não provocou nenhuma alteração.
        assertEquals("Categoria Teste", categoriaInalterada.getNome());
        assertEquals("Descrição da categoria teste", categoriaInalterada.getDescricao());
    }

    // ===============================================================
    // OPERAÇÃO: DELETE
    // ===============================================================

    /**
     * Testa a exclusão de uma categoria existente.
     *
     * Uma categoria é criada e seu identificador é armazenado.
     * Em seguida, o Service executa a exclusão.
     * Por fim, é realizada uma nova consulta ao banco para confirmar
     * que o registro não existe mais.
     */
    @Test
    @DisplayName("Deve remover categoria do banco com sucesso")
    void deveExcluirCategoriaComSucesso() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);
        UUID categoriaId = categoriaSalva.getId();

        // Executa a exclusão informando o ID da categoria e o ID do proprietário.
        categoriaService.delete(categoriaId, usuarioId);

        // Consulta o banco para verificar se a categoria foi removida.
        // O método isEmpty() retorna true quando nenhum registro é encontrado.
        // O assertTrue confirma que a exclusão foi realizada corretamente.
        assertTrue(categoriaRepository.findById(categoriaId).isEmpty());
    }

    /**
     * Testa a segurança da operação de exclusão.
     *
     * Uma categoria é criada para determinado usuário e outro usuário
     * tenta excluí-la.
     *
     * O sistema deve impedir a exclusão de registros que não pertencem
     * ao usuário responsável pela requisição.
     */
    @Test
    @DisplayName("Deve lançar exceção de segurança ao tentar excluir categoria de outro usuário")
    void deveLancarExcecaoAoExcluirCategoriaDeOutroUsuario() {
        Categoria categoriaSalva = criarCategoriaNoBanco(usuario);
        Usuario outroUsuario = criarUsuarioNoBanco();

        // Tenta excluir uma categoria informando o ID de outro usuário.
        // O assertThrows verifica se o Service bloqueia a operação
        // e lança a exceção de negócio esperada.
        assertThrows(
                BusinessException.class,
                () -> categoriaService.delete(categoriaSalva.getId(), outroUsuario.getId())
        );

        // Verifica se a categoria continua presente no banco após a tentativa.
        // isPresent() retorna true quando o registro é encontrado.
        // O assertTrue garante que a exclusão não autorizada não foi realizada.
        assertTrue(categoriaRepository.findById(categoriaSalva.getId()).isPresent());
    }

    /**
     * Testa a validação dos parâmetros obrigatórios da operação de exclusão.
     *
     * O método delete precisa receber tanto o ID da categoria quanto
     * o ID do usuário responsável pela operação.
     * Caso qualquer um desses identificadores seja nulo, a exclusão
     * deve ser interrompida com uma BusinessException.
     */
    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir com IDs nulos")
    void deveLancarExcecaoQuandoIdsNulosAoExcluir() {

        // Primeiro cenário: verifica se a exclusão é bloqueada quando
        // o ID da categoria não é informado.
        assertThrows(
                BusinessException.class,
                () -> categoriaService.delete(null, usuarioId)
        );

        // Segundo cenário: verifica se a exclusão é bloqueada quando
        // o ID do usuário responsável não é informado.
        assertThrows(
                BusinessException.class,
                () -> categoriaService.delete(UUID.randomUUID(), null)
        );
    }
}

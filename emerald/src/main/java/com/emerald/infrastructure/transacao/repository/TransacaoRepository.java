package com.emerald.infrastructure.transacao.repository;

import com.emerald.infrastructure.transacao.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {

    // Recupera todas as transações vinculadas a uma determinada conta bancária.
    List<Transacao> findByContaBancariaId(UUID contaBancariaId);

    // Recupera todas as transações associadas a um usuário específico por meio de suas contas.
    List<Transacao> findByContaBancariaUsuarioId(UUID usuarioId);

    // Filtra as transações de um usuário específico dentro de um intervalo de datas estabelecido.
    List<Transacao> findByContaBancariaUsuarioIdAndDataBetween(UUID usuarioId, Date dataInicio, Date dataFim);

    // Recupera o extrato de uma conta bancária específica filtrando por um intervalo de datas (compatível com o tipo Date da entidade)
    List<Transacao> findByContaBancariaIdAndDataBetween(UUID contaBancariaId, Date dataInicio, Date dataFim);

    /*
     * Esta consulta customizada calcula a soma de todas as despesas (valores negativos)
     * de um usuário, filtradas por uma categoria e um período específico (mês/ano).
     *
     * Em vez de carregar centenas de registros na memória da aplicação para realizar
     * a soma, o cálculo é delegado diretamente para o motor do banco de dados,
     * que retorna apenas o valor numérico final. Esse valor é crucial para atualizar
     * em tempo real o progresso das Metas Financeiras.
     *
     * IMPORTANTE: Utilizada a função 'TO_CHAR' com máscara 'MM/YYYY' para garantir
     * compatibilidade com o PostgreSQL. (A função 'DATE_FORMAT' geraria erro 500
     * pois é exclusiva de dialetos como MySQL).
     */
    @Query("SELECT COALESCE(SUM(t.valor), 0.0) FROM Transacao t " +
            "WHERE t.contaBancaria.usuario.id = :usuarioId " +
            "AND t.categoria.id = :categoriaId " +
            "AND t.valor < 0 " +
            "AND FUNCTION('TO_CHAR', t.data, 'MM/YYYY') = :mesAno")
    Double somarDespesasPorCategoriaEMesAno(
            @Param("usuarioId") UUID usuarioId,
            @Param("categoriaId") UUID categoriaId,
            @Param("mesAno") String mesAno
    );
}
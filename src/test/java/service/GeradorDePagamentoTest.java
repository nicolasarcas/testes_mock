package service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import br.com.alura.leilao.service.GeradorDePagamento;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.validation.constraints.AssertFalse;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;


public class GeradorDePagamentoTest {

    @Mock
    private PagamentoDao pagamentoDao;
    @Mock
    private Clock clockDao;

    @Captor
    private ArgumentCaptor<Pagamento> captor;

    private GeradorDePagamento geradorDePagamento;

    @BeforeEach
    public void beforeEach(){
        MockitoAnnotations.initMocks(this);
        this.geradorDePagamento = new GeradorDePagamento(pagamentoDao,clockDao);
    }

    @Test
    void deveCriarPagamentoParaVencedorDoLeilao(){

        Leilao leilao = leilao();

        LocalDate data = LocalDate.of(2020,12,7);
        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Mockito.when(clockDao.instant()).thenReturn(instant);
        Mockito.when(clockDao.getZone()).thenReturn(ZoneId.systemDefault());
        geradorDePagamento.gerarPagamento(leilao.getLanceVencedor());
        Mockito.verify(pagamentoDao).salvar(captor.capture());
        Pagamento pagamento = captor.getValue();

        Assert.assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());
        Assert.assertEquals(leilao().getLanceVencedor().getValor(), new BigDecimal("900"));
        Assert.assertFalse(pagamento.getPago());
        Assert.assertEquals(leilao.getLanceVencedor().getUsuario(), pagamento.getUsuario());
        Assert.assertEquals(leilao, pagamento.getLeilao());
    }


    private Leilao leilao(){

        Leilao leilao = new Leilao("Celular",
                new BigDecimal("500"),
                new Usuario("Fulano"));

        Lance primeiro = new Lance(new Usuario("Beltrano"),
                new BigDecimal("600"));

        Lance segundo = new Lance(new Usuario("Ciclano"),
                new BigDecimal("900"));

        leilao.propoe(primeiro);
        leilao.propoe(segundo);
        leilao.setLanceVencedor(segundo);

        return leilao;
    }
}

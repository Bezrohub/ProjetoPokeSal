package pokesall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Chave eliminatória de 4 ou 8 treinadores. Sem entrada de teclado. */
public class Torneio {
  private final Batalha batalha;
  private final RepositorioResultados repositorio;
  private final RegistroBatalha registro;
  private final List<Treinador> ranking = new ArrayList<>();

  /**
   * Cria uma instancia de Torneio.
   *
   * @param batalha valor de batalha
   * @param repositorio valor de repositorio
   * @param registro valor de registro
   */
  public Torneio(Batalha batalha, RepositorioResultados repositorio, RegistroBatalha registro) {
    this.batalha = Objects.requireNonNull(batalha);
    this.repositorio = Objects.requireNonNull(repositorio);
    this.registro = Objects.requireNonNull(registro);
  }

  /**
   * Executa realizar.
   *
   * @param inscritos valor de inscritos
   * @return resultado de realizar
   */
  public Treinador realizar(List<Treinador> inscritos) {
    if (inscritos == null || (inscritos.size() != 4 && inscritos.size() != 8)
        || inscritos.stream().anyMatch(Objects::isNull)
        || new java.util.HashSet<>(inscritos).size() != inscritos.size()) {
      throw new IllegalArgumentException("O torneio exige 4 ou 8 treinadores diferentes");
    }

    ranking.clear();
    ranking.addAll(inscritos);
    Map<Treinador, Integer> vitorias = new HashMap<>();
    for (Treinador treinador : inscritos) {
      vitorias.put(treinador, 0);
    }

    List<Treinador> fase = new ArrayList<>(inscritos);
    while (fase.size() > 1) {
      String rodada = fase.size() == 8 ? "Quartas de final"
          : fase.size() == 4 ? "Semifinais" : "Final";
      registro.registrar("CHAVEAMENTO", "=== " + rodada + " ===");
      List<Treinador> classificados = new ArrayList<>();
      for (int i = 0; i < fase.size(); i += 2) {
        Treinador primeiro = fase.get(i);
        Treinador segundo = fase.get(i + 1);
        registro.registrar("CONFRONTO", primeiro.getNome() + " x " + segundo.getNome());
        ResultadoBatalha resultado = batalha.disputar(primeiro, segundo);
        if (resultado == null ||
            !((resultado.getVencedor() == primeiro && resultado.getPerdedor() == segundo)
            || (resultado.getVencedor() == segundo && resultado.getPerdedor() == primeiro))) {
          throw new IllegalStateException("A batalha retornou um vencedor inválido");
        }
        registro.registrar("RESULTADO", "Venceu: " + resultado.getVencedor().getNome()
            + " (" + resultado.getNumeroTurnos() + " turnos)");
        classificados.add(resultado.getVencedor());
        vitorias.put(resultado.getVencedor(), vitorias.get(resultado.getVencedor()) + 1);

        // Uma falha de armazenamento NÃO desfaz a vitória.
        try {
          repositorio.salvar(resultado);
        } catch (RuntimeException erro) {
          registro.registrarErro("Falha ao salvar a batalha", erro);
        }
      }
      fase = classificados;
    }

    // Ranking por número de vitórias; empates mantêm a ordem de inscrição.
    ranking.sort((a, b) -> Integer.compare(vitorias.get(b), vitorias.get(a)));
    return fase.get(0);
  }

  /**
   * Retorna ranking.
   *
   * @return resultado de get ranking
   */
  public List<Treinador> getRanking() {
    return Collections.unmodifiableList(ranking);
  }
}

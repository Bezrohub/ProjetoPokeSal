package pokesall;

/** Entrada do jogador humano. Nos testes pode ser substituída por um mock. */
public interface EntradaJogador {
  /**
   * Executa ler linha.
   *
   * @return resultado de ler linha
   */
  String lerLinha();
}

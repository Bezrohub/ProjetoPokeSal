package pokesall;

/** Fonte substituível de números aleatórios. */
public interface GeradorAleatorio {
  /** Inteiro entre zero (inclusive) e limite (exclusive). */
  int proximoInt(int limite);

  /** Número entre zero (inclusive) e um (exclusive). */
  double proximoDouble();
}

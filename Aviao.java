public class Aviao extends Entidade {
    private static final long serialVersionUID = 1L;
    private final String codigo;
    private final String nome;
    private final int capacidade;

    public Aviao(String codigo, String nome, int capacidade) {
        this.codigo = codigo;
        this.nome = nome;
        this.capacidade = capacidade;
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public int getCapacidade() { return capacidade; }
    
    public String getModelo() { 
        return nome; 
    }
    
    @Override
    public String getId() {
        return codigo;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] - %d assentos", nome, codigo, capacidade);
    }
}
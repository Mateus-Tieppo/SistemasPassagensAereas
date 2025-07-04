public class Cliente extends Entidade {
    private static final long serialVersionUID = 1L;
    private final String rg;
    private final String nome;
    private final String telefone;

    public Cliente(String rg, String nome, String telefone) {
        this.rg = rg;
        this.nome = nome;
        this.telefone = telefone;
    }

    public String getRg() { return rg; }
    public String getNome() { return nome; }
    public String getTelefone() { return telefone; }
    
    @Override
    public String getId() {
        return rg;
    }

    @Override
    public String toString() {
        return String.format("%s (RG: %s, Tel: %s)", nome, rg, telefone);
    }
}
import java.util.Collection;
import java.util.List;

public class GeradorRelatorio implements Relatorio {
    private final SistemaVendaPassagens sistema;
    
    public GeradorRelatorio(SistemaVendaPassagens sistema) {
        this.sistema = sistema;
    }

    @Override
    public String gerar() {
        StringBuilder sb = new StringBuilder();
        sb.append("RELATORIO COMPLETO DO SISTEMA\n\n");
        
        sb.append("CLIENTES CADASTRADOS\n");
        sb.append("--------------------\n");
        Collection<Cliente> clientes = sistema.getClientes();
        if (clientes.isEmpty()) {
            sb.append("Nenhum cliente cadastrado.\n\n");
        } else {
            clientes.forEach(c -> sb.append(" * ").append(c).append("\n"));
            sb.append("\nTotal: ").append(clientes.size()).append(" cliente(s)\n\n");
        }
        
        sb.append("AVIOES CADASTRADOS\n");
        sb.append("------------------\n");
        Collection<Aviao> avioes = sistema.getAvioes();
        if (avioes.isEmpty()) {
            sb.append("Nenhum aviao cadastrado.\n\n");
        } else {
            avioes.forEach(a -> sb.append(" * ").append(a).append("\n"));
            sb.append("\nTotal: ").append(avioes.size()).append(" aviao(es)\n\n");
        }
        
        sb.append("VOOS CADASTRADOS\n");
        sb.append("----------------\n");
        Collection<Voo> voos = sistema.getVoos();
        if (voos.isEmpty()) {
            sb.append("Nenhum voo cadastrado.\n\n");
        } else {
            voos.forEach(v -> sb.append(" * ").append(v).append("\n"));
            sb.append("\nTotal: ").append(voos.size()).append(" voo(s)\n\n");
        }
        
        sb.append("PASSAGENS VENDIDAS\n");
        sb.append("------------------\n");
        List<Passagem> passagens = sistema.getPassagens();
        if (passagens.isEmpty()) {
            sb.append("Nenhuma passagem vendida.\n\n");
        } else {
            passagens.forEach(p -> sb.append(" * ").append(p).append("\n"));
            sb.append("\nTotal: ").append(passagens.size()).append(" passagem(s)\n");
        }
        
        return sb.toString();
    }
}
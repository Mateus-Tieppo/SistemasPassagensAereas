import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Passagem extends Entidade {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final Cliente cliente;
    private final Voo voo;
    private final LocalDateTime dataCompra;

    public Passagem(String id, Cliente cliente, Voo voo, LocalDateTime dataCompra) {
        this.id = id;
        this.cliente = cliente;
        this.voo = voo;
        this.dataCompra = dataCompra;
    }

    @Override
    public String getId() {
        return id;
    }

    public Cliente getCliente() { return cliente; }
    public Voo getVoo() { return voo; }
    public LocalDateTime getDataCompra() { return dataCompra; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("%s - %s | Voo: %s | Compra: %s", 
                id, cliente.getNome(), voo.getCodigo(), dataCompra.format(formatter));
    }
}
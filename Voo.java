import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;

public class Voo extends Entidade {
    private static final long serialVersionUID = 1L;
    private final String codigo;
    private final String origem;
    private final String destino;
    private final LocalDateTime horario;
    private final Aviao aviao;
    private StatusVoo status;
    private int assentosVendidos;
    private final Queue<Cliente> filaEspera = new LinkedList<>();

    public Voo(String codigo, String origem, String destino, LocalDateTime horario, Aviao aviao) {
        this.codigo = codigo;
        this.origem = origem;
        this.destino = destino;
        this.horario = horario;
        this.aviao = aviao;
        this.status = StatusVoo.AGENDADO;
        this.assentosVendidos = 0;
    }

    public String getCodigo() { return codigo; }
    public String getOrigem() { return origem; }
    public String getDestino() { return destino; }
    public LocalDateTime getHorario() { return horario; }
    public Aviao getAviao() { return aviao; }
    public StatusVoo getStatus() { return status; }
    public int getAssentosDisponiveis() { return aviao.getCapacidade() - assentosVendidos; }
    public Queue<Cliente> getFilaEspera() { return filaEspera; }

    public void setStatus(StatusVoo status) { this.status = status; }
    public void incrementarAssentosVendidos() { assentosVendidos++; }
    public void setAssentosVendidos(int assentosVendidos) { this.assentosVendidos = assentosVendidos; }

    public int getAssentosVendidos() {
        return assentosVendidos;
    }
    
    @Override
    public String getId() {
        return codigo;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("%s: %s -> %s | %s | %s (%d/%d assentos)",
                codigo, origem, destino, horario.format(formatter), 
                status, getAssentosDisponiveis(), aviao.getCapacidade());
    }
}
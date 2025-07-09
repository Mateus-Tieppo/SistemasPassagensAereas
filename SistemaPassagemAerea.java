// Sistema de Venda de Passagens Aéreas
// Desenvolvido por:
// - Mateus Tieppo

import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class SistemaPassagemAerea {
    private SistemaVendaPassagens sistema = new SistemaVendaPassagens();
    private JFrame frame;
    private JTextArea outputArea;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SistemaPassagemAerea().iniciar());
    }

    public void iniciar() {
        configurarJanelaPrincipal();
        configurarComponentes();
        frame.setVisible(true);
    }

    private void configurarJanelaPrincipal() {
        frame = new JFrame("Sistema de Passagens Aereas");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1100, 750);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(245, 240, 255));
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcao = JOptionPane.showConfirmDialog(
                    frame, 
                    "Deseja salvar os dados antes de sair?", 
                    "Sair", 
                    JOptionPane.YES_NO_CANCEL_OPTION, 
                    JOptionPane.QUESTION_MESSAGE
                );
                if (opcao == JOptionPane.YES_OPTION) {
                    salvarDados();
                    System.exit(0);
                } else if (opcao == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    private void configurarComponentes() {
        JPanel titlePanel = new GradientPanel();
        JLabel title = new JLabel("SISTEMA DE PASSAGENS AEREAS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        titlePanel.add(title);
        frame.add(titlePanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(245, 240, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 15, 15));
        gridPanel.setOpaque(false);

        String[] botoes = {
            "Cadastrar Cliente", "Cadastrar Aviao", "Cadastrar Voo", 
            "Vender Passagem", "Cancelar Voo", "Relatorios",
            "Salvar Dados", "Carregar Dados", "Sair"
        };
        
        Color[] cores = {
            new Color(128, 0, 128), new Color(147, 39, 143), 
            new Color(148, 0, 211), new Color(220, 20, 60),
            new Color(186, 85, 211), new Color(178, 34, 34),
            new Color(0, 100, 0),    
            new Color(70, 130, 180), 
            new Color(75, 0, 130)    
        };

        for (int i = 0; i < botoes.length; i++) {
            JButton botao = criarBotaoEstilizado(botoes[i], cores[i]);
            gridPanel.add(botao);
        }

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(gridPanel);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        outputArea = new JTextArea(16, 45);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        outputArea.setBackground(new Color(255, 255, 240));
        outputArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Saida do Sistema"));

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setOpaque(false);
        outputPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(outputPanel, BorderLayout.SOUTH);

        frame.add(mainPanel, BorderLayout.CENTER);
    }

    private JButton criarBotaoEstilizado(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(
                    0, 0, bgColor.brighter().brighter(), 
                    0, getHeight(), bgColor.darker().darker()
                );
                
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.setColor(new Color(0, 0, 0, 50));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(200, 70));
        button.addActionListener(this::manipularClique);
        return button;
    }

    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            GradientPaint gp = new GradientPaint(
                0, 0, new Color(85, 10, 150), 
                getWidth(), getHeight(), new Color(138, 30, 138)
            );
            
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void manipularClique(ActionEvent e) {
        String comando = e.getActionCommand();
        outputArea.setText("");

        try {
            switch (comando) {
                case "Cadastrar Cliente" -> cadastrarCliente();
                case "Cadastrar Aviao" -> cadastrarAviao();
                case "Cadastrar Voo" -> cadastrarVoo();
                case "Vender Passagem" -> venderPassagem();
                case "Cancelar Voo" -> cancelarVoo();
                case "Relatorios" -> mostrarRelatorios();
                case "Salvar Dados" -> salvarDados();
                case "Carregar Dados" -> carregarDados();
                case "Sair" -> {
                    int opcao = JOptionPane.showConfirmDialog(
                        frame, 
                        "Deseja salvar os dados antes de sair?", 
                        "Sair", 
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.QUESTION_MESSAGE
                    );
                    if (opcao == JOptionPane.YES_OPTION) {
                        salvarDados();
                    }
                    System.exit(0);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cadastrarCliente() {
        try {
            String rg = mostrarInputDialog("RG do cliente (7-10 digitos):", "Cadastro de Cliente");
            if (rg == null) return;
            
            String nome = mostrarInputDialog("Nome completo:", "Cadastro de Cliente");
            if (nome == null) return;
            
            String telefone = mostrarInputDialog("Telefone (10-11 digitos):", "Cadastro de Cliente");
            if (telefone == null) return;
            
            sistema.cadastrarCliente(rg, nome, telefone);
            outputArea.setText("CLIENTE CADASTRADO COM SUCESSO!\n\n" +
                    "Nome: " + nome + "\n" +
                    "RG: " + rg + "\n" +
                    "Telefone: " + telefone);
        } catch (SistemaVendaPassagens.SistemaException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void cadastrarAviao() {
        try {
            String codigo = mostrarInputDialog("Codigo do aviao (formato A999):", "Cadastro de Aviao");
            if (codigo == null) return;
            
            String nome = mostrarInputDialog("Modelo do aviao:", "Cadastro de Aviao");
            if (nome == null) return;
            
            String capacidadeStr = mostrarInputDialog("Capacidade de assentos (1-1000):", "Cadastro de Aviao");
            if (capacidadeStr == null) return;
            
            int capacidade = Integer.parseInt(capacidadeStr);
            sistema.cadastrarAviao(codigo, nome, capacidade);
            outputArea.setText("AVIAO CADASTRADO COM SUCESSO!\n\n" +
                    "Modelo: " + nome + "\n" +
                    "Codigo: " + codigo + "\n" +
                    "Capacidade: " + capacidade + " assentos");
        } catch (NumberFormatException ex) {
            mostrarErro("Capacidade deve ser um numero valido!");
        } catch (SistemaVendaPassagens.SistemaException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void cadastrarVoo() {
        try {
            String codigoVoo = mostrarInputDialog("Codigo do voo (formato V999):", "Cadastro de Voo");
            if (codigoVoo == null) return;
            
            String origem = mostrarInputDialog("Cidade de origem:", "Cadastro de Voo");
            if (origem == null) return;
            
            String destino = mostrarInputDialog("Cidade de destino:", "Cadastro de Voo");
            if (destino == null) return;
            
            String horario = mostrarInputDialog("Data e hora (dd/MM/yyyy HH:mm):", "Cadastro de Voo");
            if (horario == null) return;
            
            String codigoAviao = mostrarInputDialog("Codigo do aviao:", "Cadastro de Voo");
            if (codigoAviao == null) return;
            
            sistema.cadastrarVoo(codigoVoo, origem, destino, horario, codigoAviao);
            outputArea.setText("VOO CADASTRADO COM SUCESSO!\n\n" +
                    "Codigo: " + codigoVoo + "\n" +
                    "Rota: " + origem + " -> " + destino + "\n" +
                    "Horario: " + horario + "\n" +
                    "Aviao: " + codigoAviao);
        } catch (SistemaVendaPassagens.SistemaException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void venderPassagem() {
        try {
            String rg = mostrarInputDialog("RG do cliente:", "Venda de Passagem");
            if (rg == null) return;
            
            String codigoVoo = mostrarInputDialog("Codigo do voo:", "Venda de Passagem");
            if (codigoVoo == null) return;
            
            sistema.venderPassagem(rg, codigoVoo);
            
            Cliente cliente = sistema.getClientes().stream()
                .filter(c -> c.getRg().equals(rg))
                .findFirst()
                .orElseThrow();
            
            Voo voo = sistema.getVoos().stream()
                .filter(v -> v.getCodigo().equals(codigoVoo))
                .findFirst()
                .orElseThrow();
            
            outputArea.setText("PASSAGEM VENDIDA COM SUCESSO!\n\n" +
                    "Cliente: " + cliente.getNome() + "\n" +
                    "Voo: " + voo.getCodigo() + " (" + voo.getOrigem() + " -> " + voo.getDestino() + ")\n" +
                    "Horario do voo: " + voo.getHorario().format(dateFormatter) + "\n" +
                    "Assentos disponiveis: " + voo.getAssentosDisponiveis() + "/" + voo.getAviao().getCapacidade());
        } catch (SistemaVendaPassagens.SistemaException ex) {
            if (ex instanceof SistemaVendaPassagens.AssentoIndisponivelException) {
                outputArea.setText(ex.getMessage() + "\n\nO cliente foi adicionado na fila de espera.");
            } else {
                mostrarErro(ex.getMessage());
            }
        } catch (java.util.NoSuchElementException ex) {
            mostrarErro("Cliente ou voo nao encontrado!");
        }
    }

    private void cancelarVoo() {
        try {
            String codigoVoo = mostrarInputDialog("Codigo do voo a cancelar:", "Cancelamento de Voo");
            if (codigoVoo == null) return;
            
            sistema.cancelarVoo(codigoVoo);
            
            Voo voo = sistema.getVoos().stream()
                .filter(v -> v.getCodigo().equals(codigoVoo))
                .findFirst()
                .orElseThrow();
            
            outputArea.setText("VOO CANCELADO COM SUCESSO!\n\n" +
                    "Codigo: " + voo.getCodigo() + "\n" +
                    "Rota: " + voo.getOrigem() + " -> " + voo.getDestino() + "\n" +
                    "Horario: " + voo.getHorario().format(dateFormatter) + "\n" +
                    "Status: " + voo.getStatus() + "\n\n" +
                    "Todos os passageiros serao reembolsados.");
        } catch (SistemaVendaPassagens.SistemaException | java.util.NoSuchElementException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void mostrarRelatorios() {
        GeradorRelatorio gerador = new GeradorRelatorio(sistema);
        outputArea.setText(gerador.gerar());
    }
    
    private String mostrarInputDialog(String mensagem, String titulo) {
        return JOptionPane.showInputDialog(frame, mensagem, titulo, JOptionPane.QUESTION_MESSAGE);
    }
    
    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(frame, mensagem, "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
    }

    private void salvarDados() {
        try {
            sistema.salvarCSV();
            outputArea.setText("Dados salvos em CSV com sucesso!");
        } catch (SistemaVendaPassagens.SistemaException e) {
            mostrarErro("Erro ao salvar: " + e.getMessage());
        }
    }

    private void carregarDados() {
        int confirm = JOptionPane.showConfirmDialog(
            frame,
            "Isso substituirá todos os dados atuais. Continuar?",
            "Confirmar Carregamento",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try {
            sistema.carregarCSV();
            outputArea.setText("Dados carregados de CSV com sucesso!");
        } catch (SistemaVendaPassagens.SistemaException e) {
            mostrarErro("Erro ao carregar: " + e.getMessage());
        }
    }
}

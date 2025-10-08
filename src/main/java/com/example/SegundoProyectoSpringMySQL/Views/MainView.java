package com.example.SegundoProyectoSpringMySQL.Views;

import com.example.SegundoProyectoSpringMySQL.entities.Categoria;
import com.example.SegundoProyectoSpringMySQL.entities.Mensaje;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MainView extends JFrame {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080";

    public MainView(String title, RestTemplate restTemplate) {
        super(title);
        this.restTemplate = restTemplate;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Categorías", new CategoriaPanel());
        tabs.add("Mensajes", new MensajePanel());
        setContentPane(tabs);
    }

    // ---------------- Panel Categorías ----------------
    private class CategoriaPanel extends JPanel {
        private JTable table;
        private DefaultTableModel model;
        private JTextField txtId;
        private JTextField txtNombre;

        CategoriaPanel() {
            setLayout(new BorderLayout());
            model = new DefaultTableModel(new Object[]{"ID","Nombre"},0) {
                public boolean isCellEditable(int r,int c){return false;}
            };
            table = new JTable(model);
            table.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                    int row = table.getSelectedRow();
                    txtId.setText(model.getValueAt(row,0).toString());
                    txtNombre.setText(model.getValueAt(row,1).toString());
                }
            });
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel form = new JPanel(new GridLayout(2,2,5,5));
            txtId = new JTextField(); txtId.setEditable(false);
            txtNombre = new JTextField();
            form.add(new JLabel("ID:")); form.add(txtId);
            form.add(new JLabel("Nombre:")); form.add(txtNombre);

            JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton btnCargar = new JButton("Cargar");
            JButton btnNuevo = new JButton("Crear");
            JButton btnEditar = new JButton("Editar");
            JButton btnEliminar = new JButton("Eliminar");
            JButton btnLimpiar = new JButton("Limpiar");
            acciones.add(btnCargar);acciones.add(btnNuevo);acciones.add(btnEditar);
            acciones.add(btnEliminar);acciones.add(btnLimpiar);

            btnCargar.addActionListener(e -> cargar());
            btnNuevo.addActionListener(e -> crear());
            btnEditar.addActionListener(e -> editar());
            btnEliminar.addActionListener(e -> eliminar());
            btnLimpiar.addActionListener(e -> limpiar());

            JPanel south = new JPanel(new BorderLayout());
            south.add(form, BorderLayout.CENTER);
            south.add(acciones, BorderLayout.SOUTH);
            add(south, BorderLayout.SOUTH);

            cargar();
        }

        private void cargar() {
            model.setRowCount(0);
            ResponseEntity<Categoria[]> resp = restTemplate.getForEntity(baseUrl + "/categorias", Categoria[].class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody()!=null) {
                for (Categoria c : resp.getBody()) {
                    model.addRow(new Object[]{c.getId(), c.getNombreCategoria()});
                }
            }
        }

        private void crear() {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) { msg("Nombre requerido"); return; }
            Categoria c = new Categoria();
            c.setNombreCategoria(nombre);
            ResponseEntity<Categoria> r = restTemplate.postForEntity(baseUrl + "/categorias", c, Categoria.class);
            if (r.getStatusCode().is2xxSuccessful() || r.getStatusCode()==HttpStatus.CREATED) {
                cargar(); limpiar(); msg("Creado");
            }
        }

        private void editar() {
            if (txtId.getText().isEmpty()) { msg("Selecciona una fila"); return; }
            Long id = Long.valueOf(txtId.getText());
            Categoria c = new Categoria();
            c.setNombreCategoria(txtNombre.getText().trim());

            HttpHeaders h = new HttpHeaders();
            h.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Categoria> entity = new HttpEntity<>(c, h);
            ResponseEntity<Categoria> resp = restTemplate.exchange(baseUrl + "/categorias/" + id, HttpMethod.PUT, entity, Categoria.class);
            if (resp.getStatusCode().is2xxSuccessful()) { cargar(); msg("Actualizado"); }
        }

        private void eliminar() {
            if (txtId.getText().isEmpty()) { msg("Selecciona una fila"); return; }
            int opt = JOptionPane.showConfirmDialog(this,"¿Eliminar?","Confirmar",JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION) return;
            restTemplate.delete(baseUrl + "/categorias/" + txtId.getText());
            cargar(); limpiar(); msg("Eliminado");
        }

        private void limpiar() {
            txtId.setText("");
            txtNombre.setText("");
            table.clearSelection();
        }

        private void msg(String m){ JOptionPane.showMessageDialog(this,m); }
    }

    // ---------------- Panel Mensajes ----------------
    private class MensajePanel extends JPanel {
        private JTable table;
        private DefaultTableModel model;
        private JTextField txtId, txtTitulo;
        private JTextArea txtTexto;
        private JComboBox<CategoriaItem> cbCategoria;
        private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        MensajePanel() {
            setLayout(new BorderLayout());
            model = new DefaultTableModel(new Object[]{"ID","Título","Texto","CategoriaId","Fecha"},0) {
                public boolean isCellEditable(int r,int c){return false;}
            };
            table = new JTable(model);
            table.setAutoCreateRowSorter(true);
            table.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                    int row = table.convertRowIndexToModel(table.getSelectedRow());
                    txtId.setText(model.getValueAt(row,0).toString());
                    txtTitulo.setText(model.getValueAt(row,1).toString());
                    txtTexto.setText(model.getValueAt(row,2).toString());
                    Object val = model.getValueAt(row,3);
                    Long catId = (val instanceof Long)? (Long) val : null;
                    selectCategoria(catId);
                }
            });
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(3,3,3,3);
            gc.fill = GridBagConstraints.HORIZONTAL;
            int y=0;
            txtId = new JTextField(); txtId.setEditable(false);
            txtTitulo = new JTextField();
            txtTexto = new JTextArea(4,20);
            cbCategoria = new JComboBox<>();
            JScrollPane spTexto = new JScrollPane(txtTexto);

            addField(form,gc,y++,"ID:",txtId);
            addField(form,gc,y++,"Título:",txtTitulo);
            addField(form,gc,y++,"Texto:",spTexto);
            addField(form,gc,y++,"Categoría:",cbCategoria);

            JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton btnCargar = new JButton("Cargar");
            JButton btnNuevo = new JButton("Crear");
            JButton btnEditar = new JButton("Editar");
            JButton btnEliminar = new JButton("Eliminar");
            JButton btnLimpiar = new JButton("Limpiar");
            acciones.add(btnCargar);acciones.add(btnNuevo);acciones.add(btnEditar);
            acciones.add(btnEliminar);acciones.add(btnLimpiar);

            btnCargar.addActionListener(e -> { cargarCategorias(); cargarMensajes(); });
            btnNuevo.addActionListener(e -> crear());
            btnEditar.addActionListener(e -> editar());
            btnEliminar.addActionListener(e -> eliminar());
            btnLimpiar.addActionListener(e -> limpiar());

            JPanel south = new JPanel(new BorderLayout());
            south.add(form, BorderLayout.CENTER);
            south.add(acciones, BorderLayout.SOUTH);
            add(south, BorderLayout.SOUTH);

            cargarCategorias();
            cargarMensajes();
        }

        private void addField(JPanel p, GridBagConstraints gc, int y, String label, Component comp) {
            gc.gridx=0;gc.gridy=y;gc.weightx=0; p.add(new JLabel(label),gc);
            gc.gridx=1;gc.weightx=1; p.add(comp,gc);
        }

        private void cargarCategorias() {
            cbCategoria.removeAllItems();
            ResponseEntity<Categoria[]> resp = restTemplate.getForEntity(baseUrl + "/categorias", Categoria[].class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody()!=null) {
                for (Categoria c : resp.getBody()) {
                    cbCategoria.addItem(new CategoriaItem(c.getId(), c.getNombreCategoria()));
                }
            }
        }

        private void cargarMensajes() {
            model.setRowCount(0);
            ResponseEntity<Mensaje[]> resp = restTemplate.getForEntity(baseUrl + "/mensajes", Mensaje[].class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody()!=null) {
                for (Mensaje m: resp.getBody()) {
                    model.addRow(new Object[]{
                            m.getId(),
                            m.getTitulo(),
                            m.getTexto(),
                            m.getCategoria()!=null? m.getCategoria().getId(): null,
                            m.getFechaCreacion()!=null? dtf.format(m.getFechaCreacion()): ""
                    });
                }
            }
        }

        private void crear() {
            if (txtTitulo.getText().trim().isEmpty()) { msg("Título requerido"); return; }
            if (cbCategoria.getSelectedItem()==null) { msg("Selecciona categoría"); return; }
            Long catId = ((CategoriaItem)cbCategoria.getSelectedItem()).id();
            Map<String,Object> payload = new HashMap<>();
            payload.put("titulo", txtTitulo.getText().trim());
            payload.put("texto", txtTexto.getText().trim());
            payload.put("categoria", Map.of("id", catId));
            ResponseEntity<Mensaje> r = restTemplate.postForEntity(baseUrl + "/mensajes", payload, Mensaje.class);
            if (r.getStatusCode().is2xxSuccessful() || r.getStatusCode()==HttpStatus.CREATED) {
                cargarMensajes(); limpiar(); msg("Mensaje creado");
            }
        }

        private void editar() {
            if (txtId.getText().isEmpty()) { msg("Selecciona un mensaje"); return; }
            Long id = Long.valueOf(txtId.getText());
            Long catId = cbCategoria.getSelectedItem()==null? null: ((CategoriaItem)cbCategoria.getSelectedItem()).id();
            Map<String,Object> payload = new HashMap<>();
            payload.put("id", id);
            payload.put("titulo", txtTitulo.getText().trim());
            payload.put("texto", txtTexto.getText().trim());
            payload.put("categoria", catId!=null? Map.of("id", catId): null);

            HttpHeaders h = new HttpHeaders();
            h.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String,Object>> entity = new HttpEntity<>(payload, h);
            ResponseEntity<Mensaje> resp = restTemplate.exchange(baseUrl + "/mensajes/" + id, HttpMethod.PUT, entity, Mensaje.class);
            if (resp.getStatusCode().is2xxSuccessful()) {
                cargarMensajes(); msg("Actualizado");
            }
        }

        private void eliminar() {
            if (txtId.getText().isEmpty()) { msg("Selecciona un mensaje"); return; }
            int opt = JOptionPane.showConfirmDialog(this,"¿Eliminar?","Confirmar",JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION) return;
            restTemplate.delete(baseUrl + "/mensajes/" + txtId.getText());
            cargarMensajes(); limpiar(); msg("Eliminado");
        }

        private void limpiar() {
            txtId.setText("");
            txtTitulo.setText("");
            txtTexto.setText("");
            table.clearSelection();
        }

        private void selectCategoria(Long id) {
            if (id == null) { cbCategoria.setSelectedIndex(-1); return; }
            for (int i=0;i<cbCategoria.getItemCount();i++) {
                if (cbCategoria.getItemAt(i).id().equals(id)) {
                    cbCategoria.setSelectedIndex(i);
                    break;
                }
            }
        }

        private void msg(String m){ JOptionPane.showMessageDialog(this,m); }

        record CategoriaItem(Long id, String nombre) {
            public String toString(){ return id + " - " + nombre; }
        }
    }
}


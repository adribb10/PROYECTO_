/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import java.sql.*;
import javax.swing.JOptionPane;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.HeadlessException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.panamahitek.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;



import com.panamahitek.ArduinoException;
import com.panamahitek.PanamaHitek_Arduino ;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import Ventanas.*;
import com.itextpdf.text.Paragraph;

import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import com.mysql.jdbc.Connection;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.HeadlessException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;





/**
 *
 * @author Adrian
 */
public class Ventana extends javax.swing.JFrame implements Runnable{
   
     java.sql.Connection con=null;
     ResultSet rs=null;
     //conectar a base de datos
    public java.sql.Connection conexion(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con=DriverManager.getConnection("jdbc:mysql://localhost/skill acceso","root","");
           System.out.println("conexion establecida");
           JOptionPane.showMessageDialog(null, "conexion establecida");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("error de conexion");
            JOptionPane.showMessageDialog(null, "error de conexion"+e);
        }
        return con;
    }
    
    
    
    
   final XYSeries Serie=new XYSeries("Temperatura Celsius");
    final XYSeriesCollection Coleccion=new XYSeriesCollection();
    DefaultTableModel modelo;
    
    
    String hora,minutos,segundos;
    Thread hilo;
    PanamaHitek_Arduino ino;
    static JFreeChart Grafica;
     ChartPanel panel;
     
    int cont  = 0;  
    float celsius=0;
     SerialPortEventListener listener=new SerialPortEventListener() {
        @Override
        public void serialEvent(SerialPortEvent spe) {
            try {
                if (ino.isMessageAvailable()==true){//Si llega un valor
                    cont++;
                    celsius=(500*Float.parseFloat(ino.printMessage())/1024);//transformo un string a float
                    Celsius.setText(String.format("%.2f", celsius)+" C");//Etiqueta imprimo C
                    Serie.add(cont,celsius);
                   
                }
            } catch (SerialPortException ex) {
                Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ArduinoException ex) {
                Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
    public static boolean statusHilo=false;
    
    
    
    public static String fecha(){
    Date fecha = new Date();
    SimpleDateFormat formatofecha = new SimpleDateFormat ("dd/MM/YYYY");
    return formatofecha.format(fecha);

}
    
    
    
    public Ventana() {
        initComponents();
        bfecha.setText(fecha());
        hilo = new Thread(this);
        hilo.start();
        setVisible(true);
        this.setLocationRelativeTo(null);
        
     
  
    
    ino = new PanamaHitek_Arduino ();
        try{
            ino.arduinoRXTX("COM5", 9600, listener);
        }catch (ArduinoException ex){
            Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        //ino.arduinoRXTX("COM5", ABORT,events);
        //ino.arduinoRXTX("COM5", ABORT,events);
         Serie.add(0,0);
            Coleccion.addSeries(Serie);
            
            Grafica=ChartFactory.createXYLineChart("Temperatura Celsius vs. Tiempo","Tiempo","Temperatura Celsius",
                    Coleccion,PlotOrientation.VERTICAL, true, true, false);
        
            Timer timer = new Timer();
            TimerTask tarea = new TimerTask(){
            @Override
            public void run() {
                try
        {
            Connection con;
            Class.forName("com.mysql.jdbc.Driver");
            con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/temperatura","root","");
            System.out.println("Base conectada");
            PreparedStatement pps = con.prepareStatement("INSERT into datos(Temperatura,Fecha,Hora,Max,Min,Prom) VALUES (?,?,?,?,?,?)");
            pps.setString(1,Celsius.getText());
            pps.setString(2,bfecha.getText());
            pps.setString(3,bhora.getText());
            pps.setString(4,jmin.getText());
            pps.setString(5,bmin.getText());
            pps.setString(6,jprom.getText());
            
            //variable tem
            //pps.setString(3,tempPro.getText());
            
            pps.executeUpdate();
            
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(Temperatura) FROM datos");
            while (rs.next())
             jmin.setText(String.valueOf(rs.getString(1)));
            
            con.close();  
            
        }catch(Exception e){
            System.out.println("Error " + e);
        }
                try
        {
            Connection con;
            Class.forName("com.mysql.jdbc.Driver");
            con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/temperatura","root","");
            System.out.println("Base conectada");
            PreparedStatement pps = con.prepareStatement("INSERT into datos(Temperatura,Fecha,Hora,Max,Min,Prom) VALUES (?,?,?,?,?,?)");
            pps.setString(1,Celsius.getText());
            pps.setString(2,bfecha.getText());
            pps.setString(3,bhora.getText());
            pps.setString(4,jmin.getText());
            pps.setString(5,bmin.getText());
            pps.setString(6,jprom.getText());
            
            //variable tem
            //pps.setString(3,tempPro.getText());
            
            pps.executeUpdate();
            
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MIN(Temperatura) from datos");
            while (rs.next())
             bmin.setText(String.valueOf(rs.getString(1)));
            
            con.close();  
            
        }catch(Exception e){
            System.out.println("Error " + e);
        }
                  try
        {
            Connection con;
            Class.forName("com.mysql.jdbc.Driver");
            con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/temperatura","root","");
            System.out.println("Base conectada");
            PreparedStatement pps = con.prepareStatement("INSERT into datos(Temperatura,Fecha,Hora,Max,Min,Prom) VALUES (?,?,?,?,?,?)");
            pps.setString(1,Celsius.getText());
            pps.setString(2,bfecha.getText());
            pps.setString(3,bhora.getText());
            pps.setString(4,jmin.getText());
            pps.setString(5,bmin.getText());
            pps.setString(6,jprom.getText());
            
            //variable tem
            //pps.setString(3,tempPro.getText());
            
            pps.executeUpdate();
            
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT AVG(Temperatura) from datos");
            while (rs.next())
             jprom.setText(String.valueOf(rs.getString(1)));
            
            con.close();  
            
        }catch(Exception e){
            System.out.println("Error " + e);
        }
                    }
            };   
            timer.schedule(tarea, 5000, 15000);
    ChartPanel panel=new ChartPanel(Grafica);
        JFrame Ventana=new JFrame("Celsius");
        Ventana.getContentPane().add(panel);
        Ventana.pack();
        Ventana.setVisible(true);
        Ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    
    
    
  
    
    
    
    
    
     
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        label_status = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        Celsius = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        bfecha = new javax.swing.JTextField();
        bhora = new javax.swing.JTextField();
        bmin = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jmin = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jprom = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(102, 255, 255));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        label_status.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        getContentPane().add(label_status, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 315, -1, -1));

        jPanel1.setBackground(new java.awt.Color(204, 251, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(204, 255, 204)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(153, 153, 255));
        jLabel4.setText("TEMPERATURA");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 54, -1, 40));

        Celsius.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CelsiusActionPerformed(evt);
            }
        });
        jPanel1.add(Celsius, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 102, 30));

        jButton1.setBackground(new java.awt.Color(255, 102, 102));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagen/paper-graph_icon-icons.com_53090 (1).png"))); // NOI18N
        jButton1.setText("Graficar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 240, -1, -1));

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(153, 153, 255));
        jLabel5.setText("Fecha");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 100, -1, -1));

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(153, 153, 255));
        jLabel6.setText("Hora");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 130, -1, -1));

        bfecha.setText("DD/MM/YYYY");
        jPanel1.add(bfecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 100, -1, -1));

        bhora.setText("00:00:00");
        jPanel1.add(bhora, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 130, -1, -1));

        bmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bminActionPerformed(evt);
            }
        });
        jPanel1.add(bmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(136, 170, 60, 30));

        jButton5.setBackground(new java.awt.Color(255, 102, 102));
        jButton5.setFont(new java.awt.Font("Times New Roman", 3, 14)); // NOI18N
        jButton5.setText("Generar PDF");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, -1, -1));
        jPanel1.add(jmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 170, 60, 30));

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(153, 153, 255));
        jLabel2.setText("Prom:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 160, -1, 50));
        jPanel1.add(jprom, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 170, 50, 30));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 102, 255));
        jLabel3.setText("Sistema de temperatura");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 420, 40));

        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(153, 153, 255));
        jLabel8.setText("Min:");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 170, 40, 30));

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(153, 153, 255));
        jLabel10.setText("Max:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 170, -1, 30));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagen/thermometer_cold_winter_temperature_icon_175548 (1).png"))); // NOI18N
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 60, -1, -1));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagen/icons8-male-64.png"))); // NOI18N
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 240, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 430, 300));

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagen/icons8-termómetro-96.png"))); // NOI18N

        jPanel3.setBackground(new java.awt.Color(235, 236, 204));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagen/icons8-configuraciones-de-imac-96.png"))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 41, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel1)
                .addContainerGap(63, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 529, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jLabel7.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 110, 300));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    public void hora(){
        Calendar calendario = new GregorianCalendar();
        Date horaactual = new Date();
        calendario.setTime(horaactual);
        hora = calendario.get(Calendar.HOUR_OF_DAY)>9?""+calendario.get(Calendar.HOUR_OF_DAY):"0"+calendario.get(Calendar.HOUR_OF_DAY);
        minutos=calendario.get(Calendar.MINUTE)>9?""+calendario.get(Calendar.MINUTE):"0"+calendario.get(Calendar.MINUTE);
        segundos=calendario.get(Calendar.SECOND)>9?""+calendario.get(Calendar.SECOND):"0"+calendario.get(Calendar.SECOND);
    }
    
    public void run(){
        //WHILE
        Thread current = Thread.currentThread();
        
        while(current == hilo){
            hora();
            bhora.setText(hora+":"+minutos+":"+segundos);
        }
    }
    
    
    

    
    
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        
       
        try {
            String file_name = "C:\\generate_pdf\\Temperatura3.pdf";
            Document documento = new Document(); 
            PdfWriter.getInstance(documento, new FileOutputStream(file_name));
            documento.open();

            Connection con;
            Class.forName("com.mysql.jdbc.Driver");
            con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/temperatura","root","");  
            PreparedStatement pps = con.prepareStatement("INSERT into datos(Temperatura,Fecha,Hora,Max,Min,Prom) VALUES (?,?,?,?,?,?)");
           
           
            ResultSet rs = pps.executeQuery("SELECT * from datos");
             while(rs.next()){
                 Paragraph para = new Paragraph(("temperatura:   ")+rs.getString("Temperatura")+("     Fecha:    ")+rs.getString("Fecha")+("   ")+("hora:  ")+rs.getString("Hora")+("  T.Máxima:  ")+rs.getString("Max")+("  T.Minima: ")+rs.getString("Min")+(" T.Promedio:  ")+rs.getString("Prom"));
                 documento.add(para);
                 documento.add(new Paragraph(" "));
             }
            
            documento.close();
                System.out.println("Finalizado");
        }catch (Exception e) {
            System.err.println(e);
                     }
    
            
         
   
    }//GEN-LAST:event_jButton5ActionPerformed

    
    
       
     
     
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        ChartPanel panel=new ChartPanel(Grafica);
        JFrame Ventana=new JFrame("Celsius");
        Ventana.getContentPane().add(panel);
        Ventana.pack();
        Ventana.setVisible(true);
        Ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
    }//GEN-LAST:event_jButton1ActionPerformed

    private void CelsiusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CelsiusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CelsiusActionPerformed

    private void bminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bminActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bminActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Ventana().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Celsius;
    private javax.swing.JTextField bfecha;
    private javax.swing.JTextField bhora;
    private javax.swing.JTextField bmin;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jmin;
    private javax.swing.JTextField jprom;
    private javax.swing.JLabel label_status;
    // End of variables declaration//GEN-END:variables

}

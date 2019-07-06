/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.util.Date;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Emanuel Rodrigues
 */
@XmlRootElement(name = "Transferencia")
public class Transferencia {
    
    //variaveis
    private int id;
    private int estado;
    private String destino;
    private String fonte;
    private String nome;
    private Date data;
    private String caminho;

    //construtores
    public Transferencia() {
    }
    
    
    public Transferencia(String destino, String fonte) {
        this.estado = 0;
        this.destino = destino;
        this.fonte = fonte;
        this.data=new Date();
    }
    
    public Transferencia(int id,String destino, String fonte) {
        this.id=id;
        this.estado = 0;
        this.destino = destino;
        this.fonte = fonte;
    }
    
    //getter and setters
    
    @XmlAttribute
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }
    
    
    
    
}

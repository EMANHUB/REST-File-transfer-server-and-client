/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Emanuel Rodrigues
 */
@XmlRootElement(name = "Cliente")
public class Cliente {

    //variaveis
    private String nome;
    private List<String> ficheiros;
    private Date data;

    //construtor
    public Cliente() {
    }

    public Cliente(String nome, Date dataLogin) {
        this.nome = nome;
    }

    //getter and setters
    @XmlAttribute
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<String> getFicheiros() {
        return ficheiros;
    }

    public void setFicheiros(List<String> ficheiros) {
        this.ficheiros = ficheiros;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}

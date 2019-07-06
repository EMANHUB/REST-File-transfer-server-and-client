/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Emanuel Rodrigues
 */
public class AtualizarLog implements Runnable {
    
    //variaveis
    private Principal frame;
    private String nome;
    private Client cliente;
    private String baseUri;
    private boolean flag;
    
    //construtor
    public AtualizarLog(Principal frame, String baseUri, String nome, Client cliente) {
        this.frame = frame;
        this.baseUri = baseUri;
        this.nome = nome;
        this.flag = true;
        this.cliente = cliente;
    }
    
    //função para acabar a thread pois acaba com o ciclo e acaba o método run logo acaba a thread
    public void matar() {
        flag = false;
    }

    @Override
    
    //função que permite atualizar o log da aplicação
    public void run() {
        while (flag) {
      

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                System.out.println("Interrumpida!");
            }
        }
    }

}

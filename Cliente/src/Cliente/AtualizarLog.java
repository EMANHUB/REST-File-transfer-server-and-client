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
    private Client cliente;
    private String baseUri;
    private boolean flag;
    int n;
    
    //construtor
    public AtualizarLog(Principal frame, String baseUri, Client cliente) {
        this.frame = frame;
        this.baseUri = baseUri;
        this.flag = true;
        this.cliente = cliente;
        this.n=-1;
    }
    
    //função para acabar a thread pois acaba com o ciclo e acaba o método run logo acaba a thread
    public void matar() {
        flag = false;
    }

    @Override
    
    //função que permite atualizar o log da aplicação
    public void run() {
        while (flag) {
            Response resultado = cliente.target(baseUri)
                    .path("Log/"+n)
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();
            
            if (resultado.getStatus() == 200) {
                ArrayList<log> log = (ArrayList<log>) resultado.readEntity(new GenericType<List<log>>() {
                });
                String append="";
                log ultimo=null;
                for(log x: log){
                    ultimo=x;
                    append+=x.getMsg()+"\n";
                }
                if(ultimo!=null)this.n=ultimo.getId();
                frame.log(append);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                System.out.println("Interrumpida!");
            }
        }
    }

}

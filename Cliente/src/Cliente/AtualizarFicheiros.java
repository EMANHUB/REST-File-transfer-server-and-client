/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 *
 * @author Emanuel Rodrigues
 */
public class AtualizarFicheiros implements Runnable {
    
    //variaveis
    private String nome;
    private String caminho;
    private String baseUri;
    private Client client;
    private boolean flag;
    
    //construtor
    public AtualizarFicheiros(String nome, String caminho, String baseUri, Client client) {
        this.nome = nome;
        this.caminho = caminho;
        this.baseUri = baseUri;
        this.client = client;
        this.flag=true;
    }

    @Override
    public void run() {
        while(flag){
            //mandar o a atualização das diretorias e a data de atualização para verificar se o cliente não está
            //expirado caso tenha ocorrido uma falha de rede  ou o cliente abortasse o programa forçadamente
            System.out.println(updateCliente(nome,caminho));
            System.out.println(new Date());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AtualizarFicheiros.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    //função para acabar a thread pois acaba com o ciclo e acaba o método run logo acaba a thread
    public void matar(){
        flag=false;
    }

    public boolean updateCliente(String nome, String caminho) {
        
        //fazer o get deste cliente através do seu nome unico
        Response resultado = client.target(baseUri)
                .path(nome)
                .request()
                .get();
        
        if(resultado.getStatus()!=200)return false;
        
        
        Cliente Cliente = resultado.readEntity(Cliente.class);
        //atualizar os ficheiros do cliente
        Cliente.setFicheiros(listar_directoria(caminho));
        Date data = new Date();
        //colocar a data da atualização
        Cliente.setData(data);
        //atualizar o cliente no servidor
        resultado = client.target(baseUri)
                .path(nome)
                .request()
                .put(Entity.xml(Cliente));

        if (resultado.getStatus() == 200) {
            return true;
        } else {
            return false;
        }
    }
    //transformar a diretoria num arraylist de strings com os ficheiros
    public ArrayList<String> listar_directoria(String caminho) {
        ArrayList<String> lista = new ArrayList<String>();
        try {
            Files.list(new File(caminho).toPath())
                    .forEach(path -> {
                        File ficheiro = new File(path.toString());
                        if (!ficheiro.isDirectory()) {
                            lista.add(ficheiro.getName());
                        }
                    });
        } catch (IOException ex) {
            System.out.println("Directoria não encontrada");
        }
        return lista;
    }

}

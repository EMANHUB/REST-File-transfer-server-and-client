/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Emanuel Rodrigues
 */
public class AtualizaTransferencias implements Runnable {

    //variaveis
    private Principal frame;
    private String nome;
    private String caminho;
    private Client cliente;
    private String baseUri;
    private boolean flag;

    //contrutor
    public AtualizaTransferencias(Principal frame, String baseUri, String nome, String caminho, Client cliente) {
        this.frame = frame;
        this.baseUri = baseUri;
        this.nome = nome;
        this.flag = true;
        this.caminho = caminho;
        this.cliente = cliente;
    }

    //função para acabar a thread pois acaba com o ciclo e acaba o método run logo acaba a thread
    public void matar() {
        flag = false;
    }

    @Override
    public void run() {
        while (flag) {
            //obter todas as transferencias
            Response resultado = cliente.target(baseUri)
                    .path("transferencias")
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();
            //caso o pedido tenha sido com sucesso
            if (resultado.getStatus() == 200) {
                ArrayList<Transferencia> transferencias = (ArrayList<Transferencia>) resultado.readEntity(new GenericType<List<Transferencia>>() {
                });
                for (Transferencia x : transferencias) {
                    //caso seja necessário este cliente fazer um upload para o servidor
                    if (x.getEstado() == 0 && x.getFonte().equals(nome)) {
                        File f = new File(caminho + File.separator + x.getNome());
                        if (f.exists()) {
                            Response resultado2 = cliente.target(baseUri)
                                    .path("transferencias/" + x.getId())
                                    .request()
                                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                                    .put(Entity.entity(f, MediaType.APPLICATION_OCTET_STREAM));
                            if (resultado2.getStatus() == 200) {
                                System.out.println("Enviado com sucesso!");
                            }
                        }

                    }

                    //caso este cliente tenha um download para fazer
                    if (x.getEstado() == 1 && x.getDestino().equals(nome)) {

                        Response resultado2 = cliente.target(baseUri)
                                .path("transferencias/" + x.getId())
                                .request()
                                .accept(MediaType.APPLICATION_OCTET_STREAM)
                                .get();
                        if (resultado2.getStatus() == 200) {

                            File ficheiro = resultado2.readEntity(new GenericType<File>() {
                            });
                            File destino = new File(caminho + File.separator + x.getNome());

                            //se já existir o ficheiro na pasta destino
                            if (destino.exists()) {
                                int reply = JOptionPane.showConfirmDialog(null, "Pretende substituir o ficheiro?", "Ficheiro já existe!", JOptionPane.YES_NO_OPTION);
                                if (reply != JOptionPane.YES_OPTION) {
                                    ficheiro.delete();

                                } else {
                                    copyFileUsingChannel(ficheiro, destino);
                                    ficheiro.delete();
                                    System.out.println("Download com sucesso!");
                                }
                            } else {
                                copyFileUsingChannel(ficheiro, destino);
                                ficheiro.delete();
                                System.out.println("Download com sucesso!");
                            }

                        }

                    }

                }

            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                System.out.println("Interrumpida!");
            }
        }
    }

    //função para coipar o ficheiro da pasta temporaria para a diretoria selecionada pelo utilizador
    //retirada de https://www.journaldev.com/861/java-copy-file
    private static void copyFileUsingChannel(File source, File dest) {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            try {
                sourceChannel = new FileInputStream(source).getChannel();
                destChannel = new FileOutputStream(dest).getChannel();
                destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            } finally {
                sourceChannel.close();
                destChannel.close();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Ficheiro não encontrado!");
        } catch (IOException ex) {
            System.out.println("Erro nos inputs!");
        }
    }

}

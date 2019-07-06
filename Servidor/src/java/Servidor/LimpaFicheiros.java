/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe responsável por ser uma thread que vai limpar todos os ficheiros que já foram transferidos pelos utilizadores
 * por intermediário do servidor
 * @author Emanuel Rodrigues
 */
public class LimpaFicheiros implements Runnable {

    private ArrayList<Transferencia> transferencias;//arraylist com a lista das transferencias
    
    //contrutor
    public LimpaFicheiros(ArrayList<Transferencia> transferencias) {
        this.transferencias = transferencias;//inicialização do arraylist de transferencias
    }
    
    //método run da thread responsável por determinar as ações a efetuar na inicialização da mesma
    @Override
    public void run() {

        while (true) {
            synchronized (transferencias) {//impede race coditions
                for (Transferencia x : transferencias) {//percorre o array de transferencias
                    if(x.getEstado()==2){//caro o estádo seja dois, ou seja, tranferido transfere com sucesso
                        File f = new File(x.getCaminho());//verifica se o ficheiro exite mesmo no servidor
                        if(f.exists()){//caso exista
                            x.setEstado(4);//muda o estado da transferencia para 4 e elimina o ficheiro
                            f.delete();
                        }
                    };
                }
            }
            
            try {
                Thread.sleep(2000);//thread efetuada de 2 em 2 segundos
            } catch (InterruptedException ex) {
                Logger.getLogger(LimpaFicheiros.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

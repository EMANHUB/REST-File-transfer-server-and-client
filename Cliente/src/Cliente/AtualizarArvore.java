/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

/**
 *
 * @author Emanuel Rodrigues
 */
public class AtualizarArvore implements Runnable {
    
    //variaveis
    private Principal frame;
    private boolean flag;
    
    //construtor
    public AtualizarArvore(Principal frame) {
        this.frame = frame;
        this.flag=true;
    }
    
    //função para acabar a thread pois acaba com o ciclo e acaba o método run logo acaba a thread
    public void matar(){
        flag=false;
    }

    @Override
    public void run() {
        while (flag) {
            //atalualizar a arvore de diretorias
            frame.atualizarArvore();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                System.out.println("Interrumpida!");
            }
        }
    }

}

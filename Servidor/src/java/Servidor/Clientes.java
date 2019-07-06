package Servidor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("Clientes")//definição do path
public class Clientes {

    private ArrayList<Cliente> clientes = new ArrayList<Cliente>(); //inicialização do arraylist de clientes ativos
    private ArrayList<log> log = new ArrayList<log>(); //inicialização do arraylist de clientes inativos
    private Integer nLog = 0;//inicialização do contador do id do log

    @GET
    @Produces({"application/xml", "application/json"})//definição do media type que retorna
    public List<Cliente> getClientes() {//devolve todos os clientes
        verificaUtilizadores();
        synchronized (clientes) {
            return clientes;
        }
    }

    @GET
    @Path("{nome}")
    @Produces({"application/xml", "application/json"})//definição do media type que retorna
    public Cliente getCliente(@PathParam("nome") String nome) {//devolve um cliente com o nome dado pelo path
        verificaUtilizadores();
        synchronized (clientes) {//impede race conditions
            for (Cliente x : clientes) {//perpcorre todos os clientes
                if (x.getNome().equals(nome)) {//encontra o cliente com nome do path
                    return x;//retorna o cliente com o nome do path
                }
            }
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);//caso contrário devolve not found 404
    }

    @POST
    @Consumes({"application/xml", "application/json"})//definir que tipo consume neste caso tipo formulário
    public void login(Cliente cliente) {//parametros a receber
        verificaUtilizadores();
        synchronized (clientes) {
            for (Cliente x : clientes) {//percorrer array para veridicar se existe um utilizaidor ativo com este nome
                if (x.getNome().equals(cliente.getNome())) {
                    throw new WebApplicationException(Response.Status.CONFLICT);// caso o nome exista manda codigo 209 
                }
            }
            if (clientes.add(cliente)) {//insere nos clientes ativos
                adicionaLog("O cliente " + cliente.getNome() + " iniciou sessão");
                throw new WebApplicationException(Response.Status.OK);//caso de inserção com sucesso devolve 200 ok
            } else {
                throw new WebApplicationException(Response.Status.CONFLICT);//caso de erro devolve 209
            }
        }
    }

    @DELETE
    @Path("{nome}")
    @Produces({"application/xml", "application/json"})//definição do media type que retorna
    public Cliente delCliente(@PathParam("nome") String nome) {//elimina um cliente com o nome dado por path
        verificaUtilizadores();
        synchronized (clientes) {//impede race conditions
            for (Cliente x : clientes) {//percorre todos os clientes
                if (x.getNome().equals(nome)) {//encontra o cliente com o nome do path
                    clientes.remove(x);//remove da lista de clientes ativos
                    System.out.println("Cliente logout");
                    adicionaLog("O cliente " + x.getNome() + " fez logout");
                    throw new WebApplicationException(Response.Status.OK);//codigo 200 ok sucesso
                }
            }
        }

        throw new WebApplicationException(Response.Status.NOT_FOUND);//caso o nome não seja encontrado
    }

    @PUT
    @Path("{nome}")
    @Consumes({"application/xml", "application/json"})//definir que tipo consume neste caso tipo formulário
    public void updateCliente(Cliente cliente, @PathParam("nome") String nome) {//parametros a receber
        verificaUtilizadores();
        synchronized (clientes) {
            for (Cliente x : clientes) {//percorrer array
                if (x.getNome().equals(nome)) {
                    x.setFicheiros(cliente.getFicheiros());
                    x.setData(cliente.getData());
                    throw new WebApplicationException(Response.Status.OK);//caso de inserção com sucesso devolve 200 ok
                }
            }
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);//caso de erro devolve 404

    }

    public void verificaUtilizadores() {

        ArrayList<Cliente> remove = new ArrayList<Cliente>();//array de utilizadores a remover
        if (clientes != null) {
            synchronized (clientes) {//impede race contions

                for (Cliente x : clientes) {//percorre os clientes
                    long data1 = x.getData().getTime();//tempo da ultima atualização
                    long data2 = new Date().getTime();//tempo atual
                    System.out.println((data2 - data1));
                    if ((data2 - data1) > 10000) {//diferença maior que 10 segundos cliente expirado
                        remove.add(x);//adiciona o cliente à lista de clientes para eliminar
                        System.out.println("Cliente Expirado");
                        adicionaLog("");
                    }
                }
                clientes.removeAll(remove);//remove todos os clientes da lista para remover
            }
        }
        

    }

    public void adicionaLog(String acao) {
        synchronized (log) {//impede race coditions
            SimpleDateFormat formato = new SimpleDateFormat("HH:mm:ss");
            synchronized (nLog) {
                log.add(new log(formato.format(new Date()) + " - " + acao, nLog));
                nLog++;
            }
        }
    }

    @GET
    @Path("Log/{n}")
    @Produces({"application/xml", "application/json"})//definição do media type que retorna
    public List<log> getLog(@PathParam("n") int n) {//devolve todos os clientes
        ArrayList<log> devolve = new ArrayList<log>();
        synchronized (log) {
            for (log x : log) {
                if (x.getId() > n) {
                    devolve.add(x);
                }
            }
        }
        return devolve;
    }

}

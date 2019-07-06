package Servidor;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("Transferencias")//definição do path
public class Transferencias {

    @Context
    private ApplicationConfig app;
    private Clientes getCliente() {
        for (Object o : app.getSingletons()) {
            if (o instanceof Clientes) {
                return (Clientes) o;
            }
        }
        return null;
    }

    private ArrayList<Transferencia> transferencias = new ArrayList<Transferencia>(); //inicialização do arraylist de transferencias 
    private Integer nTranferencias = 0;//inicialização do contador do ir das tranferencias

    @GET
    //@Produces({"application/xml", "application/json"})//definição do media type que retorna
    @Produces(MediaType.TEXT_PLAIN)

    public String getClientes() {//devolve todos os clientes
        return "sucesso";

    }

    @POST
    @Consumes({"application/xml", "application/json"})//definir que tipo consume neste caso tipo formulário
    public void addTransferencia(Transferencia t) {//insere uma transferencia
        limpaFicheiros();
        synchronized (transferencias) {
            synchronized (nTranferencias) {//incrementa o id das tranferencias pprotegido de race conditions
                t.setId(++nTranferencias);
            }
            if (transferencias.add(t)) {//adicona a transferencia ao array de tranferencias
                throw new WebApplicationException(Response.Status.OK);//caso de inserção com sucesso devolve 200 ok
            } else {
                throw new WebApplicationException(Response.Status.CONFLICT);//caso de erro devolve 404
            }
        }
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_OCTET_STREAM})//definir que tipo consume neste caso tipo formulário
    public void upTransferencia(@PathParam("id") int id, File ficheiro) {//faz o upload do ficheiro para o servidor
        synchronized (transferencias) {
            limpaFicheiros();
            for (Transferencia x : transferencias) {//encontra transferencia com id do path
                if (x.getId() == id && x.getEstado() == 0) {//encontra transferencia com id do path
                    x.setData(new Date());//atualiza a data da transferencia
                    x.setCaminho(ficheiro.toString());//coloca o caminho em que está no servidor
                    x.setEstado(1);//muda o estado para 1(encontra-se o servidor)
                    throw new WebApplicationException(Response.Status.OK);//caso de inserção com sucesso devolve 200 ok
                }
            }

            throw new WebApplicationException(Response.Status.NOT_FOUND);//caso de erro devolve 404
        }
    }

    @GET
    @Produces({"application/xml", "application/json"})//definição do media type que retorna
    public List<Transferencia> getTransferencias() {//devolve todas as transferencias
        synchronized (transferencias) {
            limpaFicheiros();
            return transferencias;
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)//definição do media type que retorna
    public Response getFile(@PathParam("id") int id) {
        File file = null;
        synchronized (transferencias) {

            for (Transferencia x : transferencias) {//percorre transferencias
                if (x.getId() == id && x.getEstado() < 2) {//só fica disponivel uma vez para download
                    file = new File(x.getCaminho());//abre o ficheiro
                    if (file.exists()) {//caso exista
                        x.setEstado(2);//muda estado para 2 (transferido)
                        getCliente().adicionaLog("O cliente "+x.getFonte()+" fez o download com sucesso do ficheiro "+x.getNome()+" de "+x.getDestino());
                    }
                }
            }
        }
        limpaFicheiros();
        
        if (file != null && file.exists()) {//caso ficheiro exista devolve o ficheiro codigo 200ok
            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();//caso sontrario 404
        }

    }

    public void limpaFicheiros() {
        synchronized (transferencias) {//impede race coditions
            for (Transferencia x : transferencias) {//percorre o array de transferencias
                if (x.getEstado() == 2) {//caro o estádo seja dois, ou seja, tranferido transfere com sucesso
                    File f = new File(x.getCaminho());//verifica se o ficheiro exite mesmo no servidor
                    if (f.exists()) {//caso exista
                        x.setEstado(4);//muda o estado da transferencia para 4 e elimina o ficheiro
                        f.delete();
                    }
                }
            }
        }
    }

}

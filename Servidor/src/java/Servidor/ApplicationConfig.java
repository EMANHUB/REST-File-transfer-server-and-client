package Servidor;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("resources")//declaração do application path
public class ApplicationConfig extends Application{
   private Set<Object> singletons = new HashSet<Object>();

   public ApplicationConfig() {
      singletons.add(new Clientes());
      singletons.add(new Transferencias());
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }    
    
}

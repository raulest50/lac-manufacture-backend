package lacosmetics.planta.lacmanufacture.service.produccion;

import lacosmetics.planta.lacmanufacture.model.producto.Material;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//@Service
//@Slf4j
//@Transactional(rollbackFor = Exception.class)
//@RequiredArgsConstructor
public class MPSService {


    /**
     * este metodo debe recibir las necesidades de produccion.
     * en base a ellas, debe entregar una propuesta de produccion para la semana.
     * es decir, cantidades a producir.
     */
    public void computeMPS(){

    }

    public void computePOs_MPS(){

    }


    /**
     * Recibe un terminado. A partir de este,
     * calcula o propone los Ordenes de Produccion
     * PO, con todas las fechas recomendadas teniendo en
     * cuenta el diagrama de procesos y los tiempos estimados
     * ademas de las horas laborales y el tipo de proceso.
     * hay procesos como espera de enfriamiento que se pueden
     * hacer fiera de las horas laborales.
     */
    public void computePOs_single(Terminado te){

    }

    /**
     * la idea es que este metodo se use unicamente para
     * terminado o semiterminado nada mas
     * @param p
     */
    public void dispensacion(Producto p){

        if(p.getClass().equals(Material.class)){
            //throws new UnsupportedOperationException("No se puede usar este metodo para un producto de tipo Material");
        }

        if(p.getClass().equals(SemiTerminado.class)){

        }

        if(p.getClass().equals(Terminado.class)){

        }

    }

    public void estimarCapacidadProductiva(){

    }

    public void validarCapacidadProductiva(){

    }

}

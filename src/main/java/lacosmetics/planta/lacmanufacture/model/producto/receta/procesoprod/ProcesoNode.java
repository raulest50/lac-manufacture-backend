package lacosmetics.planta.lacmanufacture.model.producto.receta.procesoprod;


import jakarta.persistence.*;

@Entity
@DiscriminatorValue("procesoNode")
public class ProcesoNode extends Node{

    /**
     * 1: minutos
     * 2: horas
     */
    private int unidadesTiempo;

    /**
     * Tiempo estimado para el proceso.
     * sirve para comparar con el tiempo real que toma la
     * ejecucion del mismo en la practica.
     */
    private int tiempo;

    private String nombreProceso;

    /**
     * Instrucciones sobre como llevar a cabo este subproceso
     */
    private String instrucciones;

    /**
     * una descripcion cualitativa y/o cuantitativa
     * sobre las caracteristicas del producto de
     * salida del subproceso. como el parent entity es
     * ProcesoProduccion entonces a los nodos ProcesoNode,
     * que estan encapsulados en ProcesoProduccion los
     * consideramos SubProcesos.
     */
    private String descripcionSalida;

}

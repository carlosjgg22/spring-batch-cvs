package com.pollo.code.demospringbatchapp.processor;

import com.pollo.code.demospringbatchapp.model.Persona;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.adapter.PropertyExtractingDelegatingItemWriter;

public class PersonaItemProcessor implements ItemProcessor<Persona, Persona> {
    private static final Logger LOG = LoggerFactory.getLogger(PersonaItemProcessor.class);
    @Override
    public Persona process(Persona item) throws Exception {
        //Para los Datos
        String nombre = item.getNombre().toUpperCase();
        String apellido = item.getApellido().toUpperCase();
        String telefono = "+54"+item.getTelefono();

        Persona persona = new Persona(nombre, apellido, telefono );

        LOG.info("Convirtiendo ("+item+") a ("+persona+") ");
        return persona;
    }
}

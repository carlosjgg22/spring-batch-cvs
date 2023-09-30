package com.pollo.code.demospringbatchapp.listener;

import com.pollo.code.demospringbatchapp.model.Persona;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobListener extends JobExecutionListenerSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JobListener.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JobListener(JdbcTemplate jdbcTemplate){
        super();
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void afterJob(JobExecution jobExecution){
        if(jobExecution.getStatus()== BatchStatus.COMPLETED){
            LOG.info("FINALIZÓ EL JOB...! Verifica los Resultados:");
            jdbcTemplate
                    .query("SELECT nombre, apellido, telefono from persona",
                            (rs, row) -> new Persona(rs.getString(1), rs.getString(2), rs.getString(3)))
                    .forEach(persona -> LOG.info("Registro <"+persona+">"));
        }

    }
}

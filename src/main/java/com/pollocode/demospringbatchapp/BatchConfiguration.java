package com.pollocode.demospringbatchapp;

import com.pollo.code.demospringbatchapp.listener.JobListener;
import com.pollo.code.demospringbatchapp.model.Persona;
import com.pollo.code.demospringbatchapp.processor.PersonaItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobListenerFactoryBean;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    //reader para el job
    @Bean
    public FlatFileItemReader<Persona> reader(){
        return new FlatFileItemReaderBuilder<Persona>()
                .name("personaItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"nombre","apellido","telefono"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Persona>(){{
                    setTargetType(Persona.class);
                }})
                .build();

    }

    //Writer Processor
    @Bean
    public PersonaItemProcessor processor(){
        return new PersonaItemProcessor();
    }

    //Writer
    @Bean
    public JdbcBatchItemWriter<Persona> writer (DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Persona>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO persona (nombre, apellido, telefono) VALUES (:nombre, :apellido, :telefono)")
                .dataSource(dataSource)
                .build();
    }
    //Creando el Job
    //el Job para importar la persona debe recibir el jdbc del listener
    @Bean
    public Job ImportPersonaJob(Step step1, JdbcTemplate jdbcTemplate){
        return jobBuilderFactory.get("ImportPersonaJob") //mismo nombre que la clase
                .incrementer(new RunIdIncrementer())
                .listener(JobListenerFactoryBean.getListener(new JobListener(jdbcTemplate)))
                .flow(step1)
                .end()
                .build();
    }

    //definiendo el Step
    @Bean
    public Step step1(JdbcBatchItemWriter<Persona> writer){
        return stepBuilderFactory.get("step1")
                .<Persona, Persona>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

}

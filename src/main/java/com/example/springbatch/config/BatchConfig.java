package com.example.springbatch.config;


import com.example.springbatch.model.Customer;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Configuration
@AllArgsConstructor
public class BatchConfig {

    private FirstWriter firstWriter;

    private JobRepository jobRepository;

    private PlatformTransactionManager transactionManager;

    @Bean
//    @StepScope
    public FlatFileItemReader<Customer> customerFlatFileItemReader()
    {
        FlatFileItemReader<Customer> flatFileItemReader=new FlatFileItemReader<>();

        flatFileItemReader.setResource(
                new FileSystemResource((new File("D:\\springbatch\\src\\main\\resources\\customer.csv")))
        );

        flatFileItemReader.setLineMapper(new DefaultLineMapper<Customer>()
        {
            {

                //provide the column name
                setLineTokenizer(new DelimitedLineTokenizer()
                {
                    {
                        setNames("Id","Name","Age");

                    }
                });

                //get the data from csv
                setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(Customer.class);
                    }

                });

            }
        });

        System.out.println("Reader");
        flatFileItemReader.setLinesToSkip(1);

        return flatFileItemReader;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(4);
        return taskExecutor;
    }

//
//    @Bean
//    public FlatFileItemWriter<Customer> customerFlatFileItemWriter() throws Exception {
//        FlatFileItemWriter<Customer> fileItemWriter=new FlatFileItemWriter<>();
//
//        fileItemWriter.write(new Chunk<>());
//
//        return fileItemWriter;
//    }


    @Bean
    public Step step() throws Exception {
        System.out.println("Step");
        return new StepBuilder("csv",jobRepository)
                .<Customer,Customer>chunk(3,transactionManager)
                .reader(customerFlatFileItemReader())
                .writer(firstWriter)
                .taskExecutor(taskExecutor())
                .build();

    }

    @Bean
    public Job job() throws Exception {
        System.out.println("Job");
        return new JobBuilder("job",jobRepository)
                .start(step())
                .build();
    }






}

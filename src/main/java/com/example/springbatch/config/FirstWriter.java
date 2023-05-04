package com.example.springbatch.config;

import com.example.springbatch.model.Customer;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class FirstWriter implements ItemWriter<Customer> {
    @Override
    public void write(Chunk<? extends Customer> chunk)  {
        System.out.println("Writer");
        chunk.getItems().forEach(System.out::println);
    }
}

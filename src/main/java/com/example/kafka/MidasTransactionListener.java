package com.example.kafka;

import com.example.model.Transaction;
import com.example.service.TransactionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MidasTransactionListener {
    private final Logger log = LoggerFactory.getLogger(MidasTransactionListener.class);
    private final TransactionStore store;

    public MidasTransactionListener(TransactionStore store) {
        this.store = store;
    }

    // Use the property that stores the topic name. Replace property name if different in your application.yml
    @KafkaListener(topics = "${midas.topic.transactions}", containerFactory = "kafkaListenerContainerFactory")
    public void receive(Transaction tx) {
        log.info("Received transaction id={} amount={}", safeId(tx), safeAmount(tx));
        store.add(tx);
        // IMPORTANT: do nothing else for now — integration only; processing comes later.
    }

    private Object safeId(Transaction tx) {
        try { return tx == null ? "null" : tx.getId(); } catch (Throwable t) { return "<err-getId>"; }
    }

    private Object safeAmount(Transaction tx) {
        try { return tx == null ? "null" : tx.getAmount(); } catch (Throwable t) { return "<err-getAmount>"; }
    }
}
package com.sofka.accountservice.messaging;

import com.sofka.accountservice.config.RabbitMqConfig;
import com.sofka.accountservice.domain.ClienteReferencia;
import com.sofka.accountservice.repository.ClienteReferenciaRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ClienteEventListener {

    private final ClienteReferenciaRepository clienteReferenciaRepository;

    public ClienteEventListener(ClienteReferenciaRepository clienteReferenciaRepository) {
        this.clienteReferenciaRepository = clienteReferenciaRepository;
    }

    @RabbitListener(queues = RabbitMqConfig.CUSTOMER_QUEUE, containerFactory = "customerSyncListenerContainerFactory")
    public void onClienteEvent(ClienteEvent event) {
        if ("CLIENTE_DELETE".equals(event.eventType())) {
            clienteReferenciaRepository.deleteById(event.clienteId());
            return;
        }

        ClienteReferencia cliente = clienteReferenciaRepository.findById(event.clienteId())
                .orElseGet(ClienteReferencia::new);
        cliente.setClienteId(event.clienteId());
        cliente.setNombre(event.nombre());
        cliente.setIdentificacion(event.identificacion());
        cliente.setEstado(event.estado());
        clienteReferenciaRepository.save(cliente);
    }
}

package com.sofka.accountservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sofka.accountservice.domain.ClienteReferencia;
import com.sofka.accountservice.repository.ClienteReferenciaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteReferenciaRepository clienteReferenciaRepository;

    @Test
    void shouldCreateCuenta() throws Exception {
        guardarClienteReferencia(1L, "Jose Lema", "1234567890");

        String body = """
                {
                  "numeroCuenta": 585545,
                  "tipoCuenta": "Corriente",
                  "saldoInicial": 1000.00,
                  "estado": true,
                  "clienteId": 1,
                  "clienteNombre": "Jose Lema"
                }
                """;

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCuenta").value(585545))
                .andExpect(jsonPath("$.saldoDisponible").value(1000.00));
    }

    @Test
    void shouldGenerateReporteByClienteAndFecha() throws Exception {
        guardarClienteReferencia(2L, "Marianela Montalvo", "9876543210");

        String cuentaBody = """
                {
                  "numeroCuenta": 225487,
                  "tipoCuenta": "Corriente",
                  "saldoInicial": 100.00,
                  "estado": true,
                  "clienteId": 2,
                  "clienteNombre": "Marianela Montalvo"
                }
                """;

        String movimientoBody = """
                {
                  "fecha": "2022-02-10",
                  "tipoMovimiento": "DEPOSITO",
                  "valor": 600.00,
                  "numeroCuenta": 225487
                }
                """;

        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuentaBody));

        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(movimientoBody));

        mockMvc.perform(get("/reportes")
                        .param("fechaDesde", "2022-02-08")
                        .param("fechaHasta", "2022-02-10")
                        .param("clienteId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cliente").value("Marianela Montalvo"))
                .andExpect(jsonPath("$[0].saldoDisponible").value(700.00));
    }

    private void guardarClienteReferencia(Long clienteId, String nombre, String identificacion) {
        ClienteReferencia cliente = new ClienteReferencia();
        cliente.setClienteId(clienteId);
        cliente.setNombre(nombre);
        cliente.setIdentificacion(identificacion);
        cliente.setEstado(true);
        clienteReferenciaRepository.save(cliente);
    }
}

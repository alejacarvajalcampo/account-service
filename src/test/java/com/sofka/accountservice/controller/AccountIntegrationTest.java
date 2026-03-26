package com.sofka.accountservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofka.accountservice.repository.ClienteReferenciaRepository;
import com.sofka.accountservice.soporte.ClienteReferenciaPruebaBuilder;
import java.util.Map;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deberiaCrearCuenta() throws Exception {
        guardarClienteReferencia(
                ClienteReferenciaPruebaBuilder.unClienteReferencia().construir()
        );

        String body = objectMapper.writeValueAsString(Map.of(
                "numeroCuenta", 585545,
                "tipoCuenta", "Corriente",
                "saldoInicial", 1000.00,
                "estado", true,
                "clienteId", 1
        ));

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCuenta").value(585545))
                .andExpect(jsonPath("$.saldoDisponible").value(1000.00));
    }

    @Test
    void deberiaGenerarReportePorClienteYFecha() throws Exception {
        guardarClienteReferencia(
                ClienteReferenciaPruebaBuilder.unClienteReferencia()
                        .conClienteId(2L)
                        .conNombre("Marianela Montalvo")
                        .conIdentificacion("9876543210")
                        .construir()
        );

        String cuentaBody = objectMapper.writeValueAsString(Map.of(
                "numeroCuenta", 225487,
                "tipoCuenta", "Corriente",
                "saldoInicial", 100.00,
                "estado", true,
                "clienteId", 2
        ));

        String movimientoBody = objectMapper.writeValueAsString(Map.of(
                "fecha", "2022-02-10",
                "tipoMovimiento", "DEPOSITO",
                "valor", 600.00,
                "numeroCuenta", 225487
        ));

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
                .andExpect(jsonPath("$[0].movimientos", hasSize(1)))
                .andExpect(jsonPath("$[0].movimientos[0].saldoDisponible").value(700.00));
    }

    private void guardarClienteReferencia(com.sofka.accountservice.domain.ClienteReferencia cliente) {
        clienteReferenciaRepository.save(cliente);
    }
}
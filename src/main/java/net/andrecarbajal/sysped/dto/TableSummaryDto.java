package net.andrecarbajal.sysped.dto;

public record TableSummaryDto(
    long countLibres,
    long countEsperandoPedido,
    long countFaltaAtencion,
    long countPedidoEntregado
) {
}

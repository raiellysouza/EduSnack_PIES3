package com.example.edusnack.model

enum class StatusPedido(val descricao: String) {
    AGUARDANDO("Aguardando"),
    EM_PREPARO("Em Preparo"),
    PENDENTE("Ainda não concluido"),
    PAGO("Pago"),
    PRONTO("Pronto"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado")
}

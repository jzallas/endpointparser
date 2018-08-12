package com.jzallas.endpointparser

data class EndpointInfo(
    val httpMethod: String,
    val endpoint: String,
    val methodName: String,
    val className: String,
    val parameters: List<Parameters> = emptyList(),
    val result: String? = null
) {
  data class Parameters(val type: String, val name: String)
}


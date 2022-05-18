package br.edu.ufabc.reciclabc.ui.shared

sealed class Status {
    class Error(val e: Exception) : Status()
    object Success : Status()
}

data class Result<T>(
    val result: T?,
    val status: Status,
)

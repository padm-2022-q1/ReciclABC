package br.edu.ufabc.reciclabc.model

enum class Weekday { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY; fun toNumeric(): Int { return this.ordinal + 1 } }

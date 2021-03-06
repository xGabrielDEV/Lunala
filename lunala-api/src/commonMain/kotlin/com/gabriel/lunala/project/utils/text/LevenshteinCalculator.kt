package com.gabriel.lunala.project.utils.text

object LevenshteinCalculator {

    fun levenshtein(s: String, t: String, charScore : (Char, Char) -> Int = { c1, c2 -> if (c1 == c2) 0 else 1}) : Int {
        if (s == t)  return 0
        if (s == "") return t.length
        if (t == "") return s.length

        val initialRow : List<Int> = (0 until t.length + 1).map { it }.toList()
        return (s.indices).fold(initialRow, { previous, u ->
            (t.indices).fold( mutableListOf(u+1), {
                row, v -> row.add(minOf(row.last() + 1,
                    previous[v+1] + 1,
                    previous[v] + charScore(s[u],t[v])))
                row
            })
        }).last()
    }


}
package com.example.sendready

import androidx.media3.transformer.Transformer
import org.junit.Test

class DumpTransformer {
    @Test
    fun dumpMethods() {
        val methods = Transformer.Builder::class.java.methods
        methods.forEach { println(it.name + " : " + it.parameterTypes.joinToString { p -> p.simpleName }) }
    }
}

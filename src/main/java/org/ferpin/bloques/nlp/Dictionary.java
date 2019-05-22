package org.ferpin.bloques.nlp;

import java.util.HashMap;

public final class Dictionary {
    public static final HashMap<Concept, String[]> SYNONYMS = synonyms();

    private static HashMap<Concept, String[]> synonyms() {
        HashMap<Concept, String[]> map = new HashMap<>();
        map.put(Concept.NO, new String[]{"no"});
        map.put(Concept.PUT, new String[]{"poner"});
        map.put(Concept.SET, new String[]{"colocar", "coloca", "sube", "subir"});
        map.put(Concept.QUIT, new String[]{"quitar", "quita", "remover", "remueve"});
        map.put(Concept.TAKEOUT, new String[]{"sacar"});
        map.put(Concept.TYPE, new String[]{"tipo", "modelo", "patron"});
        map.put(Concept.PRISM, new String[]{"prisma", "figura", "patron", "poliedro"});
        map.put(Concept.THEN, new String[]{"entonces"});
        map.put(Concept.NOTHING, new String[]{"nada", "vacio"});
        map.put(Concept.BLOCK, new String[]{"bloque", "pieza"});
        map.put(Concept.IF, new String[]{"si", "cuando"});
        map.put(Concept.COLOR, new String[]{"color", "tinte", "tono"});
        map.put(Concept.WRITE, new String[]{"escribir", "mostrar", "enseñar"});
        map.put(Concept.BE, new String[]{"ser", "estar", "tener", "es", "son", "existe"});
        map.put(Concept.FLOOR, new String[]{"piso", "suelo", "tierra", "pavimento", "estrato"});
        map.put(Concept.ABOVE, new String[]{"sobre", "arriba", "encima", "por encima", "por arriba", "superior"});
        map.put(Concept.SIZE, new String[]{"tamano", "longitud", "dimensión", "volumen", "medida", "magnitud", "envergadura", "altura", "capacidad"});
        map.put(Concept.ENTITY, new String[]{"objeto", "entidad", "ente", "individuo"});
        map.put(Concept.ATTACH, new String[]{"vincular", "ligar", "unir", "asociar", "enlazar", "relacionar", "conectar"});
        map.put(Concept.RULE, new String[]{"regla", "orden", "comando", "pauta", "estatuto", "precepto", "instruccion", "mandato", "decreto"});
        map.put(Concept.DETACH, new String[]{"desvincular", "desligar", "desunir", "separar", "desasociar", "desenlazar", "relacionar", "desconectar"});
        map.put(Concept.FEATURE, new String[]{"caracteristica", "caracteristicas", "estado", "estados", "propiedad", "propiedades", "particularidad", "particularidades"});
        return map;
    }


    public static final HashMap<Concept, String[]> CATEGORIES = categories();

    private static HashMap<Concept, String[]> categories() {
        HashMap<Concept, String[]> map = new HashMap<>();
        map.put(Concept.EXCEPTIONS, new String[]{"de"});
        map.put(Concept.QUESTIONS, new String[]{"que", "cuando", "donde", "por que", "cual", "porque", "quien", "como"});
        map.put(Concept.PRISM, new String[] {"cubo", "piramide", "cilindro", "esfera", "cono", "piramide cuadrada",
                "prisma hexagonal", "prisma triangular", "prisma rectangular", "tetraedro",
                "caja"});
        map.put(Concept.SIZE, new String[] {"enorme", "inmenso","inmensa", "grande", "alto", "largo","alta", "larga", "mayusculo", "mediano", "mayuscula", "mediana",
                "medio", "regular", "chico", "pequeno", "corto", "diminuto", "chica", "pequena", "corta", "diminuta"}); //from the biggest to the smallest);
        map.put(Concept.COLOR, new String[] {"negro", "azul", "marron", "gris", "verde", "naranja", "rosa", "purpura",
                "negra","roja", "blanca", "amarilla", "rojo", "blanco", "amarillo", "turquesa", "verde oliva", "verde menta",
                "borgona", "lavanda", "magenta", "salmon", "cian", "beige", "rosado",
                "verde oscuro", "verde oliva", "lila", "amarillo palido", "fucsia", "mostaza",
                "ocre", "trullo", "malva", "purpura oscuro", "verde lima", "verde claro",
                "ciruela", "azul claro", "melocoton", "violeta", "tan", "granate"});
        return map;
    }
}

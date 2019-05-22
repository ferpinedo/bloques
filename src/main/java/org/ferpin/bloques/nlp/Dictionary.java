package org.ferpin.bloques.nlp;

public class Dictionary {
    public final static String[] EXCEPTIONS = {"de"};

    public static final class Synonyms { //TODO: final HashMap<Concept ENUM, String[]>
        public final static String[] THEN = {"entonces"};
        public final static String[] NO = {"no"};
        public final static String[] NOTHING = {"nada"};
        public final static String[] FLOOR = {"piso"};
        public final static String[] IF = {"si", "cuando"};
        public final static String[] BE = {"ser", "estar", "tener", "es", "son"};
        public final static String[] ENTITY = {"objeto", "entidad", "ente", "individuo"};
        public final static String[] ATTACH = {"vincular", "ligar", "unir", "asociar", "enlazar", "relacionar",
                "conectar"};
        public final static String[] RULE = {"regla", "orden", "comando", "pauta", "estatuto", "precepto",
                "instruccion", "mandato", "decreto"};
        public final static String[] DETACH = {"desvincular", "desligar", "desunir", "separar", "desasociar", "desenlazar",
                "relacionar", "desconectar"};
        public final static String[] FEATURE = {"caracteristica", "caracteristicas", "estado", "estados", "propiedad",
                "propiedades", "particularidad", "particularidades"};
    }
}

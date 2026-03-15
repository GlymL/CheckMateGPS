package com.example.demo;

import java.util.HashSet;

public class ListaViviendasImp extends ListaViviendas{
    
    private final HashSet<Vivienda> lista;


    public ListaViviendasImp(){
        lista = new HashSet<>();
    }


    @Override
    public int InsertVivienda(Vivienda v) throws Exception {
        boolean inside = lista.add(v);
        if(inside)
            return lista.size();
        else
            throw new Existing();
    }


}

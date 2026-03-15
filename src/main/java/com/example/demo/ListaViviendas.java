package com.example.demo;

public abstract class ListaViviendas {

    
    private static ListaViviendasImp instance;

    public ListaViviendas(){
    }

	public static ListaViviendasImp getInstance() {
		
		if(instance==null){
			instance = new ListaViviendasImp();
		}
		return instance;
	}

    public abstract int InsertVivienda(Vivienda v) throws Exception;


}

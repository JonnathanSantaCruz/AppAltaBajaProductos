package com.example.scannerdecodigodebarras;

import com.firebase.ui.firestore.ObservableSnapshotArray;

import java.util.ArrayList;

public class Global {

    public static Usuario usuario;
    public static boolean terminoRetiro;
    public static int cantPantallas;
    //EscanearCodigo - IngresarElemento - IngresarTecnico - MostrarElementos - OrdenarCaja - MostrarCaja
    public static boolean [] pantallasCreadas;

    public static ArrayList<String> filiales = new ArrayList<>();

    //INICIAR SESION
    public static boolean cuentaUsada;

    //VARIABLES INGRESAR ELEMENTOS
    public static String codigo;
    public static ArrayList<String> elementos = new ArrayList<>();

    //VARIABLES INICAR RETIRO
    public static String filial;
    public static int nroFilial;
    public static String fechaRetiro;
    public static String horaInicioRetiro;
    public static String horaFinRetiro;

    public static boolean retiroIniciado;

    //INGRESAR DATOS TECNICO
    public static Tecnico tecnico;
    public static boolean tecnicoRegistrado;

    //MOSTRAR ELEMENTOS
    public static boolean hayElementosCargados;

    //ORDENAR CAJAS
    public static ObservableSnapshotArray<Elemento> arrayElemCargados;
    public static ArrayList<Caja> cajas;

    //MOSTRAR CAJA
    public static int cantCajas;
    public static String codCaja;
    public static String nroCaja;
    public static int nroImplementacion;
    public static String responsableRetiro;
}

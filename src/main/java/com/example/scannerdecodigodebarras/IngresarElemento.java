package com.example.scannerdecodigodebarras;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;


import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.HashMap;
import java.util.Map;

public class IngresarElemento extends General {

    private Button volver, agregarElemento;
    private EditText editCodigoBarra, editFilial;
    private SearchableSpinner editNombreElemento;
    private NavigationView navigationView;
    private Elemento elemento;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog dialog;

    //ESTO ES PARA EL BOTON DEL TOOLBAR PARA ABRIR EL NAVIGATION VIEW
    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;

    private String[] filiales = {"1 - Centro","2 - Nuñez","3 - Ramos Mejia","4 - P.Patricios","5 - V.Insuperable" ,"6 - Villa Crespo","7 - Liniers"
    ,"8 - Ciudadela","10 - V.Alsina","11 - Colegiales" ,"13 - Castelar","14 - Abasto","15 - Pompeya","17 - Piñeyro","18 - Monte Grande"
    ,"19 - La Plata"  ,"20 - San Martin","21 - Berazategui","22 - V.Pueyrredón","23 - J.Ingenieros","24 - San Fernando","25 - Hurlingham"
    ,"26 - Once","27 - V.Devoto","28 - Lanús","29 - V.Lynch" ,"30 - Moreno","31 - Paternal","32 - V.Soldati","33 - Munro","34 - San Justo"
    ,"35 - Turdera","36 - Ituzaingó","37 - Haedo","38 - Moron","39 - San Cristobal","41 - P.Chacabuco","42 - Saavedra","43 - V.Mitre","44 - Quilmes"
    ,"46 - Flores Sud","47 - Tucuman","49 - L.de Zamora","50 - Mataderos","52 - V.Sahores","54 - Avellaneda","55 - V.Adelina","56 - V.Maipu"
    ,"57 - V.Urquiza","59 - Barracas","60 - V.Ortuzar","61 - Wilde","62 - S.F.Solano","63 - San Juan","65 - Flores Ctro","66 - R.de Escalada"
    ,"67 - Dock Sud","68 - V.P.Caseros","69 - V.Martelli","70 - Tribunales","72 - L.del Mirador","73 - Jujuy","74 - Caseros Ctro","76 - Martinez"
    ,"78 - R.Castillo","79 - Crucesita","80 - Puan","81 - Rosario Norte","82 - C.Rivadavia","83 - 27 de Febrero","84 - J.B.Justo","85 - Salta"
    ,"86 - La Florida","89 - MDP Centro","90 - Ing.White","91 - O.Lagos","92 - Zarate","93 - Neuquen","94 - Puerto","95 - Luján","96 - S.Rafael"
    ,"100 - CBA Centro","103 - Necochea","104 - Tres Arroyos","105 - S.del Estero","106 - H.Ascasubi","108 - Maipu","110 - Cent. Rosario","111 - Pigüé"
    ,"112 - V.María","113 - Dorrego","115 - Mendoza Ctro","116 - Godoy Cruz","117 - 9 de Julio","118 - Monolito","119 - Monserrat","120 - B.B.Shopping"
    ,"121 - Miramar","122 - Pilar","124 - Casbas","125 - Las Flores","126 - Carhue","127 - B.Blanca","128 - Bragado","129 - Las Villas","130 - Noroeste"
    ,"131 - Saladillo","132 - Cipolletti","134 - R.Colorado","135 - Gral.Cerri","136 - Tandil","137 - Bariloche","138 - Batan","139 - 25 de Mayo","140 - Pcias.Unidas"
    ,"141 - Concordia","142 - Chacabuco","143 - Hospital","144 - Chivilcoy","145 - Parana","146 - Trelew","147 - J.Arauz","148 - Darragueira","149 - Olivos"
    ,"151 - Pto.Madryn","153 - Balvanera","156 - Bolivar","157 - Villa Rosas","168 - Centenario","173 - Plaza","174 - Pto.Madero","186 - T.Lauquen","197 - El Talar"
    ,"198 - 9 de Abril","217 - Gualeguaychu","218 - Plaza Lavalle","219 - C.del Uruguay","220 - Mercado Central","221 - Flores Norte","222 - Campana","223 - San Isidro"
    ,"224 - San Nicolas","225 - Caballito","226 - Belgrano","227 - Altos del Talar","229 - Posadas","230 - Olavarria","231 - Loma Hermosa","232 - Los Boulevares"
    ,"233 - Burzaco","236 - Berisso","237 - Villa Contitución","238 - General Roca","239 - San Luis","240 - Florencio Varela","241 - Almagro","242 - Viedma"
    ,"244 - Puente de la Mujer","246 - Plaza de Mayo","247 - Bicentenario","248 - Munro Centro","249 - Plaza Miserere","250 - Marcos Juarez","251 - Santa Rosa"
    ,"252 - Caleta Olivia","253 - Catamarca","254 - Cultra Co","256 - Lanus Oeste","262 - Centeno","264 - Mria.Teresa","265 - Las Rosas","266 - El Trebol"
    ,"267 - Mria.Juana","268 - Fighiera","270 - A.Ledesma","272 - San Lorenzo","274 - Coral","275 - C.de Gomez","276 - V.Gob.Galvez","281 - V.Tuerto","283 - Las Parejas"
    ,"285 - Gral.Lagos","286 - Arroyo Seco","287 - P.Esther","288 - Rosario Ctro","289 - Arribeños","291 - Lincoln","292 - Perez","293 - Firmat","294 - Las Flores SFE"
    ,"295 - Soldini","296 - Mria.Susana","315 - Lavalle","316 - Morandini","317 - Mercado","318 - Rivadavia","319 - Junin MZA","320 - Palmira","321 - Tunuyan"
    ,"322 - Tupungato","323 - Gutierrez","324 - R.de la Cruz","331 - Piamonte","333 - Videla","334 - M.Cabal","335 - Sastre","336 - S.C.de Saguier","338 - Pergamino"
    ,"340 - Santa Fe","341 - C.Casares","342 - Rojas","343 - Avellaneda SFE","344 - Rosario Sur","345 - Malabrigo","346 - San Francisco","348 - San Vicente","349 - Colon"
    ,"350 - Salto","353 - Sta.Isabel","357 - Carmen","358 - Arrecifes","359 - Elortondo","362 - San Jorge","364 - G.Baigorria","365 - San Cristobal", "366 - L.Paiva"
    ,"368 - Los Cardos","369 - Rafaela","370 - Bombal","371 - Pehuajo","372 - Calchaquí","374 - S.M.de las Escobas","375 - Resistencia","376 - Corrientes","377 - Vera"
    ,"378 - R.S.Peña","380 - V.Mugheta","381 - Casilda","383 - Bigand","385 - San Genaro","386 - Murphy","388 - Reconquista","416 - San Vicente","429 - Alem"
    ,"432 - Z.Sur La Plata","434 - Ensenada","437 - La Cumbre","439 - Plaza S.Martin","440 - Catedral","490 - Margarita","494 - R.Cuarto","495 - A.Gigena","496 - Almafuerte"
    ,"497 - Rio Tercero","501 - Barrio Centro","502 - Peatonal Cordoba","503 - Barrio Poeta Lugones","504 - Ruta 20","505 - Dalmacio Velez Sarsfield","506 - Alta Cordoba"
    ,"508 - Alta Gracia","509 - Carlos Paz","511 - Jesus Maria","512 - La Falda","513 - Cosquin","514 - Rio Ceballos","515 - Primera Junta","516 - Echesortu","517 - Microcentro"
    ,"528 - Azul","532 - Villa Allende","533 - Villa Cañas","537 - Junin BA","584 - R.C.T","585 - Coop. De Servicios Publicos C. Paz (COOPI)","586 - Coop. Credito Necochea"
    ,"587 - VN Global","588 - Estudio Beretta","590 - Oficina de Promocion. Univ. Litoral","593 - CALF","594 - Universidad del Litoral - Rectorado"
    ,"595 - Universidad UNL - Ciud. Univ. - El Pozo","597 - Universidad UNL - Esperanza","598 - Oficina de Promocion River Plate","599 - Oficina Promocion Munic. de Berazategui"
    ,"601 - Seguvip","602 - DATCO DATACENTER","603 - Testing","662 - Testing ATM-TAS","664 - Implementaciones","676 - Capacitación - Corrientes 550"
    ,"685 - Estacion Terminal Omnibus Sta. Fe","687 - Cash IP River Plate","688 - Coop. Eléctrica de Zarate","689 - Oficina Promocion Munic. de Bahia Blanca"
    ,"690 - DIRECT GROUP (Ptos. Credicoop)","691 - Estudio Martinez de Alzaga","694 - GSA Collection Argentina","695 - Cash Municipalidad de Berazategui"
    ,"696 - STARLIGHT Electronica SRL","697 - Nancel SA","855 - BUP (Base Unica de Personas)","900 - Farmacia - Rafael Castillo"
    ,"901 - Cooperativa de Serv. Pub., Vivienda y Credito","902 - Asoc. Medica Rosario","903 - IMFC - Rosario","904 - IMFC - Bs. As","905 - Edificio Sanidad-Berazategui"
    ,"906 - YPF La Norteña","907 - MOQSA","908 - Cerro Otto","909 - Norauto Auchan","910 - Municipalidad de Berazategui","911 - Municipalidad de Cañada de Gomez"
    ,"912 - Municipalidad de Cipolletti","913 - Municipalidad Quilino","914 - Shell - Carlos Peters","915 - Colonia Montes de Oca","916 - Transp. Don Pedro Campana - Euroamerica"
    ,"917 - CCT - Call Center","918 - Coop. Telefonica de Tortuguitas","919 - Municipalidad de Junin","920 - COTO - Temperley","921 - COTO - José C. Paz","922 - COTO - Lanus"
    ,"923 - COTO - Avellaneda","924 - COTO - Jose C. Paz","925 - ATM Retiro","926 - COOPI Villa Carlos Paz","927 - Cooperativa Electrica del Viso","928 - Almacén de Golosinas"
    ,"929 - Transporte la Perlita","930 - Universidad del Comahue","931 - Universidad del Litoral - Economicas","932 - Universidad de Cordoba","933 - Universidad Nac. De la Patagonia"
    ,"940 - YPF Luis Guillon","941 - Caja Popular de Tucumán","942 - Petroquimica Rio Tercero","943 - Estación de Servicio Buzancy","944 - Centro Cultural de la Cooperacion"
    ,"945 - CNP","946 - Cooperativa Mariano Acosta","947 - Auditoria Rosario","948 - Instituto Rosario","949 - SEGURCOOP","950 - Sup. Carrefour - Mte. Grande"
    ,"951 - Sup. Carrefour - Mendoza","955 - Shopping Moreno","956 - La Fundación","957 - Cooperativa de Zarate","958 - Hiper. Coop. Obrera de Bahia Blanca"
    ,"959 - Comuna de Alvear","960 - Delegacion Municipal de Sta. Fe","961 - Mercado Norte de Santa Fe","962 - Ferrovias Boulogne","963 - Estacion de Servicio RHASA Don Torcuato"
    ,"964 - Coop.Wilde Viv. Cons.y Servicios","965 - Shopping Plaza Bahia Blanca","966 - TANDANOR S.A","967 - Cooperativa Electrica Lujan","968 - Hipermercado Comodin"
    ,"969 - Cooperativa Parada Robles (CEPRAL)","970 - Terminal de Omnibus Viedma","971 - Delegacion Municipal La Colonia (Mendoza)","972 - Terminal de Omnibus Pacheco"
    ,"973 - Estacion ESSO Ciudadela-Flagor S.A","974 - Estación de Servicio Agrinda S.A","975 - Coop. TICINENSE de Serv. Públicos","976 - Hospital Municipal de B. Blanca"
    ,"977 - Transp. Don Pedro-Hurlingham","978 - Transp. Don Pedro-Mendoza","979 - Est. De Servicio SOL-El Talar","980 - Drago 458","981 - Club Velez Sarfield"
    ,"982 - Soc. Coop. Popular Ltda. C. Rivadavia","983 -  YPF Monte Grande","984 - Transp. Don Pedro-Bahia Blanca","985 - Estacion de Serv. SOL - Ezpeleta"
    ,"986 - Dalvian-Barrio Privado","990 - Agroactiva","999 - Gastos Grales Del Proyecto","1720 - Coop.Telefónica M.Acosta","1721 - Coop.de Agua Potable Lisandro Olmos"
    ,"1722 - Coop.de Servicios Públicos de Fatima","1723 - Coop. De Agua Potable Marcelino Escalada","1724 - Confiar Coop.de Trabajo Ltda","1725 - Coop.Eléctrica de Bragado"
    ,"1726 - Coop.de agua de Suipacha","1727 - Sociedad Coop.Popular Ltda. De C.Rivadavia","1728 - Liga de Comercio y la Industria de San Martín"
    ,"1730 - Ctro.de Almaceneros y Afines de Gualeguaychú","1731 - Coop.de Agua Potable Lna.Alsina","1732 - Asoc.de Empresarios de la Región Ctro.Argentino"
    ,"1733 - Camara Comercio e Industria F.Varela","1734 - Asoc.Mutual Ctro.Económico Cañada de Gomez","1735 - Coop.Eléctrica Mar del Sud"
    ,"1736 - Ctro.Económico San Jose de la Esquina","1737 - Cámara de Comercio 30 de Agosto","1738 - Coop. De O. Y Serv. Públicos de Cañada de Ucle"
    ,"1739 - Camara de Comercio e Ind. De Campana","1740 - Coop. O y Serv.Públicos de Oliva","1741 - Mutual de Integ. Poder Judicial de Sta.Fe","1742 - Coop.Eléctrica de V.Gesell"
    ,"1743 - Coop.Oy Serv.Públicos Salsipuedes","1744 - Coop.Tel.y de Serv. Villa Flandria","1745 - Cooperativa de San Gregorio Limitada","1746 - Coop. De Vivienda Crédito y Consumo habitar"
    ,"1747 - Club Mutual Chanear Ladeado","1749 - Coop. Electrica Gualeguaychu","1750 - Coop.de Agua Potable de Villa Mantero","1751 - Coop.de Elec.S.Publicos y V. De Diego de Alvear"
    ,"1752 - Coop. De Tamberos de Gualeguaychú","1753 - Actual S.A","1754 - Coop de Provisión de Servicios de Iriarte Ltda","1755 - Coop.Agrop.e Industría de Rauch"
    ,"1756 - Club A. Social Deportivo Racing de Villada","1757 - Coopersive Ltda","1758 - Sociedad Italiana de Correa","1759 - Camara Empresaria de Escobar"
    ,"1760 - Coop. De Obras Tres Límites","1761 - Mutual de Escribanos de Mendoza","1762 - Cooperativa de Agua Luis Beltran","1763 - Coop.Serv.Públicos de Garré"
    ,"1765 - Asoc.Mutual Emp.Ministerio de Acc.Social","1766 - Coop Telefónica de Villa Eloísa","1767 - Coop.Serv.Villa Giardino","1768 - Asoc.Mutual Asoc. y Ad. Club Colon de San Justo"
    ,"1769 - Mutual Personal ACINDAR","1770 - Mutual de Ayuda Las Petacas","1771 - MUTUAL DE SOCIOS ESPORTIVO FÚTBOL CLUB","1773 - Coop.Elect. Parada Robles A.de la Cruz"
    ,"1774 - Coop. De Trabajo CONFIAR","5000 - GERCOM-Implementaciones","5001 - GERCOM-Alsina","5002 - GERCOM-Tucuman 5° Piso","5003 - GERCOM-Tucuman 6° Piso"
    ,"5005 - Reconquista 468 1ºPiso","5006 - Reconquista 468 2ºPiso 3°Cuerpo","5100 - GEROPE-Bup","5200 - GERHUM-Capacitación","5201 - GERHUM-Edificio Corrientes 550"
    ,"5300 - GEREXT-Comercio Exterior","5400 - Gcia. De Procesamiento","5410 - Gcia. Asuntos Legales","5420 - Gcia. De Estrategia Comercial","5450 - Banca Personal"
    ,"5490 - Reconquista 406  2°Piso","5500 - Reconquista 484 EP-Subsuelo","5501 - Reconquista 484 2ºPiso","5502 - Reconquista 484 4ºPiso","5503 - Reconquista 484 3ºPiso"
    ,"5504 - Reconquista 484 6ºPiso","5505 - Oficina Carlos Heller","5506 - Reconquista 484 5ºPiso","5507 - Reconquista 484 7ºPiso","5508 - Tesoreria General-Reconquista 484 1ºSS"
    ,"5509 - Reconquista 468 8°Piso","5510 - Reconquista 452 Entre Piso","5520 - Reconquista 468 3°Piso","5521 - Reconquista 468-Cochera","5522 - Reconquista 468 4ºPiso"
    ,"5523 - Reconquista 468 6°Piso","5524 - Reconquista 468 5°Piso","5530 - Maipu 66 3ºPiso","5531 - Maipu 66 1ºPiso","5540 - Lavalle 406 3°Piso","5541 - Lavalle 406 5°Piso"
    ,"5542 - Lavalle 406 4°Piso","5543 - Asesoria en Optimizaciones y Procesos","5544 - Lavalle 406 7°Piso","5550 - Gerencia de Calidad de Servicio","5560 - Drago 458 PB"
    ,"5561 - Edificio Drago 440","5570 - Edificio Tucuman","5600 - Contaduria","5700 - GERINF-Seg. Informatica-Maipú 66","5800 - GERFIN - Mesa de Dinero","5900 - Deposito de Munro"};


    public IngresarElemento(){
        if(!Global.pantallasCreadas[1]){
            Global.pantallasCreadas[1]=true;
            cantPantallasCreadas++;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_elemento);

        firebaseFirestore = FirebaseFirestore.getInstance();

        navigationView = findViewById(R.id.navigation);
        editCodigoBarra = findViewById(R.id.editCodigoElemento);
        editNombreElemento = findViewById(R.id.edit_nombre_elemento);
        editFilial = findViewById(R.id.edit_filial);
        drawerLayout = findViewById(R.id.drawerLayoutIngElem);
        toolbar = findViewById(R.id.toolbarIngElem);

        editNombreElemento.setTitle("Elija un elemento");

        verificarElementosYTecnicoCargado();

        //ACA ACTIVO EL BOTON DEL TOOLBAR PARA ABRIR EL NAVIGATION VIEW
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab!=null){
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        cargarSpinner();

        String[] nroFilial1 = Global.usuario.getEmail().split("@");
        String nroFilial = nroFilial1[0];
        String[] nroFilial2 = nroFilial.split("al");
        Global.nroFilial = Integer.parseInt(nroFilial2[1]);
        int i = 0, j=0;
        do {
            String[] nroFilial3 = filiales[i].split("-");
            //Log.d("PRUEBA","" + Integer.parseInt(nroFilial3[0].trim()));
            if(Global.nroFilial==Integer.parseInt(nroFilial3[0].trim())){
                Global.filial = filiales[i];
                j=1;
                editFilial.setText(Global.filial);
            }
            i++;
        }while (filiales.length > i && j==0);

        if(Global.codigo==null || Global.codigo.equals("")){
            editCodigoBarra.setText(Global.codigo);
            editCodigoBarra.setEnabled(true);
            editCodigoBarra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editCodigoBarra.setFocusable(true);
                }
            });
        }else{
            editCodigoBarra.setText(Global.codigo);
            editCodigoBarra.setEnabled(false);
        }

        volver = findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent (IngresarElemento.this, EscanearCodigo.class));
            }
        });



        agregarElemento = findViewById(R.id.agregar_elemento);
        agregarElemento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarElemento();
            }
        }
        );

        // ACTIVAR O HACER QUE FUNCIONEN LOS BOTONES DEL MENU DESPLEGABLE DEL MENU
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem Item) {
                switch (Item.getItemId()) {
                    case R.id.escanearCodigo:
                        Global.codigo="";
                        startActivity(new Intent(IngresarElemento.this, EscanearCodigo.class));
                        return true;

                    case R.id.mostrarElementos:
                        if(Global.hayElementosCargados){
                            startActivity(new Intent(IngresarElemento.this, MostrarElementos.class));
                        }else{
                            Toast.makeText(IngresarElemento.this, "No hay ningun elemento cargado", Toast.LENGTH_LONG).show();
                        }
                        return true;
                }
                return true;
            }
        });

        guardarDatosServicio();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return(true);
        }
        return super.onOptionsItemSelected(item);
    }
    private void guardarDatosServicio() {
        SharedPreferences preferences = getSharedPreferences("datosServicio", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("terminoRetiro", Global.terminoRetiro);
        editor.commit();
    }

    private void agregarElemento() {
        String nombre = editNombreElemento.getSelectedItem().toString();
        String codigo = editCodigoBarra.getText().toString().trim();
        String filial = editFilial.getText().toString().trim();

        if(codigo.isEmpty()){
            editCodigoBarra.setError("El codigo no puede estar vacio");
            return;
        }else if (codigo.length() < 5){
            editCodigoBarra.setError("Complete el codigo del elemento");
            return;
        }

        dialog = ProgressDialog.show(IngresarElemento.this,"", "Verificando datos...");

        elemento = new Elemento(nombre, codigo, filial);

        Map<String, Object> dato = new HashMap<>();
        dato.put("nombre", elemento.getNombre());
        dato.put("codigo", elemento.getCodigo());
        dato.put("filial", elemento.getFilial());

        verificarExisteElemento(codigo, dato);
    }

    private void verificarExisteElemento(String codigo, Object dato) {
        boolean[] existe = new boolean[1];
        boolean[] nuncaExistio = new boolean[1];
        firebaseFirestore.collection(Global.usuario.getEmail())
                .document(Global.fechaRetiro).collection("cajas")
                .document(Global.horaInicioRetiro).collection("elementos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                existe[0] = document.getId().equals(codigo);
                                if(existe[0]){
                                    dialog.dismiss();
                                    nuncaExistio[0] = true;
                                    mostrarAlerta(codigo, dato);
                                }
                            }
                        }
                        if(!nuncaExistio[0]){
                            actualizarAgregarElemento(codigo, dato, false);
                        }
                    }
                });
    }

    private void mostrarMensaje(boolean existe){
        if(!existe) {
            Toast.makeText(this, "Elemento agregado exitosamente", Toast.LENGTH_SHORT).show();
            verificarElementosYTecnicoCargado();
        }else{
            Toast.makeText(this, "El elemento ha sido actualizado", Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
        Global.codigo="";
        startActivity(new Intent (IngresarElemento.this, EscanearCodigo.class));
    }

    private void cargarSpinner(){
        // CARGAR NOMBRES EN LOS SPINNERS
        ArrayAdapter<String> nombreElementosAdapter = new ArrayAdapter<String>(IngresarElemento.this, android.R.layout.simple_spinner_item, Global.elementos);
        nombreElementosAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        editNombreElemento.setAdapter(nombreElementosAdapter);
    }

    private void actualizarAgregarElemento(String codigo, Object dato, Boolean estado){
        firebaseFirestore.collection(Global.usuario.getEmail())
                .document(Global.fechaRetiro).collection("cajas")
                .document(Global.horaInicioRetiro).collection("elementos")
                .document(codigo).set(dato)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                mostrarMensaje(estado);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(IngresarElemento.this, "Error al cargar el elemento", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarAlerta(String codigo, Object dato) {
        AlertDialog.Builder builder = new AlertDialog.Builder(IngresarElemento.this);
        builder.setMessage("El elemento ya existe. Desea actualizarlo?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        actualizarAgregarElemento(codigo, dato, true);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Global.codigo="";
                        startActivity(new Intent (IngresarElemento.this, EscanearCodigo.class));
                    }
                });

        builder.show();
    }

    private void verificarElementosYTecnicoCargado(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        //VERIFICO SI SE CARGO AL MENOS UN ELEMENTO
        firebaseFirestore.collection(Global.usuario.getEmail())
                .document(Global.fechaRetiro).collection("cajas")
                .document(Global.horaInicioRetiro).collection("elementos").limit(1)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){
                            Global.hayElementosCargados = false;
                        }else{
                            Global.hayElementosCargados = true;
                        }
                    }
                });

        //VERIFICO SI SE CARGO UN TECNICO
        firebaseFirestore.collection(Global.usuario.getEmail()).document(Global.fechaRetiro)
                .collection("cajas").document(Global.horaInicioRetiro).collection("tecnico").limit(1)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){
                            Global.tecnicoRegistrado=false;
                        }else{
                            Global.tecnicoRegistrado=true;
                        }
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            startActivity(new Intent(this, EscanearCodigo.class));
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        pantallas[1] = false;
        actualizarEstadoCuenta();
        cargarSpinner();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pantallas[1] = true;
        actualizarEstadoCuenta();
    }
}
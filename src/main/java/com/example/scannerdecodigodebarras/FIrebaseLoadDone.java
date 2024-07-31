package com.example.scannerdecodigodebarras;

import java.util.List;

public interface FIrebaseLoadDone {

    void onFirebaseLoadSuccess(List<Producto> productos);
    void onFirebaseLoadFailed(String mensaje);

}

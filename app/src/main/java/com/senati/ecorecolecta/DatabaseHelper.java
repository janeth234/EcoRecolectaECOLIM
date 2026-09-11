package com.senati.ecorecolecta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Administrador de la base de datos local SQLite.
 * La autenticación de trabajadores se delega a Firebase Authentication
 * (ver MainActivity); esta base de datos se dedica exclusivamente a la
 * tabla "residuos": la bitácora de recolección, con bandera
 * "sincronizado" para saber qué registros faltan enviar a la API
 * RESTful remota.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ecolim.db";
    private static final int DB_VERSION = 2;

    public static final String TABLA_RESIDUOS = "residuos";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLA_RESIDUOS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tipo_residuo TEXT NOT NULL, " +
                "cantidad_kg REAL NOT NULL, " +
                "area TEXT, " +
                "observaciones TEXT, " +
                "fecha_hora TEXT NOT NULL, " +
                "usuario TEXT, " +
                "sincronizado INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_RESIDUOS);
        onCreate(db);
    }

    // ---------- RESIDUOS (CRUD) ----------

    public long insertarResiduo(Residuo r) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("tipo_residuo", r.getTipoResiduo());
        valores.put("cantidad_kg", r.getCantidadKg());
        valores.put("area", r.getArea());
        valores.put("observaciones", r.getObservaciones());
        valores.put("fecha_hora", r.getFechaHora());
        valores.put("usuario", r.getUsuario());
        valores.put("sincronizado", 0);
        return db.insert(TABLA_RESIDUOS, null, valores);
    }

    public List<Residuo> obtenerResiduos(String filtroTipo, String fechaDesde, String fechaHasta) {
        List<Residuo> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        StringBuilder sql = new StringBuilder("SELECT * FROM " + TABLA_RESIDUOS + " WHERE 1=1");
        List<String> args = new ArrayList<>();

        if (filtroTipo != null && !filtroTipo.equals("Todos")) {
            sql.append(" AND tipo_residuo = ?");
            args.add(filtroTipo);
        }
        if (fechaDesde != null) {
            sql.append(" AND date(fecha_hora) >= date(?)");
            args.add(fechaDesde);
        }
        if (fechaHasta != null) {
            sql.append(" AND date(fecha_hora) <= date(?)");
            args.add(fechaHasta);
        }
        sql.append(" ORDER BY fecha_hora DESC");

        Cursor c = db.rawQuery(sql.toString(), args.toArray(new String[0]));
        while (c.moveToNext()) {
            Residuo r = new Residuo();
            r.setId(c.getInt(c.getColumnIndexOrThrow("id")));
            r.setTipoResiduo(c.getString(c.getColumnIndexOrThrow("tipo_residuo")));
            r.setCantidadKg(c.getDouble(c.getColumnIndexOrThrow("cantidad_kg")));
            r.setArea(c.getString(c.getColumnIndexOrThrow("area")));
            r.setObservaciones(c.getString(c.getColumnIndexOrThrow("observaciones")));
            r.setFechaHora(c.getString(c.getColumnIndexOrThrow("fecha_hora")));
            r.setUsuario(c.getString(c.getColumnIndexOrThrow("usuario")));
            r.setSincronizado(c.getInt(c.getColumnIndexOrThrow("sincronizado")));
            lista.add(r);
        }
        c.close();
        return lista;
    }

    public List<Residuo> obtenerPendientesSincronizar() {
        return obtenerResiduosPorSync(0);
    }

    private List<Residuo> obtenerResiduosPorSync(int estado) {
        List<Residuo> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLA_RESIDUOS + " WHERE sincronizado = ?",
                new String[]{String.valueOf(estado)});
        while (c.moveToNext()) {
            Residuo r = new Residuo();
            r.setId(c.getInt(c.getColumnIndexOrThrow("id")));
            r.setTipoResiduo(c.getString(c.getColumnIndexOrThrow("tipo_residuo")));
            r.setCantidadKg(c.getDouble(c.getColumnIndexOrThrow("cantidad_kg")));
            r.setArea(c.getString(c.getColumnIndexOrThrow("area")));
            r.setFechaHora(c.getString(c.getColumnIndexOrThrow("fecha_hora")));
            lista.add(r);
        }
        c.close();
        return lista;
    }

    public void marcarComoSincronizado(int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sincronizado", 1);
        db.update(TABLA_RESIDUOS, cv, "id = ?", new String[]{String.valueOf(id)});
    }
}

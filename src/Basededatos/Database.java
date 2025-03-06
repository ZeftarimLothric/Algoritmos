package Basededatos;

import DatabaseConnection.ConnectionDatabases;
import SQLService.SQLSentences;
import java.sql.Connection;
import java.util.List;

import Basededatos.clases.Users;

public class Database {
    public static void main(String[] args) {
        ConnectionDatabases db = new ConnectionDatabases();
        try {
            Connection conexion = db.getMariaDbConnection("MyBaseDeDatos", "root", "");
            System.out.println("Conexión exitosa");
            SQLSentences<Users> usuarios = new SQLSentences<>(Users.class);
            List<Users> resultado = usuarios.DynamicGetListMethod("select * from user", conexion);
            for (int i = 0; i < resultado.size(); i++) {
                System.out.println(resultado.get(i));

            }
            boolean insertRes = usuarios.DynamicInsertMethod("user", "username,pass",
                    new Object[] { "Rodrigo", "654321" },
                    conexion);
            if (insertRes) {
                System.out.println("Registro insertado");
            } else {
                System.out.println("Error al insertar");
            }
            conexion.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
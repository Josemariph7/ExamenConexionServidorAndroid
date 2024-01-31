package com.example.examenconexionservidorandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class CRUD extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud);

        Button btnAgregarUsuario = findViewById(R.id.btnAgregarUsuario);
        Button btnModificarContrasena = findViewById(R.id.btnModificarContrasena);
        Button btnEliminarUsuario = findViewById(R.id.btnEliminarUsuario);
        Button btnEliminarTodosUsuarios = findViewById(R.id.btnEliminarTodosUsuarios);

        btnModificarContrasena.setOnClickListener(v -> mostrarDialogoCambiarContrasena());
        btnEliminarTodosUsuarios.setOnClickListener(v -> eliminarTodosUsuarios());
        btnAgregarUsuario.setOnClickListener(v -> mostrarDialogoInsertarUsuario());
        btnEliminarUsuario.setOnClickListener(v -> mostrarDialogoEliminarUsuario());

    }

    private void eliminarTodosUsuarios() {
        new EliminarTodosUsuariosTask().execute();
    }

    private class EliminarTodosUsuariosTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL("http://192.168.240.160/examen/eliminarTodosLosUsuarios.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.getOutputStream().close();

                // Leer la respuesta del servidor
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString(); // Retorna la respuesta del servidor
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(CRUD.this, result, Toast.LENGTH_LONG).show();
            startActivity(new Intent(CRUD.this, MainActivity.class));
            finish();
        }
    }

    private void mostrarDialogoInsertarUsuario() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CRUD.this);
        builder.setTitle("Insertar nuevo usuario");
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_insertar_usuario, null);
        builder.setView(customLayout);
        builder.setPositiveButton("Insertar", (dialog, which) -> {
            EditText editTextUsuario = customLayout.findViewById(R.id.editTextUsuario);
            EditText editTextPassword = customLayout.findViewById(R.id.editTextPassword);
            String usuario = editTextUsuario.getText().toString();
            String password = editTextPassword.getText().toString();
            insertarUsuario(usuario, password);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    public void insertarUsuario(String usuario, String contrasena) {
        AsyncTask.execute(() -> {
            try {
                URL url = new URL("http://192.168.240.160/examen/insertarUsuario.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data = URLEncoder.encode("usuario", "UTF-8") + "=" + URLEncoder.encode(usuario, "UTF-8") + "&" +
                        URLEncoder.encode("contrasena", "UTF-8") + "=" + URLEncoder.encode(contrasena, "UTF-8");
                writer.write(data);
                writer.flush();
                writer.close();
                os.close();
                conn.getInputStream();
                conn.disconnect();
                startActivity(new Intent(CRUD.this, MainActivity.class));
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void mostrarDialogoEliminarUsuario() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CRUD.this);
        builder.setTitle("Eliminar Usuario");
        final EditText input = new EditText(CRUD.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombreUsuario = input.getText().toString();
                eliminarUsuario(nombreUsuario);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void eliminarUsuario(String usuario) {
        new EliminarUsuarioTask().execute(usuario);
        startActivity(new Intent(CRUD.this, MainActivity.class));
        finish();
    }

    private class EliminarUsuarioTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String usuario = params[0];
            HttpURLConnection conn = null;
            try {
                URL url = new URL("http://192.168.240.160/examen/eliminarUsuario.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data = URLEncoder.encode("usuario", "UTF-8") + "=" + URLEncoder.encode(usuario, "UTF-8");
                writer.write(data);
                writer.flush();
                writer.close();
                os.close();
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(CRUD.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoCambiarContrasena() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CRUD.this);
        builder.setTitle("Cambiar Contraseña");
        final EditText inputUsuario = new EditText(CRUD.this);
        inputUsuario.setInputType(InputType.TYPE_CLASS_TEXT);
        inputUsuario.setHint("Nombre de usuario");
        builder.setView(inputUsuario);
        builder.setPositiveButton("Siguiente", (dialog, which) -> {
            String nombreUsuario = inputUsuario.getText().toString();
            mostrarDialogoCambiarContrasenaDetalle(nombreUsuario);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void mostrarDialogoCambiarContrasenaDetalle(String nombreUsuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CRUD.this);
        builder.setTitle("Cambiar Contraseña para " + nombreUsuario);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_cambiar_contrasena, null);
        builder.setView(customLayout);
        builder.setPositiveButton("Cambiar", (dialog, which) -> {
            EditText editTextContrasenaActual = customLayout.findViewById(R.id.editTextContrasenaActual);
            EditText editTextContrasenaNueva = customLayout.findViewById(R.id.editTextContrasenaNueva);
            String contrasenaActual = editTextContrasenaActual.getText().toString();
            String contrasenaNueva = editTextContrasenaNueva.getText().toString();
            cambiarContrasena(nombreUsuario, contrasenaActual, contrasenaNueva);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void cambiarContrasena(String usuario, String contrasenaActual, String contrasenaNueva) {
        new CambiarContrasenaTask().execute(usuario, contrasenaActual, contrasenaNueva);
        startActivity(new Intent(CRUD.this, MainActivity.class));
        finish();
    }

    private class CambiarContrasenaTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String usuario = params[0];
            String contrasenaActual = params[1];
            String contrasenaNueva = params[2];
            HttpURLConnection conn = null;
            try {
                URL url = new URL("http://192.168.240.160/examen/cambiarPass.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data = URLEncoder.encode("usuario", "UTF-8") + "=" + URLEncoder.encode(usuario, "UTF-8") +
                        "&" + URLEncoder.encode("contrasenaActual", "UTF-8") + "=" + URLEncoder.encode(contrasenaActual, "UTF-8") +
                        "&" + URLEncoder.encode("contrasenaNueva", "UTF-8") + "=" + URLEncoder.encode(contrasenaNueva, "UTF-8");
                writer.write(data);
                writer.flush();
                writer.close();
                os.close();
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(CRUD.this, result, Toast.LENGTH_LONG).show();
        }
    }
}

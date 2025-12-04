package com.juatsapp.app;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Servicio de autenticación encargado de registrar usuarios nuevos
 * y validar el inicio de sesión contra la base de datos.
 */
public class AuthService {

    private final UserDao userDao;

    /**
     * Crea una nueva instancia del servicio de autenticación
     * inicializando el acceso a la colección de usuarios.
     */
    public AuthService() {
        this.userDao = new UserDao();
    }

    /**
     * Registra un nuevo usuario si el teléfono no existe aún.
     * La contraseña se almacena hasheada mediante BCrypt.
     *
     * @return true si el registro fue exitoso, false si el teléfono ya existe
     */
    public boolean register(String phone, String rawPassword,
                            java.util.Date birthDate, String address, String sex) {
        if (phone == null || rawPassword == null || birthDate == null || address == null || sex == null) {
            return false;
        }

        // Verificar si ya existe un usuario con ese teléfono
        User existing = userDao.findByPhone(phone);
        if (existing != null) {
            return false; // ya existe
        }

        // Hashear contraseña
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        User user = new User(phone, hash, birthDate, address, sex);
        userDao.registerUser(user);
        return true;
    }

    /**
     * Intenta iniciar sesión con un teléfono y contraseña.
     *
     * @return el usuario autenticado o null si las credenciales no son válidas
     */
    public User login(String phone, String rawPassword) {
        User user = userDao.findByPhone(phone);
        if (user == null) {
            return null;
        }

        boolean ok = BCrypt.checkpw(rawPassword, user.getPasswordHash());
        return ok ? user : null;
    }
}

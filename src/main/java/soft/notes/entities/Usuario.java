package soft.notes.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class Usuario { // Cambiado a Singular

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "telefono")
    private String telefono; // CORREGIDO: Integer -> String

    @Column(name = "correo")
    private String correo;

    @Column(name = "rol")
    private String rol;

    @Column(name = "password")
    private String password;

    @Column(name = "activo")
    private Boolean activo;
}
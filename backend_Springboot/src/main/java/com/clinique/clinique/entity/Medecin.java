package com.clinique.clinique.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "medecins")
public class Medecin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    private String nom;

    @NotBlank(message = "La spécialité est obligatoire")
    private String specialite;

    @NotNull(message = "La disponibilité est obligatoire")
    private Boolean disponibilite;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Column(unique = true)
    private String email;

    @Column(nullable = true)
    private String username;

    @Column(nullable = true)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.MEDECIN;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.PENDING;

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RendezVous> rendezVous;

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DoctorInvitation> invitations;

    @ManyToMany
    @JoinTable(
        name = "medecin_secteurs",
        joinColumns = @JoinColumn(name = "medecin_id"),
        inverseJoinColumns = @JoinColumn(name = "secteur_id")
    )
    private List<Secteur> secteurs;

    // Getters
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getSpecialite() { return specialite; }
    public Boolean getDisponibilite() { return disponibilite; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public List<RendezVous> getRendezVous() { return rendezVous; }
    public List<DoctorInvitation> getInvitations() { return invitations; }
    public List<Secteur> getSecteurs() { return secteurs; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    public void setDisponibilite(Boolean disponibilite) { this.disponibilite = disponibilite; }
    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(UserRole role) { this.role = role; }
    public void setStatus(UserStatus status) { this.status = status; }
    public void setRendezVous(List<RendezVous> rendezVous) { this.rendezVous = rendezVous; }
    public void setInvitations(List<DoctorInvitation> invitations) { this.invitations = invitations; }
    public void setSecteurs(List<Secteur> secteurs) { this.secteurs = secteurs; }
}
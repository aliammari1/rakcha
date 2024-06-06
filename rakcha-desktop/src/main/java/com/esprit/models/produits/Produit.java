package com.esprit.models.produits;


public class Produit {


    private int id_produit;

    private String nom;
    private int prix;
    private String image;
    private String description;

    private Categorie_Produit categorieProduit;

    private int quantiteP;


    public Produit(int id_produit, String nom, int prix, String image, String description, Categorie_Produit categorieProduit, int quantiteP) {
        this.id_produit = id_produit;
        this.nom = nom;
        this.prix = prix;
        this.image = image;
        this.description = description;
        this.categorieProduit = categorieProduit;
        this.quantiteP = quantiteP;
    }


    public Produit(String nom, int prix, String image, String description, Categorie_Produit categorieProduit, int quantiteP) {
        this.nom = nom;
        this.prix = prix;
        this.image = image;
        this.description = description;
        this.categorieProduit = categorieProduit;
        this.quantiteP = quantiteP;
    }


    public Produit(int id_produit) {
        this.id_produit = id_produit;
    }

    public Produit() {

    }

    
    /** 
     * @return int
     */
    public int getId_produit() {
        return id_produit;
    }

    
    /** 
     * @param id_produit
     */
    public void setId_produit(int id_produit) {
        this.id_produit = id_produit;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantiteP() {
        return quantiteP;
    }

    public void setQuantiteP(int quantiteP) {
        this.quantiteP = quantiteP;
    }


    public Categorie_Produit getCategorie() {
        return categorieProduit;
    }


    public void setCategorie(Categorie_Produit categorieProduit) {
        this.categorieProduit = categorieProduit;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id_produit=" + id_produit +
                ", nom='" + nom + '\'' +
                ", prix='" + prix + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", categorie=" + categorieProduit +
                ", quantiteP=" + quantiteP +
                '}';
    }


    public Categorie_Produit getId_categorieProduit() {
        return categorieProduit;
    }

    public String getNom_categorie() {
        return categorieProduit.getNom_categorie();
    }
}

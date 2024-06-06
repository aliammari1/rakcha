package com.esprit.models.produits;

import com.esprit.models.users.Client;

import java.util.Date;

public class Commentaire {


    private int idcommentaire;

    private Client client;

    private String commentaire;

    private Produit produit;



    public Commentaire() {
    }

    public Commentaire(Client client, String commentaire, Produit produit) {

        this.client = client;
        this.commentaire = commentaire;
        this.produit = produit;

    }


    public Commentaire(int idcommentaire, Client client, String commentaire, Produit produit) {
        this.idcommentaire = idcommentaire;
        this.client = client;
        this.commentaire = commentaire;
        this.produit = produit;

    }



    
    /** 
     * @return int
     */
    public int getIdCommentaire() {
        return idcommentaire;
    }

    
    /** 
     * @param idCommentaire
     */
    public void setIdCommentaire(int idCommentaire) {
        this.idcommentaire = idCommentaire;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }





    @Override
    public String toString() {
        return "Commentaire{" +
                "idcommentaire=" + idcommentaire +
                ", client=" + client +
                ", commentaire='" + commentaire + '\'' +
                ", produit=" + produit +

                '}';
    }
}

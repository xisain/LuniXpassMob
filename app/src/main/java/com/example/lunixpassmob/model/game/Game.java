package com.example.lunixpassmob.model.game;

import java.util.HashMap;
import java.util.List;

public class Game {
     private String gameName;
     private String gameDesc;
     private String gameImage;


     private String id;

     private List<String> genre;
     private HashMap<String, Integer> gameDetail;


     public Game(String id ,String gameName, String gameDesc, String gameImage, List<String> genre, HashMap<String, Integer> gameDetail){
          this.id = id;
          this.gameDetail = gameDetail;
          this.gameName = gameName;
          this.genre = genre;
          this.gameImage = gameImage;
          this.gameDesc = gameDesc;
     }

     public List<String> getGenre() {
          return genre;
     }

     public void setGenre(List<String> genre) {
          this.genre = genre;
     }

     public String getGameName() {
          return gameName;
     }

     public void setGameName(String gameName) {
          this.gameName = gameName;
     }

     public String getGameDesc() {
          return gameDesc;
     }

     public void setGameDesc(String gameDesc) {
          this.gameDesc = gameDesc;
     }

     public String getGameImage() {
          return gameImage;
     }

     public void setGameImage(String gameImage) {
          this.gameImage = gameImage;
     }

     public HashMap<String, Integer> getGameDetail() {
          return gameDetail;
     }

     public void setGameDetail(HashMap<String, Integer> gameDetail) {
          this.gameDetail = gameDetail;
     }

     public String getId() {
          return id;
     }

     public void setId(String id) {
          this.id = id;
     }

}

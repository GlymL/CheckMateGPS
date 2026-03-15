package com.example.demo;

import org.springframework.web.multipart.MultipartFile;

public class Vivienda{

    private String name;
    private String description;
    private MultipartFile image;
    

    public Vivienda(String name, String desc, MultipartFile image){
        this.name = name;
        this.description = desc;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Vivienda))
            return false;
        Vivienda other = (Vivienda)o;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
}

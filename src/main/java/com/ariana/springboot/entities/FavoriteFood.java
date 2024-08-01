package com.ariana.springboot.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "favorite_foods")
@Data
public class FavoriteFood {

    @EmbeddedId
    private FavoriteFoodId id;

    @Column(name = "is_favorite")
    private boolean isFavorite;
}

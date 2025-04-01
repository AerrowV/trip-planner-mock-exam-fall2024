package dat.config;

import dat.entities.Guide;
import dat.entities.Trip;
import dat.entities.enums.TripCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class Populator {

    public static void populate(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Guide guide1 = new Guide("Lars", "Wanderlust", "alice@trips.com", "12345678", 7);
        Guide guide2 = new Guide("Bob", "Trailblazer", "bob@adventures.com", "87654321", 5);
        Guide guide3 = new Guide("Denice", "Explorer", "charlie@nature.com", "55555555", 10);

        em.persist(guide1);
        em.persist(guide2);
        em.persist(guide3);

        List<Trip> trips = List.of(
                new Trip("Beach Paradise", "55.6761,12.5683", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 499.99, TripCategory.BEACH, guide1),
                new Trip("City Lights Tour", "48.8566,2.3522", LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), 399.99, TripCategory.CITY, guide1),
                new Trip("Forest Retreat", "61.9241,25.7482", LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(5), 299.99, TripCategory.FOREST, guide2),
                new Trip("Lake Chillout", "46.9479,7.4474", LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(6), 349.99, TripCategory.LAKE, guide2),
                new Trip("Snow Adventure", "60.4720,8.4689", LocalDateTime.now().plusDays(6), LocalDateTime.now().plusDays(8), 599.99, TripCategory.SNOW, guide3)
        );

        trips.forEach(em::persist);

        em.getTransaction().commit();
        em.close();
    }
}
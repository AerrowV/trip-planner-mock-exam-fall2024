package dat.daos.impl;

import dat.daos.IDAO;
import dat.daos.ITripGuideDAO;
import dat.dtos.TripDTO;
import dat.entities.Guide;
import dat.entities.Trip;
import dat.services.TripMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TripDAO implements IDAO<TripDTO, Integer>, ITripGuideDAO {

    private static TripDAO instance;
    private static EntityManagerFactory emf;

    public static TripDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new TripDAO();
        }
        return instance;
    }

    @Override
    public TripDTO create(TripDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            Guide guide = dto.getGuideId() != null ? em.find(Guide.class, dto.getGuideId()) : null;
            Trip trip = TripMapper.toEntity(dto, guide);

            em.getTransaction().begin();
            em.persist(trip);
            em.getTransaction().commit();

            return TripMapper.toDTO(trip);
        } finally {
            em.close();
        }
    }

    @Override
    public List<TripDTO> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Trip> trips = em.createQuery("SELECT t FROM Trip t", Trip.class).getResultList();
            return trips.stream().map(TripMapper::toDTO).toList();
        } finally {
            em.close();
        }
    }

    @Override
    public TripDTO getById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            Trip trip = em.find(Trip.class, id);
            if (trip == null) {
                throw new NoResultException("Trip with ID " + id + " not found");
            }
            return TripMapper.toDTO(trip);
        } finally {
            em.close();
        }
    }

    @Override
    public TripDTO update(Integer id, TripDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            Trip trip = em.find(Trip.class, id);
            if (trip == null) {
                throw new NoResultException("Trip with ID " + id + " not found");
            }

            Guide guide = dto.getGuideId() != null ? em.find(Guide.class, dto.getGuideId()) : null;

            em.getTransaction().begin();
            trip.setName(dto.getName());
            trip.setPrice(dto.getPrice());
            trip.setStartPosition(dto.getStartposition());
            trip.setStartTime(dto.getStarttime());
            trip.setEndTime(dto.getEndtime());
            trip.setCategory(dto.getCategory());
            trip.setGuide(guide);
            em.getTransaction().commit();

            return TripMapper.toDTO(trip);
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            Trip trip = em.find(Trip.class, id);
            if (trip == null) {
                throw new NoResultException("Trip with ID " + id + " not found");
            }

            em.getTransaction().begin();
            em.remove(trip);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void addGuideToTrip(int tripId, int guideId) {
        EntityManager em = emf.createEntityManager();
        try {
            Trip trip = em.find(Trip.class, tripId);
            Guide guide = em.find(Guide.class, guideId);

            if (trip == null || guide == null) {
                throw new NoResultException("Trip or Guide not found");
            }

            em.getTransaction().begin();
            trip.setGuide(guide);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Set<TripDTO> getTripsByGuide(int guideId) {
        EntityManager em = emf.createEntityManager();
        try {
            Guide guide = em.find(Guide.class, guideId);
            if (guide == null) {
                throw new NoResultException("Guide with ID " + guideId + " not found");
            }

            return guide.getTrips()
                    .stream()
                    .map(TripMapper::toDTO)
                    .collect(Collectors.toSet());
        } finally {
            em.close();
        }
    }
}

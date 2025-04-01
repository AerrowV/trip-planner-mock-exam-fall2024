package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.GuideDTO;
import dat.entities.Guide;
import dat.services.GuideMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;

import java.util.List;

public class GuideDAO implements IDAO<GuideDTO, Integer> {

    private static GuideDAO instance;
    private static EntityManagerFactory emf;

    public static GuideDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new GuideDAO();
        }
        return instance;
    }

    @Override
    public GuideDTO create(GuideDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            Guide guide = GuideMapper.toEntity(dto);

            em.getTransaction().begin();
            em.persist(guide);
            em.getTransaction().commit();

            return GuideMapper.toDTO(guide);
        } finally {
            em.close();
        }
    }

    @Override
    public GuideDTO getById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            Guide guide = em.find(Guide.class, id);
            if (guide == null) {
                throw new NoResultException("Guide with ID " + id + " not found");
            }
            return GuideMapper.toDTO(guide);
        } finally {
            em.close();
        }
    }

    @Override
    public List<GuideDTO> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Guide> guides = em.createQuery("SELECT g FROM Guide g", Guide.class).getResultList();
            return guides.stream().map(GuideMapper::toDTO).toList();
        } finally {
            em.close();
        }
    }

    @Override
    public GuideDTO update(Integer id, GuideDTO dto) {
        EntityManager em = emf.createEntityManager();
        try {
            Guide guide = em.find(Guide.class, id);
            if (guide == null) {
                throw new NoResultException("Guide with ID " + id + " not found");
            }

            em.getTransaction().begin();
            guide.setFirstname(dto.getFirstname());
            guide.setLastname(dto.getLastname());
            guide.setEmail(dto.getEmail());
            guide.setPhone(dto.getPhone());
            guide.setYearsOfExperience(dto.getYearsOfExperience());
            em.getTransaction().commit();

            return GuideMapper.toDTO(guide);
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            Guide guide = em.find(Guide.class, id);
            if (guide == null) {
                throw new NoResultException("Guide with ID " + id + " not found");
            }

            em.getTransaction().begin();
            em.remove(guide);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}

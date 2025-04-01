package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.config.Populator;
import dat.controllers.IController;
import dat.daos.impl.GuideDAO;
import dat.daos.impl.TripDAO;
import dat.dtos.TripDTO;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class TripController implements IController<TripDTO, Integer> {

    private final TripDAO tripDao;
    private final GuideDAO guideDao;
    private final EntityManagerFactory emf;

    public TripController() {
        this.emf = HibernateConfig.getEntityManagerFactory();
        this.tripDao = TripDAO.getInstance(emf);
        this.guideDao = GuideDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid trip ID")
                .get();
        TripDTO dto = tripDao.getById(id);
        ctx.status(200).json(dto);
    }

    @Override
    public void readAll(Context ctx) {
        List<TripDTO> trips = tripDao.getAll();
        ctx.status(200).json(trips);
    }

    @Override
    public void create(Context ctx) {
        TripDTO tripDTO = validateEntity(ctx);
        TripDTO created = tripDao.create(tripDTO);
        ctx.status(201).json(created);
    }

    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid trip ID")
                .get();
        TripDTO tripDTO = validateEntity(ctx);
        TripDTO updated = tripDao.update(id, tripDTO);
        ctx.status(200).json(updated);
    }

    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid trip ID")
                .get();
        tripDao.delete(id);
        ctx.status(204);
    }

    public void addGuideToTrip(Context ctx) {
        int tripId = Integer.parseInt(ctx.pathParam("tripId"));
        int guideId = Integer.parseInt(ctx.pathParam("guideId"));
        tripDao.addGuideToTrip(tripId, guideId);
        ctx.status(204);
    }

    public void populate(Context ctx) {
        Populator.populate(emf);
        ctx.status(201).result("Database populated");
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return id != null && id > 0;
    }

    @Override
    public TripDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(TripDTO.class)
                .check(t -> t.getName() != null && !t.getName().isEmpty(), "Name is required")
                .check(t -> t.getCategory() != null, "Category is required")
                .check(t -> t.getPrice() >= 0, "Price must be non-negative")
                .get();
    }
}

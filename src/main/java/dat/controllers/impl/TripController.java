package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.config.Populator;
import dat.controllers.IController;
import dat.daos.impl.GuideDAO;
import dat.daos.impl.TripDAO;
import dat.dtos.GuideTotalDTO;
import dat.dtos.PackingItemDTO;
import dat.dtos.TripDTO;
import dat.entities.enums.TripCategory;
import dat.exceptions.ApiException;
import dat.exceptions.ApiRuntimeException;
import dat.services.PackingService;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;

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
        if (dto == null) {
            throw new ApiRuntimeException(404, "Trip with ID " + id + " not found");
        }

        var packingItems = PackingService.getPackingItems(dto.getCategory());

        ctx.json(Map.of(
                "trip", dto,
                "packingItems", packingItems
        ));
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

        try {
            TripDTO updated = tripDao.update(id, tripDTO);
            ctx.status(200).json(updated);
        } catch (Exception e) {
            throw new ApiRuntimeException(404, "Trip with ID " + id + " not found for update");
        }
    }

    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid trip ID")
                .get();

        try {
            tripDao.delete(id);
            ctx.status(204);
        } catch (Exception e) {
            throw new ApiRuntimeException(404, "Trip with ID " + id + " not found for deletion");
        }
    }

    public void addGuideToTrip(Context ctx) {
        int tripId = Integer.parseInt(ctx.pathParam("tripId"));
        int guideId = Integer.parseInt(ctx.pathParam("guideId"));

        try {
            tripDao.addGuideToTrip(tripId, guideId);
            ctx.status(204);
        } catch (Exception e) {
            throw new ApiRuntimeException(404, "Could not assign guide. Trip ID " + tripId + " or Guide ID " + guideId + " not found");
        }
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

    public void getTripsByCategory(Context ctx) {
        try {
            TripCategory category = TripCategory.valueOf(ctx.pathParam("category").toUpperCase());
            List<TripDTO> result = tripDao.getTripsByCategory(category);
            ctx.status(200).json(result);
        } catch (IllegalArgumentException e) {
            throw new ApiRuntimeException(400, "Invalid category: " + ctx.pathParam("category"));
        }
    }

    public void getTotalPriceForGuide(Context ctx) {
        int guideId = Integer.parseInt(ctx.pathParam("guideId"));
        double total = tripDao.getTotalPriceByGuide(guideId);
        ctx.json(Map.of(
                "guideId", guideId,
                "totalPrice", total
        ));
    }

    public void getTotalPerGuide(Context ctx) {
        List<GuideTotalDTO> result = tripDao.getGuideTripTotals();
        ctx.json(result);
    }

    public void getPackingList(Context ctx) {
        int tripId = Integer.parseInt(ctx.pathParam("tripId"));
        TripDTO dto = tripDao.getById(tripId);

        if (dto == null) {
            throw new ApiRuntimeException(404, "Trip with ID " + tripId + " not found");
        }

        List<PackingItemDTO> packingItems = PackingService.getPackingItems(dto.getCategory());

        ctx.json(Map.of(
                "tripId", tripId,
                "category", dto.getCategory(),
                "packingItems", packingItems
        ));
    }

    public void getTotalPackingWeight(Context ctx) {
        int tripId = Integer.parseInt(ctx.pathParam("tripId"));
        TripDTO dto = tripDao.getById(tripId);

        if (dto == null) {
            throw new ApiRuntimeException(404, "Trip with ID " + tripId + " not found");
        }

        int totalWeight = PackingService.getPackingItems(dto.getCategory())
                .stream()
                .mapToInt(item -> item.getWeightInGrams() * item.getQuantity())
                .sum();

        ctx.json(Map.of(
                "tripId", tripId,
                "category", dto.getCategory(),
                "totalWeightInGrams", totalWeight
        ));
    }
}

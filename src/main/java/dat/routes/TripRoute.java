package dat.routes;

import dat.controllers.impl.TripController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class TripRoute {

    private final TripController tripController = new TripController();

    public EndpointGroup getRoutes() {
        return () -> {
            post("/populate", tripController::populate);
            get("/", tripController::readAll);
            get("/{id}", tripController::read);
            post("/", tripController::create);
            put("/{id}", tripController::update);
            delete("/{id}", tripController::delete);
            put("/{tripId}/guides/{guideId}", tripController::addGuideToTrip);
            get("/category/{category}", tripController::getTripsByCategory);
            get("/guides/{guideId}/totalprice", tripController::getTotalPriceForGuide);
            get("/guides/totalprice", tripController::getTotalPerGuide);
            get("/packinglist/{tripId}", tripController::getPackingList);
            get("/packinglist/{tripId}/weight", tripController::getTotalPackingWeight);
        };
    }
}

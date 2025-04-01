package dat.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final TripRoute tripRoute = new TripRoute();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/trips", tripRoute.getRoutes());
        };
    }
}

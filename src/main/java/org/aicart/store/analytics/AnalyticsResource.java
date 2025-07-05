package org.aicart.store.analytics;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.analytics.dto.DashboardStatsDTO;

@Path("/analytics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnalyticsResource {

    @Inject
    AnalyticsService analyticsService;

    @GET
    @Path("/dashboard")
    public Response getDashboardStats(@QueryParam("period") @DefaultValue("month") String period) {
        try {
            DashboardStatsDTO stats = analyticsService.getDashboardStats(period);
            return Response.ok(stats).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching dashboard stats: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/revenue")
    public Response getRevenueAnalytics(
            @QueryParam("period") @DefaultValue("month") String period,
            @QueryParam("groupBy") @DefaultValue("day") String groupBy) {
        try {
            // This can be expanded for more detailed revenue analytics
            DashboardStatsDTO stats = analyticsService.getDashboardStats(period);
            return Response.ok(stats.getRevenueChart()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching revenue analytics: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/orders")
    public Response getOrderAnalytics(
            @QueryParam("period") @DefaultValue("month") String period,
            @QueryParam("groupBy") @DefaultValue("day") String groupBy) {
        try {
            DashboardStatsDTO stats = analyticsService.getDashboardStats(period);
            return Response.ok(stats.getOrderChart()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching order analytics: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/categories")
    public Response getCategoryAnalytics(@QueryParam("period") @DefaultValue("month") String period) {
        try {
            DashboardStatsDTO stats = analyticsService.getDashboardStats(period);
            return Response.ok(stats.getTopCategories()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching category analytics: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/recent-orders")
    public Response getRecentOrders() {
        try {
            DashboardStatsDTO stats = analyticsService.getDashboardStats("month");
            return Response.ok(stats.getRecentOrders()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching recent orders: " + e.getMessage())
                    .build();
        }
    }
}

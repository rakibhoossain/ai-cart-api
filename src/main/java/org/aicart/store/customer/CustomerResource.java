package org.aicart.store.customer;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.customer.dto.*;
import org.aicart.store.customer.entity.CustomerType;
import org.aicart.store.customer.entity.CustomerTier;
import org.aicart.store.customer.mapper.CustomerAddressMapper;
import org.aicart.store.customer.mapper.CustomerMapper;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Inject
    CustomerService customerService;

    @Inject
    CustomerAddressMapper customerAddressMapper;

    private Long getShopId() {
        // In a real application, this would come from authentication context
        return 1L;
    }

    private String getCurrentUser() {
        // In a real application, this would come from authentication context
        return "admin@example.com";
    }

    // Legacy endpoints for backward compatibility
    @POST
    @Path("/legacy")
    public Response createCustomerLegacy(@Valid CustomerDTO dto) {
        return Response.status(Response.Status.CREATED)
                .entity(CustomerMapper.toDto(customerService.createCustomer(dto)))
                .build();
    }

    @GET
    @Path("/legacy/{id}")
    public Response getCustomerLegacy(@PathParam("id") Long id) {
        return customerService.getCustomer(id)
                .map(customer -> Response.ok(CustomerMapper.toDto(customer)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/legacy/{id}")
    public Response updateCustomerLegacy(@PathParam("id") Long id, @Valid CustomerDTO dto) {
        return Response.ok(CustomerMapper.toDto(customerService.updateCustomer(id, dto))).build();
    }

    @DELETE
    @Path("/legacy/{id}")
    public Response deleteCustomerLegacy(@PathParam("id") Long id) {
        customerService.deleteCustomer(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{customerId}/primary-address/{addressId}")
    public Response setPrimaryAddress(
            @PathParam("customerId") Long customerId,
            @PathParam("addressId") Long addressId
    ) {
        return Response.ok(CustomerMapper.toDto(customerService.setPrimaryAddress(customerId, addressId))).build();
    }

    @GET
    @Path("/{customerId}/primary-address")
    public Response getPrimaryAddress(@PathParam("customerId") Long customerId) {
        return customerService.getCustomer(customerId)
                .map(customer -> Response.ok(customerAddressMapper.toDTO(customer.primaryAddress)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // New comprehensive endpoints

    /**
     * Create a new customer
     */
    @POST
    public Response createCustomer(@Valid CustomerCreateRequestDTO createRequest) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            CustomerDetailDTO customer = customerService.createCustomer(shop, createRequest, getCurrentUser());
            return Response.status(Response.Status.CREATED).entity(customer).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Get paginated list of customers with filters
     */
    @GET
    public Response getCustomers(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("search") String search,
            @QueryParam("customerType") CustomerType customerType,
            @QueryParam("customerTier") CustomerTier customerTier,
            @QueryParam("emailVerified") Boolean emailVerified,
            @QueryParam("accountLocked") Boolean accountLocked,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("order") @DefaultValue("desc") String order) {

        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            // Parse dates
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;

            if (startDate != null && !startDate.trim().isEmpty()) {
                startDateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            if (endDate != null && !endDate.trim().isEmpty()) {
                endDateTime = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            CustomerListResponseDTO response = customerService.getCustomers(
                shop, search, customerType, customerTier, emailVerified,
                accountLocked, startDateTime, endDateTime, sortBy, order, page, size
            );

            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Get customer by ID
     */
    @GET
    @Path("/{id}")
    public Response getCustomer(@PathParam("id") Long id) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            CustomerDetailDTO customer = customerService.getCustomerDetail(shop, id);
            return Response.ok(customer).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Update an existing customer
     */
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, @Valid CustomerUpdateRequestDTO updateRequest) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            CustomerDetailDTO customer = customerService.updateCustomer(shop, id, updateRequest, getCurrentUser());
            return Response.ok(customer).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Delete a customer (soft delete)
     */
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            customerService.deleteCustomer(shop, id, getCurrentUser());
            return Response.ok(Map.of("message", "Customer deleted successfully")).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Get customer statistics
     */
    @GET
    @Path("/stats")
    public Response getCustomerStats() {
        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            CustomerStatsDTO stats = customerService.getCustomerStats(shop);
            return Response.ok(stats).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Export customers to CSV
     */
    @GET
    @Path("/export")
    @Produces("text/csv")
    public Response exportCustomers(
            @QueryParam("search") String search,
            @QueryParam("customerType") CustomerType customerType,
            @QueryParam("customerTier") CustomerTier customerTier,
            @QueryParam("emailVerified") Boolean emailVerified,
            @QueryParam("accountLocked") Boolean accountLocked,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate) {

        try {
            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            // Parse dates
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;

            if (startDate != null && !startDate.trim().isEmpty()) {
                startDateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            if (endDate != null && !endDate.trim().isEmpty()) {
                endDateTime = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            // Get all customers (no pagination for export)
            CustomerListResponseDTO response = customerService.getCustomers(
                shop, search, customerType, customerTier, emailVerified,
                accountLocked, startDateTime, endDateTime, "createdAt", "desc", 0, Integer.MAX_VALUE
            );

            // Generate CSV
            StringBuilder csv = new StringBuilder();
            csv.append("ID,First Name,Last Name,Email,Phone,Company,Customer Type,Customer Tier,Email Verified,Account Locked,Total Orders,Total Spent,Lifetime Value,Created At\n");

            for (CustomerListDTO customer : response.getCustomers()) {
                csv.append(customer.getId()).append(",")
                   .append(escapeCSV(customer.getFirstName())).append(",")
                   .append(escapeCSV(customer.getLastName())).append(",")
                   .append(escapeCSV(customer.getEmail())).append(",")
                   .append(escapeCSV(customer.getPhone())).append(",")
                   .append(escapeCSV(customer.getCompany())).append(",")
                   .append(customer.getCustomerType()).append(",")
                   .append(customer.getCustomerTier()).append(",")
                   .append(customer.isEmailVerified()).append(",")
                   .append(customer.isAccountLocked()).append(",")
                   .append(customer.getTotalOrders()).append(",")
                   .append(customer.getTotalSpent()).append(",")
                   .append(customer.getLifetimeValue()).append(",")
                   .append(customer.getCreatedAt()).append("\n");
            }

            return Response.ok(csv.toString())
                    .header("Content-Disposition", "attachment; filename=\"customers.csv\"")
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    // Helper method to escape CSV values
    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

package adapter.rest;

import dto.RequestPaymentData;
import dto.ReturnAccountInfo;
import entities.Account;
import service.DtuPayService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/merchant")
public class MerchantResource<T> {
    DtuPayService<T> service = new DtuPayFactory<T>().getDtuPayService();

    @POST
    @Path("/register")
    @Consumes("application/json")
    @Produces({"application/json", MediaType.TEXT_PLAIN})
    public Response registerCustomerAccount(Account account) {
        try{
            var newAccountId = service
                    .getAccountService()
                    .registerAccount(account);
            if (((ReturnAccountInfo) newAccountId)
                    .getAccountId()
                    .equals("")){
                // Account was NOT created
                return Response.status(Response.Status.BAD_REQUEST).entity(((ReturnAccountInfo) newAccountId).getErrorMessage()).build();
            } else {
                // Account was created
                return Response.status(Response.Status.OK).entity(((ReturnAccountInfo) newAccountId).getAccountId()).build();
            }
        }
        catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/payment")
    @Consumes("application/json")
    @Produces({"application/json", MediaType.TEXT_PLAIN})
    public Response createPayment(RequestPaymentData paymentData) {
        try{
            var response = service
                    .getPaymentService()
                    .createPayment(paymentData);

            if(response.getClass() == Boolean.class) {
                // we get a boolean which means payment succeeded
                 return Response.status(Response.Status.OK).entity("Payment Succeeded").build();
            } else {
                // payment failed
                return Response.status(Response.Status.BAD_REQUEST).entity("Payment Failed, " + ((String) response) ).build();
            }
        }
        catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Response getCustomerAccountById(@PathParam("id") String id){
        try{
            var response = service
                    .getAccountService()
                    .getAccount(id);

            if(response.getAccount() != null){
                return Response.status(Response.Status.OK).entity(response.getAccount()).build();
            }else{
                return Response.status(Response.Status.NOT_FOUND).entity(response.getErrorMessage()).build();
            }
        } catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST).entity("Something went wrong, " + e ).build();
        }
    }
}

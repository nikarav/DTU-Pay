package adapter.rest;

import dto.RequestTokens;
import dto.ReturnAccountInfo;
import entities.Account;
import service.DtuPayService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.UUID;

//TODO: FIX API request/response information retrieved from queues. Make it as minimal as possible.
// return only necessary info.

@Path("/customer")
public class CustomerResource<T> {

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
                return Response.status(Response.Status.CREATED).entity(((ReturnAccountInfo) newAccountId).getAccountId()).build();
            }
        }
        catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/getToken")
    @Consumes("application/json")
    @Produces({"application/json", MediaType.TEXT_PLAIN})
    public Response getCustomerTokens(RequestTokens reqTokens){
        try{
            var response = service
                    .getTokenService()
                    .getTokens(reqTokens);

            if(response.getClass() == String.class){
                // Something went wrong in the token microservice
                return Response.status(Response.Status.BAD_REQUEST).entity((String)response).build();
            } else {
                // We have received the tokens
                return Response.status(Response.Status.OK).entity((ArrayList<UUID>)response).build();
            }
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Something went wrong, " + e ).build();
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

//package Controllers;
//
//import DTO.CreateAccountData;
//import javax.ws.rs.*;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import Entities.Account;
//import services.AccountService;
//import java.util.ArrayList;
//import java.util.List;
//
//
///*
//TODO: refactor exposed endpoints to communicate with message queues
//      Do the tests in the same project. We don't need end to end for
//      each microservice
// */
//
//@Path("/accounts")
//public class AccountController {
//    AccountService accountService = new AccountService();
//
//    @GET
//    @Path("/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getAccountById(@PathParam("id") String id){
//        try{
//            Account accountToBeReturned = accountService.getAccount(id);
//            if(accountToBeReturned != null){
//                return Response.status(Response.Status.OK).entity(accountToBeReturned).build();
//            }else{
//                return Response.status(Response.Status.NOT_FOUND).entity("Account with id: " + id + " not found").build();
//            }
//        } catch (Exception e){
//            return Response.status(Response.Status.BAD_REQUEST).entity("Something went wrong, " + e ).build();
//        }
//    }
//
//    //get all accounts
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getAllAccounts(){
//        List<Account> accountsToBeReturned = new ArrayList<>();
//
//        try{
//            accountsToBeReturned = accountService.getAccounts();
//            return Response.status(Response.Status.OK).entity(accountsToBeReturned).build();
//        }catch (Exception e){
//            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
//        }
//    }
//
//    //create accounts
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
//    public Response postAccount(CreateAccountData createAccountData) {
//        try{
//            Account accountToBeCreated = accountService.createAccountFromData(createAccountData);
//            return Response.status(Response.Status.CREATED).entity(accountToBeCreated).build();
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
//        }
//    }
//
//    //delete account
//    @DELETE
//    @Path("/{id}")
//    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
//    public Response deleteAccount(@PathParam("id") String id){
//        try{
//            boolean accountDeleted = accountService.deleteAccountInfo(id);
//            if(accountDeleted){
//                return Response.status(Response.Status.NO_CONTENT).entity("Account with id: " + id + " deleted").build();
//            }else{
//                return Response.status(Response.Status.NOT_FOUND).entity("Account with id: " + id + " not found").build();
//            }
//        }catch (Exception e){
//            return Response.status(Response.Status.BAD_REQUEST).entity("Something went wrong, " + e ).build();
//        }
//    }
//}

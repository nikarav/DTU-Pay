//package controllers;
//
//import dto.CreatePaymentData;
//import models.Transaction;
//import services.PaymentService;
//import javax.ws.rs.*;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import java.util.ArrayList;
//import java.util.List;
//
//
//@Path("/payment")
//public class PaymentController {
//
//    PaymentService paymentService = new PaymentService();
//
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.TEXT_PLAIN)
//    public Response createPayment(CreatePaymentData paymentData) {
//
//        try{
//            if (paymentService.postCreatePayment(paymentData)){
//                return Response.status(Response.Status.NO_CONTENT).entity("OK").build();
//            }
//            else{
//                return Response.status(Response.Status.FORBIDDEN).entity("Debit or Credit client not found").build();
//            }
//        }catch (Exception e){
//            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
//        }
//    }
//
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getAllTransactions(){
//        List<Transaction> transactionsToBeReturned = new ArrayList<>();
//
//        try{
//            transactionsToBeReturned = paymentService.getTransactionList();
//            return Response.status(Response.Status.OK).entity(transactionsToBeReturned).build();
//        }catch (Exception e){
//            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
//        }
//    }
//
//    @GET
//    @Path("/merchant/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getMerchantTransactionsById(@PathParam("id") String id){
//        try{
//            List<Transaction> transactionList = paymentService.getMerchantTransactions(id);
//            return Response.status(Response.Status.OK).entity(transactionList).build();
//        } catch (Exception e){
//            return Response.status(Response.Status.BAD_REQUEST).entity("Something went wrong, " + e ).build();
//        }
//    }
//
//    @GET
//    @Path("/customer/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getCustomerTransactionsById(@PathParam("id") String id){
//        try{
//            List<Transaction> transactionList = paymentService.getCustomerTransactions(id);
//            return Response.status(Response.Status.OK).entity(transactionList).build();
//        } catch (Exception e){
//            return Response.status(Response.Status.BAD_REQUEST).entity("Something went wrong, " + e ).build();
//        }
//    }
//}

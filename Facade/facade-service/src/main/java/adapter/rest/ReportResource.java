package adapter.rest;

import service.DtuPayService;

import javax.print.attribute.standard.Media;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/report")
public class ReportResource<T> {

    DtuPayService<T> service = new DtuPayFactory<T>().getDtuPayService();

    @GET
    @Path("/manager")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getManagerReport(){
        try{
            System.out.println("Report manager has requested.");

            var reportResponse = service.getReportService().getReportManager();
            return Response.status(Response.Status.OK).entity(reportResponse).build();

        } catch (Exception e){
            System.out.println("Report manager failed");
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/customer/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getCustomerReport(@PathParam("id") String id){
        try{
            System.out.println("Report customer has requested.");

            var reportResponse = service.getReportService().getReportCustomer(id);
            return Response.status(Response.Status.OK).entity(reportResponse).build();

        } catch (Exception e){
            System.out.println("Report customer failed");
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/merchant/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getMerchantReport(@PathParam("id") String id){
        try{
            System.out.println("Report merchant has requested.");

            var reportResponse = service.getReportService().getReportMerchant(id);
            return Response.status(Response.Status.OK).entity(reportResponse).build();

        } catch (Exception e){
            System.out.println("Report merchant failed");
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}

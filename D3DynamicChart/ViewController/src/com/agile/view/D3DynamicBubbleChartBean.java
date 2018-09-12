package com.agile.view;

import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import javax.faces.context.FacesContext;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;



import javax.naming.Context;
import javax.naming.InitialContext;

import javax.sql.DataSource;





import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;



public class D3DynamicBubbleChartBean {
    public D3DynamicBubbleChartBean() {
        executeD3Query();
    }
    Connection con;
    PreparedStatement nodeStmt;
    PreparedStatement linkStmt;
    ResultSet nodeRslt;
    ResultSet linkRslt;
    String nodes;
    String links;
     JsonObject d3JsonObject; 
   

    public void setD3JsonObject(JsonObject d3JsonObject) {
        this.d3JsonObject = d3JsonObject;
    }

    public JsonObject getD3JsonObject() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExtendedRenderKitService service =
            (ExtendedRenderKitService) org.apache.myfaces.trinidad.util.Service.getRenderKitService(facesContext,
                                                                                                    ExtendedRenderKitService.class);
      //  service.addScript(facesContext, "generateNetworkGraph(" + d3JsonObject + ");");
        service.addScript(facesContext, "sayHello1("+d3JsonObject+")");
       
        return d3JsonObject;
    }



    
    
    
    public String executeD3Query() {
        System.out.println("executeD3Query");

        DataSource datasource = null;
        JsonArrayBuilder jArrayRelationshipObj = Json.createArrayBuilder();
        JsonArrayBuilder jArrayNodeObj = Json.createArrayBuilder();
        try {
            Context initialContext = new InitialContext();
            if (initialContext == null) {
            }
            datasource = (DataSource) initialContext.lookup("java:comp/env/jdbc/hrDS");
            if (datasource != null) {
                con = datasource.getConnection();
                System.out.println("con " + con);
            } else {
                System.out.println("Failed to Find JDBC DataSource.");

            }
            nodes = "select emp.EMPLOYEE_ID as id from employees emp\n" + 
            "union\n" + 
            "select dep.DEPARTMENT_ID as id from DEPARTMENTS dep";

            links =
                "select emp.EMPLOYEE_ID as source,dep.DEPARTMENT_ID as target from employees emp\n" + 
                "join DEPARTMENTS dep on emp.DEPARTMENT_ID = dep.DEPARTMENT_ID";
            
            
            nodeStmt = con.prepareStatement(nodes);
            

            nodeRslt = nodeStmt.executeQuery();
            while (nodeRslt.next()) {
                System.out.println("id-" + nodeRslt.getInt(1));
                jArrayNodeObj.add(Json.createObjectBuilder().add("id", nodeRslt.getInt(1) +""));
           //      System.out.println("jArrayNodeObj "+jArrayNodeObj);               


            }
            linkStmt = con.prepareStatement(links);
            linkRslt = linkStmt.executeQuery();
            while (linkRslt.next()) {
                System.out.println("source" + linkRslt.getInt(1) + "target" +   linkRslt.getString(2));
                jArrayRelationshipObj.add(Json.createObjectBuilder().add("source", linkRslt.getInt(1) +"".trim()).add("target", linkRslt.getInt(2)+"".trim()));
                                
           

            }
            
         d3JsonObject= Json.createObjectBuilder().add("nodes", jArrayNodeObj).add("links", jArrayRelationshipObj).build();
         
           
            System.out.println(" d3JsonObject "+d3JsonObject);
        } catch (SQLException e) {
            e.printStackTrace();

        } 
        catch (Exception e) {
                    e.printStackTrace();

                }
        finally {
            try {
                con.close();
                nodeRslt.close();
                nodeStmt.close();
                linkStmt.close();
                linkRslt.close();


            } catch (Exception e) {

            }
        }

   return null;
    }
    
    
    
    
}

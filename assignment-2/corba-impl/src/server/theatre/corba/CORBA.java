package server.theatre.corba;

import org.omg.CORBA.*;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import server.theatre.Theatre;
import server.theatre.corba.impl.AdminImpl;
import server.theatre.corba.impl.CustomerImpl;

/**
 * @author mkjodhani
 * @version 1.1
 * @project CORBA implementation
 * @since 16/02/23
 */
public class CORBA implements Runnable {
    String args[];
    String serverPrefix;

    public CORBA(String[] args, String serverPrefix) {
        this.args = args;
        this.serverPrefix = serverPrefix;
    }

    @Override
    public void run() {
        try{
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            AdminImpl admin = new AdminImpl();
            Theatre.setAdmin(admin);
            admin.setOrb(orb);

            CustomerImpl customer = new CustomerImpl();
            Theatre.setCustomer(customer);
            customer.setOrb(orb);


            // get object reference from the servant
            org.omg.CORBA.Object customerRef = rootpoa.servant_to_reference(customer);
            Customer customerHref = CustomerHelper.narrow(customerRef);

            // get object reference from the servant
            org.omg.CORBA.Object adminRef = rootpoa.servant_to_reference(admin);
            Admin adminHref = AdminHelper.narrow(adminRef);

            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            org.omg.CORBA.Object adminObjRef =
                    orb.resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncAdminRef = NamingContextExtHelper.narrow(adminObjRef);


            // get the root naming context
            // NameService invokes the name service
            org.omg.CORBA.Object customerObjRef =
                    orb.resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncCustomerRef = NamingContextExtHelper.narrow(customerObjRef);


            // bind the Object Reference in Naming
            NameComponent adminPath[] = ncAdminRef.to_name( "admin"+serverPrefix );
            ncRef.rebind(adminPath, adminHref);

            // bind the Object Reference in Naming
            NameComponent customerPath[] = ncCustomerRef.to_name( "customer"+serverPrefix );
            ncRef.rebind(customerPath, customerHref);

            System.out.println("CORBA service is ready and waiting ...");

            // wait for invocations from clients
            orb.run();
        }

        catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }
    }
}

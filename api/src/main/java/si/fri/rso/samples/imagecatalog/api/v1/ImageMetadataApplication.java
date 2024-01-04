package si.fri.rso.samples.imagecatalog.api.v1;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


import com.kumuluz.ee.cors.annotations.CrossOrigin;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(title = "Customers API", version = "v1",
        contact = @Contact(email = "rso@fri.uni-lj.si"),
        license = @License(name = "dev"), description = "API for managing customers."),
//        servers = @Server(url = "http://localhost:8081/")
        servers = @Server(url = "http://20.242.208.205:8081")
)

@ApplicationPath("/v1")
@CrossOrigin
public class ImageMetadataApplication extends Application {

}

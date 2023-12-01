package si.fri.rso.samples.imagecatalog.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.samples.imagecatalog.models.entities.ServiceType;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

@RequestScoped
public class ServiceTypeBean {
    private Logger log = Logger.getLogger(ServiceTypeBean.class.getName());

    @Inject
    private EntityManager em;


    public List<ServiceType> getServiceTypesFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, ServiceType.class, queryParameters);
    }

    public ServiceType getServiceType(Integer id) {

        ServiceType serviceType = em.find(ServiceType.class, id);

        if (serviceType == null) {
            throw new NotFoundException();
        }



        return serviceType;
    }

    public ServiceType createServiceType(ServiceType serviceType) {
        try {
            beginTx();
            em.persist(serviceType);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (serviceType.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return serviceType;
    }



    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}

package com.kapture.security.util;
import com.kapture.security.user.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

@Component
public class QueryUtil {

    @Autowired
    private SessionFactory sessionFactory;
    private final Logger logger = LoggerFactory.getLogger(QueryUtil.class);

    public <T> List<T> runQueryHelper(String queryString, Map<String, Object> parametersToSet, Class<T> className, Integer limit, Integer offset) {
        List<T> list = null;
        try (Session session = sessionFactory.openSession()) {
            TypedQuery<T> query = session.createNativeQuery(queryString, className);
            if (parametersToSet != null && !parametersToSet.isEmpty()) {
                parametersToSet.forEach(query::setParameter);
            }

            if (limit != null && offset != null) {
                query.setMaxResults(limit);
                query.setFirstResult(offset);
            }
            list = query.getResultList();
            if (list != null && list.size() == 0) {
                list = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public <T> boolean saveOrUpdateUser(T user) {
        boolean success = false;
        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            session.saveOrUpdate(user);
            tx.commit();
            success = true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.error("Exception in saveOrUpdate() {} : {}", user.getClass().getSimpleName(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return success;
    }

//    public int executeSaveUserQuery(String queryString, Map<String, Object> parametersToSet) {
//        try (Session session = sessionFactory.openSession()) {
//            session.beginTransaction(); // Begin a transaction before executing the insert query
//            TypedQuery<?> query = session.createNativeQuery(queryString);
//            if (parametersToSet != null && !parametersToSet.isEmpty()) {
//                parametersToSet.forEach(query::setParameter);
//            }
//            int noRowAffected = query.executeUpdate();
//            session.getTransaction().commit();
//            return noRowAffected;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return -1;
//        }
//    }

//    public int updateQueryHelper(Map<String, Object> parameterToPass, String queryString){
//        try(Session session = sessionFactory.openSession()){
//            session.beginTransaction();
//            NativeQuery<?> query = session.createNativeQuery(queryString);
//            if(parameterToPass != null && !parameterToPass.isEmpty()){
//                parameterToPass.forEach(query::setParameter);
//            }
//            int noOfRowsAffected=query.executeUpdate();
//            session.getTransaction().commit();
//            return noOfRowsAffected;
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            return -1;
//        }
//    }


}

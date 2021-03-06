package org.openmrs.module.conceptreview.api.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.api.APIException;
import org.openmrs.module.conceptpropose.PackageStatus;
import org.openmrs.module.conceptpropose.ProposalStatus;
import org.openmrs.module.conceptreview.ProposedConceptReview;
import org.openmrs.module.conceptreview.ProposedConceptReviewPackage;
import org.openmrs.module.conceptreview.api.db.ProposedConceptPackageReviewDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Hibernate specific database methods for {@link ProposedConceptPackageReviewDAO}
 */
@Repository
public class HibernateProposedConceptPackageReviewDAO implements ProposedConceptPackageReviewDAO {
	
	private static Log log = LogFactory.getLog(HibernateProposedConceptPackageReviewDAO.class);

	@Autowired
	private SessionFactory sessionFactory;

    @Override
    public List<ProposedConceptReviewPackage> getCompletedConceptProposalReviewPackages(){
        List<ProposedConceptReviewPackage> result = (List<ProposedConceptReviewPackage>) sessionFactory.getCurrentSession().createQuery("from ProposedConceptReviewPackage package where package.status = 'CLOSED'").list();
        if (log.isDebugEnabled()) {
            log.info("getCompletedConceptProposalReviewPackages returned: " + result.size() + " results");
        }
        return result;

    }

    @Override
    public List<ProposedConceptReviewPackage> getDeletedConceptProposalReviewPackages(){
        List<ProposedConceptReviewPackage> result = (List<ProposedConceptReviewPackage>) sessionFactory.getCurrentSession().createQuery("from ProposedConceptReviewPackage package where package.status = 'DELETED'").list();
        if (log.isDebugEnabled()) {
            log.info("getDeletedConceptProposalReviewPackages returned: " + result.size() + " results");
        }
        return result;

    }

    @Override
    public List<ProposedConceptReviewPackage> getOpenConceptProposalReviewPackages(){
        List<ProposedConceptReviewPackage> result = (List<ProposedConceptReviewPackage>) sessionFactory.getCurrentSession().createQuery("from ProposedConceptReviewPackage package where package.status = 'RECEIVED'").list();
        if (log.isDebugEnabled()) {
            log.info("getOpenConceptProposalReviewPackages returned: " + result.size() + " results");
        }
        return result;

    }

	@Override
	public List<ProposedConceptReviewPackage> getAllConceptProposalReviewPackages() {
		@SuppressWarnings("unchecked")
        List<ProposedConceptReviewPackage> result = (List<ProposedConceptReviewPackage>) sessionFactory.getCurrentSession().createQuery("from ProposedConceptReviewPackage").list();
		if (log.isDebugEnabled()) { log.info("getAllConceptProposalPackageReviews returned: " + result.size() + " results");
		}
		return result;
	}
	
	@Override
	public ProposedConceptReviewPackage getConceptProposalReviewPackageById(Integer id) {
		if (id != null) {
			Query query = sessionFactory.getCurrentSession().createQuery("from ProposedConceptReviewPackage package where package.id = :id");
			query.setInteger("id", id);
			ProposedConceptReviewPackage result = (ProposedConceptReviewPackage) query.uniqueResult();
			if (log.isDebugEnabled()) { log.debug("getConceptProposalPackageReviewById returned: " + result); }
			return result;
		} else {
			log.warn("Attempting to get package with null id");
			return null;
		}
	}
	
	@Override
	public ProposedConceptReviewPackage getConceptProposalReviewPackageByProposalUuid(String uuid) {
		if (uuid != null) {
			Query query = sessionFactory.getCurrentSession().createQuery("from ProposedConceptReviewPackage package where package.proposedConceptPackageUuid = :uuid");
			query.setString("uuid", uuid);
			ProposedConceptReviewPackage result = (ProposedConceptReviewPackage) query.uniqueResult();
			if (log.isDebugEnabled()) { log.debug("getConceptProposalPackageReviewByProposalUuid returned: " + result); }
			return result;
		} else {
			log.warn("Attempting to get package with null uuid");
			return null;
		}
	}
	
	@Override
	public ProposedConceptReviewPackage saveConceptProposalReviewPackage(ProposedConceptReviewPackage conceptPackageReview) {
		if (conceptPackageReview != null) {
			boolean hasUnprocessedConcepted = false;
			if(conceptPackageReview.getProposedConcepts() != null && conceptPackageReview.getStatus() != null)
			{
				for (final ProposedConceptReview conceptProposal : conceptPackageReview.getProposedConcepts()) {
					if(conceptProposal.getStatus() == ProposalStatus.RECEIVED) {
						hasUnprocessedConcepted = true;
					}
				}
				conceptPackageReview.setStatus(hasUnprocessedConcepted ? PackageStatus.RECEIVED : PackageStatus.CLOSED);
			}
			sessionFactory.getCurrentSession().saveOrUpdate(conceptPackageReview);
			return conceptPackageReview;
		} else {
			log.warn("Attempting to save or update null package");
			return null;
		}
	}
	
	@Override
	public void deleteConceptProposalReviewPackage(ProposedConceptReviewPackage conceptPackageReview) {
		if (conceptPackageReview != null) {
			sessionFactory.getCurrentSession().delete(conceptPackageReview);
		} else {
			log.warn("Attempting to delete null package");
		}
		
	}

	@Override
	public void deleteConceptProposalReviewPackageById(final int proposalId) {
        ProposedConceptReviewPackage proposedConceptReviewPackage = getConceptProposalReviewPackageById(proposalId);
        proposedConceptReviewPackage.setStatus(PackageStatus.DELETED);
        sessionFactory.getCurrentSession().saveOrUpdate(proposedConceptReviewPackage);
	}

	@Override
	public ProposedConceptReview getConceptProposalReviewBySourceProposalUuidAndSourceConceptUuid(String packageUuid, String conceptUuid) throws APIException {
		if (packageUuid!= null && conceptUuid != null) {
			ProposedConceptReviewPackage conceptReviewPackage = getConceptProposalReviewPackageByProposalUuid(packageUuid);
			ProposedConceptReview result = null;
			for(ProposedConceptReview review : conceptReviewPackage.getProposedConcepts())
			{
				if(review.getProposedConceptUuid().equals(conceptUuid)){
					result = review;
					break;
				}
			}
			if (log.isDebugEnabled()) { log.debug("getConceptProposalReviewBySourceProposalUuidAndSourceConceptUuid returned: " + result); }
			return result;
		} else {
			log.warn("Attempting to get concept with null source package uuid and null source concept uuid");
			return null;
		}
	}


}

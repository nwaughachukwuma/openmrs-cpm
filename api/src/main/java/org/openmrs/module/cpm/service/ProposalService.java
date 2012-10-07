package org.openmrs.module.cpm.service;

import java.util.List;

import org.openmrs.module.cpm.model.Proposal;

public interface ProposalService {

	Proposal getProposalById(Integer id);

	List<Proposal> findAll();

	Integer createProposal(Proposal newProposal);

	void saveProposal(Proposal proposal);

	void deleteProposal(Integer valueOf);

}
package edu.kit.kastel.dsis.fluidtrust.uncertainty.result.interpretation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.pcm.allocation.impl.AllocationContextImpl;
import org.palladiosimulator.pcm.core.composition.impl.AssemblyContextImpl;
import org.palladiosimulator.pcm.seff.impl.BranchActionImpl;
import org.palladiosimulator.pcm.seff.util.SeffSwitch;
import org.palladiosimulator.pcm.uncertainty.variation.UncertaintyVariationModel.gen.pcm.VariationManager;
import org.palladiosimulator.pcm.uncertainty.variation.UncertaintyVariationModel.gen.pcm.adapter.resource.ModelResourceAbstraction;
import org.palladiosimulator.pcm.uncertainty.variation.UncertaintyVariationModel.gen.pcm.adapter.resource.ResourceAbstraction;
import org.palladiosimulator.pcm.usagemodel.impl.BranchImpl;

import UncertaintyVariationModel.VariationPoint;
import UncertaintyVariationModel.impl.UncertaintyVariationsImpl;
import edu.kit.kastel.dsis.fluidtrust.casestudy.pcs.analysis.dto.ActionBasedQueryResult;
import edu.kit.kastel.dsis.fluidtrust.casestudy.pcs.analysis.dto.ActionSequence;
import edu.kit.kastel.dsis.fluidtrust.casestudy.pcs.analysis.dto.SEFFActionSequenceElementImpl;
import edu.kit.kastel.dsis.fluidtrust.uncertainty.dataflow.analysis.ViolatingConstraintActionSequence;

public class Step3ResultInterpretation implements ResultInterpretation {

	@Override
	public Object getInterpretation(ArrayList<ActionBasedQueryResult> violations) {
		final URI umURI = URI.createPlatformPluginURI(
				"/edu.kit.kastel.dsis.fluidtrust.uncertainty.variation.analysis/models/My.uncertaintyvariationmodel",
				false);

		ResourceAbstraction rs = new ModelResourceAbstraction();
		VariationManager vm = new VariationManager(umURI, rs);

		UncertaintyVariationsImpl test = (UncertaintyVariationsImpl) vm.loadUncertaintyVariantModel();
		var test2 = test.getVariationPoints();

		HashSet<String> uncertaintyPointIds = new HashSet<String>();
		HashMap<String,VariationPoint> uncertaintyPointIdsToVariationPoint = new HashMap<String,VariationPoint>();

		String variationPoints = "";
		for (VariationPoint entry : test2) {
			var varyingSubjects = entry.getVaryingSubjects();
			for (var entry2 : varyingSubjects) {
				var id = entry2.getId();
				uncertaintyPointIds.add(id);
				uncertaintyPointIdsToVariationPoint.put(id, entry);
				/*
				 * if (entry2 instanceof AssemblyContextImpl) { var assemblyName =
				 * ((AssemblyContextImpl) entry2).getEntityName(); variationPoints +=
				 * assemblyName + " "; var x = 1; } else if (entry2 instanceof BranchActionImpl)
				 * { var branchName = ((BranchActionImpl) entry2).getEntityName();
				 * variationPoints += branchName + " "; var x = 1; } else if (entry2 instanceof
				 * AllocationContextImpl) { var allocationName = ((AllocationContextImpl)
				 * entry2).getEntityName(); variationPoints += allocationName + " "; var x = 1;
				 * } else if (entry2 instanceof BranchImpl) { var branchName = ((BranchImpl)
				 * entry2).getEntityName(); variationPoints += branchName + " "; var x = 1; }
				 * else { throw new Error(); }
				 */
			}

		}

		for (var violation : violations) {
			var entrySet = violation.getResults().entrySet();
			
			for (var result : entrySet) {
				ActionSequence key = result.getKey();
				var x = 1;
				
				// TODO: MISSING INFORMATION about which uncertainty modifies which which literal or architectural decision
				ViolatingConstraintActionSequence violatingConstraint = (ViolatingConstraintActionSequence) key
						.get(key.size() - 1);
				SEFFActionSequenceElementImpl occuringElement = (SEFFActionSequenceElementImpl) violatingConstraint
						.getOccuringElement();
				key.remove(key.size() - 1);
				
				HashSet<String> violationIds = AnalysisUtility.getIdsFromEntrySet(key);
								
				HashSet<VariationPoint> influencingUncertainties = new HashSet<VariationPoint>();
				
				for (var violationId: violationIds) {
					for (var uncertaintyPointId: uncertaintyPointIds) {
						if (violationId.equals(uncertaintyPointId)) {
							influencingUncertainties.add(uncertaintyPointIdsToVariationPoint.get(uncertaintyPointId));
							int i = 1;
						}
					}
				}
				
				influencingUncertainties.forEach(u -> System.out.print(u.getEntityName() + ", "));
				System.out.println("\n---------\n");
			}
		}

		return null;
	}

}
